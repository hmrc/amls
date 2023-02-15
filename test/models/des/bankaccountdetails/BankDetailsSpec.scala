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

package models.des.bankaccountdetails

import models.des.bankdetails.{AccountNumber, BankAccount, BankDetails, ukAccount}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import models.fe.bankdetails.{PersonalAccount, UKAccount, BankDetails => FEBankDetails}
import org.scalatestplus.mockito.MockitoSugar

class BankDetailsSpec extends PlaySpec with MockitoSugar {
  "BankAccountDetails" must {
    val bankDetailsModel = BankDetails("1",
      Some(Seq(BankAccount("Personal", "Personal", true, ukAccount(sortCode = "112233", accountNumber = "12345678")))))


    "serialise BankDetails model " in {
      BankDetails.format.writes(bankDetailsModel) must be(Json.obj("noOfMlrBankAccounts" -> "1",
        "bankAccounts" -> Json.arr(Json.obj(
          "accountName" -> "Personal", "accountType" -> "Personal", "doYouHaveUkBankAccount" -> true,
          "bankAccountDetails" -> Json.obj("ukAccount" -> Json.obj("accountNumber" -> "12345678", "sortCode" -> "112233"))))))

    }

    val multiAccountModel = BankDetails("2",
      Some(Seq(BankAccount("Personal account", "Personal", true, ukAccount(sortCode = "112233", accountNumber = "12345678")),
        BankAccount("Business account", "This business's", false, AccountNumber(accountNumber = "12345678")))))

    "serialise BankDetails model with multiple accounts " in {
      BankDetails.format.writes(multiAccountModel) must be(Json.obj("noOfMlrBankAccounts" -> "2",
        "bankAccounts" -> Json.arr(Json.obj(
          "accountName" -> "Personal account", "accountType" -> "Personal", "doYouHaveUkBankAccount" -> true,
          "bankAccountDetails" -> Json.obj("ukAccount" -> Json.obj("accountNumber" -> "12345678", "sortCode" -> "112233"))),
          Json.obj(
            "accountName" -> "Business account", "accountType" -> "This business's", "doYouHaveUkBankAccount" -> false,
            "bankAccountDetails" -> Json.obj("nonUkAccount" -> Json.obj("accountHasIban" -> false, "accountNumber" -> Json.obj("bankAccountNumber" -> "12345678")))))))

    }

    "convert frontend to des successfully" in {
      BankDetails.convert(Seq.empty) must be(BankDetails("0", None))
    }

    "convert frontend to dev successfully with a list of bank accounts" in {

      val bankDetails = Seq(
        FEBankDetails(PersonalAccount, "Test account 1", mock[UKAccount]),
        FEBankDetails(PersonalAccount, "Test account 2", mock[UKAccount])
      )

      BankDetails.convert(bankDetails).noOfMlrBankAccounts mustBe "2"
    }

  }
}
