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
import generators.PayApiGenerator
import metrics.PayAPI
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.client.RequestBuilder
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}
import utils.AmlsBaseSpec

import scala.concurrent.Future

class PayAPIConnectorSpec extends AmlsBaseSpec with PayApiGenerator {

  val testPayment = payApiPaymentGen.sample.get
  val paymentUrl  = s"http://localhost:1234/pay-api/payment/summary/${testPayment.id}"

  val testConnector                      = new PayAPIConnector(mockAppConfig, mockHttpClient, mockMetrics) {
    override private[connectors] val paymentUrl = "http://localhost:1234"
  }
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]

  val testPaymentId = testPayment.id

  val mockTimer = mock[Timer.Context]

  when {
    testConnector.metrics.timer(ArgumentMatchers.eq(PayAPI))
  } thenReturn mockTimer

  "PayAPIConnector" must {

    "return a successful response" in {

      val response = HttpResponse(
        status = OK,
        json = Json.toJson(testPayment),
        headers = Map.empty
      )

      when {
        testConnector.httpClientV2.get(url"$paymentUrl")
      } thenReturn mockRequestBuilder
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(response))

      whenReady(testConnector.getPayment(testPaymentId)) {
        _ mustEqual testPayment
      }
    }

    "return an unsuccessful response when a non-200 response is returned" in {

      val response = HttpResponse(status = BAD_REQUEST, body = "")

      when {
        testConnector.httpClientV2.get(url"$paymentUrl")
      } thenReturn mockRequestBuilder
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(response))

      whenReady(testConnector.getPayment(testPaymentId).failed) { case HttpStatusException(status, body) =>
        status mustEqual BAD_REQUEST
        body.getOrElse("").isEmpty mustEqual true
      }
    }

    "return an unsuccessful response when an exception is thrown" in {

      when {
        testConnector.httpClientV2.get(url"$paymentUrl")
      } thenReturn mockRequestBuilder
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.failed(new Exception("message")))

      whenReady(testConnector.getPayment(testPaymentId).failed) { case HttpStatusException(status, body) =>
        status mustEqual INTERNAL_SERVER_ERROR
        body mustEqual Some("message")
      }
    }
  }
}
