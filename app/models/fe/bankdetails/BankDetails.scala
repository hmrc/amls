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

import models.des.bankdetails.{BankAccountView, BankDetailsView}

case class BankDetails (
                         bankAccountType: BankAccountType,
                         accountName: String,
                         bankAccount: Account
                        )

object BankDetails {

  import play.api.libs.json._

  implicit val format =Json.format[BankDetails]

  def convBankAccount(bankDtls: BankAccountView): BankDetails = {

    BankDetails(bankDtls.accountType, bankDtls.accountName, bankDtls)

  }

  implicit def conv(desBanks: Option[BankDetailsView]): Seq[BankDetails] = {

    desBanks match {
      case Some(db) => {
        db.bankAccounts match {
          case Some(bankAcct) => bankAcct.map(x => convBankAccount(x))
          case None => Seq.empty
        }
      }
      case None => Seq.empty
    }

  }
}
