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

import audit.DeregisterSubscriptionEvent
import config.ApplicationConfig
import exceptions.HttpStatusException
import metrics.{API10, Metrics}
import models.des.{DeregisterSubscriptionRequest, DeregisterSubscriptionResponse}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsSuccess, Json, Writes}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import utils.{ApiRetryHelper, AuditHelper}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeregisterSubscriptionConnector @Inject() (
  private[connectors] val appConfig: ApplicationConfig,
  private[connectors] val ac: AuditConnector,
  private[connectors] val httpClientV2: HttpClientV2,
  private[connectors] val metrics: Metrics
) extends DESConnector(appConfig) {

  def deregistration(amlsRegistrationNumber: String, data: DeregisterSubscriptionRequest)(implicit
    ec: ExecutionContext,
    wr1: Writes[DeregisterSubscriptionRequest],
    wr2: Writes[DeregisterSubscriptionResponse],
    hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[DeregisterSubscriptionResponse] =
    apiRetryHelper.doWithBackoff(() => deregistrationFunction(amlsRegistrationNumber, data))

  private def deregistrationFunction(amlsRegistrationNumber: String, data: DeregisterSubscriptionRequest)(implicit
    ec: ExecutionContext,
    wr1: Writes[DeregisterSubscriptionRequest],
    wr2: Writes[DeregisterSubscriptionResponse],
    hc: HeaderCarrier
  ): Future[DeregisterSubscriptionResponse] = {
    val prefix     = "[DESConnector][deregistration]"
    val bodyParser = JsonParsed[DeregisterSubscriptionResponse]
    val timer      = metrics.timer(API10)
    logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val audit: Audit = new Audit(AuditHelper.appName, ac)

    val url = s"$fullUrl/$amlsRegistrationNumber/deregistration"
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
        case r @ status(OK) & bodyParser(JsSuccess(body: DeregisterSubscriptionResponse, _)) =>
          metrics.success(API10)
          audit.sendDataEvent(DeregisterSubscriptionEvent(amlsRegistrationNumber, data, body))
          logHttpResponse(prefix, r, success = true)
          Future.successful(body)
        case r @ status(s)                                                                   =>
          metrics.failed(API10)
          logHttpResponse(prefix, r, success = false)
          Future.failed(HttpStatusException(s, Option(r.body)))
      }
      .recoverWith {
        case e: HttpStatusException =>
          logException(prefix, e)
          Future.failed(e)
        case e                      =>
          timer.stop()
          metrics.failed(API10)
          logException(prefix, e)
          Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
      }
  }
}
