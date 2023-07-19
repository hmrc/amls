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

package controllers

import connectors.SubscriptionStatusDESConnector
import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import models.des

import java.time.LocalDateTime
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.{AmlsBaseSpec, AuthAction, SuccessfulAuthAction}

import scala.concurrent.Future

class SubscriptionStatusControllerSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator {

  lazy val ssConn = new SubscriptionStatusDESConnector(mockAppConfig, mockAuditConnector, mockHttpClient, mockMetrics)
  val authAction: AuthAction = SuccessfulAuthAction

  lazy val Controller: SubscriptionStatusController = new SubscriptionStatusController(ssConn, authAction, mockCC) {
    override val connector = mock[SubscriptionStatusDESConnector]
  }

  val request = FakeRequest()
    .withHeaders(CONTENT_TYPE -> "application/json")

  "SubscriptionStatusController" must {

    "return a `BadRequest` response when the amls registration number is invalid" in {

      val result = Controller.get("test", "test", "test")(request)
      val failure = Json.obj("errors" -> Seq("Invalid AMLS Registration Number"))

      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(failure)
    }

    "return a valid response when the amls registration number is valid" in {

      val response = des.ReadStatusResponse(LocalDateTime.now(), "Approved",
        None, None, None, None, false)

      when {
        Controller.connector.status(ArgumentMatchers.eq(amlsRegistrationNumber))(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      val result = Controller.get("test", "test", amlsRegistrationNumber)(request)

      status(result) must be(OK)
      contentAsJson(result) must be(Json.toJson(response))
    }

    "return an invalid response when the service fails" in {
      when {
        Controller.connector.status(ArgumentMatchers.eq(amlsRegistrationNumber))(any(), any(), any(), any())
      } thenReturn Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

      whenReady(Controller.get("test", "test", amlsRegistrationNumber)(request).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }

  }
}