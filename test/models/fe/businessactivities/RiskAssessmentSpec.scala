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

import models.des.businessactivities.{FormalRiskAssessmentDetails, RiskAssessmentFormat}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class RiskAssessmentSpec extends PlaySpec with MockitoSugar {

  "RiskAssessmentSpec" must {

    val formalRiskAssessments: Set[RiskAssessmentType] = Set(PaperBased, Digital)

    "JSON validation" must {

      "successfully validate given values" in {
        val json = Json.obj(
          "hasPolicy" -> true,
          "riskassessments" -> Seq("01", "02"))

        Json.fromJson[RiskAssessmentPolicy](json) must
          be(JsSuccess(RiskAssessmentPolicyYes(formalRiskAssessments)))
      }

      "successfully validate given values with option No" in {
        val json = Json.obj("hasPolicy" -> false)

        Json.fromJson[RiskAssessmentPolicy](json) must
          be(JsSuccess(RiskAssessmentPolicyNo))
      }

      "fail when on invalid data" in {
        Json.fromJson[RiskAssessmentPolicy](Json.obj("hasPolicy" -> true,"riskassessments" -> Seq("01","03"))) mustBe a[JsError]
      }

      "write valid data in using json write" in {
        Json.toJson[RiskAssessmentPolicy](RiskAssessmentPolicyYes(Set(PaperBased, Digital))) must be(Json.obj("hasPolicy" -> true,
          "riskassessments" -> Seq("01", "02")
        ))
      }

      "write valid data in using json write with Option No" in {
        Json.toJson[RiskAssessmentPolicy](RiskAssessmentPolicyNo) must be(Json.obj("hasPolicy" -> false))
      }

    }

    "convert des to frontend model successfully" in {
      val desModel = Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true, true))))
      RiskAssessmentPolicy.conv(desModel) must be(Some(RiskAssessmentPolicyYes(Set(Digital, PaperBased))))
    }

    "convert des to frontend model successfully1" in {
      val desModel = Some(FormalRiskAssessmentDetails(false, None))
      RiskAssessmentPolicy.conv(desModel) must be(Some(RiskAssessmentPolicyNo))
    }

    "convert des to frontend model successfully when input is none" in {
      RiskAssessmentPolicy.conv(None) must be(Some(RiskAssessmentPolicyNo))
    }
  }
}
