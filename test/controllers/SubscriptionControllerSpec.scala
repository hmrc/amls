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
import models.fe.aboutthebusiness._
import models.fe.bankdetails._
import models.fe.businesscustomer.{Address, ReviewDetails}
import models.fe.businessmatching.{BusinessMatching, BusinessActivities => BMBusinessActivities, BusinessType => BT}
import models.fe.businessactivities.BusinessActivities
import models.fe.declaration.{AddPerson, Director, RoleWithinBusiness}
import models.{des, fe}
import org.joda.time.LocalDate
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SubscriptionService
import utils.IterateeHelpers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubscriptionControllerSpec
  extends PlaySpec
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with IterateeHelpers
    with OneAppPerSuite {

  object SubscriptionController extends SubscriptionController {
    override val service = mock[SubscriptionService]
  }

  "SubscriptionController" must {

    val safeId = "XA0001234567890"
    // scalastyle:off
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
      aboutTheBusinessSection = AboutTheBusiness(PreviouslyRegisteredNo, Some(ActivityStartDate(new LocalDate(1990, 2, 24))), Some(VATRegisteredNo),
        Some(CorporationTaxRegisteredYes("1234567890")), ContactingYou("123456789", "asas@gmail.com"), RegisteredOfficeUK("1", "2", None, None, "postcode")),
      bankDetailsSection = Seq(BankDetails(PersonalAccount, BankAccount("name", NonUKAccountNumber("1234567896")))),
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

    "return a `BadRequest` response when the safeId is invalid" in {

      val result = SubscriptionController.subscribe("test", "test", "test")(postRequest)
      val failure = Json.obj("errors" -> Seq("Invalid SafeId"))

      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(failure)
    }

    "return a valid response when the payload is valid" in {

      val response = des.SubscriptionResponse(
        etmpFormBundleNumber = "111111",
        amlsRefNo = "XAAM00000123456",
        Some(1301737.96),
        Some(231.42),
        870458,
        2172427.38,
        "string"
      )

      when {
        SubscriptionController.service.subscribe(eqTo(safeId), any())(any(), any())
      } thenReturn Future.successful(response)

      val result = SubscriptionController.subscribe("test", "orgRef", safeId)(postRequest)

      status(result) must be(OK)
      contentAsJson(result) must be(Json.toJson(response))
    }

    "return an invalid response when the service fails" in {

      when {
        SubscriptionController.service.subscribe(eqTo(safeId), any())(any(), any())
      } thenReturn Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

      whenReady(SubscriptionController.subscribe("test", "OrgRef", safeId)(postRequest).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }

    "return a `BadRequest` response when the json fails to parse" in {

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

      val result = SubscriptionController.subscribe("test", "orgRef", safeId)(requestWithEmptyBody)

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result) mustEqual response
    }
  }
}
