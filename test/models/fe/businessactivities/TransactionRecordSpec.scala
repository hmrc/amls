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

package models.fe.businessactivities

import models.des.businessactivities.{ExpectedAMLSTurnover => DesExpectedAMLSTurnover,_}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}


class TransactionRecordSpec extends PlaySpec with MockitoSugar {

  "TransactionType" must {

    "JSON validation" must {

      "successfully validate given values" in {
        val json =  Json.obj("isRecorded" -> true,
          "transactions" -> Seq("01","02"))

        Json.fromJson[TransactionRecord](json) must
          be(JsSuccess(TransactionRecordYes(Set(Paper, DigitalSpreadsheet))))
      }

      "successfully validate given values with option No" in {
        val json =  Json.obj("isRecorded" -> false)

        Json.fromJson[TransactionRecord](json) must
          be(JsSuccess(TransactionRecordNo))
      }

      "successfully validate given values with option Digital software" in {
        val json =  Json.obj("isRecorded" -> true,
          "transactions" -> Seq("03", "02"),
        "digitalSoftwareName" -> "test")

        Json.fromJson[TransactionRecord](json) must
          be(JsSuccess(TransactionRecordYes(Set(DigitalSoftware("test"), DigitalSpreadsheet))))
      }

      "fail when on path is missing" in {
        Json.fromJson[TransactionRecord](Json.obj("isRecorded" -> true,
          "transaction" -> Seq("01"))) must
          be(JsError((JsPath \ "transactions") -> ValidationError("error.path.missing")))
      }

      "fail when on invalid data" in {
        Json.fromJson[TransactionRecord](Json.obj("isRecorded" -> true,"transactions" -> Seq("40"))) must
          be(JsError((JsPath \ "transactions") -> ValidationError("error.invalid")))
      }

      "write valid data in using json write" in {
        Json.toJson[TransactionRecord](TransactionRecordYes(Set(Paper, DigitalSoftware("test657")))) must be (Json.obj("isRecorded" -> true,
        "transactions" -> Seq("01", "03"),
          "digitalSoftwareName" -> "test657"
        ))
      }

      "write valid data in using json write with Option No" in {
        Json.toJson[TransactionRecord](TransactionRecordNo) must be (Json.obj("isRecorded" -> false))
      }
    }

    "convert des model to frontend model successfully" in {
      val businessActivitiesAll = BusinessActivitiesAll(
        Some("2001-01-01"),
        BusinessActivityDetails(true, Some(DesExpectedAMLSTurnover(Some("11122233344")))),
        Some(FranchiseDetails(true, Some(Seq("FranchiserName1", "FranchiserName2")))),
        Some("12345678901"),
        Some("11223344556"),
        NonUkResidentCustDetails(true, Some(Seq("AD", "GB"))),
        AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("CommercialPackageName")))),
        true,
        true,
        Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true, true)))),
        MlrAdvisor(false, None))

      TransactionRecord.conv(businessActivitiesAll) must be(Some(TransactionRecordYes(Set(Paper,
        DigitalSpreadsheet, DigitalSoftware("CommercialPackageName")))))
    }

    "convert des model to frontend model successfully when countries option is none" in {
      val businessActivitiesAll = BusinessActivitiesAll(
        Some("2001-01-01"),
        BusinessActivityDetails(true, Some(DesExpectedAMLSTurnover(Some("11122233344")))),
        Some(FranchiseDetails(true, Some(Seq("FranchiserName1", "FranchiserName2")))),
        Some("12345678901"),
        Some("11223344556"),
        NonUkResidentCustDetails(false, None),
        AuditableRecordsDetails("No", None),
        true,
        true,
        Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true, true)))),
        MlrAdvisor(false, None))

      TransactionRecord.conv(businessActivitiesAll) must be(Some(TransactionRecordNo))
    }
  }
}
