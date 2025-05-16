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
import metrics.API9
import models.des
import com.codahale.metrics.Timer
import models.des.ReadStatusResponse

import java.time.LocalDateTime
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterAll
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.RequestBuilder
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}
import utils.{AmlsBaseSpec, ApiRetryHelper}

import scala.concurrent.Future

class SubscriptionStatusDESConnectorSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator {
  val connector = new SubscriptionStatusDESConnector(mockAppConfig, mockAuditConnector, mockHttpClient, mockMetrics){
    override private[connectors] val baseUrl: String = "http://localhost:1234"
    override private[connectors] val token: String   = "token"
    override private[connectors] val env: String     = "ist0"
    override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl"
  }
  val mockApiRetryHelper = mock[ApiRetryHelper]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]
  val successModel = des.ReadStatusResponse(LocalDateTime.now(), "Approved", None, None, None, None, false)
  val mockTimer = mock[Timer.Context]

  val url = s"${connector.fullUrl}/${amlsRegistrationNumber}/status"
  when(connector.metrics.timer(ArgumentMatchers.eq(API9))).thenReturn(mockTimer)
  "DESConnector" must {
    "return a successful future" in {
      val response = HttpResponse(
        status = OK,
        json = Json.toJson(successModel),
        headers = Map.empty
      )

      when {
        connector.httpClientV2.get(url"$url")
      } thenReturn mockRequestBuilder

      when(mockRequestBuilder.setHeader(
        ("Authorization", "token"),
        ("Environment", "ist0"),
        ("Accept", "application/json"),
        ("Content-Type", "application/json;charset=utf-8")
      )).thenReturn(mockRequestBuilder)

      when(mockRequestBuilder.execute[HttpResponse](any(), any()))
        .thenReturn(Future.successful(response))

      whenReady(connector.status(amlsRegistrationNumber)) { result =>
        result mustEqual successModel
      }
    }

    "return a failed future" in {
      val response = HttpResponse(
        status = BAD_REQUEST,
        body = "",
        headers = Map.empty
      )

      when {
        connector.httpClientV2.get(url"$url")
      } thenReturn mockRequestBuilder

      when(mockRequestBuilder.setHeader(
        ("Authorization", "token"),
        ("Environment", "ist0"),
        ("Accept", "application/json"),
        ("Content-Type", "application/json;charset=utf-8")
      )).thenReturn(mockRequestBuilder)

      when(mockRequestBuilder.execute[HttpResponse](any(), any()))
        .thenReturn(Future.successful(response))

      whenReady(connector.status(amlsRegistrationNumber).failed) { case HttpStatusException(status, body) =>
        status mustEqual BAD_REQUEST
        body.getOrElse("").isEmpty mustEqual true
      }
    }

    "return a failed future (json validation)" in  {
      val response = HttpResponse(
        status = OK,
        json = Json.toJson("message"),
        headers = Map.empty
      )

      when {
        connector.httpClientV2.get(url"$url")
      } thenReturn mockRequestBuilder

      when(mockRequestBuilder.setHeader(
        ("Authorization", "token"),
        ("Environment", "ist0"),
        ("Accept", "application/json"),
        ("Content-Type", "application/json;charset=utf-8")
      )).thenReturn(mockRequestBuilder)

      when(mockRequestBuilder.execute[HttpResponse](any(), any()))
        .thenReturn(Future.successful(response))

      whenReady(connector.status(amlsRegistrationNumber).failed) { case HttpStatusException(status, body) =>
        status mustEqual OK
        body mustBe Some("\"message\"")
      }
    }

    "return a failed future (exception)" in  {
      when {
        connector.httpClientV2.get(url"$url")
      } thenReturn mockRequestBuilder

      when(mockRequestBuilder.setHeader(
        ("Authorization", "token"),
        ("Environment", "ist0"),
        ("Accept", "application/json"),
        ("Content-Type", "application/json;charset=utf-8")
      )).thenReturn(mockRequestBuilder)

      when(mockRequestBuilder.execute[HttpResponse](any(), any()))
        .thenReturn(Future.failed(new Exception("message")))

      whenReady(connector.status(amlsRegistrationNumber).failed) { case HttpStatusException(status, body) =>
        status mustEqual INTERNAL_SERVER_ERROR
        body mustBe Some("message")
      }
    }
  }
}