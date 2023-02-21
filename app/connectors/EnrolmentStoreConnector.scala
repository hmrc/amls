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

import audit.KnownFactsEvent
import config.ApplicationConfig
import exceptions.HttpStatusException

import javax.inject.Inject
import metrics.{EnrolmentStoreKnownFacts, Metrics}
import models.enrolment.{AmlsEnrolmentKey, KnownFacts}
import play.api.{Logger, Logging}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT}
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnrolmentStoreConnector @Inject()(private[connectors] val httpClient: HttpClient,
                                        private[connectors] val metrics: Metrics,
                                        private[connectors] val mac: AuditConnector,
                                        private[connectors] val config: ApplicationConfig) extends HttpResponseHelper with Logging {

  def addKnownFacts(enrolmentKey: AmlsEnrolmentKey, knownFacts: KnownFacts)(implicit headerCarrier: HeaderCarrier,
                                                                            writes: Writes[KnownFacts]): Future[HttpResponse] = {
    addKnownFactsFunction(enrolmentKey, knownFacts)
  }

  private def addKnownFactsFunction(enrolmentKey: AmlsEnrolmentKey, knownFacts: KnownFacts)(implicit headerCarrier: HeaderCarrier,
                                                                                            writes: Writes[KnownFacts]): Future[HttpResponse] = {

    val url = s"${config.enrolmentStoreUrl}/tax-enrolments/enrolments/${enrolmentKey.key}"

    val prefix = "[EnrolmentStore][Enrolments]"
    val timer = metrics.timer(EnrolmentStoreKnownFacts)

    val audit: Audit = new Audit(AuditHelper.appName, mac)

    logger.debug(s"$prefix - Request body: ${Json.toJson(knownFacts)}")

    httpClient.PUT(url, knownFacts) map { response =>
      timer.stop()
      logger.debug(s"$prefix - Base Response: ${response.status}")
      logger.debug(s"$prefix - Response body: ${response.body}")
      response
    } flatMap {
      case response @ status(NO_CONTENT) =>
        metrics.success(EnrolmentStoreKnownFacts)
        audit.sendDataEvent(KnownFactsEvent(knownFacts))
        logger.debug(s"$prefix - Success Response")
        logger.debug(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.successful(response)
      case response @ status(s) =>
        metrics.failed(EnrolmentStoreKnownFacts)
        logger.warn(s"$prefix - Failure Response: $s")
        logger.warn(s"$prefix - Response body: ${Option(response.body) getOrElse ""}")
        Future.failed(HttpStatusException(s, Option(response.body)))
    } recoverWith {
      case e: HttpStatusException =>
        logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(EnrolmentStoreKnownFacts)
        logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }
  }
}
