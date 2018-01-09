/*
 * Copyright 2018 HM Revenue & Customs
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

import models.des.responsiblepeople.RegDetails
import play.api.libs.json._
import utils.MappingUtils.Implicits._

sealed trait SaRegistered

case class SaRegisteredYes(value: String) extends SaRegistered

case object SaRegisteredNo extends SaRegistered

object SaRegistered {

  implicit val jsonReads: Reads[SaRegistered] =
    (__ \ "saRegistered").read[Boolean] flatMap {
      case true => (__ \ "utrNumber").read[String] map (SaRegisteredYes.apply _)
      case false => Reads(_ => JsSuccess(SaRegisteredNo))
    }

  implicit val jsonWrites = Writes[SaRegistered] {
    case SaRegisteredYes(value) => Json.obj(
      "saRegistered" -> true,
      "utrNumber" -> value
    )
    case SaRegisteredNo => Json.obj("saRegistered" -> false)
  }

  implicit def conv(des: Option[RegDetails]): Option[SaRegistered] = {
    Some(
      des match {
        case Some(RegDetails(_, _, true, Some(regNo))) => SaRegisteredYes(regNo)
        case _ => SaRegisteredNo
      }
    )
  }
}
