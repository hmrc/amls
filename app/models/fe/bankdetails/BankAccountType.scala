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

package models.fe.bankdetails

import play.api.data.validation.ValidationError
import play.api.libs.json._

sealed trait BankAccountType

case object PersonalAccount extends BankAccountType
case object BelongsToBusiness extends BankAccountType
case object BelongsToOtherBusiness extends BankAccountType

object BankAccountType {

  import utils.MappingUtils.Implicits._

  implicit val jsonReads : Reads[BankAccountType] = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "bankAccountType").read[String] flatMap {
      case "01" => PersonalAccount
      case "02" => BelongsToBusiness
      case "03" => BelongsToOtherBusiness
      case _ =>
        ValidationError("error.invalid")
    }
  }

  implicit val jsonWrites = Writes[BankAccountType] {
    case PersonalAccount => Json.obj("bankAccountType"->"01")
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
