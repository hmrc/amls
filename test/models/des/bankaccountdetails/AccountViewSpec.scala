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

import models.des.bankdetails._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class AccountViewSpec extends PlaySpec {

  "AccountView" must {
    "deserialise the account json" when {
      "given uk account json" in {

        val json = Json.parse(
          """{
          "ukAccount": {
            "sortCode": "112233",
            "accountNumber": "12345678"
          }
          }"""
        )
        val ukAccountViewModel = ukAccountView("112233", "12345678")

        json.as[AccountView] must be(ukAccountViewModel)
      }

      "given non-uk account json with account number" in {

        val json = Json.parse(
          """{
          "nonUkAccount": {
            "accountHasIban": false,
            "accountNumber": {
              "bankAccountNumber": "12345678"
            }
          }
          }"""
        )
        val nonUkAccountViewModel = AccountNumberView(accountNumber = "12345678")

        json.as[AccountView] must be(nonUkAccountViewModel)
      }

      "given non-uk account json with iban" in {

        val json = Json.parse(
          """{
          "nonUkAccount": {
            "accountHasIban": true,
            "accountNumber": {
              "iban": "87654321"
            }
          }
          }"""
        )
        val nonUkIBANNumberViewModel = IBANNumberView(iban = "87654321")

        json.as[AccountView] must be(nonUkIBANNumberViewModel)
      }
    }

    "serialise the account json" when {
      "given uk account json" in {

        val json = Json.parse(
          """{
          "ukAccount": {
            "sortCode": "112233",
            "accountNumber": "12345678"
          }
          }"""
        )
        val ukAccountViewModel = ukAccountView("112233", "12345678")

        Json.toJson(ukAccountViewModel: AccountView) must be(json)
      }

      "given non-uk account json with account number" in {

        val json = Json.parse(
          """{
          "nonUkAccount": {
            "accountHasIban": false,
            "accountNumber": {
              "bankAccountNumber": "12345678"
            }
          }
          }"""
        )
        val nonUkAccountViewModel = AccountNumberView(accountNumber = "12345678")

        Json.toJson(nonUkAccountViewModel: AccountView) must be(json)
      }

      "given non-uk account json with iban" in {

        val json = Json.parse(
          """{
          "nonUkAccount": {
            "accountHasIban": true,
            "accountNumber": {
              "iban": "87654321"
            }
          }
          }"""
        )
        val nonUkIBANNumberViewModel = IBANNumberView(iban = "87654321")

        Json.toJson(nonUkIBANNumberViewModel: AccountView) must be(json)
      }
    }
  }
}
