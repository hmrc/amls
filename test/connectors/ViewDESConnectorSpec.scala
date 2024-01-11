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

import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import metrics.API5
import models.des._
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.AmlsBaseSpec

import scala.concurrent.Future

class ViewDESConnectorSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator {

  trait Fixture {
    val testDESConnector = new ViewDESConnector(mockAppConfig, mockAuditConnector, mockHttpClient, mockMetrics) {
      override private[connectors] val baseUrl: String = "baseUrl"
      override private[connectors] val token: String = "token"
      override private[connectors] val env: String = "ist0"
      override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl/"
    }

    val mockTimer = mock[Timer.Context]

    val url = s"${testDESConnector.fullUrl}/$amlsRegistrationNumber"

    when {
      testDESConnector.metrics.timer(eqTo(API5))
    } thenReturn mockTimer
  }


  "DESConnector" must {

    "return a successful future" in new Fixture {

      val response = HttpResponse(
        status = OK,
        json = Json.toJson(ViewSuccessModel),
        headers = Map.empty
      )

      when(testDESConnector.httpClient.GET[HttpResponse](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(response))

      whenReady(testDESConnector.view(amlsRegistrationNumber)) {
        _ mustEqual ViewSuccessModel
      }
    }

    "return a failed future" in new Fixture {

      val response = HttpResponse(
        status = BAD_REQUEST,
        body = "",
        headers = Map.empty
      )
      when {
        testDESConnector.httpClient.GET[HttpResponse](eqTo(url), any(), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.view(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body.getOrElse("").isEmpty mustEqual true
      }
    }

    "return a failed future (json validation)" in new Fixture {

      val response = HttpResponse(
        status = OK,
        body = "message",
        headers = Map.empty
      )

      when {
        testDESConnector.httpClient.GET[HttpResponse](eqTo(url), any(), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.view(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, _) =>
          status mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "return a failed future (exception)" in new Fixture {

      when {
        testDESConnector.httpClient.GET[HttpResponse](eqTo(url), any(), any())(any(), any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady(testDESConnector.view(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustBe Some("message")
      }
    }
  }

  val ViewSuccessModel = SubscriptionView(
    etmpFormBundleNumber = "111111",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI5,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersons),
    Some(DesConstants.testAmp),
    None,
    DesConstants.extraFields
  )

}
