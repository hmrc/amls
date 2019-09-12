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
import metrics.{API8, Metrics}
import models.des
import models.des.{WithdrawSubscriptionRequest, WithdrawSubscriptionResponse, WithdrawalReason}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.{Configuration, Environment}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.{AmlsBaseSpec, ApiRetryHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WithdrawSubscriptionConnectorSpec extends AmlsBaseSpec {

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  trait Fixture {
    val withdrawSubscriptionConnector = new WithdrawSubscriptionConnector(app, mockRunModeConf, mockEnvironment, mockAppConfig, mockAuditConnector) {
      override private[connectors] val baseUrl: String = "baseUrl"
      override private[connectors] val token: String = "token"
      override private[connectors] val env: String = "ist0"
      override private[connectors] val httpGet: HttpGet = mock[HttpGet]
      override private[connectors] val httpPost: HttpPost = mock[HttpPost]
      override private[connectors] val metrics: Metrics = mock[Metrics]
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
        responseStatus = OK,
        responseHeaders = Map(
          "CorrelationId" -> Seq("my-correlation-id")
        ),
        responseJson = Some(Json.toJson(successModel))
      )

      when {
        withdrawSubscriptionConnector.httpPost.POST[des.WithdrawSubscriptionRequest,
          HttpResponse](eqTo(url), any(), any())(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(withdrawSubscriptionConnector.withdrawal(amlsRegistrationNumber, testRequest)) {
        _ mustEqual successModel
      }
    }

    "return failed response on invalid request" in new Fixture {
      val response = HttpResponse(
        responseStatus = BAD_REQUEST,
        responseHeaders = Map(
          "CorrelationId" -> Seq("my-correlation-id")
        ),
        responseString = Some("message")
      )

      when {
        withdrawSubscriptionConnector.httpPost.POST[des.WithdrawSubscriptionRequest,
          HttpResponse](eqTo(url), any(), any())(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(withdrawSubscriptionConnector.withdrawal(amlsRegistrationNumber, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status must be(BAD_REQUEST)
          body must be(Some("message"))
      }
    }

    "return failed response on exception" in new Fixture {
      val response = HttpResponse(
        responseStatus = BAD_REQUEST,
        responseHeaders = Map(
          "CorrelationId" -> Seq("my-correlation-id")
        ),
        responseString = Some("message")
      )

      when {
        withdrawSubscriptionConnector.httpPost.POST[des.WithdrawSubscriptionRequest,
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
