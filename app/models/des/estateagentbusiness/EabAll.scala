/*
 * Copyright 2017 HM Revenue & Customs
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

package models.des.estateagentbusiness

import models.fe.estateagentbusiness._
import play.api.libs.json.Json

case class EabAll(
  estateAgencyActProhibition : Boolean,
  estAgncActProhibProvideDetails : Option[String],
  prevWarnedWRegToEstateAgencyActivities : Boolean,
  prevWarnWRegProvideDetails : Option[String]
)


object EabAll {
  implicit val format = Json.format[EabAll]

  implicit def convert(eab: EstateAgentBusiness): EabAll = {
    val (penalised, penalisedDesc) = convPenalisedUnderEstateAgentsAct(eab.penalisedUnderEstateAgentsAct)
    val (professionalBody, professionalBodyDesc) = convProfessionalBody(eab.professionalBody )

    EabAll(penalised, penalisedDesc, professionalBody, professionalBodyDesc)
  }

  def convPenalisedUnderEstateAgentsAct(penalised: Option[PenalisedUnderEstateAgentsAct]) : (Boolean, Option[String]) = {
    penalised match {
      case Some(data) => data match {
        case PenalisedUnderEstateAgentsActYes(desc) => (true, Some(desc))
        case PenalisedUnderEstateAgentsActNo => (false, None)
      }
      case _ => (false, None)
    }
  }

  def convProfessionalBody(penalised: Option[ProfessionalBody]) : (Boolean, Option[String]) = {
    penalised match {
      case Some(data) => data match {
        case ProfessionalBodyYes(desc) => (true, Some(desc))
        case ProfessionalBodyNo => (false, None)
      }
      case _ => (false, None)
    }
  }
}
