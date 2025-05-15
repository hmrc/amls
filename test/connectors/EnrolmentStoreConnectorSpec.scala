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

import models.enrolment.{AmlsEnrolmentKey, KnownFact, KnownFacts}
import org.mockito.ArgumentMatchers.any
import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.RequestBuilder
import uk.gov.hmrc.http.HttpResponse
import metrics.EnrolmentStoreKnownFacts
import generators.{AmlsReferenceNumberGenerator, BaseGenerator}
import org.mockito.ArgumentMatchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NO_CONTENT}
import utils.AmlsBaseSpec
import scala.concurrent.Future
import java.net.URL

class EnrolmentStoreConnectorSpec extends AmlsBaseSpec with BaseGenerator with AmlsReferenceNumberGenerator {

  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]
  val mockTimer = mock[Timer.Context]
  val connector = new EnrolmentStoreConnector(mockHttpClient, mockMetrics, mockAuditConnector, mockAppConfig)
  val enrolmentKey = AmlsEnrolmentKey(amlsRegistrationNumber)
  val knownFacts = KnownFacts(
    Set(
      KnownFact("Postcode", postcodeGen.sample.get),
      KnownFact("SafeId", "safeId"),
      KnownFact("MLRRefNumber", amlsRegistrationNumber)
    )
  )
  val url = new URL(s"http://localhost:1234/tax-enrolments/enrolments/${enrolmentKey.key}")

  "addKnownFacts" must {
    "return successful response when DES returns 204" in {
      when(mockAppConfig.enrolmentStoreUrl).thenReturn("http://localhost:1234")
      when(mockHttpClient.put(ArgumentMatchers.eq(url))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(knownFacts)))(any(), any(), any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(HttpResponse(NO_CONTENT, "")))
      when {
        connector.metrics.timer(ArgumentMatchers.eq(EnrolmentStoreKnownFacts))
      } thenReturn (mockTimer)
      val response = HttpResponse(status = NO_CONTENT, body = "message")
      when {
        connector.addKnownFacts(enrolmentKey, knownFacts)
      } thenReturn (Future.successful(response))
      whenReady(connector.addKnownFacts(enrolmentKey, knownFacts)) { result => result mustEqual response }
      response.status shouldBe NO_CONTENT
    }

    "return an unsuccessful response when a non-200 response is returned" in {
      when(mockAppConfig.enrolmentStoreUrl).thenReturn("http://localhost:1234")
      when(mockHttpClient.put(ArgumentMatchers.eq(url))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(knownFacts)))(any(), any(), any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, "")))
      when {
        connector.metrics.timer(ArgumentMatchers.eq(EnrolmentStoreKnownFacts))
      } thenReturn (mockTimer)
      val response = HttpResponse(status = BAD_REQUEST, body = "")
      when {
        connector.addKnownFacts(enrolmentKey, knownFacts)
      } thenReturn (Future.successful(response))
      whenReady(connector.addKnownFacts(enrolmentKey, knownFacts).failed) { case HttpStatusException(status, body) =>
        status mustEqual BAD_REQUEST
        body.getOrElse("").isEmpty mustEqual true
      }
      response.status shouldBe BAD_REQUEST
    }

    "return an unsuccessful response when an exception is thrown" in {
      when(mockAppConfig.enrolmentStoreUrl).thenReturn("http://localhost:1234")
      when(mockHttpClient.put(ArgumentMatchers.eq(url))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(knownFacts)))(any(), any(), any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn((Future.failed(new Exception("message"))))
      when {
        connector.metrics.timer(ArgumentMatchers.eq(EnrolmentStoreKnownFacts))
      } thenReturn (mockTimer)
      when {
        connector.addKnownFacts(enrolmentKey, knownFacts)
      } thenReturn (Future.failed(new Exception("message")))
      whenReady(connector.addKnownFacts(enrolmentKey, knownFacts).failed) { case HttpStatusException(status, body) =>
        status mustEqual INTERNAL_SERVER_ERROR
        body mustBe Some("message")
      }
    }
  }
}
