/*
 * Copyright 2019 HM Revenue & Customs
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

import models.des.businessactivities._
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

import scala.language.postfixOps

class HowManyEmployeesSpec extends PlaySpec {

  "HowManyEmployees" must {

    "JSON read" must {

      "fail to validate when given employeeCountAMLSSupervision is missing" in {
        val json = Json.obj("employeeCount" -> "12345678901")
        Json.fromJson[HowManyEmployees](json) must
          be(JsError((JsPath \ "employeeCountAMLSSupervision") -> ValidationError("error.path.missing")))
      }

      "fail to validate when given employeeCount is missing" in {
        val json = Json.obj("employeeCountAMLSSupervision" -> "12345678901")
        Json.fromJson[HowManyEmployees](json) must
          be(JsError((JsPath \ "employeeCount") -> ValidationError("error.path.missing")))
      }

      "successfully read the JSON value to create the Model" in {
        val json = Json.obj("employeeCount" -> "12345678901", "employeeCountAMLSSupervision" -> "123456789")
        Json.fromJson[HowManyEmployees](json) must
          be(JsSuccess(HowManyEmployees("12345678901", "123456789"), JsPath))
      }
    }

    "JSON write the correct value" must {

      "be populated in the JSON from the Model" in {
        val howManyEmployees = HowManyEmployees("12345678901", "123456789")
        Json.toJson(howManyEmployees) must
          be(Json.obj("employeeCount" -> "12345678901", "employeeCountAMLSSupervision" -> "123456789"))
      }
    }

    "convert des ro frontend model successfully" in {

      val all = BusinessActivitiesAll(
        None,None,None,
        BusinessActivityDetails(false, None),
        Some(FranchiseDetails(true, Some(Seq("FranchiserName1", "FranchiserName2")))),
        Some("12345678901"),
        Some("11223344556"),
        NonUkResidentCustDetails(true, Some(Seq("AD", "GB"))),
        AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("CommercialPackageName")))),
        true,
        true,
        None,
        None
      )
      HowManyEmployees.conv(all) must be(Some(HowManyEmployees("12345678901", "11223344556")))
    }

    "convert des ro frontend model successfully when inputs are none" in {

      val all = BusinessActivitiesAll(
        None,None,None,
        BusinessActivityDetails(false, None),
        Some(FranchiseDetails(true, Some(Seq("FranchiserName1", "FranchiserName2")))),
        None,
        None,
        NonUkResidentCustDetails(true, Some(Seq("AD", "GB"))),
        AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("CommercialPackageName")))),
        true,
        true,
        None,
        None
      )
      HowManyEmployees.conv(all) must be(None)
    }
  }
}
