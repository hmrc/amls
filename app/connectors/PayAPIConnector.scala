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

import config.ApplicationConfig
import exceptions.HttpStatusException
import javax.inject.Inject
import metrics.{Metrics, PayAPI}
import models.payapi.Payment
import play.api.Mode.Mode
import play.api.http.Status._
import play.api.libs.json.JsSuccess
import play.api.{Application, Configuration, Environment, Logger}
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig
import utils.HttpResponseHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PayAPIConnector @Inject()(app: Application, override val runModeConfiguration: Configuration, environment: Environment, applicationConfig: ApplicationConfig, val httpClient: HttpClient) extends HttpResponseHelper with ServicesConfig {

  private[connectors] val paymentUrl = applicationConfig.payAPIUrl
  private[connectors] val metrics: Metrics = app.injector.instanceOf[Metrics]
  override protected def mode: Mode = environment.mode

  def getPayment(paymentId: String)(implicit headerCarrier: HeaderCarrier): Future[Payment] = {
    getPaymentFunction(paymentId)
  }

  private def getPaymentFunction(paymentId: String)(implicit headerCarrier: HeaderCarrier): Future[Payment] = {

    val url = s"$paymentUrl/pay-api/payment/$paymentId"

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
