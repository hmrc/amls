/*
 * Copyright 2018 HM Revenue & Customs
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

import models.des.aboutyou._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsSuccess, Json}

class AddPersonSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  "JSON" must {

    "Read and write the json successfully" when {
      "given no Other and no ExternalAccountant role" in {

        val json = Json.obj(
          "firstName" -> "name",
          "middleName" -> "some",
          "lastName" -> "surname",
          "roleWithinBusiness" -> Seq(
            "Partner",
            "SoleProprietor",
            "DesignatedMember",
            "NominatedOfficer",
            "Director",
            "BeneficialShareholder",
            "InternalAccountant"
          )
        )

        val model = AddPerson(
          "name", Some("some"), "surname",
          models.fe.declaration.RoleWithinBusiness(Set(
            models.fe.declaration.BeneficialShareholder,
            models.fe.declaration.Director,
            models.fe.declaration.Partner,
            models.fe.declaration.InternalAccountant,
            models.fe.declaration.SoleProprietor,
            models.fe.declaration.NominatedOfficer,
            models.fe.declaration.DesignatedMember
          ))
        )

        AddPerson.jsonReads.reads(json) must be(JsSuccess(model))
        AddPerson.jsonWrites.writes(model) must be(json)
      }

      "given Other and ExternalAccountant role for the reads" in {

        val json = Json.obj(
          "firstName" -> "name",
          "middleName" -> "some",
          "lastName" -> "surname",
          "roleWithinBusiness" -> Json.arr(
            "Partner",
            "Other",
            "SoleProprietor",
            "DesignatedMember",
            "NominatedOfficer",
            "Director",
            "BeneficialShareholder",
            "ExternalAccountant",
            "InternalAccountant"
        ),
          "roleWithinBusinessOther" -> "Other details here"
        )


        val model = AddPerson(
          "name", Some("some"), "surname",
          models.fe.declaration.RoleWithinBusiness(Set(
            models.fe.declaration.BeneficialShareholder,
            models.fe.declaration.Director,
            models.fe.declaration.Partner,
            models.fe.declaration.InternalAccountant,
            models.fe.declaration.ExternalAccountant,
            models.fe.declaration.SoleProprietor,
            models.fe.declaration.NominatedOfficer,
            models.fe.declaration.DesignatedMember,
            models.fe.declaration.Other("Other details here")
          ))
        )

        AddPerson.jsonReads.reads(json) must be(JsSuccess(model))

      }

      "given Other and ExternalAccountant role for the writes" in {

        val json = Json.obj(
          "firstName" -> "name",
          "middleName" -> "some",
          "lastName" -> "surname",
          "roleWithinBusiness" -> Json.arr(
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
          "roleWithinBusinessOther" -> "Other details here"
        )


        val model = AddPerson(
          "name", Some("some"), "surname",
          models.fe.declaration.RoleWithinBusiness(Set(
            models.fe.declaration.BeneficialShareholder,
            models.fe.declaration.Director,
            models.fe.declaration.Partner,
            models.fe.declaration.InternalAccountant,
            models.fe.declaration.ExternalAccountant,
            models.fe.declaration.SoleProprietor,
            models.fe.declaration.NominatedOfficer,
            models.fe.declaration.DesignatedMember,
            models.fe.declaration.Other("Other details here")
          ))
        )

        AddPerson.jsonWrites.writes(model) must be(json)

      }
    }

    "convert des model to frontend model" when {
      "given des model without external accountant or Other" in {
        val desModel = AboutYouRelease7(
          Some(IndividualDetails("fName", None, "lName")),
          true,
          Some(RolesWithinBusiness(true, true, true, true, true, true, true, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        val frontendModel = AddPerson("fName", None, "lName",
          models.fe.declaration.RoleWithinBusiness(Set(
            models.fe.declaration.BeneficialShareholder,
            models.fe.declaration.Director,
            models.fe.declaration.Partner,
            models.fe.declaration.InternalAccountant,
            models.fe.declaration.SoleProprietor,
            models.fe.declaration.NominatedOfficer,
            models.fe.declaration.DesignatedMember
          ))
        )

        AddPerson.convert(desModel) must be(frontendModel)
      }

      "given des model where everything is false" in {
        val desModel = AboutYouRelease7(
          Some(IndividualDetails("fName", None, "lName")),
          true,
          Some(RolesWithinBusiness(false, false, false, false, false, false, false, false, None)),
          Some(RoleForTheBusiness(false, false, None))
        )

        val frontendModel = AddPerson("fName", None, "lName",
          models.fe.declaration.RoleWithinBusiness(Set())
        )

        AddPerson.convert(desModel) must be(frontendModel)

      }

      "given des model where roles within and for are None" in {
        val desModel = AboutYouRelease7(
          Some(IndividualDetails("fName", None, "lName")),
          true,
          None,
          None
        )

        val frontendModel = AddPerson("fName", None, "lName",
          models.fe.declaration.RoleWithinBusiness(Set())
        )

        AddPerson.convert(desModel) must be(frontendModel)

      }

      "given des model where there is None for individual details" in {
        val desModel = AboutYouRelease7(
          None,
          true,
          None,
          None
        )

        val frontendModel = AddPerson("", None, "",
          models.fe.declaration.RoleWithinBusiness(Set())
        )

        AddPerson.convert(desModel) must be(frontendModel)

      }
    }

  }
}
