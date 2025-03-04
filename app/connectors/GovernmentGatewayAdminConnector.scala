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

import audit.KnownFactsEvent
import config.ApplicationConfig
import exceptions.HttpStatusException
import metrics.{GGAdmin, Metrics}
import models.KnownFactsForService
import play.api.Logging
import play.api.http.Status._
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import utils._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GovernmentGatewayAdminConnector @Inject() (
  private[connectors] val applicationConfig: ApplicationConfig,
  private[connectors] val auditConnector: AuditConnector,
  private[connectors] val httpClient: HttpClient,
  private[connectors] val metrics: Metrics
)(implicit executionContext: ExecutionContext)
    extends HttpResponseHelper
    with Logging {

  private[connectors] val serviceURL   = applicationConfig.ggUrl
  private[connectors] val audit: Audit = new Audit(AuditHelper.appName, auditConnector)

  lazy val postUrl = s"$serviceURL/government-gateway-admin/service/HMRC-MLR-ORG/known-facts"

  def addKnownFacts(
    knownFacts: KnownFactsForService
  )(implicit headerCarrier: HeaderCarrier, writes: Writes[KnownFactsForService]): Future[HttpResponse] =
    addKnownFactsFunction(knownFacts)

  private def addKnownFactsFunction(
    knownFacts: KnownFactsForService
  )(implicit headerCarrier: HeaderCarrier, writes: Writes[KnownFactsForService]): Future[HttpResponse] = {
    val prefix = "[GovernmentGatewayAdminConnector][addKnownFacts]"
    val timer  = metrics.timer(GGAdmin)
    logger.debug(s"$prefix - Request body: ${Json.toJson(knownFacts)}")
    httpClient.POST[KnownFactsForService, HttpResponse](postUrl, knownFacts) map { response =>
      timer.stop()
      logger.debug(s"$prefix - Base Response: ${response.status}")
      logger.debug(s"$prefix - Response body: ${response.body}")
      response
    } flatMap {
      case response @ status(OK) =>
        metrics.success(GGAdmin)
        audit.sendDataEvent(KnownFactsEvent(knownFacts))
        logger.debug(s"$prefix - Success Response")
        logger.debug(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.successful(response)
      case response @ status(s)  =>
        metrics.failed(GGAdmin)
        logger.warn(s"$prefix - Failure Response: $s")
        logger.warn(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.failed(HttpStatusException(s, Option(response.body)))
    } recoverWith {
      case e: HttpStatusException =>
        logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e                      =>
        timer.stop()
        metrics.failed(GGAdmin)
        logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }
  }
}
