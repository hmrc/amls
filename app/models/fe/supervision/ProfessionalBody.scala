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

package models.fe.supervision

import models.des.supervision.ProfessionalBodyDetails
import play.api.libs.json._

sealed trait ProfessionalBody

case class ProfessionalBodyYes(value: String) extends ProfessionalBody

case object ProfessionalBodyNo extends ProfessionalBody

object ProfessionalBody {

  implicit val jsonReads: Reads[ProfessionalBody] =
    (__ \ "penalised").read[Boolean] flatMap {
      case true  => (__ \ "professionalBody").read[String] map ProfessionalBodyYes.apply
      case false => Reads(_ => JsSuccess(ProfessionalBodyNo))
    }

  implicit val jsonWrites: Writes[ProfessionalBody] = Writes[ProfessionalBody] {
    case ProfessionalBodyYes(value) =>
      Json.obj(
        "penalised"        -> true,
        "professionalBody" -> value
      )
    case ProfessionalBodyNo         => Json.obj("penalised" -> false)
  }

  implicit def conv(supDtls: Option[ProfessionalBodyDetails]): Option[ProfessionalBody] =
    supDtls match {
      case Some(data) =>
        data.prevWarnedWRegToAspActivities match {
          case true  => Some(ProfessionalBodyYes(data.detailsIfFinedWarned.getOrElse("")))
          case false => Some(ProfessionalBodyNo)
        }
      case None       => Some(ProfessionalBodyNo)
    }
}
