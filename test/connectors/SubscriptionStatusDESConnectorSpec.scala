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

import audit.MockAudit
import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import metrics.{API9, Metrics}
import models.des
import org.joda.time.{DateTimeUtils, LocalDateTime}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubscriptionStatusDESConnectorSpec
  extends PlaySpec
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience with OneServerPerSuite with BeforeAndAfterAll {

  override def beforeAll {
    DateTimeUtils.setCurrentMillisFixed(1000)
  }

  override def afterAll: Unit = {
    DateTimeUtils.setCurrentMillisSystem()
  }

  trait Fixture {

    object testDESConnector extends SubscriptionStatusDESConnector {
      override private[connectors] val baseUrl: String = "baseUrl"
      override private[connectors] val token: String = "token"
      override private[connectors] val env: String = "ist0"
      override private[connectors] val httpGet: HttpGet = mock[HttpGet]
      override private[connectors] val httpPost: HttpPost = mock[HttpPost]
      override private[connectors] val metrics: Metrics = mock[Metrics]
      override private[connectors] val audit = MockAudit
      override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl/"
      override private[connectors] def auditConnector = mock[AuditConnector]

    }
    implicit val hc = HeaderCarrier()

    val successModel = des.ReadStatusResponse(LocalDateTime.now(), "Approved",
      None, None, None, None, false, safeId = "ABCDEF123456789")

    val mockTimer = mock[Timer.Context]

    val amlsRegistrationNumber = "test"

    val url = s"${testDESConnector.fullUrl}/$amlsRegistrationNumber/status"

    when {
      testDESConnector.metrics.timer(eqTo(API9))
    } thenReturn mockTimer
  }

  "DESConnector" must {

    "return a succesful future" in new Fixture {

      val response = HttpResponse(
        responseStatus = OK,
        responseHeaders = Map.empty,
        responseJson = Some(Json.toJson(successModel))
      )

      when {
        testDESConnector.httpGet.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.status(amlsRegistrationNumber)) {
        _ mustEqual successModel
      }
    }

    "return a failed future" in new Fixture {

      val response = HttpResponse(
        responseStatus = BAD_REQUEST,
        responseHeaders = Map.empty
      )
      when {
        testDESConnector.httpGet.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.status(amlsRegistrationNumber).failed) {
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
        testDESConnector.httpGet.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.status(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual OK
          body mustEqual Some("message")
      }
    }

    "return a failed future (exception)" in new Fixture {

      when {
        testDESConnector.httpGet.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady(testDESConnector.status(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }
  }

}
