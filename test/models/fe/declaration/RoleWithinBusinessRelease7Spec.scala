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

package models.fe.declaration.release7

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class RoleWithinBusinessRelease7Spec extends PlaySpec with MockitoSugar {

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

}
