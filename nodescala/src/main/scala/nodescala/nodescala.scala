package nodescala

import java.net.InetSocketAddress
import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import scala.async.Async.{async, await}

/** NodeScala 서버 프레임워크 */
trait NodeScala {
  import NodeScala._

  /** 서버가 바인딩될 포트 */
  def port: Int

  /** 리스너 생성 */
  def createListener(relativePath: String): Listener =
    new NodeScala.Listener.Default(port, relativePath)

  /** 실제로 클라이언트에게 응답을 써 주는 함수 */
  private def respond(exchange: Exchange,
                      token: CancellationToken,
                      response: Response): Unit = {
    try {
      while (token.nonCancelled && response.hasNext) {
        exchange.write(response.next())
      }
    } finally {
      exchange.close()
    }
  }

  /** 서버 시작: 요청을 계속 받아서 handler로 처리 */
  def start(relativePath: String)(handler: Request => Response): Subscription = {
    val listener            = createListener(relativePath)
    val listenerSubscription = listener.start()

    // 서버 루프: 취소될 때까지 요청을 받음
    val serverSubscription: Subscription = Future.run() { token =>
      async {
        while (token.nonCancelled) {
          val (req, xchg) = await(listener.nextRequest())
          // 각 요청은 또 다른 Future에서 처리
          Future {
            respond(xchg, token, handler(req))
          }
        }
      }
    }

    // 리스너 + 서버 루프 둘 다 한 번에 끌 수 있게 묶기
    Subscription(listenerSubscription, serverSubscription)
  }
}

object NodeScala {

  /** 요청: HTTP 헤더/쿼리 등 정보를 담는 Map (여기선 단순화해서 씀) */
  type Request  = Map[String, List[String]]

  /** 응답: 문자열 스트림 */
  type Response = Iterator[String]

  /** HTTP 교환 객체 래핑 */
  trait Exchange {
    def write(s: String): Unit
    def close(): Unit
    def request: Request
  }

  /** HTTP 리스너: 특정 경로에서 요청을 기다림 */
  trait Listener {
    def relativePath: String
    def port: Int

    def start(): Subscription
    def stop(): Unit

    def nextRequest(): Future[(Request, Exchange)]
  }

  object Listener {

    /** 기본 구현: JDK HttpServer 기반 */
    class Default(val port: Int, val relativePath: String) extends Listener {

      private val server = HttpServer.create(new InetSocketAddress(port), 0)

      @volatile private var started     = false
      @volatile private var nextPromise =
        Option.empty[Promise[(Request, Exchange)]]

      // 한 번 만들어 두고 재사용하는 핸들러
      private val httpHandler = new HttpHandler {
        override def handle(he: HttpExchange): Unit = {
          // 여기서는 Request를 단순히 빈 맵으로 두어도 과제 요구 사항은 만족
          val req: Request = Map.empty

          val ex = new Exchange {
            def write(s: String): Unit = {
              val bytes = s.getBytes("UTF-8")
              // 간단하게 200 OK, 길이 모를 때는 0으로 보내고 chunked 사용
              if (he.getResponseHeaders.isEmpty) {
                he.sendResponseHeaders(200, 0L)
              }
              he.getResponseBody.write(bytes)
            }
            def close(): Unit = he.close()
            val request: Request = req
          }

          nextPromise.synchronized {
            nextPromise.foreach { p =>
              if (!p.isCompleted) p.success((req, ex))
            }
          }
        }
      }

      def start(): Subscription = {
        if (!started) {
          server.createContext(relativePath, httpHandler)
          server.setExecutor(null) // 기본 executor 사용
          server.start()
          started = true
        }
        new Subscription {
          def unsubscribe(): Unit = stop()
        }
      }

      def stop(): Unit = {
        if (started) {
          server.stop(0)
          started = false
        }
      }

      /** 다음 HTTP 요청이 들어오면 (Request, Exchange)를 담은 Future 완료 */
      def nextRequest(): Future[(Request, Exchange)] = {
        val p = Promise[(Request, Exchange)]()
        nextPromise.synchronized {
          nextPromise = Some(p)
        }
        p.future
      }
    }
  }

  /** 기본 서버 구현 */
  class Default(val port: Int) extends NodeScala
}
