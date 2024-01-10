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

import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import models.des.AmendVariationRequest
import models.fe
import models.fe.bankdetails._
import models.fe.businessactivities.BusinessActivities
import models.fe.businesscustomer.{Address, ReviewDetails}
import models.fe.businessdetails._
import models.fe.businessmatching.{BusinessMatching, BusinessActivities => BMBusinessActivities, BusinessType => BT}
import models.fe.declaration.{AddPerson, Director, RoleWithinBusiness}
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentCaptor, ArgumentMatchers}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AmendVariationService
import utils._

import java.time.LocalDate
import scala.concurrent.Future

class AmendVariationControllerSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator {

  val avs: AmendVariationService = mock[AmendVariationService]
  val authAction: AuthAction = SuccessfulAuthAction

  val testController = new AmendVariationController(avs, authAction, mockBodyParsers, mockCC)

  val body = fe.SubscriptionRequest(
    businessMatchingSection =
      BusinessMatching(
        activities = BMBusinessActivities(Set.empty),
        reviewDetails = ReviewDetails(
          "", BT.SoleProprietor, Address(
            line_1 = "",
            line_2 = None,
            line_3 = None,
            line_4 = None,
            postcode = None,
            country = ""
          ), ""
        )
      ),
    eabSection = None,
    tradingPremisesSection = None,
    businessDetailsSection = BusinessDetails(
      PreviouslyRegisteredNo,
      Some(ActivityStartDate(LocalDate.of(1990, 2, 24))),
      Some(VATRegisteredNo),
      Some(CorporationTaxRegisteredYes("1234567890")),
      ContactingYou("123456789", "asas@gmail.com"),
      RegisteredOfficeUK("1", Some("2"), None, None, "AA1 1AA"),
      altCorrespondenceAddress = false
    ),
    bankDetailsSection = Seq(BankDetails(PersonalAccount, "name", NonUKAccountNumber("1234567896"))),
    aboutYouSection = AddPerson("name", Some("name"), "name", RoleWithinBusiness(Set(Director))),
    businessActivitiesSection = BusinessActivities(None),
    responsiblePeopleSection = None,
    tcspSection = None,
    aspSection = None,
    msbSection = None,
    hvdSection = None,
    ampSection = None,
    supervisionSection = None
  )

  val postRequest = FakeRequest("POST", "/")
    .withHeaders(CONTENT_TYPE -> "application/json")
    .withBody[JsValue](Json.toJson(body))

  val requestWithEmptyBody = FakeRequest("POST", "/")
    .withHeaders(CONTENT_TYPE -> "application/json")
    .withBody[JsValue](Json.parse("{}"))


  val feResponse = fe.AmendVariationResponse(
    processingDate = "2016-09-17T09:30:47Z",
    etmpFormBundleNumber = "111111",
    1301737.96,
    None, None,
    115.0,
    None,
    124.58,
    None, None
  )

