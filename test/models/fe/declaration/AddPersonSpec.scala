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

import models.des.aboutyou.{IndividualDetails, Aboutyou}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class AddPersonSpec extends PlaySpec with MockitoSugar {

  "JSON" must {

    "Read the json and return the AddPerson domain object successfully" in {

      val json = Json.obj(
        "firstName" -> "name",
        "middleName" -> "some",
        "lastName" -> "surname",
        "roleWithinBusiness" -> "02"
      )

      AddPerson.jsonReads.reads(json) must be(JsSuccess(AddPerson("name", Some("some"), "surname", Director)))
    }

    "Write the json successfully from the AddPerson domain object created" in {

      val addPerson = AddPerson("name", Some("some"), "surname", Director)

      val json = Json.obj(
        "firstName" -> "name",
        "middleName" -> "some",
        "lastName" -> "surname",
        "roleWithinBusiness" -> "02"
      )

      AddPerson.jsonWrites.writes(addPerson) must be(json)
    }

    "convert des model to frontend model:roleWithinBusiness" in {

      val desAboutYou = Aboutyou(
        Some(IndividualDetails(
          "FirstName",
          Some("MiddleName"),
          "LastName")),
        true,
        Some("Beneficial Shareholder"),
        None,
        Some("Other"),
        Some("SpecifyOtherRoleForBusiness")
      )

      AddPerson.conv(desAboutYou) must be(AddPerson("FirstName",Some("MiddleName"),"LastName", BeneficialShareholder))
    }

    "convert des model to frontend model:roleForTheBusiness" in {

      val desAboutYou = Aboutyou(
        Some(IndividualDetails(
          "FirstName",
          Some("MiddleName"),
          "LastName")),
        false,
        Some("Beneficial Shareholder"),
        None,
        Some("External Accountant"),
        None
      )

      AddPerson.conv(desAboutYou) must be(AddPerson("FirstName",Some("MiddleName"),"LastName", ExternalAccountant))
    }

    "convert des model to frontend model:Other" in {

      val desAboutYou = Aboutyou(
        Some(IndividualDetails(
          "FirstName",
          Some("MiddleName"),
          "LastName")),
        false,
        Some("Beneficial Shareholder"),
        None,
        None,
        Some("Other")
      )

      AddPerson.conv(desAboutYou) must be(AddPerson("FirstName",Some("MiddleName"),"LastName", Other("Other")))
    }
  }
}


class AddPersonRelease7Spec extends PlaySpec with MockitoSugar {

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

        val model = AddPersonRelease7(
          "name", Some("some"), "surname",
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

        AddPersonRelease7.jsonReads.reads(json) must be(JsSuccess(model))
        AddPersonRelease7.jsonWrites.writes(model) must be(json)

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
          "otherDetails" -> "Other details here"
        )


        val model = AddPersonRelease7(
          "name", Some("some"), "surname",
          models.fe.declaration.release7.RoleWithinBusiness(Set(
            models.fe.declaration.release7.BeneficialShareholder,
            models.fe.declaration.release7.Director,
            models.fe.declaration.release7.Partner,
            models.fe.declaration.release7.InternalAccountant,
            models.fe.declaration.release7.ExternalAccountant,
            models.fe.declaration.release7.SoleProprietor,
            models.fe.declaration.release7.NominatedOfficer,
            models.fe.declaration.release7.DesignatedMember,
            models.fe.declaration.release7.Other("Other details here")
          ))
        )

        AddPersonRelease7.jsonReads.reads(json) must be(JsSuccess(model))

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
          "otherDetails" -> "Other details here"
        )


        val model = AddPersonRelease7(
          "name", Some("some"), "surname",
          models.fe.declaration.release7.RoleWithinBusiness(Set(
            models.fe.declaration.release7.BeneficialShareholder,
            models.fe.declaration.release7.Director,
            models.fe.declaration.release7.Partner,
            models.fe.declaration.release7.InternalAccountant,
            models.fe.declaration.release7.ExternalAccountant,
            models.fe.declaration.release7.SoleProprietor,
            models.fe.declaration.release7.NominatedOfficer,
            models.fe.declaration.release7.DesignatedMember,
            models.fe.declaration.release7.Other("Other details here")
          ))
        )

        AddPersonRelease7.jsonWrites.writes(model) must be(json)

      }
    }

  }
}
