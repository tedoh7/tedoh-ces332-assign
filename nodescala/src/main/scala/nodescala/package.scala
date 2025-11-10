package object nodescala {

  import scala.concurrent._
  import scala.concurrent.duration._
  import scala.util._
  import scala.util.control.NonFatal
  import scala.concurrent.blocking
  import ExecutionContext.Implicits.global

  /** Future companion object 확장 메소드들 */
  implicit class FutureCompanionOps(val f: Future.type) extends AnyVal {

    /** 항상 주어진 value로 즉시 완료되는 Future */
    def always[T](value: T): Future[T] =
      Future.successful(value)

    /** 절대 완료되지 않는 Future */
    def never[T]: Future[T] =
      Promise[T]().future

    /** 모든 Future가 끝나면 결과 List를 돌려주는 Future */
    def all[T](fs: List[Future[T]]): Future[List[T]] =
      fs.foldRight(Future.successful(List.empty[T])) { (ft, acc) =>
        for {
          x  <- ft
          xs <- acc
        } yield x :: xs
      }

    /** 가장 먼저 완료되는 Future의 결과로 완료되는 Future */
    def any[T](fs: List[Future[T]]): Future[T] = {
      val p = Promise[T]()
      fs.foreach { ft =>
        ft.onComplete(p.tryComplete)
      }
      p.future
    }

    /** 주어진 시간 뒤에 완료되는 Future[Unit] */
    def delay(t: Duration): Future[Unit] = Future {
      blocking {
        Thread.sleep(t.toMillis)
      }
    }

    /** 콘솔에서 한 줄 입력을 비동기로 받는 Future */
    def userInput(message: String): Future[String] = Future {
      blocking {
        scala.io.StdIn.readLine(message)
      }
    }

    /** CancellationToken 을 받아 비동기 작업을 실행하고, 취소용 Subscription 반환 */
    def run()(f: CancellationToken => Future[Unit]): Subscription = {
      val cts = CancellationTokenSource()
      f(cts.cancellationToken)   // 비동기 작업 시작
      cts                         // 취소는 cts.unsubscribe()로
    }
  }

  /** 개별 Future[T] 확장 메소드들 */
  implicit class FutureOps[T](val f: Future[T]) extends AnyVal {

    /** Future가 이미 완료돼 있으면 값을 돌려주고, 아니면 예외 */
    def now: T = f.value match {
      case Some(Success(v)) => v
      case Some(Failure(e)) => throw e
      case None             => throw new NoSuchElementException("Future not completed")
    }

    /** 이 Future가 끝난 뒤, Future 자체를 받아서 S를 계산 */
    def continueWith[S](cont: Future[T] => S): Future[S] = {
      val p = Promise[S]()
      f.onComplete { _ =>
        try p.success(cont(f))
        catch {
          case NonFatal(e) => p.failure(e)
        }
      }
      p.future
    }

    /** 이 Future의 Try[T] 결과를 받아서 S를 계산 */
    def continue[S](cont: Try[T] => S): Future[S] = {
      val p = Promise[S]()
      f.onComplete { t =>
        try p.success(cont(t))
        catch {
          case NonFatal(e) => p.failure(e)
        }
      }
      p.future
    }
  }

  /** Subscription: 이벤트/작업 구독 해제용 */
  trait Subscription {
    def unsubscribe(): Unit
  }

  object Subscription {
    /** 두 개의 Subscription을 하나로 묶어주는 helper */
    def apply(s1: Subscription, s2: Subscription): Subscription =
      new Subscription {
        def unsubscribe(): Unit = {
          s1.unsubscribe()
          s2.unsubscribe()
        }
      }
  }

  /** 취소 여부를 조회하는 토큰 */
  trait CancellationToken {
    def isCancelled: Boolean
    def nonCancelled: Boolean = !isCancelled
  }

  /** CancellationToken을 만들어 주는 소스 + Subscription 역할 */
  trait CancellationTokenSource extends Subscription {
    def cancellationToken: CancellationToken
  }

  object CancellationTokenSource {
    def apply(): CancellationTokenSource = new CancellationTokenSource {
      private val p = Promise[Unit]()

      val cancellationToken: CancellationToken = new CancellationToken {
        def isCancelled: Boolean = p.future.isCompleted
      }

      def unsubscribe(): Unit = {
        // 여러 번 호출돼도 안전
        p.trySuccess(())
      }
    }
  }
}
