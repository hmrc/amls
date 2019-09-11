/*
 * Copyright 2019 HM Revenue & Customs
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
import javax.inject.Inject
import metrics.API10
import models.des
import models.des.{DeregisterSubscriptionRequest, DeregisterSubscriptionResponse}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsSuccess, Json, Writes}
import play.api.{Application, Configuration, Environment, Logger}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import utils.ApiRetryHelper
import javax.inject.Singleton

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeregisterSubscriptionConnector @Inject()(app: Application, val rmc: Configuration, env: Environment, appConfig: ApplicationConfig) extends DESConnector(app, rmc, env, appConfig) {

  def deregistration(amlsRegistrationNumber: String, data: DeregisterSubscriptionRequest) (
    implicit ec: ExecutionContext,
    wr1: Writes[DeregisterSubscriptionRequest],
    wr2: Writes[DeregisterSubscriptionResponse],
    hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[DeregisterSubscriptionResponse] = {
    apiRetryHelper.doWithBackoff(() => deregistrationFunction(amlsRegistrationNumber, data))
  }

  private def deregistrationFunction(amlsRegistrationNumber: String, data: DeregisterSubscriptionRequest)(implicit ec: ExecutionContext,
                                                                                          wr1: Writes[DeregisterSubscriptionRequest],
                                                                                          wr2: Writes[DeregisterSubscriptionResponse],
                                                                                          hc: HeaderCarrier
  ): Future[DeregisterSubscriptionResponse] = {
    val prefix = "[DESConnector][deregistration]"
    val bodyParser = JsonParsed[DeregisterSubscriptionResponse]
    val timer = metrics.timer(API10)
    Logger.debug(s"$prefix - Request body: ${Json.toJson(data)}")

    val url = s"$fullUrl/$amlsRegistrationNumber/deregistration"
    httpPost.POST[des.DeregisterSubscriptionRequest, HttpResponse](url, data)(wr1,implicitly[HttpReads[HttpResponse]],desHeaderCarrier,ec) map {
      response =>
        timer.stop()
        Logger.debug(s"$prefix - Base Response: ${response.status}")
        Logger.debug(s"$prefix - Response Body: ${response.body}")
        response
    } flatMap {
      case r@status(OK) & bodyParser(JsSuccess(body: DeregisterSubscriptionResponse, _)) =>
        metrics.success(API10)
        audit.sendDataEvent(DeregisterSubscriptionEvent(amlsRegistrationNumber, data, body))
        Logger.debug(s"$prefix - Success response")
        Logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
        Logger.debug(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        Future.successful(body)
      case r@status(s) =>
        metrics.failed(API10)
        Logger.warn(s"$prefix - Failure response: $s")
        Logger.warn(s"$prefix - CorrelationId: ${r.header("CorrelationId") getOrElse ""}")
        Future.failed(HttpStatusException(s, Option(r.body)))
    } recoverWith {
      case e: HttpStatusException =>
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(API10)
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }
  }
}
