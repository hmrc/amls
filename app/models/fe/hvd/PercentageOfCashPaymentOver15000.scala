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

package models.fe.hvd

import play.api.data.validation.ValidationError
import play.api.libs.json._
import models.des.hvd.{Hvd=> DesHvd}

sealed trait PercentageOfCashPaymentOver15000

object PercentageOfCashPaymentOver15000 {
  val `twenty` = 20
  val `forty` = 40
  val `sixty` = 60
  val `eighty` = 80
  val `hundred` = 100

  case object First extends PercentageOfCashPaymentOver15000
  case object Second extends PercentageOfCashPaymentOver15000
  case object Third extends PercentageOfCashPaymentOver15000
  case object Fourth extends PercentageOfCashPaymentOver15000
  case object Fifth extends PercentageOfCashPaymentOver15000

  implicit val jsonReads = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "percentage").read[String].flatMap[PercentageOfCashPaymentOver15000] {
      case "01" => Reads(_ => JsSuccess(First))
      case "02" => Reads(_ => JsSuccess(Second))
      case "03" => Reads(_ => JsSuccess(Third))
      case "04" => Reads(_ => JsSuccess(Fourth))
      case "05" => Reads(_ => JsSuccess(Fifth))
      case _ =>
        Reads(_ => JsError((JsPath \ "percentage") -> ValidationError("error.invalid")))
    }
  }

  implicit val jsonWrites = Writes[PercentageOfCashPaymentOver15000] {
    case First => Json.obj("percentage" -> "01")
    case Second => Json.obj("percentage" -> "02")
    case Third => Json.obj("percentage" -> "03")
    case Fourth => Json.obj("percentage" -> "04")
    case Fifth => Json.obj("percentage" -> "05")
  }

  implicit def conv(hvd: DesHvd): Option[PercentageOfCashPaymentOver15000] = {

    hvd.hvPercentageTurnover match {
      case Some(data) => data match {
        case `twenty` => Some(First)
        case `forty` => Some(Second)
        case `sixty` => Some(Third)
        case `eighty` => Some(Fourth)
        case `hundred` => Some(Fifth)
        case _ => None
      }
      case None => None
    }
  }
}
