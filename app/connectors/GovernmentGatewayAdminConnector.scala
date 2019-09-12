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

import audit.KnownFactsEvent
import config.ApplicationConfig
import exceptions.HttpStatusException
import javax.inject.Inject
import metrics.{GGAdmin, Metrics}
import models.KnownFactsForService
import play.api.Mode.Mode
import play.api.http.Status._
import play.api.libs.json.{Json, Writes}
import play.api.{Application, Configuration, Environment, Logger}
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig
import utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GovernmentGatewayAdminConnector @Inject()(app: Application, override val runModeConfiguration: Configuration, environment: Environment, applicationConfig: ApplicationConfig, val auditConnector: AuditConnector, val httpClient: HttpClient) extends ServicesConfig with HttpResponseHelper {

  private[connectors] val serviceURL = applicationConfig.ggUrl
  //private[connectors] val wsHttp: WSHttp = app.injector.instanceOf(classOf[WSHttp])

  //private[connectors] val http: CorePost with CoreGet with CorePut = wsHttp
  private[connectors] val metrics: Metrics = app.injector.instanceOf[Metrics]
  private[connectors] val audit: Audit = new Audit(AuditHelper.appName, auditConnector)
  protected def mode: Mode = environment.mode

  lazy val postUrl = s"$serviceURL/government-gateway-admin/service/HMRC-MLR-ORG/known-facts"

  def addKnownFacts
  (knownFacts: KnownFactsForService)
  (implicit
   headerCarrier: HeaderCarrier,
   writes: Writes[KnownFactsForService]
  ): Future[HttpResponse] = {
    addKnownFactsFunction(knownFacts)
  }

  private def addKnownFactsFunction
  (knownFacts: KnownFactsForService)
  (implicit
   headerCarrier: HeaderCarrier,
   writes: Writes[KnownFactsForService]
  ): Future[HttpResponse] = {
    val prefix = "[GovernmentGatewayAdminConnector][addKnownFacts]"
    val timer = metrics.timer(GGAdmin)
    Logger.debug(s"$prefix - Request body: ${Json.toJson(knownFacts)}")
    httpClient.POST[KnownFactsForService, HttpResponse](postUrl, knownFacts) map {
      response =>
        timer.stop()
        Logger.debug(s"$prefix - Base Response: ${response.status}")
        Logger.debug(s"$prefix - Response body: ${response.body}")
        response
    } flatMap {
      case response @ status(OK) =>
        metrics.success(GGAdmin)
        audit.sendDataEvent(KnownFactsEvent(knownFacts))
        Logger.debug(s"$prefix - Success Response")
        Logger.debug(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.successful(response)
      case response @ status(s) =>
        metrics.failed(GGAdmin)
        Logger.warn(s"$prefix - Failure Response: $s")
        Logger.warn(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.failed(HttpStatusException(s, Option(response.body)))
    } recoverWith {
      case e: HttpStatusException =>
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(GGAdmin)
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }
  }
}