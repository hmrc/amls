/*
 * Copyright 2016 HM Revenue & Customs
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

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}


class RoleWithinBusinessSpec extends PlaySpec with MockitoSugar {

  "JSON" must {

    "Read the json and return the RoleWithinBusiness domain object successfully for the BeneficialShareholder" in {
      val json = Json.obj(
        "roleWithinBusiness" -> "01"
      )
      RoleWithinBusiness.jsonReads.reads(json) must be(JsSuccess(BeneficialShareholder))
    }


    "Read the json and return the RoleWithinBusiness domain object successfully for the Director" in {
      val json = Json.obj(
        "roleWithinBusiness" -> "02"
      )
      RoleWithinBusiness.jsonReads.reads(json) must be(JsSuccess(Director))
    }


    "Read the json and return the RoleWithinBusiness domain object successfully for the ExternalAccountant" in {
      val json = Json.obj(
        "roleWithinBusiness" -> "03"
      )
      RoleWithinBusiness.jsonReads.reads(json) must be(JsSuccess(ExternalAccountant))
    }

    "Read the json and return the RoleWithinBusiness domain object successfully for the InternalAccountant" in {
      val json = Json.obj(
        "roleWithinBusiness" -> "04"
      )
      RoleWithinBusiness.jsonReads.reads(json) must be(JsSuccess(InternalAccountant))
    }

    "Read the json and return the RoleWithinBusiness domain object successfully for the NominatedOfficer" in {
      val json = Json.obj(
        "roleWithinBusiness" -> "05"
      )
      RoleWithinBusiness.jsonReads.reads(json) must be(JsSuccess(NominatedOfficer))
    }

    "Read the json and return the RoleWithinBusiness domain object successfully for the Partner" in {
      val json = Json.obj(
        "roleWithinBusiness" -> "06"
      )
      RoleWithinBusiness.jsonReads.reads(json) must be(JsSuccess(Partner))
    }


    "Read the json and return the RoleWithinBusiness domain object successfully for the SoleProprietor" in {
      val json = Json.obj(
        "roleWithinBusiness" -> "07"
      )
      RoleWithinBusiness.jsonReads.reads(json) must be(JsSuccess(SoleProprietor))
    }


    "Read the json and return the given `other` value" in {

      val json = Json.obj(
        "roleWithinBusiness" -> "08",
        "roleWithinBusinessOther" -> "any other value"
      )

      Json.fromJson[RoleWithinBusiness](json) must
        be(JsSuccess(Other("any other value"), JsPath \ "roleWithinBusinessOther"))
    }

    "Read the json and return error if an invalid value is found" in {
      val json = Json.obj(
        "roleWithinBusiness" -> "09"
      )
      RoleWithinBusiness.jsonReads.reads(json) must be(JsError((JsPath \ "roleWithinBusiness") -> ValidationError("error.invalid")))
    }



    "Write the json successfully from the BeneficialShareholder domain object created" in {

      val roleWithinBusiness: RoleWithinBusiness = BeneficialShareholder
      val json = Json.obj(
        "roleWithinBusiness" -> "01"
      )
      RoleWithinBusiness.jsonWrites.writes(roleWithinBusiness) must be(json)
    }


    "Write the json successfully from the Director domain object created" in {

      val roleWithinBusiness: RoleWithinBusiness = Director
      val json = Json.obj(
        "roleWithinBusiness" -> "02"
      )
      RoleWithinBusiness.jsonWrites.writes(roleWithinBusiness) must be(json)
    }

    "Write the json successfully from the ExternalAccountant domain object created" in {

      val roleWithinBusiness: RoleWithinBusiness = ExternalAccountant
      val json = Json.obj(
        "roleWithinBusiness" -> "03"
      )
      RoleWithinBusiness.jsonWrites.writes(roleWithinBusiness) must be(json)
    }

    "Write the json successfully from the InternalAccountant domain object created" in {

      val roleWithinBusiness: RoleWithinBusiness = InternalAccountant
      val json = Json.obj(
        "roleWithinBusiness" -> "04"
      )
      RoleWithinBusiness.jsonWrites.writes(roleWithinBusiness) must be(json)
    }

    "Write the json successfully from the NominatedOfficer domain object created" in {

      val roleWithinBusiness: RoleWithinBusiness = NominatedOfficer
      val json = Json.obj(
        "roleWithinBusiness" -> "05"
      )
      RoleWithinBusiness.jsonWrites.writes(roleWithinBusiness) must be(json)
    }

    "Write the json successfully from the Partner domain object created" in {

      val roleWithinBusiness: RoleWithinBusiness = Partner
      val json = Json.obj(
        "roleWithinBusiness" -> "06"
      )
      RoleWithinBusiness.jsonWrites.writes(roleWithinBusiness) must be(json)
    }

    "Write the json successfully from the SoleProprietor domain object created" in {

      val roleWithinBusiness: RoleWithinBusiness = SoleProprietor
      val json = Json.obj(
        "roleWithinBusiness" -> "07"
      )
      RoleWithinBusiness.jsonWrites.writes(roleWithinBusiness) must be(json)
    }

    "Write the json successfully from the Other domain object created" in {

      val roleWithinBusiness: RoleWithinBusiness = Other("any other value")
      val json = Json.obj(
        "roleWithinBusiness" -> "08",
        "roleWithinBusinessOther" -> "any other value"

      )
      RoleWithinBusiness.jsonWrites.writes(roleWithinBusiness) must be(json)
    }


  }


}
