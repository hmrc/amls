/*
 * Copyright 2023 HM Revenue & Customs
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
import javax.inject.{Inject, Singleton}
import metrics.{API10, Metrics}
import models.des
import models.des.{DeregisterSubscriptionRequest, DeregisterSubscriptionResponse}
import play.api.Logger
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsSuccess, Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.ApiRetryHelper

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeregisterSubscriptionConnector @Inject()(private[connectors] val appConfig: ApplicationConfig,
                                                private[connectors] val ac: AuditConnector,
                                                private[connectors] val httpClient: HttpClient,
                                                private[connectors] val metrics: Metrics) extends DESConnector(appConfig, ac) {

  def deregistration(amlsRegistrationNumber: String, data: DeregisterSubscriptionRequest)
                    (implicit ec: ExecutionContext, wr1: Writes[DeregisterSubscriptionRequest], wr2: Writes[DeregisterSubscriptionResponse],
                     hc: HeaderCarrier, apiRetryHelper: ApiRetryHelper): Future[DeregisterSubscriptionResponse] = {
    apiRetryHelper.doWithBackoff(() => deregistrationFunction(amlsRegistrationNumber, data))
  }

  private def deregistrationFunction(amlsRegistrationNumber: String, data: DeregisterSubscriptionRequest)
                                    (implicit ec: ExecutionContext, wr1: Writes[DeregisterSubscriptionRequest],
                                     wr2: Writes[DeregisterSubscriptionResponse], hc: HeaderCarrier
                                    ): Future[DeregisterSubscriptionResponse] = {
    val prefix = "[DESConnector][deregistration]"
    val bodyParser = JsonParsed[DeregisterSubscriptionResponse]
    val timer = metrics.timer(API10)
    logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val url = s"$fullUrl/$amlsRegistrationNumber/deregistration"
    httpClient.POST[des.DeregisterSubscriptionRequest, HttpResponse](url, data, headers = desHeaders)(wr1, implicitly[HttpReads[HttpResponse]], hc, ec) map {
      response =>
        timer.stop()
        logger.debug(s"$prefix - Base Response: ${response.status}")
        logger.debug(s"$prefix - Response Body: ${response.body}")
        response
    } flatMap {
      case r@status(OK) & bodyParser(JsSuccess(body: DeregisterSubscriptionResponse, _)) =>
        metrics.success(API10)
        audit.sendDataEvent(DeregisterSubscriptionEvent(amlsRegistrationNumber, data, body))
        logger.debug(s"$prefix - Success response")
        logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
        logger.debug(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        Future.successful(body)
      case r@status(s) =>
        metrics.failed(API10)
        logger.warn(s"$prefix - Failure response: $s")
        logger.warn(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        Future.failed(HttpStatusException(s, Option(r.body)))
    } recoverWith {
      case e: HttpStatusException =>
        logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(API10)
        logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }
  }
}
