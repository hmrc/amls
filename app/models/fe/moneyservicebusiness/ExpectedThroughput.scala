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

package models.fe.moneyservicebusiness

import models.des.msb.MsbAllDetails
import play.api.libs.json._

sealed trait ExpectedThroughput

object ExpectedThroughput {

  case object First extends ExpectedThroughput

  case object Second extends ExpectedThroughput

  case object Third extends ExpectedThroughput

  case object Fourth extends ExpectedThroughput

  case object Fifth extends ExpectedThroughput

  case object Sixth extends ExpectedThroughput

  case object Seventh extends ExpectedThroughput

  implicit val jsonReads: Reads[ExpectedThroughput] = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "throughput").read[String].flatMap[ExpectedThroughput] {
      case "01" => Reads(_ => JsSuccess(First))
      case "02" => Reads(_ => JsSuccess(Second))
      case "03" => Reads(_ => JsSuccess(Third))
      case "04" => Reads(_ => JsSuccess(Fourth))
      case "05" => Reads(_ => JsSuccess(Fifth))
      case "06" => Reads(_ => JsSuccess(Sixth))
      case "07" => Reads(_ => JsSuccess(Seventh))
      case _ =>
        Reads(_ => JsError(JsPath \ "throughput", JsonValidationError("error.invalid")))
    }
  }

  implicit val jsonWrites: Writes[ExpectedThroughput] = Writes[ExpectedThroughput] {
    case First => Json.obj("throughput" -> "01")
    case Second => Json.obj("throughput" -> "02")
    case Third => Json.obj("throughput" -> "03")
    case Fourth => Json.obj("throughput" -> "04")
    case Fifth => Json.obj("throughput" -> "05")
    case Sixth => Json.obj("throughput" -> "06")
    case Seventh => Json.obj("throughput" -> "07")
  }

  implicit def convMsbAll(msbAll: Option[MsbAllDetails]): Option[ExpectedThroughput] = {
    msbAll match {
      case Some(msbDtls) => msbDtls.anticipatedTotThrputNxt12Mths.map(x => convThroughput(x))
      case None => None
    }
  }

  def convThroughput(msbAll: String): ExpectedThroughput = {
    msbAll match {
      case "£0-£15k" => First
      case "£15k-50k" => Second
      case "£50k-£100k" => Third
      case "£100k-£250k" => Fourth
      case "£250k-£1m" => Fifth
      case "£1m-10m" => Sixth
      case "£10m+" => Seventh
    }
  }
}
