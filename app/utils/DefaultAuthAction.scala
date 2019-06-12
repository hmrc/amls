package utils

import javax.inject.Inject
import play.api.mvc.{ActionBuilder, ActionFilter, Result, Results}
import uk.gov.hmrc.http.{HeaderCarrier, Request}
import uk.gov.hmrc.play.HeaderCarrierConverter
import uk.gov.hmrc.play.auth.microservice.connectors.ConfidenceLevel

import scala.concurrent.{ExecutionContext, Future}

class DefaultAuthAction @Inject()(
                                   val authConnector: NewAuthConnector
                                 )(implicit ec: ExecutionContext) extends AuthAction with AuthorisedFunctions {

  override protected def filter[A](request: Request[A]): Future[Option[Result]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorised(ConfidenceLevel.L50) {
      Future.successful(None)
    }.recover[Option[Result]] {
      case _: AuthorisationException =>
        Some(Results.Unauthorized)
    }
  }
}



trait AuthAction extends ActionFilter[Request] with ActionBuilder[Request]