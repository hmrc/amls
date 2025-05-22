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
import metrics.API8
import models.des.{WithdrawSubscriptionRequest, WithdrawSubscriptionResponse, WithdrawalReason}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.scalatest.time.{Millis, Seconds, Span}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.RequestBuilder
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}
import utils.AmlsBaseSpec

import scala.concurrent.Future

class WithdrawSubscriptionConnectorSpec extends AmlsBaseSpec {

  implicit val defaultPatience: PatienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  val withdrawSubscriptionConnector      =
    new WithdrawSubscriptionConnector(mockAppConfig, mockAuditConnector, mockHttpClient, mockMetrics) {
      override private[connectors] val baseUrl: String = "http://localhost:1234"
      override private[connectors] val token: String   = "token"
      override private[connectors] val env: String     = "ist0"
      override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl"
    }
  val amlsRegistrationNumber             = "1121212UUUI"
  val mockTimer                          = mock[Timer.Context]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]
  when {
    withdrawSubscriptionConnector.metrics.timer(ArgumentMatchers.eq(API8))
  } thenReturn mockTimer
  val url                                = s"${withdrawSubscriptionConnector.fullUrl}/$amlsRegistrationNumber/withdrawal"
  val successModel                       = WithdrawSubscriptionResponse("2016-09-17T09:30:47Z")
  val testRequest                        = WithdrawSubscriptionRequest(
    "AEF7234BGG12539GH143856HEA123412",
    "2015-08-23",
    WithdrawalReason.Other,
    Some("Other Reason")
  )

  "WithdrawSubscriptionConnector" must {

    "return successful response for valid request" in {

      val response = HttpResponse(
        status = OK,
        json = Json.toJson(successModel),
        headers = Map(
          "CorrelationId" -> Seq("my-correlation-id")
        )
      )
      when(withdrawSubscriptionConnector.httpClientV2.post(url"$url")) thenReturn mockRequestBuilder
      when(
        mockRequestBuilder.setHeader(
          ("Authorization", "token"),
          ("Environment", "ist0"),
          ("Accept", "application/json"),
          ("Content-Type", "application/json;charset=utf-8")
        )
      ).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(testRequest)))(any(), any(), any()))
        .thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(response))
      whenReady(withdrawSubscriptionConnector.withdrawal(amlsRegistrationNumber, testRequest)) { result =>
        result mustEqual successModel
      }

    }

    "return failed response on invalid request" in {
      val response = HttpResponse(
        status = BAD_REQUEST,
        body = "",
        headers = Map("CorrelationId" -> Seq("my-correlation-id"))
      )

      when(withdrawSubscriptionConnector.httpClientV2.post(url"$url")) thenReturn mockRequestBuilder
      when(
        mockRequestBuilder.setHeader(
          ("Authorization", "token"),
          ("Environment", "ist0"),
          ("Accept", "application/json"),
          ("Content-Type", "application/json;charset=utf-8")
        )
      ).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(testRequest)))(any(), any(), any()))
        .thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(response))
      whenReady(withdrawSubscriptionConnector.withdrawal(amlsRegistrationNumber, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body.getOrElse("").isEmpty mustEqual true
      }
    }

    "return failed response on exception" in {

      when(withdrawSubscriptionConnector.httpClientV2.post(url"$url")) thenReturn mockRequestBuilder
      when(
        mockRequestBuilder.setHeader(
          ("Authorization", "token"),
          ("Environment", "ist0"),
          ("Accept", "application/json"),
          ("Content-Type", "application/json;charset=utf-8")
        )
      ).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(testRequest)))(any(), any(), any()))
        .thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.failed(new Exception("message")))
      whenReady(withdrawSubscriptionConnector.withdrawal(amlsRegistrationNumber, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status must be(INTERNAL_SERVER_ERROR)
          body   must be(Some("message"))
      }
    }
  }

}
