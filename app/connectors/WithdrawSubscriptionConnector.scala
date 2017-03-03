package connectors

import audit.WithdrawSubscriptionEvent
import exceptions.HttpStatusException
import metrics.API8
import models.des
import models.des.{WithdrawSubscriptionRequest, WithdrawSubscriptionResponse}
import play.api.Logger
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsSuccess, Json, Writes}
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.{ExecutionContext, Future}

trait WithdrawSubscriptionConnector extends DESConnector {

  def withdrawal(amlsRegistrationNumber: String, data: WithdrawSubscriptionRequest)(implicit ec: ExecutionContext,
                                                                                  wr1: Writes[WithdrawSubscriptionRequest],
                                                                                  wr2: Writes[WithdrawSubscriptionResponse]
  ): Future[WithdrawSubscriptionResponse] = {
    val prefix = "[DESConnector][withdrawal]"
    val bodyParser = JsonParsed[WithdrawSubscriptionResponse]
    val timer = metrics.timer(API8)
    Logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val url = s"$fullUrl/$amlsRegistrationNumber/withdrawal"
    httpPost.POST[des.WithdrawSubscriptionRequest, HttpResponse](url, data) map {
      response =>
        timer.stop()
        Logger.debug(s"$prefix - Base Response: ${response.status}")
        Logger.debug(s"$prefix - Response Body: ${response.body}")
        response
    } flatMap {
      case r@status(OK) & bodyParser(JsSuccess(body: WithdrawSubscriptionResponse, _)) =>
        metrics.success(API8)
        audit.sendDataEvent(WithdrawSubscriptionEvent(amlsRegistrationNumber, data, body))
        Logger.debug(s"$prefix - Success response")
        Logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
        Logger.debug(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        Future.successful(body)
      case r@status(s) =>
        metrics.failed(API8)
        Logger.warn(s"$prefix - Failure response: $s")
        Logger.warn(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        Future.failed(HttpStatusException(s, Option(r.body)))
    } recoverWith {
      case e: HttpStatusException =>
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(API8)
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }
  }
}
