/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import audit.{SubscriptionEvent, SubscriptionFailedEvent}
import exceptions.HttpStatusException
import metrics.API4
import models.des
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsSuccess, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, HttpPost, HttpReads, HttpResponse}
import utils.BackOffHelper

trait SubscribeDESConnector extends DESConnector with BackOffHelper {

  private[connectors] def httpPost: HttpPost

  def subscribe
  (safeId: String, data: des.SubscriptionRequest)
  (implicit
   ec: ExecutionContext,
   wr1: Writes[des.SubscriptionRequest],
   wr2: Writes[des.SubscriptionResponse],
   hc: HeaderCarrier
  ): Future[des.SubscriptionResponse] = {
    doWithBackoff(() => subscribeFunction(safeId, data))
  }

  private def subscribeFunction
  (safeId: String, data: des.SubscriptionRequest)
  (implicit
   ec: ExecutionContext,
   wr1: Writes[des.SubscriptionRequest],
   wr2: Writes[des.SubscriptionResponse],
   hc: HeaderCarrier
  ): Future[des.SubscriptionResponse] = {
    val prefix = "[DESConnector][subscribe]"
    val bodyParser = JsonParsed[des.SubscriptionResponse]
    val timer = metrics.timer(API4)
    Logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val url = s"$fullUrl/$safeId"

    httpPost.POST[des.SubscriptionRequest, HttpResponse](url, data)(wr1, implicitly[HttpReads[HttpResponse]], desHeaderCarrier,ec) map {
      response =>
        timer.stop()
        Logger.debug(s"$prefix - Base Response: ${response.status}")
        Logger.debug(s"$prefix - Response Body: ${response.body}")
        response
    } flatMap {
      case r@status(OK) & bodyParser(JsSuccess(body: des.SubscriptionResponse, _)) =>
        metrics.success(API4)
        Logger.debug(s"$prefix - Success response")
        Logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
        Logger.debug(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        auditConnector.sendExtendedEvent(SubscriptionEvent(safeId, data, body))
        Future.successful(body)
      case r@status(s) =>
        metrics.failed(API4)
        Logger.warn(s"$prefix - Failure response: $s")
        Logger.warn(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        val httpEx = HttpStatusException(s, Option(r.body))
        auditConnector.sendExtendedEvent(SubscriptionFailedEvent(safeId, data, httpEx))
        Future.failed(httpEx)
    } recoverWith {
      case e: HttpStatusException =>
        Logger.warn(s"$prefix - Failure: Exception", e)
        auditConnector.sendExtendedEvent(SubscriptionFailedEvent(safeId, data, e))
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(API4)
        Logger.warn(s"$prefix - Failure: Exception", e)
        val httpEx = HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage))
        auditConnector.sendExtendedEvent(SubscriptionFailedEvent(safeId, data, httpEx))
        Future.failed(httpEx)
    }
  }
}
