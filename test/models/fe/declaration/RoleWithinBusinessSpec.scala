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

package models.fe.declaration

import models.des.aboutyou.{RoleForTheBusiness, RolesWithinBusiness, IndividualDetails, AboutYouRelease7}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsPath, JsError, JsSuccess, Json}

class RoleWithinBusinessSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  "JSON" must {

    "Read the json and return the RoleWithinBusiness domain object successfully" when {
      "given a BeneficialShareholder value" in {

        val json = Json.obj(
          "roleWithinBusiness" -> Set("BeneficialShareholder")
        )

        val model = RoleWithinBusiness(Set(BeneficialShareholder))

        RoleWithinBusiness.jsonReads.reads(json) must be(JsSuccess(model))
      }

      "given a set of all values" in {

        val json = Json.obj(
          "roleWithinBusiness" -> Set(
            "BeneficialShareholder",
            "Director",
            "Partner",
            "InternalAccountant",
            "ExternalAccountant",
            "SoleProprietor",
            "NominatedOfficer",
            "DesignatedMember",
            "Other"
          ),
          "otherDetails" -> "some other text"
        )

        val model = RoleWithinBusiness(Set(
          Partner,
          SoleProprietor,
          DesignatedMember,
          NominatedOfficer,
          Director,
          BeneficialShareholder,
          Other("some other text"),
          ExternalAccountant,
          InternalAccountant
        ))

        RoleWithinBusiness.jsonReads.reads(json) must be(JsSuccess(model))

      }
    }

    "respond with a validation error if invalid Json is provided" in {
      val json = Json.obj(
        "roleWithinBusiness" -> Set("invalid")
      )

      val model = RoleWithinBusiness(Set(BeneficialShareholder))

      RoleWithinBusiness.jsonReads.reads(json) must be(
        JsError((JsPath \ "roleWithinBusiness") -> ValidationError("error.invalid"))
      )
    }

    "Write the json successfully" when {
      "given a BeneficialShareholder value" in {

        val json = Json.obj(
          "roleWithinBusiness" -> Set("BeneficialShareholder")
        )

        val model = RoleWithinBusiness(Set(BeneficialShareholder))

        RoleWithinBusiness.jsonWrite.writes(model) must be(json)
      }

      "given a set of all values" in {

        val json = Json.obj(
          "roleWithinBusiness" -> Seq(
            "Other",
            "Partner",
            "SoleProprietor",
            "DesignatedMember",
            "NominatedOfficer",
            "Director",
            "BeneficialShareholder",
            "ExternalAccountant",
            "InternalAccountant"
          ),
          "otherDetails" -> "Some other text"
        )

        val model = RoleWithinBusiness(Set(
          Partner,
          SoleProprietor,
          DesignatedMember,
          NominatedOfficer,
          Director,
          BeneficialShareholder,
          Other("Some other text"),
          ExternalAccountant,
          InternalAccountant
        ))

        RoleWithinBusiness.jsonWrite.writes(model) must equal(json)
      }
    }
  }

  "RoleWithinBusinessRelease7" must {
    "convert from des model" in {

      val desModel = AboutYouRelease7(
        Some(IndividualDetails("firstName", None, "lastName")),
        true,
        Some(RolesWithinBusiness(true,true,true,true,true,true,true,false,None)),
        Some(RoleForTheBusiness(true,true,Some("Some other text")))
      )

      val feModel = RoleWithinBusiness(Set(
        Partner,
        SoleProprietor,
        DesignatedMember,
        NominatedOfficer,
        Director,
        BeneficialShareholder,
        Other("Some other text"),
        ExternalAccountant,
        InternalAccountant
      ))


      RoleWithinBusiness.convert(desModel) must be(feModel)

    }
  }

}
