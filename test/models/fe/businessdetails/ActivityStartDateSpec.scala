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

package models.fe.businessdetails

import models.des.businessactivities._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

import java.time.LocalDate

class ActivityStartDateSpec extends PlaySpec {
  "ActivityStartDate" must {

    "Json" should {
      // scalastyle:off
      "Read and write successfully" in {

        ActivityStartDate.format.reads(
          ActivityStartDate.format.writes(ActivityStartDate(LocalDate.of(1990, 2, 24)))
        ) must be(JsSuccess(ActivityStartDate(LocalDate.of(1990, 2, 24))))

      }

      "write successfully" in {
        ActivityStartDate.format.writes(ActivityStartDate(LocalDate.of(1990, 2, 24))) must be(
          Json.obj("startDate" -> "1990-02-24")
        )
      }
    }

    "des to frontend conversion when  input is none" in {
      ActivityStartDate.conv(None) must be(None)

    }

    "des to frontend conversion when activitiesCommenceDate is none" in {

      val desModel = Some(
        BusinessActivitiesAll(
          None,
          None,
          Some(false),
          BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("11122233344")))),
          Some(FranchiseDetails(true, Some(Seq("FranchiserName1", "FranchiserName2")))),
          Some("12345678901"),
          Some("11223344556"),
          NonUkResidentCustDetails(true, Some(Seq("AD", "GB"))),
          AuditableRecordsDetails(
            "Yes",
            Some(TransactionRecordingMethod(true, true, true, Some("CommercialPackageName")))
          ),
          true,
          true,
          Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true, true)))),
          None
        )
      )
      ActivityStartDate.conv(desModel) must be(None)

    }
  }
}
