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

import audit.{AmendmentEvent, AmendmentEventFailed}
import config.ApplicationConfig
import exceptions.HttpStatusException
import metrics.{API6, Metrics}
import models.des
import models.des.{AmendVariationRequest, AmendVariationResponse}
import play.api.http.Status.*
import play.api.libs.json.{JsSuccess, Json, Writes}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.ApiRetryHelper

import javax.inject.*
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AmendVariationDESConnector @Inject() (
  private[connectors] val appConfig: ApplicationConfig,
  private[connectors] val ac: AuditConnector,
  private[connectors] val httpClientV2: HttpClientV2,
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

  private def amendFunction(amlsRegistrationNumber: String, data: AmendVariationRequest)(implicit
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
    httpClientV2
      .put(url"$url")
      .setHeader(desHeaders: _*)
      .withBody(Json.toJson(data))
      .execute[HttpResponse]
      .map { response =>
        timer.stop()
        logHttpResponse(prefix, response, success = true)
        response
      }
      .flatMap {
        case r @ status(OK) & bodyParser(JsSuccess(body: des.AmendVariationResponse, _)) =>
          metrics.success(API6)
          logHttpResponse(prefix, r, success = true)
          ac.sendExtendedEvent(AmendmentEvent(amlsRegistrationNumber, data, body))
          Future.successful(body)
        case r @ status(s)                                                               =>
          metrics.failed(API6)
          logHttpResponse(prefix, r, success = false)
          val httpEx = HttpStatusException(s, Option(r.body))
          ac.sendExtendedEvent(AmendmentEventFailed(amlsRegistrationNumber, data, httpEx))
          Future.failed(httpEx)
      }
      .recoverWith {
        case e: HttpStatusException =>
          logException(prefix, e)
          ac.sendExtendedEvent(AmendmentEventFailed(amlsRegistrationNumber, data, e))
          Future.failed(e)
        case e                      =>
          timer.stop()
          metrics.failed(API6)
          logException(prefix, e)
          val httpEx = HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage))
          ac.sendExtendedEvent(AmendmentEventFailed(amlsRegistrationNumber, data, httpEx))
          Future.failed(httpEx)
      }
  }
}
