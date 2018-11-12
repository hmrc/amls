package utils

import akka.actor.ActorSystem
import akka.pattern.Patterns.after
import exceptions.HttpStatusException
import javax.inject.Inject
import play.Play
import play.api.Logger
import play.api.libs.concurrent.Akka

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait BackOffHelper {

  val MAX_ATTEMPTS: Int = 10
  val INITIAL_WAIT_MS: Int = 10
  val WAIT_FACTOR: Float= 1.5f

  //@Inject() private var as: ActorSystem = _

  def expBackOffHelper[T] ( f: () => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    expBackOffHelper(1, INITIAL_WAIT_MS, f)
  }

  private def expBackOffHelper[T] (currentAttempt: Int,
                                   currentWait: Int,
                                   f: () => Future[T])(implicit ec: ExecutionContext): Future[T] = {

    // TODO: This method is deprecated, but @Inject wasn't working with test - figure out neater way to do this!
    val as = Play.application.injector.instanceOf(classOf[ActorSystem])

    f.apply().recoverWith {
      case e: HttpStatusException =>
        if ( e.status == 503 && currentAttempt < MAX_ATTEMPTS) {
          val wait = Math.ceil(currentWait * WAIT_FACTOR).toInt
          Logger.warn(s"Failure, retrying after $wait ms")
          after(wait.milliseconds, as.scheduler, ec, Future.successful(1)).flatMap { _ =>
            expBackOffHelper(currentAttempt + 1, wait.toInt, f)
          }
        } else {
          Future.failed(e)
        }
      case e =>
        Future.failed(e)
    }
  }
}
