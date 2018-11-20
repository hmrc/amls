/*
 * Copyright 2018 HM Revenue & Customs
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

import connectors.DeregisterSubscriptionConnector
import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import models.des.DeregisterSubscriptionResponse
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.test.{FakeApplication, FakeRequest}
import play.api.test.Helpers.{contentAsJson, _}
import utils.ApiRetryHelper

import scala.concurrent.Future

class DeregisterSubscriptionControllerSpec extends PlaySpec
  with MockitoSugar
  with ScalaFutures
  with AmlsReferenceNumberGenerator
  with OneAppPerSuite
{
  implicit val apiRetryHelper: ApiRetryHelper = new ApiRetryHelper(as = app.actorSystem)

  trait Fixture {
    lazy val mockDeregConnector: DeregisterSubscriptionConnector = mock[DeregisterSubscriptionConnector]
    val deregisterSubscriptionController = new DeregisterSubscriptionController(
      deregisterSubscriptionConnector = mockDeregConnector,
      apiRetryHelper = mock[ApiRetryHelper]
    )
  }

  val accountType = "org"
  val accountRef = "TestOrgRef"

  val success = DeregisterSubscriptionResponse("2016-09-17T09:30:47Z")

  private val inputRequest = Json.obj(
    "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
    "deregistrationDate" -> "2015-08-23",
    "deregistrationReason" -> "Other, please specify",
    "deregReasonOther" -> "Other Reason"
  )

  private val postRequest = FakeRequest("POST", "/")
    .withHeaders("CONTENT_TYPE" -> "application/json")
    .withBody[JsValue](inputRequest)

  private val postRequestWithNoBody = FakeRequest("POST", "/")
    .withHeaders("CONTENT_TYPE" -> "application/json")
    .withBody[JsValue](JsNull)

  "DeregisterSubscriptionController" must {

    "successfully return success response on valid request" in new Fixture {

      when(
        mockDeregConnector.deregistration(any(), any())(any(), any(), any(), any(), any())
      ) thenReturn Future.successful(success)

      private val result = deregisterSubscriptionController.deregistration(accountType, accountRef, amlsRegistrationNumber)(postRequest)
      status(result) must be(OK)
      contentAsJson(result) must be(Json.toJson(success))
    }


    "successfully return failed response on invalid request" in new Fixture {
      private val response = Json.obj(
        "errors" -> Seq(
          Json.obj("path" -> "obj.deregistrationReason",
            "error" -> "error.path.missing"),
          Json.obj("path" -> "obj.acknowledgementReference",
            "error" -> "error.path.missing"),
          Json.obj("path" -> "obj.deregistrationDate",
            "error" -> "error.path.missing")
        ))

      private val result = deregisterSubscriptionController.deregistration(accountType, accountRef, amlsRegistrationNumber)(postRequestWithNoBody)
      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(response)
    }


    "return failed response on exception" in new Fixture {

      when(
        mockDeregConnector.deregistration(any(), any())(any(), any(), any(), any(), any())
      ) thenReturn Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

      whenReady(deregisterSubscriptionController.deregistration(accountType, accountRef, amlsRegistrationNumber)(postRequest).failed) {
        case HttpStatusException(status, body) =>
          status must be(INTERNAL_SERVER_ERROR)
          body must be(Some("message"))
      }

    }

    "return failed response on invalid amlsRegistrationNumber" in new Fixture {
      private val response = Json.obj(
        "errors" -> Seq("Invalid amlsRegistrationNumber")
      )

      private val result = deregisterSubscriptionController.deregistration(accountType, accountRef, "fsdfsdf")(postRequest)
      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(response)
    }
  }
}
