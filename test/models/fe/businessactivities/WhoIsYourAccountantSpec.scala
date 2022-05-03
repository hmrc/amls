/*
 * Copyright 2022 HM Revenue & Customs
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

package models.fe.businessactivities

import models.des.aboutthebusiness.Address
import models.des.businessactivities.{AdvisorNameAddress, MlrAdvisor, MlrAdvisorDetails}
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.JsSuccess

class WhoIsYourAccountantSpec extends WordSpec with Matchers {

  val DefaultName = "Default Name"
  val DefaultTradingName = Some("Default Trading Name")

  val DefaultAddressLine1 = "Default Line 1"
  val DefaultAddressLine2 = "Default Line 2"
  val DefaultAddressLine3 = Some("Default Line 3")
  val DefaultAddressLine4 = Some("Default Line 4")
  val DefaultPostcode = "BB1 1BB"
  val DefaultCountry = "Default Country"

  val DefaultUKAddress = UkAccountantsAddress(DefaultAddressLine1,
    DefaultAddressLine2,
    DefaultAddressLine3,
    DefaultAddressLine4,
    DefaultPostcode)

  val DefaultWhoIsYourAccountant = WhoIsYourAccountant(DefaultName,
    DefaultTradingName,
    DefaultUKAddress)

  "WhoIsYourAccountant" must {

    "successfully complete a round trip json conversion" in {
      WhoIsYourAccountant.jsonReads.reads(
        WhoIsYourAccountant.jsonWrites.writes(DefaultWhoIsYourAccountant)
      ) shouldBe JsSuccess(DefaultWhoIsYourAccountant)
    }

    "convert des to frontend model successfully" in {
      val mlrAdvisor = Some(MlrAdvisor(true, Some(MlrAdvisorDetails(
        Some(AdvisorNameAddress("Name", Some("TradingName"), Address(
          "AdvisorAddressLine1",
          "AdvisorAddressLine2",
          Some("AdvisorAddressLine3"),
          Some("AdvisorAddressLine4"),
          "AD",
          Some("AA1 1AA")))),
        true,
        Some("01234567890")
      ))))

      WhoIsYourAccountant.conv(mlrAdvisor) shouldBe Some(WhoIsYourAccountant(
        "Name",
        Some("TradingName"),
        UkAccountantsAddress("AdvisorAddressLine1","AdvisorAddressLine2",Some("AdvisorAddressLine3"),Some("AdvisorAddressLine4"),"AA1 1AA")))
    }

    "convert des to frontend model successfully for nonuk address" in {
      val mlrAdvisor = Some(MlrAdvisor(true, Some(MlrAdvisorDetails(
        Some(AdvisorNameAddress("Name", Some("TradingName"), Address(
          "line1",
          "line2",
          Some("line3"),
          Some("line4"),
          "GB",
          None))),
        false,
        None
      ))))

      WhoIsYourAccountant.conv(mlrAdvisor) shouldBe Some(WhoIsYourAccountant("Name",Some("TradingName"),
        NonUkAccountantsAddress("line1","line2",Some("line3"),Some("line4"),"GB")))
    }

    "convert des to frontend model successfully when MlrAdvisor None" in {
      val mlrAdvisor = None

      WhoIsYourAccountant.conv(mlrAdvisor) shouldBe None
    }

    "convert des to frontend model successfully when MlrAdvisorDetails None" in {
      val mlrAdvisor = Some(MlrAdvisor(false, None))

      WhoIsYourAccountant.conv(mlrAdvisor) shouldBe None
    }

    "convert des to frontend model successfully when AdvisorNameAddress None" in {
      val mlrAdvisor = Some(MlrAdvisor(true, Some(MlrAdvisorDetails(
        None,
        false,
        None
      ))))

      WhoIsYourAccountant.conv(mlrAdvisor) shouldBe None
    }
  }

}
