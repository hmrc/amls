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

package models.fe.businessactivities

import config.AmlsConfig
import models.des.businessactivities.{BusinessActivitiesAll, BusinessActivityDetails}
import play.Logger
import play.api.data.validation.ValidationError
import play.api.libs.json._

sealed trait ExpectedAMLSTurnover

//noinspection ScalaStyle
object ExpectedAMLSTurnover {

  case object First extends ExpectedAMLSTurnover

  case object Second extends ExpectedAMLSTurnover

  case object Third extends ExpectedAMLSTurnover

  case object Fourth extends ExpectedAMLSTurnover

  case object Fifth extends ExpectedAMLSTurnover

  case object Sixth extends ExpectedAMLSTurnover

  case object Seventh extends ExpectedAMLSTurnover

  implicit val jsonReads = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "expectedAMLSTurnover").read[String].flatMap[ExpectedAMLSTurnover] {
      case "01" => Reads(_ => JsSuccess(First))
      case "02" => Reads(_ => JsSuccess(Second))
      case "03" => Reads(_ => JsSuccess(Third))
      case "04" => Reads(_ => JsSuccess(Fourth))
      case "05" => Reads(_ => JsSuccess(Fifth))
      case "06" => Reads(_ => JsSuccess(Sixth))
      case "07" => Reads(_ => JsSuccess(Seventh))
      case _ =>
        Reads(_ =>JsError(JsPath \ "expectedAMLSTurnover", ValidationError("error.invalid")))
    }
  }

  implicit val jsonWrites = Writes[ExpectedAMLSTurnover] {
    case First => Json.obj("expectedAMLSTurnover" -> "01")
    case Second => Json.obj("expectedAMLSTurnover" -> "02")
    case Third => Json.obj("expectedAMLSTurnover" -> "03")
    case Fourth => Json.obj("expectedAMLSTurnover" -> "04")
    case Fifth => Json.obj("expectedAMLSTurnover" -> "05")
    case Sixth => Json.obj("expectedAMLSTurnover" -> "06")
    case Seventh => Json.obj("expectedAMLSTurnover" -> "07")
  }

  implicit def conv(activityDtls: BusinessActivityDetails): Option[ExpectedAMLSTurnover] = {
    Logger.debug(s"[ExpectedAMLSTurnover][conv] desValue = $activityDtls")
    activityDtls.respActvtsBusRegForOnlyActvtsCarOut match {
      case Some(data) => activityDtls.actvtsBusRegForOnlyActvtsCarOut match {
        case true => convertAMLSTurnover(data.mlrActivityTurnover)
        case false => data.otherBusActivitiesCarriedOut match {
          case Some(other) => convertAMLSTurnover(Some(other.mlrActivityTurnover))
          case None => None
        }
      }
      case None => None
    }
  }

  def convertAMLSTurnover(to: Option[String]): Option[ExpectedAMLSTurnover] = {
    if (!AmlsConfig.release7) {
      to match {
        case Some("14999") => Some(First)
        case Some("49999") => Some(Second)
        case Some("99999") => Some(Third)
        case Some("249999") => Some(Fourth)
        case Some("999999") => Some(Fifth)
        case Some("10000000") => Some(Sixth)
        case Some("100000000") => Some(Seventh)
        case _ => None
      }
    } else {
      to match {
        case Some("£0-£15k") => Some(First)
        case Some("£15k-50k") => Some(Second)
        case Some("£50k-£100k") => Some(Third)
        case Some("£100k-£250k") => Some(Fourth)
        case Some("£250k-£1m") => Some(Fifth)
        case Some("£1m-10m") => Some(Sixth)
        case Some("£10m+") => Some(Seventh)
        case _ => None
      }
    }
  }
}
