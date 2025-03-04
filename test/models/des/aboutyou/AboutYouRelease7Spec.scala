/*
 * Copyright 2024 HM Revenue & Customs
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

import models.fe.declaration.AddPerson
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class AboutYouRelease7Spec extends PlaySpec {

  "AboutYouRelease7" must {
    "be serialisable for roleWithinBusiness" in {

      val json = Json.obj(
        "individualDetails"      -> Json.obj("firstName" -> "fName", "lastName" -> "lName"),
        "employedWithinBusiness" -> true,
        "roleWithinBusiness"     -> Json.obj(
          "beneficialShareholder" -> true,
          "director"              -> true,
          "partner"               -> true,
          "internalAccountant"    -> true,
          "soleProprietor"        -> true,
          "nominatedOfficer"      -> true,
          "designatedMember"      -> true,
          "other"                 -> false
        )
      )

      val aboutyouModel = AboutYouRelease7(
        Some(IndividualDetails("fName", None, "lName")),
        true,
        Some(RolesWithinBusiness(true, true, true, true, true, true, true, false, None))
      )

      AboutYouRelease7.format.writes(aboutyouModel) must be(json)
      AboutYouRelease7.format.reads(json)           must be(JsSuccess(aboutyouModel))

    }

    "be serialisable for roleForTheBusiness" in {

      val json = Json.obj(
        "individualDetails"      -> Json.obj("firstName" -> "fName", "lastName" -> "lName"),
        "employedWithinBusiness" -> false,
        "roleForTheBusiness"     -> Json.obj(
          "externalAccountant" -> true,
          "other"              -> false
        )
      )

      val aboutyouModel = AboutYouRelease7(
        Some(IndividualDetails("fName", None, "lName")),
        false,
        None,
        Some(RoleForTheBusiness(true, false, None))
      )

      AboutYouRelease7.format.writes(aboutyouModel) must be(json)
      AboutYouRelease7.format.reads(json)           must be(JsSuccess(aboutyouModel))

    }

    "convert to the des model correctly" when {
      "given frontend model without external accountant or Other" in {

        val desModel = AboutYouRelease7(
          Some(IndividualDetails("fName", None, "lName")),
          true,
          Some(RolesWithinBusiness(true, true, true, true, true, true, true, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        val frontendModel = AddPerson(
          "fName",
          None,
          "lName",
          models.fe.declaration.RoleWithinBusiness(
            Set(
              models.fe.declaration.BeneficialShareholder,
              models.fe.declaration.Director,
              models.fe.declaration.Partner,
              models.fe.declaration.InternalAccountant,
              models.fe.declaration.SoleProprietor,
              models.fe.declaration.NominatedOfficer,
              models.fe.declaration.DesignatedMember
            )
          )
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

        val frontendModel = AddPerson(
          "fName",
          None,
          "lName",
          models.fe.declaration.RoleWithinBusiness(
            Set(
              models.fe.declaration.BeneficialShareholder,
              models.fe.declaration.Director,
              models.fe.declaration.Partner,
              models.fe.declaration.InternalAccountant,
              models.fe.declaration.ExternalAccountant,
              models.fe.declaration.SoleProprietor,
              models.fe.declaration.NominatedOfficer,
              models.fe.declaration.DesignatedMember,
              models.fe.declaration.Other("some other text")
            )
          )
        )

        AboutYouRelease7.convert(frontendModel) must be(desModel)

      }
    }

    "Convert from old model to new release 7 model" when {
      "given a Beneficial Shareholder and external accountant" in {

        val individualDetails = Some(IndividualDetails("fName", None, "lName"))

        val employedWithinBusiness = false
        val oldModel               = Aboutyou(
          individualDetails,
          employedWithinBusiness,
          Some("Beneficial Shareholder"),
          None,
          Some("External Accountant"),
          None
        )

        val release7Model = AboutYouRelease7(
          individualDetails,
          employedWithinBusiness,
          Some(
            RolesWithinBusiness(beneficialShareholder = true, false, false, false, false, false, false, false, None)
          ),
          Some(RoleForTheBusiness(externalAccountant = true, false, None))
        )

        AboutYouRelease7.convertToRelease7(oldModel) must be(release7Model)
      }

      "given a Director" in {

        val individualDetails = Some(IndividualDetails("fName", None, "lName"))

        val employedWithinBusiness = false
        val oldModel               = Aboutyou(individualDetails, employedWithinBusiness, Some("Director"), None, None, None)

        val release7Model = AboutYouRelease7(
          individualDetails,
          employedWithinBusiness,
          Some(RolesWithinBusiness(false, director = true, false, false, false, false, false, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        AboutYouRelease7.convertToRelease7(oldModel) must be(release7Model)
      }
      "given a Partner" in {

        val individualDetails = Some(IndividualDetails("fName", None, "lName"))

        val employedWithinBusiness = false
        val oldModel               = Aboutyou(individualDetails, employedWithinBusiness, Some("Partner"), None, None, None)

        val release7Model = AboutYouRelease7(
          individualDetails,
          employedWithinBusiness,
          Some(RolesWithinBusiness(false, false, partner = true, false, false, false, false, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        AboutYouRelease7.convertToRelease7(oldModel) must be(release7Model)
      }
      "given an Internal Accountant" in {

        val individualDetails = Some(IndividualDetails("fName", None, "lName"))

        val employedWithinBusiness = false
        val oldModel               =
          Aboutyou(individualDetails, employedWithinBusiness, Some("Internal Accountant"), None, None, None)

        val release7Model = AboutYouRelease7(
          individualDetails,
          employedWithinBusiness,
          Some(RolesWithinBusiness(false, false, false, internalAccountant = true, false, false, false, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        AboutYouRelease7.convertToRelease7(oldModel) must be(release7Model)
      }
      "given a Sole Proprietor" in {

        val individualDetails = Some(IndividualDetails("fName", None, "lName"))

        val employedWithinBusiness = false
        val oldModel               = Aboutyou(individualDetails, employedWithinBusiness, Some("Sole Proprietor"), None, None, None)

        val release7Model = AboutYouRelease7(
          individualDetails,
          employedWithinBusiness,
          Some(RolesWithinBusiness(false, false, false, false, soleProprietor = true, false, false, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        AboutYouRelease7.convertToRelease7(oldModel) must be(release7Model)
      }

      "given a Nominated Officer" in {

        val individualDetails = Some(IndividualDetails("fName", None, "lName"))

        val employedWithinBusiness = false
        val oldModel               = Aboutyou(individualDetails, employedWithinBusiness, Some("Nominated Officer"), None, None, None)

        val release7Model = AboutYouRelease7(
          individualDetails,
          employedWithinBusiness,
          Some(RolesWithinBusiness(false, false, false, false, false, nominatedOfficer = true, false, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        AboutYouRelease7.convertToRelease7(oldModel) must be(release7Model)
      }
      "given a Designated Member" in {

        val individualDetails = Some(IndividualDetails("fName", None, "lName"))

        val employedWithinBusiness = false
        val oldModel               = Aboutyou(individualDetails, employedWithinBusiness, Some("Designated Member"), None, None, None)

        val release7Model = AboutYouRelease7(
          individualDetails,
          employedWithinBusiness,
          Some(RolesWithinBusiness(false, false, false, false, false, false, designatedMember = true, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        AboutYouRelease7.convertToRelease7(oldModel) must be(release7Model)
      }
      "given a 'Other' role'" in {

        val individualDetails = Some(IndividualDetails("fName", None, "lName"))

        val employedWithinBusiness = false
        val oldModel               =
          Aboutyou(individualDetails, employedWithinBusiness, Some("Other"), Some("Some other text"), None, None)

        val release7Model = AboutYouRelease7(
          individualDetails,
          employedWithinBusiness,
          Some(RolesWithinBusiness(false, false, false, false, false, false, false, true, Some("Some other text"))),
          Some(RoleForTheBusiness(false, false, None))
        )

        AboutYouRelease7.convertToRelease7(oldModel) must be(release7Model)
      }
    }

  }
}
