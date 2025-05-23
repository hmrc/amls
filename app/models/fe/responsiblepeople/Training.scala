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

package models.fe.responsiblepeople

import models.des.responsiblepeople.ResponsiblePersons
import play.api.libs.json.{Writes => _}

sealed trait Training

case class TrainingYes(information: String) extends Training

case object TrainingNo extends Training

object Training {

  import play.api.libs.json._

  implicit val jsonReads: Reads[Training] =
    (__ \ "training").read[Boolean] flatMap {
      case true  => (__ \ "information").read[String] map TrainingYes.apply
      case false => Reads(_ => JsSuccess(TrainingNo))
    }

  implicit val jsonWrites: Writes[Training] = Writes[Training] {
    case TrainingYes(information) =>
      Json.obj(
        "training"    -> true,
        "information" -> information
      )
    case TrainingNo               => Json.obj("training" -> false)
  }

  implicit def conv(rp: ResponsiblePersons): Option[Training] =
    rp.amlAndCounterTerrFinTraining match {
      case true  => Some(TrainingYes(rp.trainingDetails.getOrElse("")))
      case false => Some(TrainingNo)
    }
}
