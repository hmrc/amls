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

import models.des.businessactivities.{BusinessActivitiesAll, BusinessActivityDetails}
import play.api.data.validation.ValidationError
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

  import utils.MappingUtils.Implicits._

  implicit val jsonReads = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "expectedBusinessTurnover").read[String].flatMap[ExpectedBusinessTurnover] {
      case "01" => First
      case "02" => Second
      case "03" => Third
      case "04" => Fourth
      case "05" => Fifth
      case "06" => Sixth
      case "07" => Seventh
      case _ =>
        ValidationError("error.invalid")
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

  implicit def conv(activityDtls: BusinessActivityDetails) : Option[ExpectedBusinessTurnover] = {
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
      case "14999" => Some(First)
      case "49999" => Some(Second)
      case "99999" => Some(Third)
      case "249999" => Some(Fourth)
      case "999999" => Some(Fifth)
      case "10000000" => Some(Sixth)
      case "100000000" => Some(Seventh)
      case  _ => None
    }
  }
}
