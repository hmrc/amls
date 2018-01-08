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

class BusinessTypesSpec extends PlaySpec with MockitoSugar {

  "JSON reads" must {

    "validate given values" in {
      val json =  Json.obj("businessType" -> Seq("01","02"))

      Json.fromJson[BusinessTypes](json) must
        be(JsSuccess(BusinessTypes(Set(AccountingTechnicians, CharteredCertifiedAccountants))))
    }

    "validate given values with option Digital software" in {
      val json =  Json.obj(
        "businessType" -> Seq("14", "12"),
        "specifyOtherBusiness" -> "test"
      )

      Json.fromJson[BusinessTypes](json) must
        be(JsSuccess(BusinessTypes(Set(Other("test"), AssociationOfBookkeepers))))
    }

    "fail when on path is missing" in {
      Json.fromJson[BusinessTypes](Json.obj()) must
        be(JsError((JsPath \ "businessType") -> ValidationError("error.path.missing")))
    }

    "fail when on invalid data" in {
      Json.fromJson[BusinessTypes](Json.obj("businessType" -> Seq("40"))) must
        be(JsError((JsPath \ "businessType") -> ValidationError("error.invalid")))
    }

  }

  "JSON writes" must {

    "write valid data in using json write" in {
      Json.toJson[BusinessTypes](BusinessTypes(Set(AccountantsScotland, Other("test657")))) must
        be (Json.obj(
          "businessType" -> Seq("09", "14"),
          "specifyOtherBusiness" -> "test657"
        ))
    }

  }

  "DES to frontend conversion" must {

    "define BusinessTypes" in {
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

      val convertedModel = Some(BusinessTypes(Set(
        AccountantsIreland,
        CharteredCertifiedAccountants,
        AssociationOfBookkeepers,
        AccountantsEnglandandWales,
        Bookkeepers,
        AccountingTechnicians,
        TaxationTechnicians,
        InternationalAccountants,
        LawSociety,
        InstituteOfTaxation,
        AccountantsScotland,
        FinancialAccountants,
        ManagementAccountants
      )))

      BusinessTypes.conv(desModel) must be(convertedModel)
    }

  }

}
