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

package models.des.bankdetails

import models.fe.bankdetails._
import play.api.libs.json.Json

case class BankAccount(accountName: String, accountType:String, doYouHaveUkBankAccount:Boolean, bankAccountDetails: Account)

object BankAccount {
  implicit val format = Json.format[BankAccount]

  implicit def convert(bankdetails: Seq[models.fe.bankdetails.BankDetails]):Seq[BankAccount] ={
    bankdetails map(x => x.bankAccount.account match {
        case uk:UKAccount => BankAccount(x.bankAccount.accountName,
          convertType(x.bankAccountType),
          true, ukAccount(uk.sortCode,uk.accountNumber))

        case nonukacc:NonUKAccountNumber => BankAccount(x.bankAccount.accountName,
          convertType(x.bankAccountType),
          false, AccountNumber(nonukacc.accountNumber))

        case nonukiban:NonUKIBANNumber => BankAccount(x.bankAccount.accountName,
          convertType(x.bankAccountType),
          false, IBANNumber(nonukiban.IBANNumber))
      }
    )
  }

  def convertType(accountType:BankAccountType):String ={
    accountType match{
      case PersonalAccount => "Personal"
      case BelongsToBusiness => "This business's"
      case BelongsToOtherBusiness => "Another business's"
      }
  }
}
