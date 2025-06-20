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

import audit.SubscriptionStatusEvent
import config.ApplicationConfig
import exceptions.HttpStatusException
import metrics.{API9, Metrics}
import models.des
import models.des.ReadStatusResponse
import play.api.http.Status._
import play.api.libs.json.{JsSuccess, Json, Writes}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import utils.{ApiRetryHelper, AuditHelper}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubscriptionStatusDESConnector @Inject() (
  private[connectors] val appConfig: ApplicationConfig,
  private[connectors] val ac: AuditConnector,
  private[connectors] val httpClientV2: HttpClientV2,
  private[connectors] val metrics: Metrics
) extends DESConnector(appConfig) {

  def status(amlsRegistrationNumber: String)(implicit
    ec: ExecutionContext,
    wr: Writes[ReadStatusResponse],
    hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[ReadStatusResponse] =
    apiRetryHelper.doWithBackoff(() => statusFunction(amlsRegistrationNumber))

  private def statusFunction(
    amlsRegistrationNumber: String
  )(implicit ec: ExecutionContext, wr: Writes[ReadStatusResponse], hc: HeaderCarrier): Future[ReadStatusResponse] = {

    val prefix     = "[DESConnector][readstatus]"
    val bodyParser = JsonParsed[ReadStatusResponse]
    val timer      = metrics.timer(API9)
    logger.debug(s"$prefix - reg no: $amlsRegistrationNumber")

    val audit: Audit = new Audit(AuditHelper.appName, ac)

    val Url = s"$fullUrl/$amlsRegistrationNumber"

    httpClientV2
      .get(url"$Url/status")
      .setHeader(desHeaders: _*)
      .execute[HttpResponse]
      .map { response =>
        timer.stop()
        logger.debug(s"$prefix - Base Response: ${response.status}")
        logger.debug(s"$prefix - Response Body: ${response.body}")
        response
      }
      .flatMap {
        case _ @status(OK) & bodyParser(JsSuccess(body: des.ReadStatusResponse, _)) =>
          metrics.success(API9)
          audit.sendDataEvent(SubscriptionStatusEvent(amlsRegistrationNumber, body))
          logger.debug(s"$prefix - Success response")
          logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
          Future.successful(body)
        case r @ status(s)                                                          =>
          metrics.failed(API9)
          logger.warn(s"$prefix - Failure response: $s")
          Future.failed(HttpStatusException(s, Option(r.body)))
      }
      .recoverWith {
        case e: HttpStatusException =>
          logger.warn(s"$prefix - Failure: Exception", e)
          Future.failed(e)
        case e                      =>
          timer.stop()
          metrics.failed(API9)
          logger.warn(s"$prefix - Failure: Exception", e)
          Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
      }
  }
}