  "AmendvariationController" when {
    "amend is called" must {
      "return a `BadRequest` response when the AmlsRegistrationNumber is invalid" in {

        val result = testController.amend("test", "test", "test")(postRequest)
        val failure = Json.obj("errors" -> Seq("Invalid AmlsRegistrationNumber"))

        status(result) must be(BAD_REQUEST)
        contentAsJson(result) must be(failure)
      }

      "return a valid response when the payload is valid" in {

        when(testController.service.compareAndUpdate(any(), any())(any(), any()))
          .thenReturn(Future.successful(mock[AmendVariationRequest]))

        when {
          testController.service.update(ArgumentMatchers.eq(amlsRegistrationNumber), any())(any(), any(), any())
        } thenReturn Future.successful(feResponse)

        val result = testController.amend("test", "orgRef", amlsRegistrationNumber)(postRequest)

        status(result) must be(OK)
        contentAsJson(result) must be(Json.toJson(feResponse))
      }

      "return an invalid response when the service fails" in {

        when(testController.service.compareAndUpdate(any(), any())(any(), any()))
          .thenReturn(Future.successful(mock[AmendVariationRequest]))

        when {
          testController.service.update(ArgumentMatchers.eq(amlsRegistrationNumber), any())(any(), any(), any())
        } thenReturn Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

        whenReady(testController.amend("test", "OrgRef", amlsRegistrationNumber)(postRequest).failed) {
          case HttpStatusException(status, body) =>
            status mustEqual INTERNAL_SERVER_ERROR
            body mustEqual Some("message")
        }
      }

      "return a `BadRequest` response when the json fails to parse" in {

        val response = Json.obj(
          "errors" -> Seq(
            Json.obj(
              "path" -> "obj.businessActivitiesSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.aboutYouSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.bankDetailsSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.businessDetailsSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.businessMatchingSection",
              "error" -> "error.path.missing"
            )
          )
        )
        val result = testController.amend("test", "orgRef", amlsRegistrationNumber)(requestWithEmptyBody)

        status(result) mustEqual BAD_REQUEST
        contentAsJson(result) mustEqual response
      }

      "call through to the service with an Amendment messageType" in {

        when(testController.service.update(any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(feResponse))

        val mockRequest = mock[AmendVariationRequest]
        val requestArgument = ArgumentCaptor.forClass(classOf[AmendVariationRequest])
        when(testController.service.compareAndUpdate(requestArgument.capture(), any())(any(), any()))
          .thenReturn(Future.successful(mockRequest))

        val resultF = testController.amend("AccountType", "Ref", "XTML00000565656")(postRequest)

        whenReady(resultF) { result: Result =>
          verify(testController.service).update(ArgumentMatchers.eq("XTML00000565656"), ArgumentMatchers.eq(mockRequest))(any(), any(), any())
          requestArgument.getValue().amlsMessageType must be("Amendment")
        }
      }
    }

    "variation is called" must {
      "return a `BadRequest` response when the AmlsRegistrationNumber is invalid" in {

        val result = testController.variation("test", "test", "test")(postRequest)
        val failure = Json.obj("errors" -> Seq("Invalid AmlsRegistrationNumber"))


        status(result) must be(BAD_REQUEST)
        contentAsJson(result) must be(failure)
      }

      "return a valid response when the payload is valid" in {

        when(testController.service.compareAndUpdate(any(), any())(any(), any()))
          .thenReturn(Future.successful(mock[AmendVariationRequest]))

        when {
          testController.service.update(ArgumentMatchers.eq(amlsRegistrationNumber), any())(any(), any(), any())
        } thenReturn Future.successful(feResponse)

        val result = testController.variation("test", "orgRef", amlsRegistrationNumber)(postRequest)

        status(result) must be(OK)
        contentAsJson(result) must be(Json.toJson(feResponse))
      }

      "return an invalid response when the service fails" in {

        when(testController.service.compareAndUpdate(any(), any())(any(), any()))
          .thenReturn(Future.successful(mock[AmendVariationRequest]))

        when {
          testController.service.update(ArgumentMatchers.eq(amlsRegistrationNumber), any())(any(), any(), any())
        } thenReturn Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

        whenReady(testController.variation("test", "OrgRef", amlsRegistrationNumber)(postRequest).failed) {
          case HttpStatusException(status, body) =>
            status mustEqual INTERNAL_SERVER_ERROR
            body mustEqual Some("message")
        }
      }

      "return a `BadRequest` response when the json fails to parse" in {

        val response = Json.obj(
          "errors" -> Seq(
            Json.obj(
              "path" -> "obj.businessActivitiesSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.aboutYouSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.bankDetailsSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.businessDetailsSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.businessMatchingSection",
              "error" -> "error.path.missing"
            )
          )
        )

        val result = testController.variation("test", "orgRef", amlsRegistrationNumber)(requestWithEmptyBody)

        status(result) mustEqual BAD_REQUEST
        contentAsJson(result) mustEqual response
      }

      "call through to the service with an Variation messageType" in {
        when(testController.service.update(any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(feResponse))

        val mockRequest = mock[AmendVariationRequest]
        val requestArgument = ArgumentCaptor.forClass(classOf[AmendVariationRequest])
        when(testController.service.compareAndUpdate(requestArgument.capture(), any())(any(), any()))
          .thenReturn(Future.successful(mockRequest))

        val resultF = testController.variation("AccountType", "Ref", "XTML00000565656")(postRequest)

        whenReady(resultF) { result: Result =>
          verify(testController.service).update(ArgumentMatchers.eq("XTML00000565656"), ArgumentMatchers.eq(mockRequest))(any(), any(), any())
          requestArgument.getValue().amlsMessageType must be("Variation")
        }
      }

      "call through to the service with an Renewal messageType" in {
        when(testController.service.update(any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(feResponse))

        val mockRequest = mock[AmendVariationRequest]
        val requestArgument = ArgumentCaptor.forClass(classOf[AmendVariationRequest])
        when(testController.service.compareAndUpdate(requestArgument.capture(), any())(any(), any()))
          .thenReturn(Future.successful(mockRequest))

        val resultF = testController.renewal("AccountType", "Ref", "XTML00000565656")(postRequest)

        whenReady(resultF) { result: Result =>
          verify(testController.service).update(ArgumentMatchers.eq("XTML00000565656"), ArgumentMatchers.eq(mockRequest))(any(), any(), any())
          requestArgument.getValue().amlsMessageType must be("Renewal")
        }
      }

      "call through to the service with an Renewal Amendment messageType" in {
        when(testController.service.update(any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(feResponse))

        val mockRequest = mock[AmendVariationRequest]
        val requestArgument = ArgumentCaptor.forClass(classOf[AmendVariationRequest])
        when(testController.service.compareAndUpdate(requestArgument.capture(), any())(any(), any()))
          .thenReturn(Future.successful(mockRequest))

        val resultF = testController.renewalAmendment("AccountType", "Ref", "XTML00000565656")(postRequest)

        whenReady(resultF) { result: Result =>
          verify(testController.service).update(ArgumentMatchers.eq("XTML00000565656"), ArgumentMatchers.eq(mockRequest))(any(), any(), any())
          requestArgument.getValue().amlsMessageType must be("Renewal Amendment")
        }
      }
    }
  }
}