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

package models.fe.aboutthebusiness

import models.des.DesConstants
import org.joda.time.LocalDate
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsNull, Json}

class AboutTheBusinessSpec extends PlaySpec with MockitoSugar {

  val previouslyRegistered = PreviouslyRegisteredYes("12345678")

  val regForVAT = VATRegisteredYes("123456789")

  // scalastyle:off
  val  activityStartDate = ActivityStartDate(new LocalDate(1990, 2, 24))

  val  newActivityStartDate = ActivityStartDate(new LocalDate(1990, 2, 24))

  val regForCorpTax = CorporationTaxRegisteredYes("1234567890")

  val contactingYou = ContactingYou("07000111222", "test@test.com")

  val regOfficeOrMainPlaceUK =  RegisteredOfficeUK("38B", "Longbenton", None, None, "AA1 1AA")

  val uKCorrespondenceAddress = UKCorrespondenceAddress("Name",
    "Business Name",
    "address 1",
    "address 2",
    Some("address 3"),
    Some("address 4"),
    "BB1 1BB")

  "AboutTheBusiness" must {
    val completeJson = Json.obj(
      "previouslyRegistered" -> Json.obj("previouslyRegistered" -> true,
      "prevMLRRegNo" -> "12345678"),
      "activityStartDate" -> Json.obj(
      "startDate" -> "1990-02-24"),
      "vatRegistered" -> Json.obj("registeredForVAT" -> true,
      "vrnNumber" -> "123456789"),
      "corporationTaxRegistered" -> Json.obj("registeredForCorporationTax" -> true,
      "corporationTaxReference" -> "1234567890"),
        "contactingYou" -> Json.obj(
        "phoneNumber" -> "07000111222",
        "email" -> "test@test.com"),
        "registeredOffice" -> Json.obj(
      "addressLine1" -> "38B",
      "addressLine2" -> "Longbenton",
      "addressLine3" -> JsNull,
      "addressLine4" -> JsNull,
      "postCode" -> "AA1 1AA"),
      "altCorrespondenceAddress" -> true,
      "correspondenceAddress" -> Json.obj(
      "yourName" -> "Name",
      "businessName" -> "Business Name",
      "correspondenceAddressLine1" -> "address 1",
      "correspondenceAddressLine2" -> "address 2",
      "correspondenceAddressLine3" -> "address 3",
      "correspondenceAddressLine4" -> "address 4",
      "correspondencePostCode" -> "BB1 1BB"
        )
      )


    val completeModel = AboutTheBusiness(
      previouslyRegistered = PreviouslyRegisteredYes("12345678"),
      activityStartDate = Some(activityStartDate),
      vatRegistered = Some(regForVAT),
      corporationTaxRegistered = Some(regForCorpTax),
      contactingYou = contactingYou,
      registeredOffice = regOfficeOrMainPlaceUK,
      altCorrespondenceAddress = true,
      correspondenceAddress = Some(uKCorrespondenceAddress)
    )

    "Serialise as expected" in {
      Json.toJson(completeModel) must
        be(completeJson)
    }

    "Deserialise as expected" in {
      completeJson.as[AboutTheBusiness] must
        be(completeModel)
    }

    "Convert des model to frontend model" in {

      val atb = AboutTheBusiness(
        previouslyRegistered = PreviouslyRegisteredNo,
        activityStartDate = Some(ActivityStartDate(new LocalDate(2001, 1, 1))),
        vatRegistered = Some(VATRegisteredYes("123456789")),
        corporationTaxRegistered = Some(CorporationTaxRegisteredYes("1234567891")),
        contactingYou = ContactingYou("07000111222","BusinessEmail"),
        registeredOffice =  RegisteredOfficeUK("BusinessAddressLine1","BusinessAddressLine2",Some("BusinessAddressLine3"),
          Some("BusinessAddressLine4"),"AA1 1AA"),
        altCorrespondenceAddress = true,
        correspondenceAddress = Some(UKCorrespondenceAddress("Name","TradingName","AlternativeAddressLine1","AlternativeAddressLine2",
          Some("AlternativeAddressLine3"),
          Some("AlternativeAddressLine4"),"AA1 1AA"))
      )

      AboutTheBusiness.conv(DesConstants.SubscriptionViewModel) must be(atb)

    }
  }
}
