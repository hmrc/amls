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

package models.fe.tradingpremises

import play.api.data.validation.ValidationError
import play.api.libs.json.{Writes, _}


sealed trait BusinessStructure

object BusinessStructure {

  case object SoleProprietor extends BusinessStructure
  case object LimitedLiabilityPartnership extends BusinessStructure
  case object Partnership extends BusinessStructure
  case object IncorporatedBody extends BusinessStructure
  case object UnincorporatedBody extends BusinessStructure


  implicit val jsonReadsBusinessStructure = {
    (__ \ "agentsBusinessStructure").read[String].flatMap[BusinessStructure] {
      case "01" => Reads(_ => JsSuccess(SoleProprietor))
      case "02" => Reads(_ => JsSuccess(LimitedLiabilityPartnership))
      case "03" => Reads(_ => JsSuccess(Partnership))
      case "04" => Reads(_ => JsSuccess(IncorporatedBody))
      case "05" => Reads(_ => JsSuccess(UnincorporatedBody))
      case _ =>
        Reads(_ =>JsError(JsPath \ "agentsBusinessStructure", ValidationError("error.invalid")))
    }
  }

  implicit val jsonWritesBusinessStructure = Writes[BusinessStructure] {
    case SoleProprietor => Json.obj("agentsBusinessStructure" -> "01")
    case LimitedLiabilityPartnership => Json.obj("agentsBusinessStructure" -> "02")
    case Partnership => Json.obj("agentsBusinessStructure" -> "03")
    case IncorporatedBody => Json.obj("agentsBusinessStructure" -> "04")
    case UnincorporatedBody => Json.obj("agentsBusinessStructure" -> "05")
  }

  implicit def conv(entity: String): Option[BusinessStructure] = {
    entity match {
      case "Sole Proprietor" => Some(SoleProprietor)
      case "Limited Liability Partnership" => Some(LimitedLiabilityPartnership)
      case "Partnership" => Some(Partnership)
      case "Corporate Body" => Some(IncorporatedBody)
      case "Unincorporated Body" => Some(UnincorporatedBody)
    }
  }
}
