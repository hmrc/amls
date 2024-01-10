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

import connectors.WithdrawSubscriptionConnector
import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import models.des.WithdrawSubscriptionResponse
import org.mockito.ArgumentMatchers.any
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.{AmlsBaseSpec, AuthAction, SuccessfulAuthAction}

import scala.concurrent.Future

class WithdrawSubscriptionControllerSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator {

  val authAction: AuthAction = SuccessfulAuthAction

  trait Fixture {
    lazy val desConnector = mock[WithdrawSubscriptionConnector]
    val controller = new WithdrawSubscriptionController(
      connector = desConnector,
      authAction = authAction,
      bodyParsers = mockBodyParsers,
      cc = mockCC
    )
  }

  val success = WithdrawSubscriptionResponse("2016-09-17T09:30:47Z")

  private val inputRequest = Json.obj("acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
    "withdrawalDate" -> "2015-08-23",
    "withdrawalReason" -> "Other, please specify",
    "withdrawalReasonOthers" -> "Other Reason")

  private val postRequest = FakeRequest("POST", "/")
    .withHeaders("CONTENT_TYPE" -> "application/json")
    .withBody[JsValue](inputRequest)

  private val postRequestWithNoBody = FakeRequest("POST", "/")
    .withHeaders("CONTENT_TYPE" -> "application/json")
    .withBody[JsValue](Json.parse("{}"))

  "WithdrawSubscriptionController" must {

    "successfully return success response on valid request" in new Fixture {
      when(desConnector.withdrawal(any(), any())(any(), any(), any(), any(), any()))
        .thenReturn(Future.successful(success))

      private val result = controller.withdrawal("org", "TestOrgRef", amlsRegistrationNumber)(postRequest)
      status(result) must be(OK)
      contentAsJson(result) must be(Json.toJson(success))
    }

    "successfully return failed response on invalid request" in new Fixture {
      private val response = Json.obj("errors" -> Seq(
          Json.obj("path" -> "obj.withdrawalReason",
          "error" -> "error.path.missing"),
          Json.obj("path" -> "obj.acknowledgementReference",
          "error" -> "error.path.missing"),
          Json.obj("path" -> "obj.withdrawalDate",
          "error" -> "error.path.missing")
      ))

      private val result = controller.withdrawal("org", "TestOrgRef", amlsRegistrationNumber)(postRequestWithNoBody)
      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(response)
    }

    "return failed response on exception" in new Fixture {
      when(desConnector.withdrawal(any(), any())(any(), any(), any(), any(), any()))
        .thenReturn(Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some("message"))))

      whenReady(controller.withdrawal("org", "TestOrgRef", amlsRegistrationNumber)(postRequest).failed) {
        case HttpStatusException(status, body) =>
          status must be(INTERNAL_SERVER_ERROR)
          body must be(Some("message"))
      }
    }

    "return failed response on invalid amlsRegistrationNumber" in new Fixture {
      private val response = Json.obj(
        "errors" -> Seq("Invalid amlsRegistrationNumber")
      )

      private val result = controller.withdrawal("org", "TestOrgRef", "fsdfsdf")(postRequest)
      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(response)
    }
  }
}
