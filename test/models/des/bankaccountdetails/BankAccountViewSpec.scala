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

package models.des.bankaccountdetails

import models.des.bankdetails._
import models.fe.bankdetails.{BankDetails, NonUKIBANNumber, PersonalAccount}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class BankAccountViewSpec extends PlaySpec {
  "BankAccountDetails" must {
    "deserialise bankAccount model" when {
      "given a UK Account" in {

        val bankAccountViewModel = BankAccountView(
          "Personal", "Personal", true, ukAccountView("112233", "12345678")
        )
        val json = Json.parse(
          """{
        "accountName": "Personal",
        "accountType": "Personal",
        "doYouHaveUkBankAccount": true,
        "bankAccountDetails": {
          "ukAccount": {
            "sortCode": "112233",
            "accountNumber": "12345678"
          }
        }
      }"""
        )

        json.as[BankAccountView] must be(
          bankAccountViewModel
        )
      }

      "given a non UK Account with account number" in {

        val bankAccountViewModel = BankAccountView(
          "Personal", "Personal", false, AccountNumberView(accountNumber = "12345678")
        )
        val json = Json.parse(
          """{
        "accountName": "Personal",
        "accountType": "Personal",
        "doYouHaveUkBankAccount": false,
        "bankAccountDetails": {
          "nonUkAccount": {
            "accountNumber": {
            "bankAccountNumber": "12345678"
            }
          }
        }
      }"""
        )

        json.as[BankAccountView] must be(
          bankAccountViewModel
        )
      }

      "given a non UK Account with iban" in {

        val bankAccountViewModel = BankAccountView(
          "Personal", "Personal", false, IBANNumberView(iban = "12345678")
        )
        val json = Json.parse(
          """{
        "accountName": "Personal",
        "accountType": "Personal",
        "doYouHaveUkBankAccount": false,
        "bankAccountDetails": {
          "nonUkAccount": {
            "accountNumber": {
            "iban": "12345678"
            }
          }
        }
      }"""
        )

        json.as[BankAccountView] must be(
          bankAccountViewModel
        )
      }
    }


    val nonUkAccountModel = BankAccountView("Personal", "Personal", false, AccountNumberView(accountNumber = "12345678"))

    "serialise bankAccount model with non UK Account " in {
      BankAccountView.format.writes(nonUkAccountModel) must be(
        Json.obj(
          "accountName" -> "Personal",
          "accountType" -> "Personal",
          "doYouHaveUkBankAccount" -> false,
          "bankAccountDetails" -> Json.obj(
            "nonUkAccount" -> Json.obj(
              "accountHasIban" -> false,
              "accountNumber" -> Json.obj(
                "bankAccountNumber" -> "12345678")
            )
          )
        )
      )

    }

    "successfully compare view model with amend model for BankDetails" in {

      val viewBankDetails = BankDetailsView(
        Some("3"),
        Some(List(
          BankAccountView("AccountName", "This business's", true, ukAccountView("123456", "12345678")),
          BankAccountView("AccountName1", "Personal", false, IBANNumberView("87654321")),
          BankAccountView("AccountName2", "Another business's", false, AccountNumberView("87654321"))))
      )
      val amendBankDetails = viewBankDetails

      viewBankDetails.equals(amendBankDetails) must be(true)
    }

    "successfully compare view model with amend model for BankDetails1" in {

      val viewBankDetails = BankDetailsView(
        Some("3"),
        Some(List(
          BankAccountView("AccountName", "This business's", false, AccountNumberView("123456")),
          BankAccountView("AccountName1", "Personal", false, IBANNumberView("87654321")),
          BankAccountView("AccountName2", "Another business's", false, AccountNumberView("87654321"))))
      )
      val amendBankDetails = BankDetailsView(
        Some("3"),
        Some(List(
          BankAccountView("AccountName", "This business's", true, ukAccountView("123456", "12345678")),
          BankAccountView("AccountName1", "Personal", false, IBANNumberView("87654321")),
          BankAccountView("AccountName2", "Another business's", false, AccountNumberView("87654321"))))
      )

      viewBankDetails.equals(amendBankDetails) must be(false)
    }

    "successfully compare view model with amend model for BankDetails2" in {

      val viewBankDetails = BankDetailsView(
        Some("4"),
        Some(List(
          BankAccountView("AccountName", "This business's", false, AccountNumberView("123456")),
          BankAccountView("AccountName1", "Personal", false, IBANNumberView("87654321")),
          BankAccountView("AccountName2", "Another business's", false, AccountNumberView("87654321")),
          BankAccountView("AccountName3", "Another business's", false, AccountNumberView("87654321"))
        ))
      )
      val amendBankDetails = BankDetailsView(
        Some("3"),
        Some(List(
          BankAccountView("AccountName", "This business's", true, ukAccountView("123456", "12345678")),
          BankAccountView("AccountName1", "Personal", false, IBANNumberView("87654321")),
          BankAccountView("AccountName2", "Another business's", false, AccountNumberView("87654321"))))
      )

      viewBankDetails.equals(amendBankDetails) must be(false)
    }


    "successfully compare view model with amend model for BankDetails3" in {

      val viewBankDetails = BankDetailsView(
        Some("0"),
        None
      )
      val amendBankDetails = BankDetailsView(
        Some("3"),
        Some(List(
          BankAccountView("AccountName", "This business's", true, ukAccountView("123456", "12345678")),
          BankAccountView("AccountName1", "Personal", false, IBANNumberView("87654321")),
          BankAccountView("AccountName2", "Another business's", false, AccountNumberView("87654321"))))
      )

      viewBankDetails.equals(amendBankDetails) must be(false)
    }

    "caps the number of bank accounts to 99" in {
      var accounts: Seq[models.fe.bankdetails.BankDetails] = Seq()

      for (i <- 0 until 110){
        accounts = accounts:+ BankDetails(PersonalAccount, s"AccountName$i", NonUKIBANNumber("87654321"))
      }

      val amendBankDetails = BankDetailsView(
        Some("99"),
        Some(accounts)
      )

      BankDetailsView.convert(accounts) must be (Some(amendBankDetails))
    }
  }
}
