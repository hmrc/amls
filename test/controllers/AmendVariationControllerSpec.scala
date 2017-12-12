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

package controllers

import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import models.des.{AmendVariationRequest, DesConstants}
import models.fe.aboutthebusiness._
import models.fe.bankdetails._
import models.fe.businessactivities.BusinessActivities
import models.fe.businesscustomer.{Address, ReviewDetails}
import models.fe.businessmatching.{BusinessMatching, BusinessActivities => BMBusinessActivities, BusinessType => BT}
import models.fe.declaration.{AddPerson, Director, RoleWithinBusiness}
import models.{des, fe}
import org.joda.time.LocalDate
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.mvc.{Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.AmendVariationService
import utils.IterateeHelpers

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

class AmendVariationControllerSpec extends PlaySpec
  with MockitoSugar
  with ScalaFutures
  with IntegrationPatience
  with AmlsReferenceNumberGenerator
  with IterateeHelpers
  with OneAppPerSuite {

  trait Fixture {

    object Controller extends AmendVariationController {
      override val service = mock[AmendVariationService]
    }

  }

  implicit val hc = HeaderCarrier()

  val body = fe.SubscriptionRequest(
    businessMatchingSection =
      BusinessMatching(
        activities = BMBusinessActivities(Set.empty),
        reviewDetails = ReviewDetails(
          "", BT.SoleProprietor, Address(
            line_1 = "",
            line_2 = "",
            line_3 = None,
            line_4 = None,
            postcode = None,
            country = ""
          ), ""
        )
      ),
    eabSection = None,
    tradingPremisesSection = None,
    aboutTheBusinessSection = AboutTheBusiness(
      PreviouslyRegisteredNo,
      Some(ActivityStartDate(new LocalDate(1990, 2, 24))),
      Some(VATRegisteredNo),
      Some(CorporationTaxRegisteredYes("1234567890")),
      ContactingYou("123456789", "asas@gmail.com"),
      RegisteredOfficeUK("1", "2", None, None, "AA1 1AA"),
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
    supervisionSection = None
  )

  val postRequest = FakeRequest("POST", "/")
    .withHeaders(CONTENT_TYPE -> "application/json")
    .withBody[JsValue](Json.toJson(body))

  val requestWithEmptyBody = FakeRequest()
    .withHeaders(CONTENT_TYPE -> "application/json")
    .withBody[JsValue](JsNull)


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
      "return a `BadRequest` response when the AmlsRegistrationNumber is invalid" in new Fixture {

        val result = Controller.amend("test", "test", "test")(postRequest)
        val failure = Json.obj("errors" -> Seq("Invalid AmlsRegistrationNumber"))

        status(result) must be(BAD_REQUEST)
        contentAsJson(result) must be(failure)
      }

      "return a valid response when the payload is valid" in new Fixture {

        val viewModel = DesConstants.SubscriptionViewModelForRp

        when(Controller.service.compareAndUpdate(any(), any())(any()))
          .thenReturn(Future.successful(mock[AmendVariationRequest]))

        when {
          Controller.service.update(eqTo(amlsRegistrationNumber), any())(any(), any())
        } thenReturn Future.successful(feResponse)

        val result = Controller.amend("test", "orgRef", amlsRegistrationNumber)(postRequest)

        status(result) must be(OK)
        contentAsJson(result) must be(Json.toJson(feResponse))
      }

      "return an invalid response when the service fails" in new Fixture {

        val viewModel = DesConstants.SubscriptionViewModelForRp

        when(Controller.service.compareAndUpdate(any(), any())(any()))
          .thenReturn(Future.successful(mock[AmendVariationRequest]))

        when {
          Controller.service.update(eqTo(amlsRegistrationNumber), any())(any(), any())
        } thenReturn Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

        whenReady(Controller.amend("test", "OrgRef", amlsRegistrationNumber)(postRequest).failed) {
          case HttpStatusException(status, body) =>
            status mustEqual INTERNAL_SERVER_ERROR
            body mustEqual Some("message")
        }
      }

      "return a `BadRequest` response when the json fails to parse" in new Fixture {

        val response = Json.obj(
          "errors" -> Seq(
            Json.obj(
              "path" -> "obj.aboutTheBusinessSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.aboutYouSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.businessActivitiesSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.businessMatchingSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.bankDetailsSection",
              "error" -> "error.path.missing"
            )
          )
        )

        val result = Controller.amend("test", "orgRef", amlsRegistrationNumber)(requestWithEmptyBody)

        status(result) mustEqual BAD_REQUEST
        contentAsJson(result) mustEqual response
      }

      "call through to the service with an Amendment messageType" in new Fixture {

        when(Controller.service.update(any(), any())(any(), any()))
          .thenReturn(Future.successful(feResponse))

        val mockRequest = mock[AmendVariationRequest]
        val requestArgument = ArgumentCaptor.forClass(classOf[AmendVariationRequest])
        when(Controller.service.compareAndUpdate(requestArgument.capture(), any())(any()))
          .thenReturn(Future.successful(mockRequest))

        private val resultF = Controller.amend("AccountType", "Ref", "XTML00000565656")(postRequest)

        whenReady(resultF) { result: Result =>
          verify(Controller.service).update(eqTo("XTML00000565656"), eqTo(mockRequest))(any(), any())
          requestArgument.getValue().amlsMessageType must be("Amendment")
        }
      }
    }

    "variation is called" must {
      "return a `BadRequest` response when the AmlsRegistrationNumber is invalid" in new Fixture {

        val result = Controller.variation("test", "test", "test")(postRequest)
        val failure = Json.obj("errors" -> Seq("Invalid AmlsRegistrationNumber"))


        status(result) must be(BAD_REQUEST)
        contentAsJson(result) must be(failure)
      }

      "return a valid response when the payload is valid" in new Fixture {

        val viewModel = DesConstants.SubscriptionViewModelForRp

        when(Controller.service.compareAndUpdate(any(), any())(any()))
          .thenReturn(Future.successful(mock[AmendVariationRequest]))

        when {
          Controller.service.update(eqTo(amlsRegistrationNumber), any())(any(), any())
        } thenReturn Future.successful(feResponse)

        val result = Controller.variation("test", "orgRef", amlsRegistrationNumber)(postRequest)

        status(result) must be(OK)
        contentAsJson(result) must be(Json.toJson(feResponse))
      }

      "return an invalid response when the service fails" in new Fixture {

        val viewModel = DesConstants.SubscriptionViewModelForRp

        when(Controller.service.compareAndUpdate(any(), any())(any()))
          .thenReturn(Future.successful(mock[AmendVariationRequest]))

        when {
          Controller.service.update(eqTo(amlsRegistrationNumber), any())(any(), any())
        } thenReturn Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

        whenReady(Controller.variation("test", "OrgRef", amlsRegistrationNumber)(postRequest).failed) {
          case HttpStatusException(status, body) =>
            status mustEqual INTERNAL_SERVER_ERROR
            body mustEqual Some("message")
        }
      }

      "return a `BadRequest` response when the json fails to parse" in new Fixture {

        val response = Json.obj(
          "errors" -> Seq(
            Json.obj(
              "path" -> "obj.aboutTheBusinessSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.aboutYouSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.businessActivitiesSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.businessMatchingSection",
              "error" -> "error.path.missing"
            ),
            Json.obj(
              "path" -> "obj.bankDetailsSection",
              "error" -> "error.path.missing"
            )
          )
        )

        val result = Controller.variation("test", "orgRef", amlsRegistrationNumber)(requestWithEmptyBody)

        status(result) mustEqual BAD_REQUEST
        contentAsJson(result) mustEqual response
      }

      "call through to the service with an Variation messageType" in new Fixture {
        when(Controller.service.update(any(), any())(any(), any()))
          .thenReturn(Future.successful(feResponse))

        val mockRequest = mock[AmendVariationRequest]
        val requestArgument = ArgumentCaptor.forClass(classOf[AmendVariationRequest])
        when(Controller.service.compareAndUpdate(requestArgument.capture(), any())(any()))
          .thenReturn(Future.successful(mockRequest))

        private val resultF = Controller.variation("AccountType", "Ref", "XTML00000565656")(postRequest)

        whenReady(resultF) { result: Result =>
          verify(Controller.service).update(eqTo("XTML00000565656"), eqTo(mockRequest))(any(), any())
          requestArgument.getValue().amlsMessageType must be("Variation")
        }
      }


      "call through to the service with an Renewal messageType" in new Fixture {
        when(Controller.service.update(any(), any())(any(), any()))
          .thenReturn(Future.successful(feResponse))

        val mockRequest = mock[AmendVariationRequest]
        val requestArgument = ArgumentCaptor.forClass(classOf[AmendVariationRequest])
        when(Controller.service.compareAndUpdate(requestArgument.capture(), any())(any()))
          .thenReturn(Future.successful(mockRequest))

        private val resultF = Controller.renewal("AccountType", "Ref", "XTML00000565656")(postRequest)

        whenReady(resultF) { result: Result =>
          verify(Controller.service).update(eqTo("XTML00000565656"), eqTo(mockRequest))(any(), any())
          requestArgument.getValue().amlsMessageType must be("Renewal")
        }
      }

      "call through to the service with an Renewal Amendment messageType" in new Fixture {
        when(Controller.service.update(any(), any())(any(), any()))
          .thenReturn(Future.successful(feResponse))

        val mockRequest = mock[AmendVariationRequest]
        val requestArgument = ArgumentCaptor.forClass(classOf[AmendVariationRequest])
        when(Controller.service.compareAndUpdate(requestArgument.capture(), any())(any()))
          .thenReturn(Future.successful(mockRequest))

        private val resultF = Controller.renewalAmendment("AccountType", "Ref", "XTML00000565656")(postRequest)

        whenReady(resultF) { result: Result =>
          verify(Controller.service).update(eqTo("XTML00000565656"), eqTo(mockRequest))(any(), any())
          requestArgument.getValue().amlsMessageType must be("Renewal Amendment")
        }
      }
    }
  }
}
