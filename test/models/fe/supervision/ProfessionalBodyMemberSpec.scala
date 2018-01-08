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

package models.fe.supervision

import models.des.supervision.{MemberOfProfessionalBody, ProfessionalBodyDesMember, ProfessionalBodyDetails}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class ProfessionalBodyMemberSpec extends PlaySpec with MockitoSugar {

  "JSON validation" must {

      "validate given values" in {
        val json =  Json.obj("isAMember" -> true,
          "businessType" -> Seq("01","02"))

        Json.fromJson[ProfessionalBodyMember](json) must
          be(JsSuccess(ProfessionalBodyMemberYes(Set(AccountingTechnicians, CharteredCertifiedAccountants))))
      }

      "validate given values with option No" in {
        val json =  Json.obj("isAMember" -> false)

        Json.fromJson[ProfessionalBodyMember](json) must
          be(JsSuccess(ProfessionalBodyMemberNo))
      }

      "validate given values with option Digital software" in {
        val json =  Json.obj("isAMember" -> true,
          "businessType" -> Seq("14", "12"),
        "specifyOtherBusiness" -> "test")

        Json.fromJson[ProfessionalBodyMember](json) must
          be(JsSuccess(ProfessionalBodyMemberYes(Set(Other("test"), AssociationOfBookkeepers))))
      }

      "fail when on path is missing" in {
        Json.fromJson[ProfessionalBodyMember](Json.obj("isAMember" -> true,
          "transaction" -> Seq("01"))) must
          be(JsError((JsPath \ "businessType") -> ValidationError("error.path.missing")))
      }

      "fail when on invalid data" in {
        Json.fromJson[ProfessionalBodyMember](Json.obj("isAMember" -> true,"businessType" -> Seq("40"))) must
          be(JsError((JsPath \ "businessType") -> ValidationError("error.invalid")))
      }

      "write valid data in using json write" in {
        Json.toJson[ProfessionalBodyMember](ProfessionalBodyMemberYes(Set(AccountantsScotland, Other("test657")))) must be (Json.obj("isAMember" -> true,
        "businessType" -> Seq("09", "14"),
          "specifyOtherBusiness" -> "test657"
        ))
      }

      "write valid data in using json write with Option No" in {
        Json.toJson[ProfessionalBodyMember](ProfessionalBodyMemberNo) must be (Json.obj("isAMember" -> false))
      }

  }

  "convert des to frontend model successfully" in {
    val desModel = Some(ProfessionalBodyDetails(
      true,
      Some("DetailsIfFinedWarned"),
      Some(ProfessionalBodyDesMember(
        true,
        Some(MemberOfProfessionalBody(
          true, true, true, true, true, true, true, true, true, true, true, true, true, false, None
        ))
      ))
    ))

    val convertedModel = Some(ProfessionalBodyMemberYes(Set(AccountantsIreland,
      CharteredCertifiedAccountants,
      AssociationOfBookkeepers, AccountantsEnglandandWales,
      Bookkeepers, AccountingTechnicians, TaxationTechnicians,
      InternationalAccountants, LawSociety, InstituteOfTaxation, AccountantsScotland,
      FinancialAccountants, ManagementAccountants)))

    ProfessionalBodyMember.conv(desModel) must be(convertedModel)
  }

  "convert des to frontend model successfully when MemberOfProfessionalBody is none" in {
    val desModel = Some(ProfessionalBodyDetails(
      true,
      Some("DetailsIfFinedWarned"),
      Some(ProfessionalBodyDesMember(
        false,
        None)
      ))
    )
    ProfessionalBodyMember.conv(desModel) must be(Some(ProfessionalBodyMemberNo))
  }

  "convert des to frontend model successfully when input is none" in {

    ProfessionalBodyMember.conv(None) must be(Some(ProfessionalBodyMemberNo))
  }

  "convert des to frontend model successfully when ProfessionalBodyDesMember is none" in {
    val desModel = Some(ProfessionalBodyDetails(
      true,
      Some("DetailsIfFinedWarned"),
      None
    ))
    ProfessionalBodyMember.conv(desModel) must be(None)
  }

  "convOther must return NOne when input is false" in {
    ProfessionalBodyMember.convOther(false, "") must be(None)
  }
}
