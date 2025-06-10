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

import audit.WithdrawSubscriptionEvent
import config.ApplicationConfig
import exceptions.HttpStatusException
import metrics.{API8, Metrics}
import models.des
import models.des.{WithdrawSubscriptionRequest, WithdrawSubscriptionResponse}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsSuccess, Json, Writes}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import utils.{ApiRetryHelper, AuditHelper}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WithdrawSubscriptionConnector @Inject() (
  private[connectors] val appConfig: ApplicationConfig,
  private[connectors] val ac: AuditConnector,
  private[connectors] val httpClientV2: HttpClientV2,
  private[connectors] val metrics: Metrics
) extends DESConnector(appConfig) {

  def withdrawal(amlsRegistrationNumber: String, data: WithdrawSubscriptionRequest)(implicit
    ec: ExecutionContext,
    wr1: Writes[WithdrawSubscriptionRequest],
    wr2: Writes[WithdrawSubscriptionResponse],
    hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[WithdrawSubscriptionResponse] =
    apiRetryHelper.doWithBackoff(() => withdrawalFunction(amlsRegistrationNumber, data))

  private def withdrawalFunction(amlsRegistrationNumber: String, data: WithdrawSubscriptionRequest)(implicit
    ec: ExecutionContext,
    wr1: Writes[WithdrawSubscriptionRequest],
    wr2: Writes[WithdrawSubscriptionResponse],
    hc: HeaderCarrier
  ): Future[WithdrawSubscriptionResponse] = {
    val prefix     = "[DESConnector][withdrawal]"
    val bodyParser = JsonParsed[WithdrawSubscriptionResponse]
    val timer      = metrics.timer(API8)
    logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val audit: Audit = new Audit(AuditHelper.appName, ac)

    val url = s"$fullUrl/$amlsRegistrationNumber/withdrawal"
    httpClientV2
      .post(url"$url")
      .setHeader(desHeaders: _*)
      .withBody(Json.toJson(data))
      .execute[HttpResponse]
      .map { response =>
        timer.stop()
        logger.debug(s"$prefix - Base Response: ${response.status}")
        logger.debug(s"$prefix - Response Body: ${response.body}")
        response
      }
      .flatMap {
        case r @ status(OK) & bodyParser(JsSuccess(body: WithdrawSubscriptionResponse, _)) =>
          metrics.success(API8)
          audit.sendDataEvent(WithdrawSubscriptionEvent(amlsRegistrationNumber, data, body))
          logger.debug(s"$prefix - Success response")
          logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
          logger.debug(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
          Future.successful(body)
        case r @ status(s)                                                                 =>
          metrics.failed(API8)
          logger.warn(s"$prefix - Failure response: $s")
          logger.warn(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
          Future.failed(HttpStatusException(s, Option(r.body)))
      }
      .recoverWith {
        case e: HttpStatusException =>
          logger.warn(s"$prefix - Failure: Exception", e)
          Future.failed(e)
        case e                      =>
          timer.stop()
          metrics.failed(API8)
          logger.warn(s"$prefix - Failure: Exception", e)
          Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
      }
  }
}
