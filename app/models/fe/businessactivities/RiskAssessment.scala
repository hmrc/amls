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

package models.fe.businessactivities

import models.des.businessactivities.FormalRiskAssessmentDetails
import play.api.libs.json._
import utils.CommonMethods

sealed trait RiskAssessmentPolicy

case class RiskAssessmentPolicyYes(riskassessments: Set[RiskAssessmentType]) extends RiskAssessmentPolicy

case object RiskAssessmentPolicyNo extends RiskAssessmentPolicy

sealed trait RiskAssessmentType

case object PaperBased extends RiskAssessmentType

case object Digital extends RiskAssessmentType

object RiskAssessmentType {

  implicit val jsonRiskAssessmentReads: Reads[RiskAssessmentType] =
    Reads {
      case JsString("01") => JsSuccess(PaperBased)
      case JsString("02") => JsSuccess(Digital)
      case _ => JsError((JsPath \ "riskassessments") -> JsonValidationError("error.invalid"))
    }

  implicit val jsonRiskAssessmentWrites: Writes[RiskAssessmentType] =
    Writes[RiskAssessmentType] {
      case PaperBased => JsString("01")
      case Digital => JsString("02")
    }
}

object RiskAssessmentPolicy {

  import utils.MappingUtils.Implicits._

  implicit def jsonReads: Reads[RiskAssessmentPolicy] =
    (__ \ "hasPolicy").read[Boolean] flatMap {
      case true =>
        (__ \ "riskassessments").read[Set[RiskAssessmentType]].flatMap(RiskAssessmentPolicyYes.apply)
      case false => Reads(_ => JsSuccess(RiskAssessmentPolicyNo))
    }

  implicit def jsonWrites: Writes[RiskAssessmentPolicy] = Writes[RiskAssessmentPolicy] {
    case RiskAssessmentPolicyYes(data) =>
      Json.obj("hasPolicy" -> true,
        "riskassessments" -> data)
    case RiskAssessmentPolicyNo =>
      Json.obj("hasPolicy" -> false)
  }

  def conv(riskAssessment: Option[FormalRiskAssessmentDetails]): Option[RiskAssessmentPolicy] = {
    riskAssessment match {
      case Some(data) => data.riskAssessmentFormat match {
        case Some(dtls) => {
          val services = Set(CommonMethods.getSpecificType[RiskAssessmentType](dtls.electronicFormat, Digital),
            CommonMethods.getSpecificType[RiskAssessmentType](dtls.manualFormat, PaperBased))
          Some(RiskAssessmentPolicyYes(services.flatten))
        }
        case _ => Some(RiskAssessmentPolicyNo)
      }
      case None => Some(RiskAssessmentPolicyNo)
    }
  }
}
