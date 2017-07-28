/*
 * Copyright 2017 HM Revenue & Customs
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
import generators.PaymentGenerator
import metrics.{Metrics, PayAPI}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

class PayAPIConnectorSpec extends PlaySpec with OneServerPerSuite with MockitoSugar with ScalaFutures with PaymentGenerator {

  trait Fixture {

    val http = mock[WSHttp]

    val testConnector = new PayAPIConnector(
      http,
      "url",
      mock[Metrics]
    )

    val testPayment = paymentGen.sample.get

    val testPaymentId = testPayment._id

    val url = s"url/payment/${testPayment._id}"

    implicit val hc = HeaderCarrier()

    val mockTimer = mock[Timer.Context]

    when {
      testConnector.metrics.timer(eqTo(PayAPI))
    } thenReturn mockTimer
  }

  "PayAPIConnector" must {

    "return a successful response" in new Fixture {

      val response = HttpResponse(OK, responseString = Some("message"))

      when {
        testConnector.http.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.successful(response)

      whenReady (testConnector.getPayment(testPaymentId)) {
        _ mustEqual response
      }
    }

    "return an unsuccessful response when a non-200 response is returned" in new Fixture {

      val response = HttpResponse(BAD_REQUEST, responseString = Some("message"))

      when {
        testConnector.http.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.successful(response)

      whenReady (testConnector.getPayment(testPaymentId).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body mustEqual Some("message")
      }
    }

    "return an unsuccessful response when an exception is thrown" in new Fixture {

      when {
        testConnector.http.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady (testConnector.getPayment(testPaymentId).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }
  }
}
