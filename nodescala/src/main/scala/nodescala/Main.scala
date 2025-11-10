package nodescala

import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global

object Main {
  def main(args: Array[String]): Unit = {

    // 1. 8191 포트에 /test 경로 서버 생성
    val myServer             = new NodeScala.Default(8191)
    val myServerSubscription = myServer.start("/test") { request =>
      // 여기선 Request가 비어 있으니 그냥 간단한 문자열만 응답
      Iterator("Hello from NodeScala server!\n")
    }

    // 2. 사용자가 ENTER를 치면 완료되는 Future
    val userInterrupted: Future[String] =
      Future.userInput("Hit ENTER to cancel... ").continueWith { f =>
        "You entered... " + f.now
      }

    // 3. 20초 뒤에 "Server timeout!"으로 완료되는 Future
    val timeOut: Future[String] =
      Future.delay(20.seconds).continue(_ => "Server timeout!")

    // 4. 둘 중 먼저 끝나는 쪽을 따르는 Future
    val terminationRequested: Future[String] =
      Future.any(List(userInterrupted, timeOut))

    // 5. 종료 시 메시지 출력 + 서버 정리
    terminationRequested.onComplete {
      case scala.util.Success(msg) =>
        println(msg)
        myServerSubscription.unsubscribe()
        println("Bye!")

      case scala.util.Failure(ex) =>
        println(s"Error: ${ex.getMessage}")
        myServerSubscription.unsubscribe()
        println("Bye!")
    }

    // main 스레드가 그냥 끝나버리지 않게, terminationRequested 기다리기
    // (과제 요구사항상 꼭 필요하진 않지만, 실행 편의를 위해 잠깐 block)
    Await.result(terminationRequested, Duration.Inf)
  }
}
