/*
 * Copyright 2022 HM Revenue & Customs
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

import models.des.businessactivities.{ExpectedAMLSTurnover => DesExpectedAMLSTurnover, _}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class CustomersOutsideUKSpec extends PlaySpec {

  "CustomersOutsideUK" must {

    "round trip through Json correctly" in {

      val model: CustomersOutsideUK = CustomersOutsideUK(true, Some(Seq("GB")))

      Json.fromJson[CustomersOutsideUK](Json.toJson(model)) mustBe JsSuccess(model)
    }

    "convert des model to frontend model successfully" in {
      val businessActivitiesAll = BusinessActivitiesAll(None,
        Some("2001-01-01"),
        None,
        BusinessActivityDetails(true, Some(DesExpectedAMLSTurnover(Some("11122233344")))),
        Some(FranchiseDetails(true, Some(Seq("FranchiserName1", "FranchiserName2")))),
        Some("12345678901"),
        Some("11223344556"),
        NonUkResidentCustDetails(true, Some(Seq("AD", "GB"))),
        AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("CommercialPackageName")))),
        true,
        true,
        Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true, true)))),
        None)

      CustomersOutsideUK.conv(businessActivitiesAll) must be(Some(CustomersOutsideUK(true, Some(List("AD", "GB")))))
    }

    "convert des model to frontend model successfully when countries option is none" in {
      val businessActivitiesAll = BusinessActivitiesAll(None,
        Some("2001-01-01"),
        None,
        BusinessActivityDetails(true, Some(DesExpectedAMLSTurnover(Some("11122233344")))),
        Some(FranchiseDetails(true, Some(Seq("FranchiserName1", "FranchiserName2")))),
        Some("12345678901"),
        Some("11223344556"),
        NonUkResidentCustDetails(false, None),
        AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("CommercialPackageName")))),
        true,
        true,
        Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true, true)))),
        None)

      CustomersOutsideUK.conv(businessActivitiesAll) must be(Some(CustomersOutsideUK(false, None)))
    }
  }
}
