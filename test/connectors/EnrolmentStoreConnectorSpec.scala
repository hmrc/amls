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

import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import generators.{AmlsReferenceNumberGenerator, BaseGenerator}
import metrics.EnrolmentStoreKnownFacts
import models.enrolment.{AmlsEnrolmentKey, KnownFact, KnownFacts}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, verify, when}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import utils.AmlsBaseSpec

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentStoreConnectorSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator with BaseGenerator {

  trait Fixture {

    val mockTimer = mock[Timer.Context]

    val connector = new EnrolmentStoreConnector(mockHttpClient, mockMetrics, mockAuditConnector, mockAppConfig)

    val baseUrl = "http://localhost:7775"
    val enrolKey = AmlsEnrolmentKey(amlsRegistrationNumber)
    val url = s"$baseUrl/tax-enrolments/enrolments/${enrolKey.key}"

    val knownFacts = KnownFacts(Set(
      KnownFact("Postcode", postcodeGen.sample.get),
      KnownFact("SafeId", "safeId"),
      KnownFact("MLRRefNumber", amlsRegistrationNumber)
    ))

    when {
      connector.metrics.timer(ArgumentMatchers.eq(EnrolmentStoreKnownFacts))
    } thenReturn mockTimer

    when {
      connector.config.enrolmentStoreUrl
    } thenReturn baseUrl

    def mockResponse(response: Future[HttpResponse]) =
      when {
        connector.httpClient.PUT[KnownFacts, HttpResponse](any(), any(), any())(any(), any(), any(), any())
      } thenReturn response

  }

  "enrol" when {
    "called" must {
      "call the ES6 enrolment store endpoint for known facts" in new Fixture {

        val response = HttpResponse(status = NO_CONTENT, body = "message")

        mockResponse(Future.successful(response))

        whenReady(connector.addKnownFacts(enrolKey, knownFacts)) { result =>
          result mustEqual response
          verify(connector.httpClient).PUT[KnownFacts, HttpResponse](ArgumentMatchers.eq(url), ArgumentMatchers.eq(knownFacts), any())(any(), any(), any(), any())
        }
      }

      "return an unsuccessful response when a non-200 response is returned" in new Fixture {

        mockResponse(Future.successful(HttpResponse(status = BAD_REQUEST, body = "")))

        whenReady(connector.addKnownFacts(enrolKey, knownFacts).failed) {
          case HttpStatusException(status, body) =>
            status mustEqual BAD_REQUEST
            body.getOrElse("").isEmpty mustEqual true
        }
      }

      "return an unsuccessful response when an exception is thrown" in new Fixture {

        mockResponse(Future.failed(new Exception("message")))

        whenReady(connector.addKnownFacts(enrolKey, knownFacts).failed) {
          case HttpStatusException(status, body) =>
            status mustEqual INTERNAL_SERVER_ERROR
            body mustBe Some("message")
        }
      }
    }
  }

}