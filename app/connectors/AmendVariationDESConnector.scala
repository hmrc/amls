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

import audit.{AmendmentEvent, AmendmentEventFailed}
import config.ApplicationConfig
import exceptions.HttpStatusException
import metrics.{API6, Metrics}
import models.des
import play.api.http.Status._
import play.api.libs.json.{JsSuccess, Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.ApiRetryHelper

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AmendVariationDESConnector @Inject() (
  private[connectors] val appConfig: ApplicationConfig,
  private[connectors] val ac: AuditConnector,
  private[connectors] val httpClient: HttpClient,
  private[connectors] val metrics: Metrics
) extends DESConnector(appConfig) {

  def amend(amlsRegistrationNumber: String, data: des.AmendVariationRequest)(implicit
    ec: ExecutionContext,
    wr1: Writes[des.AmendVariationRequest],
    wr2: Writes[des.AmendVariationResponse],
    hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[des.AmendVariationResponse] =
    apiRetryHelper.doWithBackoff(() => amendFunction(amlsRegistrationNumber, data))

  private def amendFunction(amlsRegistrationNumber: String, data: des.AmendVariationRequest)(implicit
    ec: ExecutionContext,
    wr1: Writes[des.AmendVariationRequest],
    wr2: Writes[des.AmendVariationResponse],
    hc: HeaderCarrier
  ): Future[des.AmendVariationResponse] = {
    val prefix     = "[DESConnector][amend]"
    val bodyParser = JsonParsed[des.AmendVariationResponse]
    val timer      = metrics.timer(API6)
    logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val url = s"$fullUrl/$amlsRegistrationNumber"

    httpClient.PUT[des.AmendVariationRequest, HttpResponse](url, data, headers = desHeaders)(
      wr1,
      implicitly[HttpReads[HttpResponse]],
      hc,
      ec
    ) map { response =>
      timer.stop()
      logger.debug(s"$prefix - Base Response: ${response.status}")
      logger.debug(s"$prefix - Response Body: ${response.body}")
      response
    } flatMap {
      case r @ status(OK) & bodyParser(JsSuccess(body: des.AmendVariationResponse, _)) =>
        metrics.success(API6)
        logger.debug(s"$prefix - Success response")
        logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
        logger.debug(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        ac.sendExtendedEvent(AmendmentEvent(amlsRegistrationNumber, data, body))
        Future.successful(body)
      case r @ status(s)                                                               =>
        metrics.failed(API6)
        logger.warn(s"$prefix - Failure response: $s")
        logger.warn(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        val httpEx = HttpStatusException(s, Option(r.body))
        ac.sendExtendedEvent(AmendmentEventFailed(amlsRegistrationNumber, data, httpEx))
        Future.failed(httpEx)
    } recoverWith {
      case e: HttpStatusException =>
        logger.warn(s"$prefix - Failure: Exception", e)
        ac.sendExtendedEvent(AmendmentEventFailed(amlsRegistrationNumber, data, e))
        Future.failed(e)
      case e                      =>
        timer.stop()
        metrics.failed(API6)
        logger.warn(s"$prefix - Failure: Exception", e)
        val httpEx = HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage))
        ac.sendExtendedEvent(AmendmentEventFailed(amlsRegistrationNumber, data, httpEx))
        Future.failed(httpEx)
    }
  }
}
