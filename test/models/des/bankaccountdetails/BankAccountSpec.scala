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

package models.des.bankaccountdetails

import models.des.bankdetails.{AccountNumber, BankAccount, ukAccount}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class BankAccountSpec extends PlaySpec {
  "BankAccountDetails" must {
    val bankAccountModel = BankAccount("Personal", "Personal", true, ukAccount("112233", "12345678"))

    "serialise bankAccount model with UK Account " in {
      BankAccount.format.writes(bankAccountModel) must be(
        Json.obj(
          "accountName"            -> "Personal",
          "accountType"            -> "Personal",
          "doYouHaveUkBankAccount" -> true,
          "bankAccountDetails"     -> Json.obj(
            "ukAccount" -> Json.obj("accountNumber" -> "12345678", "sortCode" -> "112233")
          )
        )
      )
    }

    val nonUkAccountModel = BankAccount("Personal", "Personal", false, AccountNumber(accountNumber = "12345678"))

    "serialise bankAccount model with non UK Account " in {
      BankAccount.format.writes(nonUkAccountModel) must be(
        Json.obj(
          "accountName"            -> "Personal",
          "accountType"            -> "Personal",
          "doYouHaveUkBankAccount" -> false,
          "bankAccountDetails"     -> Json.obj(
            "nonUkAccount" -> Json
              .obj("accountHasIban" -> false, "accountNumber" -> Json.obj("bankAccountNumber" -> "12345678"))
          )
        )
      )

    }

  }

}
