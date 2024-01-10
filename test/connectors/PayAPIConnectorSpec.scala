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
import generators.PayApiGenerator
import metrics.PayAPI
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import utils.AmlsBaseSpec

import scala.concurrent.Future

class PayAPIConnectorSpec extends AmlsBaseSpec with PayApiGenerator {

  trait Fixture {

    val testPayment = payApiPaymentGen.sample.get
    val paymentUrl = s"url/pay-api/payment/summary/${testPayment.id}"

    val testConnector = new PayAPIConnector(mockAppConfig, mockHttpClient, mockMetrics) {
      override private[connectors] val paymentUrl = "url"
    }

    val testPaymentId = testPayment.id

    val mockTimer = mock[Timer.Context]

    when {
      testConnector.metrics.timer(ArgumentMatchers.eq(PayAPI))
    } thenReturn mockTimer
  }

  "PayAPIConnector" must {

    "return a successful response" in new Fixture {

      val response = HttpResponse(
        status = OK,
        json = Json.toJson(testPayment),
        headers = Map.empty
      )

      when {
        testConnector.httpClient.GET[HttpResponse](ArgumentMatchers.eq(paymentUrl), any(), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testConnector.getPayment(testPaymentId)) {
        _ mustEqual testPayment
      }
    }

    "return an unsuccessful response when a non-200 response is returned" in new Fixture {

      val response = HttpResponse(status = BAD_REQUEST, body = "")

      when {
        testConnector.httpClient.GET[HttpResponse](ArgumentMatchers.eq(paymentUrl), any(), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testConnector.getPayment(testPaymentId).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body.getOrElse("").isEmpty mustEqual true
      }
    }

    "return an unsuccessful response when an exception is thrown" in new Fixture {

      when {
        testConnector.httpClient.GET[HttpResponse](ArgumentMatchers.eq(paymentUrl), any(), any())(any(), any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady(testConnector.getPayment(testPaymentId).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }
  }
}
