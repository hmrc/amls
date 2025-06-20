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

import audit.SubscriptionViewEvent
import config.ApplicationConfig
import exceptions.HttpStatusException
import metrics.{API5, Metrics}
import models.des.SubscriptionView
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import utils.{ApiRetryHelper, AuditHelper}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewDESConnector @Inject() (
  private[connectors] val appConfig: ApplicationConfig,
  private[connectors] val ac: AuditConnector,
  private[connectors] val httpClientV2: HttpClientV2,
  private[connectors] val metrics: Metrics
) extends DESConnector(appConfig) {

  def view(
    amlsRegistrationNumber: String
  )(implicit ec: ExecutionContext, hc: HeaderCarrier, apiRetryHelper: ApiRetryHelper): Future[SubscriptionView] =
    apiRetryHelper.doWithBackoff(() => viewFunction(amlsRegistrationNumber))

  private def viewFunction(
    amlsRegistrationNumber: String
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[SubscriptionView] = {

    val bodyParser = JsonParsed[SubscriptionView]
    val timer      = metrics.timer(API5)
    val prefix     = "[DESConnector][view]"

    val Url = s"$fullUrl/$amlsRegistrationNumber"

    val audit: Audit = new Audit(AuditHelper.appName, ac)

    httpClientV2
      .get(url"$Url")
      .setHeader(desHeaders: _*)
      .execute[HttpResponse]
      .map { response =>
        timer.stop()
        logger.debug(s"$prefix - Base Response: ${response.status}")
        logger.debug(s"$prefix - Response Body: ${response.body}")
        response
      }
      .flatMap {
        case status(OK) & bodyParser(JsSuccess(body: SubscriptionView, _)) =>
          metrics.success(API5)
          audit.sendDataEvent(SubscriptionViewEvent(amlsRegistrationNumber, body))
          logger.debug(s"$prefix - Success response")
          logger.debug(s"$prefix - Response body: $body")
          Future.successful(body)
        case r @ status(OK) & bodyParser(JsError(errs))                    =>
          metrics.failed(API5)
          logger.warn(s"$prefix - Deserialisation Errors: $errs")
          Future.failed(
            HttpStatusException(INTERNAL_SERVER_ERROR, Some("Failed to parse the json response from DES (API5)"))
          )
        case r @ status(s)                                                 =>
          metrics.failed(API5)
          logger.warn(s"$prefix - Failure response: $s")
          Future.failed(HttpStatusException(s, Option(r.body)))
      }
      .recoverWith {
        case e: HttpStatusException =>
          logger.warn(s"$prefix - Failure: Exception", e)
          Future.failed(e)
        case e                      =>
          timer.stop()
          metrics.failed(API5)
          logger.warn(s"$prefix - Failure: Exception", e)
          Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
      }
  }

}
