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

package models.des.estateagentbusiness

import models.fe.eab.Eab
import play.api.libs.json.Json

case class EabAll(
                   estateAgencyActProhibition : Boolean,
                   estAgncActProhibProvideDetails : Option[String],
                   prevWarnedWRegToEstateAgencyActivities : Boolean,
                   prevWarnWRegProvideDetails : Option[String]
                 )


object EabAll {
  implicit val format = Json.format[EabAll]

  implicit def convert(eab: Eab): EabAll = {

    val (penalised, penalisedDesc) = convData(
      eab.data.penalisedEstateAgentsAct, eab.data.penalisedEstateAgentsActDetail
    )

    val (professionalBody, professionalBodyDesc) = convData(
      eab.data.penalisedProfessionalBody, eab.data.penalisedProfessionalBodyDetail
    )

    EabAll(penalised, penalisedDesc, professionalBody, professionalBodyDesc)
  }

  def convData(flagged: Boolean, detail: Option[String]): (Boolean, Option[String]) =
    flagged match {
      case true => (true, detail)
      case _    => (false, None)
    }

}
