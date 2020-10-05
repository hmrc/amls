/*
 * Copyright 2020 HM Revenue & Customs
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

import config.ApplicationConfig
import exceptions.HttpStatusException
import javax.inject.Inject
import metrics.{Metrics, PayAPI}
import models.payapi.Payment
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.JsSuccess
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import utils.HttpResponseHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PayAPIConnector @Inject()(private[connectors] val applicationConfig: ApplicationConfig,
                                private[connectors] val httpClient: HttpClient,
                                private[connectors] val metrics: Metrics) extends HttpResponseHelper {

  private[connectors] val paymentUrl = applicationConfig.payAPIUrl

  def getPayment(paymentId: String)(implicit headerCarrier: HeaderCarrier): Future[Payment] = {
    getPaymentFunction(paymentId)
  }

  private def getPaymentFunction(paymentId: String)(implicit headerCarrier: HeaderCarrier): Future[Payment] = {

    val url = s"$paymentUrl/pay-api/payment/summary/$paymentId"

    val bodyParser = JsonParsed[Payment]

    val prefix = "[PayAPIConnector][getPayment]"
    val timer = metrics.timer(PayAPI)

    Logger.debug(s"$prefix - Request body: $paymentId")

    httpClient.GET[HttpResponse](url) map {
      response =>
        timer.stop()
        Logger.debug(s"$prefix - Base Response: ${response.status}")
        Logger.debug(s"$prefix - Response body: ${response.body}")
        response
    } flatMap {
      case response @ status(OK) & bodyParser(JsSuccess(body: Payment, _)) =>
        metrics.success(PayAPI)
        Logger.debug(s"$prefix - Success Response")
        Logger.debug(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.successful(body)
      case response @ status(s) =>
        metrics.failed(PayAPI)
        Logger.warn(s"$prefix - Failure Response: $s")
        Logger.warn(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.failed(HttpStatusException(s, Option(response.body)))
    } recoverWith {
      case e: HttpStatusException =>
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(PayAPI)
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }
  }
}
