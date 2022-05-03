/*
 * Copyright 2022 HM Revenue & Customs
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

import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import metrics.GGAdmin
import models.{KnownFact, KnownFactsForService}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import play.api.test.Helpers._
import uk.gov.hmrc.http._
import utils.AmlsBaseSpec

import scala.concurrent.Future

class GovernmentGatewayAdminConnectorSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator{

  trait Fixture {

    val testConnector = new GovernmentGatewayAdminConnector(mockAppConfig, mockAuditConnector, mockHttpClient, mockMetrics) {
      override private[connectors] val serviceURL = "url"
    }

    val knownFacts = KnownFactsForService(Seq(
      KnownFact("SafeId", "safeId"),
      KnownFact("MLRRefNumber", amlsRegistrationNumber)
    ))

    val url = "url/government-gateway-admin/service/HMRC-MLR-ORG/known-facts"

    val mockTimer = mock[Timer.Context]

    when {
      testConnector.metrics.timer(eqTo(GGAdmin))
    } thenReturn mockTimer
  }

  "GovernmentGatewayAdminConnector" must {

    "have correct serviceUrl" in new Fixture {
      testConnector.postUrl mustEqual url
    }

    "return a successful response" in new Fixture {

      val response = HttpResponse(status = OK, body = "message")
      when {
        testConnector.httpClient.POST[KnownFactsForService, HttpResponse](eqTo(url), eqTo(knownFacts), any())(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady (testConnector.addKnownFacts(knownFacts)) {
        _ mustEqual response
      }
    }

    "return an unsuccessful response when a non-200 response is returned" in new Fixture {

      val response = HttpResponse(status = BAD_REQUEST, body = "")

      when {
        testConnector.httpClient.POST[KnownFactsForService, HttpResponse](eqTo(url), eqTo(knownFacts), any())(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady (testConnector.addKnownFacts(knownFacts).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body.getOrElse("").isEmpty mustEqual true
      }
    }

    "return an unsuccessful response when an exception is thrown" in new Fixture {

      when {
        testConnector.httpClient.POST[KnownFactsForService, HttpResponse](eqTo(url), eqTo(knownFacts), any())(any(), any(), any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady (testConnector.addKnownFacts(knownFacts).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }
  }
}
