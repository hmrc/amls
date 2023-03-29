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

import models.des.bankdetails.{IBANNumberView, AccountNumberView, ukAccountView, BankAccountView}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsPath, JsSuccess, Json}

class BankAccountSpec extends PlaySpec {

  "For the Account" must {

    "JSON Read is successful for UKAccount" in {
      val jsObject = Json.obj(
        "isUK" -> true,
        "accountNumber" -> "12345678",
        "sortCode" -> "112233"
      )

      Account.jsonReads.reads(jsObject) must be(JsSuccess(UKAccount("12345678", "112233")))
    }

    "JSON Write is successful for UKAccount" in {

      val ukAccount = UKAccount("12345678", "112233")

      val jsObject = Json.obj(
        "isUK" -> true,
        "accountNumber" -> "12345678",
        "sortCode" -> "112233"
      )

      Account.jsonWrites.writes(ukAccount) must be(jsObject)
    }

    "JSON Read is successful for Non UKAccount" in {
      val jsObject = Json.obj(
        "accountName" -> "test",
        "isUK" -> false,
        "IBANNumber" -> "IB12345678",
        "isIBAN" -> true
      )

      Account.jsonReads.reads(jsObject) must be(JsSuccess(NonUKIBANNumber("IB12345678"), JsPath \ "IBANNumber"))
    }

    "JSON Write is successful for Non UK Account Number" in {

      val nonUKAccountNumber = NonUKAccountNumber("12345678")

      val jsObject = Json.obj(
        "isUK" -> false,
        "nonUKAccountNumber" -> "12345678",
        "isIBAN" -> false

      )

      Account.jsonWrites.writes(nonUKAccountNumber) must be(jsObject)
    }

    "convert des uk bankAccount to frontend uk bankAccount" in {
      val desModel = BankAccountView(
        "AccountName",
        "This business's",
        true,
        ukAccountView("123456", "12345678")
      )

      Account.convBankAccount(desModel) must be(UKAccount("12345678", "123456"))
    }

    "convert des non uk bankAccount to frontend non uk bankAccount" in {
      val desModel = BankAccountView(
        "AccountName",
        "This business's",
        false,
        AccountNumberView("123456")
      )

      Account.convBankAccount(desModel) must be(NonUKAccountNumber("123456"))
    }

    "convert des non uk iban bankAccount to frontend non uk iban bankAccount" in {
      val desModel = BankAccountView(
        "sams account",
        "This business's",
        false,
        IBANNumberView("123456898980980809809809809809809809")
      )

      Account.convBankAccount(desModel) must be(NonUKIBANNumber("123456898980980809809809809809809809"))
    }
  }

}
