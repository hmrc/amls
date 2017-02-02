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

package models.des.aboutyou

import models.fe.declaration.{BeneficialShareholder, ExternalAccountant, Other}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class AboutYouSpec extends PlaySpec {
  "AboutYouDetails" must {
    "be serialisable for roleWithinBusiness" in {
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), true, Some("Beneficial Shareholder"))

      Aboutyou.format.writes(aboutyouModel) must be(Json.obj("individualDetails" -> Json.obj("firstName" -> "fName", "lastName" -> "lName"),
        "employedWithinBusiness" -> true, "roleWithinBusiness" -> "Beneficial Shareholder"))
    }

    "be serialisable for roleForTheBusiness" in {
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), false, None, None, Some("External Accountant"))

      Aboutyou.format.writes(aboutyouModel) must be(Json.obj("individualDetails" -> Json.obj("firstName" -> "fName", "lastName" -> "lName"),
        "employedWithinBusiness" -> false, "roleForTheBusiness" -> "External Accountant"))
    }

    "be convertible from FE request to DES for roleWithinBusiness" in {
      val FEaboutyouModel = models.fe.declaration.AddPerson("fName", Some("middlename"), "lName", BeneficialShareholder)
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", Some("middlename"), "lName")), true, Some("Beneficial Shareholder"), None, Some("Other"), None)
      Aboutyou.convert(FEaboutyouModel) must be(aboutyouModel)
    }

    "be convertible from FE request to DES for roleForTheBusiness" in {
      val FEaboutyouModel = models.fe.declaration.AddPerson("fName", None, "lName", ExternalAccountant)
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), false, None, None, Some("External Accountant"), None)
      Aboutyou.convert(FEaboutyouModel) must be(aboutyouModel)
    }

    "be convertible from FE request to DES for OtherroleWithinBusiness" in {
      val FEaboutyouModel = models.fe.declaration.AddPerson("fName", None, "lName", Other("Agent"))
      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), false, None, None, Some("Other"), Some("Agent"))
      Aboutyou.convert(FEaboutyouModel) must be(aboutyouModel)
    }
  }
}

class AboutYouRelease7Spec extends PlaySpec {

  "AboutYouRelease7" must {
    "be serialisable for roleWithinBusiness" in {

      val json = Json.obj(
        "individualDetails" -> Json.obj("firstName" -> "fName", "lastName" -> "lName"),
        "employedWithinBusiness" -> true,
        "roleWithinBusiness" -> Json.obj(
          "beneficialShareholder" -> true,
          "director" -> true,
          "partner" -> true,
          "internalAccountant" -> true,
          "soleProprietor" -> true,
          "nominatedOfficer" -> true,
          "designatedMember" -> true,
          "other" -> false
        )
      )

      val aboutyouModel = AboutYouRelease7(
        Some(IndividualDetails("fName", None, "lName")),
        true,
        Some(RolesWithinBusiness(true, true, true, true, true, true, true, false, None))
      )

      AboutYouRelease7.format.writes(aboutyouModel) must be(json)
      AboutYouRelease7.format.reads(json) must be(JsSuccess(aboutyouModel))

    }

    "be serialisable for roleForTheBusiness" in {

      val json = Json.obj(
        "individualDetails" -> Json.obj("firstName" -> "fName", "lastName" -> "lName"),
        "employedWithinBusiness" -> false,
        "roleForTheBusiness" -> Json.obj(
          "externalAccountant" -> true,
          "other" -> false
        )
      )

      val aboutyouModel = AboutYouRelease7(Some(IndividualDetails("fName", None, "lName")), false, None, Some(RoleForTheBusiness(true, false, None)))

      AboutYouRelease7.format.writes(aboutyouModel) must be(json)
      AboutYouRelease7.format.reads(json) must be(JsSuccess(aboutyouModel))

    }




    //    "be convertible from FE request to DES for roleWithinBusiness" in {
    //      val FEaboutyouModel = models.fe.declaration.AddPerson("fName", Some("middlename"), "lName", BeneficialShareholder)
    //
    //
    //
    //      val aboutyouModel = AboutYouRelease7(
    //        Some(IndividualDetails("fName", None, "lName")),
    //        true,
    //        Some(RolesWithinBusiness(true,true,false,false,false,false,false, false, None))
    //      )
    //
    //
    //
    //      Aboutyou.convert(FEaboutyouModel) must be(aboutyouModel)
    //    }
    //
    //    "be convertible from FE request to DES for roleForTheBusiness" in {
    //      val FEaboutyouModel = models.fe.declaration.AddPerson("fName", None, "lName", ExternalAccountant)
    //      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), false, None, None, Some("External Accountant"), None)
    //      Aboutyou.convert(FEaboutyouModel) must be(aboutyouModel)
    //    }
    //
    //    "be convertible from FE request to DES for OtherroleWithinBusiness" in {
    //      val FEaboutyouModel = models.fe.declaration.AddPerson("fName", None, "lName", Other("Agent"))
    //      val aboutyouModel = Aboutyou(Some(IndividualDetails("fName", None, "lName")), false, None, None, Some("Other"), Some("Agent"))
    //      Aboutyou.convert(FEaboutyouModel) must be(aboutyouModel)
    //    }
  }
}