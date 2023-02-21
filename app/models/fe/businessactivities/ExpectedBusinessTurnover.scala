/*
 * Copyright 2023 HM Revenue & Customs
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

import models.des.businessactivities.BusinessActivityDetails
import play.api.libs.json._

sealed trait ExpectedBusinessTurnover

object ExpectedBusinessTurnover {

  case object First extends ExpectedBusinessTurnover

  case object Second extends ExpectedBusinessTurnover

  case object Third extends ExpectedBusinessTurnover

  case object Fourth extends ExpectedBusinessTurnover

  case object Fifth extends ExpectedBusinessTurnover

  case object Sixth extends ExpectedBusinessTurnover

  case object Seventh extends ExpectedBusinessTurnover

  implicit val jsonReads = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "expectedBusinessTurnover").read[String].flatMap[ExpectedBusinessTurnover] {
      case "01" => Reads(_ => JsSuccess(First))
      case "02" => Reads(_ => JsSuccess(Second))
      case "03" => Reads(_ => JsSuccess(Third))
      case "04" => Reads(_ => JsSuccess(Fourth))
      case "05" => Reads(_ => JsSuccess(Fifth))
      case "06" => Reads(_ => JsSuccess(Sixth))
      case "07" => Reads(_ => JsSuccess(Seventh))
      case _ =>
        Reads(_ => JsError(JsPath \ "expectedBusinessTurnover", JsonValidationError("error.invalid")))
    }
  }

  implicit val jsonWrites = Writes[ExpectedBusinessTurnover] {
    case First => Json.obj("expectedBusinessTurnover" -> "01")
    case Second => Json.obj("expectedBusinessTurnover" -> "02")
    case Third => Json.obj("expectedBusinessTurnover" -> "03")
    case Fourth => Json.obj("expectedBusinessTurnover" -> "04")
    case Fifth => Json.obj("expectedBusinessTurnover" -> "05")
    case Sixth => Json.obj("expectedBusinessTurnover" -> "06")
    case Seventh => Json.obj("expectedBusinessTurnover" -> "07")
  }

  def conv(activityDtls: BusinessActivityDetails): Option[ExpectedBusinessTurnover] = {
    activityDtls.respActvtsBusRegForOnlyActvtsCarOut match {
      case Some(data) => data.otherBusActivitiesCarriedOut match {
        case Some(other) => other.anticipatedTotBusinessTurnover
        case None => None
      }
      case None => None
    }
  }

  implicit def convertTurnover(to: String): Option[ExpectedBusinessTurnover] = {

    to match {
      case "£0-£15k" => Some(First)
      case "£15k-50k" => Some(Second)
      case "£50k-£100k" => Some(Third)
      case "£100k-£250k" => Some(Fourth)
      case "£250k-£1m" => Some(Fifth)
      case "£1m-10m" => Some(Sixth)
      case "£10m+" => Some(Seventh)
      case _ => None
    }
  }
}
