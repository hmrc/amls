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

package models.fe.bankdetails

import play.api.libs.json._

sealed trait BankAccountType

case object PersonalAccount extends BankAccountType

case object BelongsToBusiness extends BankAccountType

case object BelongsToOtherBusiness extends BankAccountType

object BankAccountType {

  implicit val jsonReads: Reads[BankAccountType] = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "bankAccountType").read[String] flatMap {
      case "01" => Reads(_ => JsSuccess(PersonalAccount))
      case "02" => Reads(_ => JsSuccess(BelongsToBusiness))
      case "03" => Reads(_ => JsSuccess(BelongsToOtherBusiness))
      case _ =>
        Reads(_ => JsError(JsPath \ "bankAccountType", JsonValidationError("error.invalid")))
    }
  }

  implicit val jsonWrites: Writes[BankAccountType] = Writes[BankAccountType] {
    case PersonalAccount => Json.obj("bankAccountType" -> "01")
    case BelongsToBusiness => Json.obj("bankAccountType" -> "02")
    case BelongsToOtherBusiness => Json.obj("bankAccountType" -> "03")
  }

  implicit def conv(accountType: String): BankAccountType = {
    accountType match {
      case "Personal" => PersonalAccount
      case "This business's" => BelongsToBusiness
      case "Another business's" => BelongsToOtherBusiness
    }
  }
}
