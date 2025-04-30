/*
 * Copyright 2024 HM Revenue & Customs
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
import play.api.http.Status._
import play.api.libs.json.{JsSuccess, Json, Writes}
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
    wr2: Writes[des.SubscriptionResponse],
    hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[des.SubscriptionResponse] =
    apiRetryHelper.doWithBackoff(() => subscribeFunction(safeId, data))

  private def subscribeFunction(safeId: String, data: des.SubscriptionRequest)(implicit
    ec: ExecutionContext,
    wr1: Writes[des.SubscriptionRequest],
    wr2: Writes[des.SubscriptionResponse],
    hc: HeaderCarrier
  ): Future[des.SubscriptionResponse] = {
    val prefix     = "[DESConnector][subscribe]"
    val bodyParser = JsonParsed[des.SubscriptionResponse]
    val timer      = metrics.timer(API4)
    logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val url = s"$fullUrl/$safeId"
    httpClientV2.post(url"$url").setHeader(desHeaders: _*).withBody(Json.toJson(data)).execute[HttpResponse]
    .map { response =>
      timer.stop()
      logger.debug(s"$prefix - Base Response: ${response.status}")
      logger.debug(s"$prefix - Response Body: ${response.body}")
      response
    }.flatMap {
      case r @ status(OK) & bodyParser(JsSuccess(body: des.SubscriptionResponse, _)) =>
        metrics.success(API4)
        logger.debug(s"$prefix - Success response")
        logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
        logger.debug(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        ac.sendExtendedEvent(SubscriptionEvent(safeId, data, body))
        Future.successful(body)
      case r @ status(s)                                                             =>
        metrics.failed(API4)
        logger.warn(s"$prefix - Failure response: $s")
        logger.warn(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        val httpEx = HttpStatusException(s, Option(r.body))
        ac.sendExtendedEvent(SubscriptionFailedEvent(safeId, data, httpEx))
        Future.failed(httpEx)
    }.recoverWith {
      case e: HttpStatusException =>
        logger.warn(s"$prefix - Failure: Exception", e)
        ac.sendExtendedEvent(SubscriptionFailedEvent(safeId, data, e))
        Future.failed(e)
      case e                      =>
        timer.stop()
        metrics.failed(API4)
        logger.warn(s"$prefix - Failure: Exception", e)
        val httpEx = HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage))
        ac.sendExtendedEvent(SubscriptionFailedEvent(safeId, data, httpEx))
        Future.failed(httpEx)
    }
  }
}
