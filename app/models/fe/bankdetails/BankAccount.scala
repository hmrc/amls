/*
 * Copyright 2022 HM Revenue & Customs
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

import models.des.bankdetails.{IBANNumberView, AccountNumberView, ukAccountView, BankAccountView}
import play.api.libs.json._

sealed trait Account

object Account {

 implicit val jsonReads: Reads[Account] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (__ \ "isUK").read[Boolean] flatMap {
      case true => (
        (__ \ "accountNumber").read[String] and
          (__ \ "sortCode").read[String]
        ) (UKAccount.apply _)

      case false =>
        (__ \ "isIBAN").read[Boolean] flatMap {
          case true => (__ \ "IBANNumber").read[String] fmap  NonUKIBANNumber.apply
          case false =>  (__ \ "nonUKAccountNumber").read[String] fmap  NonUKAccountNumber.apply
        }
    }
  }

  implicit val jsonWrites = Writes[Account] {
    case m: UKAccount =>
      Json.obj(
        "isUK" -> true,
        "accountNumber" -> m.accountNumber,
        "sortCode" -> m.sortCode
      )
    case acc: NonUKAccountNumber =>
      Json.obj(
        "isUK" -> false,
        "nonUKAccountNumber" -> acc.accountNumber,
        "isIBAN" -> false
      )
    case iban: NonUKIBANNumber =>
      Json.obj(
        "isUK" -> false,
        "IBANNumber" -> iban.IBANNumber,
        "isIBAN" -> true
      )
  }

  implicit def convBankAccount(bankDtls: BankAccountView): Account = {

     bankDtls.bankAccountDetails match {
      case ukAccountView(sortCode, accountNumber) => UKAccount(accountNumber, sortCode)
      case AccountNumberView(acctNumber) => NonUKAccountNumber(acctNumber)
      case IBANNumberView(iban) => NonUKIBANNumber(iban)
    }
  }
}

case class UKAccount(
                      accountNumber: String,
                      sortCode: String
                    ) extends Account

sealed trait NonUKAccount extends Account

case class NonUKAccountNumber(accountNumber: String) extends NonUKAccount

case class NonUKIBANNumber(IBANNumber: String) extends NonUKAccount