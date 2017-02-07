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

import models.fe.declaration.AddPersonRelease7
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

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

    "convert to the des model correctly" when {
      "given frontend model without external accountant or Other" in {

        val desModel = AboutYouRelease7(
          Some(IndividualDetails("fName", None, "lName")),
          true,
          Some(RolesWithinBusiness(true, true, true, true, true, true, true, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        val frontendModel = AddPersonRelease7("fName", None, "lName",
          models.fe.declaration.release7.RoleWithinBusiness(Set(
            models.fe.declaration.release7.BeneficialShareholder,
            models.fe.declaration.release7.Director,
            models.fe.declaration.release7.Partner,
            models.fe.declaration.release7.InternalAccountant,
            models.fe.declaration.release7.SoleProprietor,
            models.fe.declaration.release7.NominatedOfficer,
            models.fe.declaration.release7.DesignatedMember
          ))
        )

        AboutYouRelease7.convert(frontendModel) must be(desModel)
      }

      "given frontend model with external accountant and Other" in {

        val desModel = AboutYouRelease7(
          Some(IndividualDetails("fName", None, "lName")),
          false,
          Some(RolesWithinBusiness(true, true, true, true, true, true, true, true, Some("some other text"))),
          Some(RoleForTheBusiness(true, true, Some("some other text")))
        )

        val frontendModel = AddPersonRelease7("fName", None, "lName",
          models.fe.declaration.release7.RoleWithinBusiness(Set(
            models.fe.declaration.release7.BeneficialShareholder,
            models.fe.declaration.release7.Director,
            models.fe.declaration.release7.Partner,
            models.fe.declaration.release7.InternalAccountant,
            models.fe.declaration.release7.ExternalAccountant,
            models.fe.declaration.release7.SoleProprietor,
            models.fe.declaration.release7.NominatedOfficer,
            models.fe.declaration.release7.DesignatedMember,
            models.fe.declaration.release7.Other("some other text")
          ))
        )

        AboutYouRelease7.convert(frontendModel) must be(desModel)

      }
    }

    "Convert from old model to new release 7 model" in {

      val individualDetails = Some(IndividualDetails("fName", None, "lName"))

      val employedWithinBusiness = false
      val oldModel = Aboutyou(
        individualDetails,
        employedWithinBusiness,
        Some("Beneficial Shareholder"),
        None,
        Some("External Accountant"),
        None
      )

      val release7Model = AboutYouRelease7(individualDetails,
        employedWithinBusiness,
        Some(RolesWithinBusiness(beneficialShareholder = true, false,false,false,false,false,false,false,None)),
        Some(RoleForTheBusiness(externalAccountant = true, false, None))
      )

      AboutYouRelease7.convertToRelease7(oldModel) must be(release7Model)
    }



  }
}