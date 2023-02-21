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

package models.des.bankdetails

import play.api.libs.json.{Reads, Writes, Json}

sealed trait Account

object Account {
  implicit val jsonReads: Reads[Account] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (__ \ "doYouHaveUkBankAccount").read[Boolean] flatMap {
      case true => (
        (__ \ "sortCode").read[String] and
          (__ \ "accountNumber").read[String]

        ) (ukAccount.apply _)

      case false => {
        (__ \ "iban").read[String] fmap IBANNumber.apply
        (__ \ "bankAccountNumber").read[String] fmap AccountNumber.apply
      }
    }
  }

  implicit val jsonWrites = Writes[Account] {
    case m: ukAccount =>
      Json.obj(
        "ukAccount" -> Json.obj(
          "sortCode" -> m.sortCode,
          "accountNumber" -> m.accountNumber
        ))
    case acc: AccountNumber =>
      Json.obj(
        "nonUkAccount" -> Json.obj(
          "accountHasIban" -> false,
          "accountNumber" -> Json.obj("bankAccountNumber" -> acc.accountNumber)
        ))
    case iban: IBANNumber =>
      Json.obj(
        "nonUkAccount" -> Json.obj(
          "accountHasIban" -> true,
          "accountNumber" -> Json.obj("iban" -> iban.iban)
        ))
  }
}

case class ukAccount(sortCode: String, accountNumber: String) extends Account

sealed trait nonUkAccount extends Account

case class AccountNumber(accountNumber: String) extends nonUkAccount

case class IBANNumber(iban: String) extends nonUkAccount
