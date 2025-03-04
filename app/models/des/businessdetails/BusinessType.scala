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

package models.des.businessdetails

import play.api.libs.json.{JsError, JsString, JsSuccess, JsonValidationError, Reads, Writes}

sealed trait BusinessType

object BusinessType {

  import models.fe.businessmatching.{BusinessType => BT}

  case object SoleProprietor extends BusinessType
  case object LimitedCompany extends BusinessType
  case object Partnership extends BusinessType
  case object LPrLLP extends BusinessType
  case object UnincorporatedBody extends BusinessType

  implicit def convert(model: BT): BusinessType =
    model match {
      case BT.SoleProprietor     => SoleProprietor
      case BT.LimitedCompany     => LimitedCompany
      case BT.Partnership        => Partnership
      case BT.LPrLLP             => LPrLLP
      case BT.UnincorporatedBody => UnincorporatedBody
    }

  implicit val reads: Reads[BusinessType] = Reads[BusinessType] {
    case JsString("Sole Proprietor")               => JsSuccess(SoleProprietor)
    case JsString("Limited Liability Partnership") => JsSuccess(LPrLLP)
    case JsString("Partnership")                   => JsSuccess(Partnership)
    case JsString("Corporate Body")                => JsSuccess(LimitedCompany)
    case JsString("Unincorporated Body")           => JsSuccess(UnincorporatedBody)
    case _                                         =>
      JsError(JsonValidationError("error.invalid"))
  }

  implicit val writes: Writes[BusinessType] = Writes[BusinessType] {
    case SoleProprietor     => JsString("Sole Proprietor")
    case LPrLLP             => JsString("Limited Liability Partnership")
    case Partnership        => JsString("Partnership")
    case LimitedCompany     => JsString("Corporate Body")
    case UnincorporatedBody => JsString("Unincorporated Body")
  }
}
