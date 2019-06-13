package utils

import scala.concurrent.Future
import play.api.mvc._

object SuccessfulAuthAction extends AuthAction {
  override protected def filter[A](request: Request[A]): Future[Option[Result]] =
    Future.successful(None)
}

object FailedAuthAction extends AuthAction {
  override protected def filter[A](request: Request[A]): Future[Option[Result]] =
    Future.successful(Some(Results.Unauthorized))
}
