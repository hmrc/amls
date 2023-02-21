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

import audit.MockAudit
import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import metrics.API8
import models.des
import models.des.{WithdrawSubscriptionRequest, WithdrawSubscriptionResponse, WithdrawalReason}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.time.{Millis, Seconds, Span}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import utils.AmlsBaseSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WithdrawSubscriptionConnectorSpec extends AmlsBaseSpec {

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  trait Fixture {
    val withdrawSubscriptionConnector = new WithdrawSubscriptionConnector(mockAppConfig, mockAuditConnector, mockHttpClient, mockMetrics) {
      override private[connectors] val baseUrl: String = "baseUrl"
      override private[connectors] val token: String = "token"
      override private[connectors] val env: String = "ist0"
      override private[connectors] val audit = MockAudit
      override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl"
    }

    val mockTimer = mock[Timer.Context]

    when {
      withdrawSubscriptionConnector.metrics.timer(eqTo(API8))
    } thenReturn mockTimer

    val amlsRegistrationNumber = "1121212UUUI"
    val url = s"${withdrawSubscriptionConnector.fullUrl}/$amlsRegistrationNumber/withdrawal"
  }

  val successModel = WithdrawSubscriptionResponse("2016-09-17T09:30:47Z")
  val testRequest = WithdrawSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", WithdrawalReason.Other, Some("Other Reason"))

  "WithdrawSubscriptionConnector" must {

    "return successful response for valid request" in new Fixture {

      val response = HttpResponse(
        status = OK,
        json = Json.toJson(successModel),
        headers = Map(
          "CorrelationId" -> Seq("my-correlation-id")
        )
      )

      when {
        withdrawSubscriptionConnector.httpClient.POST[des.WithdrawSubscriptionRequest,
          HttpResponse](eqTo(url), any(), any())(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(withdrawSubscriptionConnector.withdrawal(amlsRegistrationNumber, testRequest)) {
        _ mustEqual successModel
      }
    }

    "return failed response on invalid request" in new Fixture {
      val response = HttpResponse(
        status = BAD_REQUEST,
        body = "",
        headers = Map("CorrelationId" -> Seq("my-correlation-id"))
      )

      when {
        withdrawSubscriptionConnector.httpClient.POST[des.WithdrawSubscriptionRequest,
          HttpResponse](eqTo(url), any(), any())(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(withdrawSubscriptionConnector.withdrawal(amlsRegistrationNumber, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status must be(BAD_REQUEST)
          body.getOrElse("").isEmpty must be(true)
      }
    }

    "return failed response on exception" in new Fixture {
      when {
        withdrawSubscriptionConnector.httpClient.POST[des.WithdrawSubscriptionRequest,
          HttpResponse](eqTo(url), any(), any())(any(), any(), any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady(withdrawSubscriptionConnector.withdrawal(amlsRegistrationNumber, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status must be(INTERNAL_SERVER_ERROR)
          body must be(Some("message"))
      }
    }
  }

}
