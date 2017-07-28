/*
 * Copyright 2017 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import exceptions.HttpStatusException
import metrics.{Metrics, PayAPI}
import models.{KnownFactsForService, Payment}
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.play.audit.model.Audit
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.http.ws.WSHttp
import utils.HttpResponseHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//override private[connectors] val http = WSHttp
//override private[connectors] lazy val serviceURL = baseUrl("government-gateway-admin")
//override private[connectors] val metrics = Metrics
//override private[connectors] val audit: Audit = new Audit(AppName.appName, MicroserviceAuditConnector)

@Singleton
class PayAPIConnector @Inject()(
                                 private[connectors] val http: WSHttp, serviceURL: String,
                                 private[connectors] val metrics: Metrics
                               ) extends HttpResponseHelper with ServicesConfig {

  def getPayment(paymentId: String)(implicit headerCarrier: HeaderCarrier, writes: Writes[KnownFactsForService]): Future[HttpResponse] = {

    val url = s"$serviceURL/payment/${paymentId}"

    val prefix = "[PayAPIConnector][getPayment]"
    val timer = metrics.timer(PayAPI)
    Logger.debug(s"$prefix - Request body: ${Json.toJson(paymentId)}")
    http.GET[HttpResponse](url) map {
      response =>
        timer.stop()
        Logger.debug(s"$prefix - Base Response: ${response.status}")
        Logger.debug(s"$prefix - Response body: ${response.body}")
        response
    } flatMap {
      case response @ status(OK) =>
        metrics.success(PayAPI)
        Logger.debug(s"$prefix - Success Response")
        Logger.debug(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.successful(response)
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