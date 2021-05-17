/*
 * Copyright 2021 HM Revenue & Customs
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

package models.des.businessactivities

import models.fe.businessactivities._
import play.api.libs.json.Json

case class RiskAssessmentFormat(electronicFormat: Boolean = false,
                                manualFormat: Boolean = false)

object RiskAssessmentFormat{
  implicit val format = Json.format[RiskAssessmentFormat]

  implicit def convert(riskType: Set[RiskAssessmentType]): Option[RiskAssessmentFormat] = {
    Some(RiskAssessmentFormat(riskType.contains(Digital), riskType.contains(PaperBased)))
  }
}

case class FormalRiskAssessmentDetails(formalRiskAssessment: Boolean,
                                       riskAssessmentFormat: Option[RiskAssessmentFormat] = None)

object FormalRiskAssessmentDetails{
  implicit val format = Json.format[FormalRiskAssessmentDetails]

  implicit def convert(riskAss:Option[RiskAssessmentPolicy]): Option[FormalRiskAssessmentDetails] ={
    riskAss match{
      case Some(RiskAssessmentPolicyYes(x)) => Some(FormalRiskAssessmentDetails(true, x))
      case Some(RiskAssessmentPolicyNo) => Some(FormalRiskAssessmentDetails(false))
      case _ => None
    }
  }
}
