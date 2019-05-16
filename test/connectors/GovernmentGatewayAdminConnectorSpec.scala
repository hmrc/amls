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

import audit.MockAudit
import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import metrics.{GGAdmin, Metrics}
import models.{KnownFact, KnownFactsForService}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.Helpers._

import scala.concurrent.Future
import uk.gov.hmrc.http._

class GovernmentGatewayAdminConnectorSpec extends PlaySpec
  with OneServerPerSuite
  with MockitoSugar
  with ScalaFutures
  with AmlsReferenceNumberGenerator{

  trait Fixture {

    object GGAdminConnector extends GovernmentGatewayAdminConnector(app) {
      override private[connectors] val serviceURL = "url"
      override private[connectors] val metrics = mock[Metrics]
      override private[connectors] val audit = MockAudit

      override private[connectors] val http = mock[CorePost with CoreGet with CorePut]
    }

    val knownFacts = KnownFactsForService(Seq(
      KnownFact("SafeId", "safeId"),
      KnownFact("MLRRefNumber", amlsRegistrationNumber)
    ))

    val url = "url/government-gateway-admin/service/HMRC-MLR-ORG/known-facts"

    implicit val hc = HeaderCarrier()

    val mockTimer = mock[Timer.Context]

    when {
      GGAdminConnector.metrics.timer(eqTo(GGAdmin))
    } thenReturn mockTimer
  }

  "GovernmentGatewayAdminConnector" must {

    "have correct serviceUrl" in new Fixture {
      GGAdminConnector.postUrl mustEqual url
    }

    "return a successful response" in new Fixture {

      val response = HttpResponse(OK, responseString = Some("message"))
      when {
        GGAdminConnector.http.POST[KnownFactsForService, HttpResponse](eqTo(url), eqTo(knownFacts), any())(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady (GGAdminConnector.addKnownFacts(knownFacts)) {
        _ mustEqual response
      }
    }

    "return an unsuccessful response when a non-200 response is returned" in new Fixture {

      val response = HttpResponse(BAD_REQUEST, responseString = Some("message"))

      when {
        GGAdminConnector.http.POST[KnownFactsForService, HttpResponse](eqTo(url), eqTo(knownFacts), any())(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady (GGAdminConnector.addKnownFacts(knownFacts).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body mustEqual Some("message")
      }
    }

    "return an unsuccessful response when an exception is thrown" in new Fixture {

      when {
        GGAdminConnector.http.POST[KnownFactsForService, HttpResponse](eqTo(url), eqTo(knownFacts), any())(any(), any(), any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady (GGAdminConnector.addKnownFacts(knownFacts).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }
  }
}
