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

package models.fe.responsiblepeople

import play.api.libs.json._

sealed trait TimeAtAddress

object TimeAtAddress {

  case object Empty extends TimeAtAddress
  case object ZeroToFiveMonths extends TimeAtAddress
  case object SixToElevenMonths extends TimeAtAddress
  case object OneToThreeYears extends TimeAtAddress
  case object ThreeYearsPlus extends TimeAtAddress

  implicit val jsonReads: Reads[TimeAtAddress] = {
      import play.api.libs.json.Reads.StringReads
      (__ \ "timeAtAddress").read[String].flatMap[TimeAtAddress] {
        case "01" => Reads(_ => JsSuccess(ZeroToFiveMonths))
        case "02" => Reads(_ => JsSuccess(SixToElevenMonths))
        case "03" => Reads(_ => JsSuccess(OneToThreeYears))
        case "04" => Reads(_ => JsSuccess(ThreeYearsPlus))
        case _ =>
          Reads(_ =>JsError(JsPath \ "timeAtAddress", JsonValidationError("error.invalid")))
      }
    }

  implicit val jsonWrites = Writes[TimeAtAddress] {
      case Empty => JsNull
      case ZeroToFiveMonths => Json.obj("timeAtAddress" -> "01")
      case SixToElevenMonths => Json.obj("timeAtAddress" -> "02")
      case OneToThreeYears => Json.obj("timeAtAddress" -> "03")
      case ThreeYearsPlus => Json.obj("timeAtAddress" -> "04")
    }
}
