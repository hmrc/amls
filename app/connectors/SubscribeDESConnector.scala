/*
 * Copyright 2026 HM Revenue & Customs
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
import config.ApplicationConfig
import exceptions.HttpStatusException
import metrics.{API4, Metrics}
import models.des
import models.des.SubscriptionResponse
import play.api.http.Status.*
import play.api.libs.json.{JsSuccess, Json, Writes}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.ApiRetryHelper

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubscribeDESConnector @Inject() (
  private[connectors] val appConfig: ApplicationConfig,
  private[connectors] val ac: AuditConnector,
  private[connectors] val httpClientV2: HttpClientV2,
  private[connectors] val metrics: Metrics
) extends DESConnector(appConfig) {

  def subscribe(safeId: String, data: des.SubscriptionRequest)(implicit
    ec: ExecutionContext,
    wr1: Writes[des.SubscriptionRequest],
    wr2: Writes[SubscriptionResponse],
    hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[SubscriptionResponse] =
    apiRetryHelper.doWithBackoff(() => subscribeFunction(safeId, data))

  private def subscribeFunction(safeId: String, data: des.SubscriptionRequest)(implicit
    ec: ExecutionContext,
    wr1: Writes[des.SubscriptionRequest],
    wr2: Writes[SubscriptionResponse],
    hc: HeaderCarrier
  ): Future[SubscriptionResponse] = {
    val prefix     = "[DESConnector][subscribe]"
    val bodyParser = JsonParsed[SubscriptionResponse]
    val timer      = metrics.timer(API4)
    logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val url = s"$fullUrl/$safeId"
    httpClientV2
      .post(url"$url")
      .setHeader(desHeaders: _*)
      .withBody(Json.toJson(data))
      .execute[HttpResponse]
      .map { response =>
        timer.stop()
        logHttpResponse(prefix, response, success = true)
        response
      }
      .flatMap {
        case r @ status(OK) & bodyParser(JsSuccess(body: SubscriptionResponse, _)) =>
          metrics.success(API4)
          logHttpResponse(prefix, r, success = true)
          ac.sendExtendedEvent(SubscriptionEvent(safeId, data, body))
          Future.successful(body)
        case r @ status(s)                                                         =>
          metrics.failed(API4)
          logHttpResponse(prefix, r, success = false)
          val httpEx = HttpStatusException(s, Option(r.body))
          ac.sendExtendedEvent(SubscriptionFailedEvent(safeId, data, httpEx))
          Future.failed(httpEx)
      }
      .recoverWith {
        case e: HttpStatusException =>
          logException(prefix, e)
          ac.sendExtendedEvent(SubscriptionFailedEvent(safeId, data, e))
          Future.failed(e)
        case e                      =>
          timer.stop()
          metrics.failed(API4)
          logException(prefix, e)
          val httpEx = HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage))
          ac.sendExtendedEvent(SubscriptionFailedEvent(safeId, data, httpEx))
          Future.failed(httpEx)
      }
  }
}
