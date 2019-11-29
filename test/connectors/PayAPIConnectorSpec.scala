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

import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import generators.PayApiGenerator
import metrics.{Metrics, PayAPI}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpResponse}

import scala.concurrent.Future

class PayAPIConnectorSpec extends PlaySpec
  with OneServerPerSuite
  with MockitoSugar
  with ScalaFutures
  with PayApiGenerator
  with IntegrationPatience {

  trait Fixture {

    val mockHttp = mock[HttpGet]
    val testPayment = payApiPaymentGen.sample.get
    val paymentUrl = s"url/pay-api/payment/${testPayment.id}"

    object testConnector extends PayAPIConnector(app) {
      override private[connectors] val httpGet: HttpGet = mockHttp
      override private[connectors] val paymentUrl = "url"
      override private[connectors] val metrics = mock[Metrics]
    }

    val testPaymentId = testPayment.id

    implicit val hc = HeaderCarrier()

    val mockTimer = mock[Timer.Context]

    when {
      testConnector.metrics.timer(eqTo(PayAPI))
    } thenReturn mockTimer
  }

  "PayAPIConnector" must {

    "return a successful response" in new Fixture {

      val response = HttpResponse(
        responseStatus = OK,
        responseHeaders = Map.empty,
        responseJson = Some(Json.toJson(testPayment))
      )

      when {
        mockHttp.GET[HttpResponse](eqTo(paymentUrl))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady (testConnector.getPayment(testPaymentId)) {
        _ mustEqual testPayment
      }
    }

    "return an unsuccessful response when a non-200 response is returned" in new Fixture {

      val response = HttpResponse(BAD_REQUEST, responseString = Some("message"))

      when {
        mockHttp.GET[HttpResponse](eqTo(paymentUrl))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady (testConnector.getPayment(testPaymentId).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body mustEqual Some("message")
      }
    }

    "return an unsuccessful response when an exception is thrown" in new Fixture {

      when {
        mockHttp.GET[HttpResponse](eqTo(paymentUrl))(any(), any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady (testConnector.getPayment(testPaymentId).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }
  }
}
