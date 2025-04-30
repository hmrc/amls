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
import metrics.API10
import models.des
import models.des.{DeregisterSubscriptionRequest, DeregisterSubscriptionResponse, DeregistrationReason}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.scalatest.time.{Seconds, Span}
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import utils.AmlsBaseSpec

import scala.concurrent.Future

class DeregisterSubscriptionConnectorSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator {

  trait Fixture {

    val testConnector =
      new DeregisterSubscriptionConnector(mockAppConfig, mockAuditConnector, mockHttpClient, mockMetrics) {
        override private[connectors] val baseUrl: String = "baseUrl"
        override private[connectors] val token: String   = "token"
        override private[connectors] val env: String     = "ist0"
        override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl"
      }

    val mockTimer = mock[Timer.Context]
    when {
      testConnector.metrics.timer(ArgumentMatchers.eq(API10))
    } thenReturn mockTimer

    implicit val hc: HeaderCarrier = HeaderCarrier()

    val url = s"${testConnector.fullUrl}/$amlsRegistrationNumber/deregistration"
  }

  val successModel = DeregisterSubscriptionResponse("2016-09-17T09:30:47Z")
  val testRequest  = DeregisterSubscriptionRequest(
    "AEF7234BGG12539GH143856HEA123412",
    "2015-08-23",
    DeregistrationReason.Other,
    Some("Other Reason")
  )

  "DeregisterSubscriptionConnector" must {

    "return successful response for valid request" in new Fixture {

      val response = HttpResponse(
        status = OK,
        json = Json.toJson(successModel),
        headers = Map("CorrelationId" -> Seq("my-correlation-id"))
      )

      when {
        testConnector.httpClientV2.post(url"$url").setHeader(any()).withBody(Json.toJson(testRequest)).execute[HttpResponse]
      } thenReturn Future.successful(response)

      whenReady(testConnector.deregistration(amlsRegistrationNumber, testRequest)) {
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
        testConnector.httpClientV2.post(url"$url").setHeader(any()).withBody(Json.toJson(testRequest)).execute[HttpResponse]
      } thenReturn Future.successful(response)

      whenReady(testConnector.deregistration(amlsRegistrationNumber, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status                     must be(BAD_REQUEST)
          body.getOrElse("").isEmpty must be(true)
      }
    }

    "return failed response on exception" in new Fixture {

      when {
        testConnector.httpClientV2.post(url"$url").setHeader(any()).withBody(Json.toJson(testRequest)).execute[HttpResponse]
      } thenReturn Future.failed(new Exception("message"))

      whenReady(
        testConnector
          .deregistration(
            amlsRegistrationNumber,
            testRequest
          )
          .failed,
        timeout(Span(2, Seconds))
      ) { case HttpStatusException(status, body) =>
        status must be(INTERNAL_SERVER_ERROR)
        body   must be(Some("message"))
      }
    }
  }

}
