/*
 * Copyright 2018 HM Revenue & Customs
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
import config.AppConfig
import exceptions.HttpStatusException
import generators.{AmlsReferenceNumberGenerator, BaseGenerator}
import metrics.{EnrolmentStoreKnownFacts, Metrics}
import models.enrolment.{AmlsEnrolmentKey, KnownFact, KnownFacts}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito.{verify, when}
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.Helpers._
import uk.gov.hmrc.http.{CorePost, HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.auth.microservice.connectors.AuthConnector

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentStoreConnectorSpec extends PlaySpec
  with MustMatchers
  with ScalaFutures
  with MockitoSugar
  with AmlsReferenceNumberGenerator
  with BaseGenerator
  with OneAppPerSuite {

  trait Fixture {

    implicit val hc = HeaderCarrier()
    implicit val ec = mock[ExecutionContext]

    val metrics = mock[Metrics]
    val http = mock[CorePost]
    val authConnector = mock[AuthConnector]
    val config = mock[AppConfig]

    val mockTimer = mock[Timer.Context]

    val connector = new EnrolmentStoreConnector(http, metrics, MockAudit, config)
    
    val baseUrl = "http://localhost:7775/enrolment-store-proxy"
    val enrolKey = AmlsEnrolmentKey(amlsRegistrationNumber)
    val url = s"$baseUrl/enrolment-store/enrolments/${enrolKey.key}"

    val knownFacts = KnownFacts(Set(
      KnownFact("Postcode", postcodeGen.sample.get),
      KnownFact("SafeId", "safeId"),
      KnownFact("MLRRefNumber", amlsRegistrationNumber)
    ))

    when {
      connector.metrics.timer(eqTo(EnrolmentStoreKnownFacts))
    } thenReturn mockTimer

    when {
      config.enrolmentStoreUrl
    } thenReturn baseUrl

    def mockResponse(response: Future[HttpResponse]) =
      when {
        connector.http.POST[KnownFacts, HttpResponse](any(), any(), any())(any(), any(), any(), any())
      } thenReturn response

  }

  "enrol" when {
    "called" must {
      "call the ES6 enrolment store endpoint for known facts" in new Fixture {

        val response = HttpResponse(OK, responseString = Some("message"))

        mockResponse(Future.successful(response))

        whenReady(connector.enrol(enrolKey, knownFacts)) { result =>
          result mustEqual response
          verify(connector.http).POST[KnownFacts, HttpResponse](eqTo(url), eqTo(knownFacts), any())(any(), any(), any(), any())
        }
      }

      "return an unsuccessful response when a non-200 response is returned" in new Fixture {

        mockResponse(Future.successful(HttpResponse(BAD_REQUEST, responseString = Some("message"))))

        whenReady (connector.enrol(enrolKey, knownFacts).failed) {
          case HttpStatusException(status, body) =>
            status mustEqual BAD_REQUEST
            body mustEqual Some("message")
        }
      }

      "return an unsuccessful response when an exception is thrown" in new Fixture {

        mockResponse(Future.failed(new Exception("message")))

        whenReady (connector.enrol(enrolKey, knownFacts).failed) {
          case HttpStatusException(status, body) =>
            status mustEqual INTERNAL_SERVER_ERROR
            body mustEqual Some("message")
        }
      }
    }
  }

}