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
import config.ApplicationConfig
import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import metrics.{API5, Metrics}
import models.des._
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.{Configuration, Environment}
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.{AmlsBaseSpec, ApiRetryHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ViewDESConnectorSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator {

  trait Fixture {
    val testDESConnector = new ViewDESConnector(app, mockRunModeConf, mockEnvironment, mockAppConfig, mockAuditConnector, mockHttpClient) {
      override private[connectors] val baseUrl: String = "baseUrl"
      override private[connectors] val token: String = "token"
      override private[connectors] val env: String = "ist0"
      override private[connectors] val metrics: Metrics = mock[Metrics]
      override private[connectors] val audit = MockAudit
      override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl/"
    }

    val mockTimer = mock[Timer.Context]

    val url = s"${testDESConnector.fullUrl}/$amlsRegistrationNumber"

    when {
      testDESConnector.metrics.timer(eqTo(API5))
    } thenReturn mockTimer
  }


  "DESConnector" must {

    "return a succesful future" in new Fixture {

      val response = HttpResponse(
        responseStatus = OK,
        responseHeaders = Map.empty,
        responseJson = Some(Json.toJson(ViewSuccessModel))
      )

      when(testDESConnector.httpClient.GET[HttpResponse](eqTo(url))(any(), any(), any()))
        .thenReturn(Future.successful(response))

      whenReady(testDESConnector.view(amlsRegistrationNumber)) {
        _ mustEqual ViewSuccessModel
      }
    }

    "return a failed future" in new Fixture {

      val response = HttpResponse(
        responseStatus = BAD_REQUEST,
        responseHeaders = Map.empty
      )
      when {
        testDESConnector.httpClient.GET[HttpResponse](eqTo(url))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.view(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body mustEqual None
      }
    }

    "return a failed future (json validation)" in new Fixture {

      val response = HttpResponse(
        responseStatus = OK,
        responseHeaders = Map.empty,
        responseString = Some("message")
      )

      when {
        testDESConnector.httpClient.GET[HttpResponse](eqTo(url))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.view(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "return a failed future (exception)" in new Fixture {

      when {
        testDESConnector.httpClient.GET[HttpResponse](eqTo(url))(any(), any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady(testDESConnector.view(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
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
    DesConstants.extraFields
  )
}
