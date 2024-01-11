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

package models.fe.moneyservicebusiness

import models.des.msb._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class WhichCurrenciesSpec extends PlaySpec with GuiceOneAppPerSuite {

  "WhichCurrencies" must {

    "serialize WhichCurrencies as expected" in {
      val input = WhichCurrencies(Seq("USD", "MNO", "PQR"),
        usesForeignCurrencies = Some(true),
        Some(BankMoneySource("Bank names")),
        Some(WholesalerMoneySource("wholesaler names")),
        true)

      val expectedJson = Json.obj(
        "currencies" -> Seq("USD", "MNO", "PQR"),
        "usesForeignCurrencies" -> Json.obj("foreignCurrencies" -> true),
        "moneySources" -> Json.obj(
          "bankMoneySource" -> "Yes", "bankNames" -> "Bank names",
          "wholesalerMoneySource" -> "Yes", "wholesalerNames" -> "wholesaler names",
          "customerMoneySource" -> "Yes"))

      Json.toJson(input) must be(expectedJson)
    }

    "deserialize WhichCurrencies as expected" in {
      val json = Json.obj(
        "currencies" -> Seq("USD", "MNO", "PQR"),
        "usesForeignCurrencies" -> Json.obj("foreignCurrencies" -> true),
        "moneySources" -> Json.obj(
          "bankMoneySource" -> "Yes", "bankNames" -> "Bank names",
          "wholesalerMoneySource" -> "Yes", "wholesalerNames" -> "wholesaler names",
          "customerMoneySource" -> "Yes"))

      val expected = WhichCurrencies(Seq("USD", "MNO", "PQR"),
        usesForeignCurrencies = Some(true),
        Some(BankMoneySource("Bank names")),
        Some(WholesalerMoneySource("wholesaler names")),
        true)

      Json.fromJson[WhichCurrencies](json) must be(JsSuccess(expected))
    }

    "round trip through Json correctly" in {

      val model = WhichCurrencies(Seq("USD", "MNO", "PQR"),
        usesForeignCurrencies = Some(true),
        Some(BankMoneySource("Bank names")),
        Some(WholesalerMoneySource("wholesaler names")),
        true)

      val json = Json.toJson(model)

      Json.fromJson[WhichCurrencies](json) mustBe JsSuccess(model)
    }

    "round trip through Json correctly" when {
      "customerMoneySource is false" in {

        val model = WhichCurrencies(Seq("USD", "MNO", "PQR"), usesForeignCurrencies = Some(true), Some(BankMoneySource("Bank names")), Some(WholesalerMoneySource("wholesaler names")), false)
        Json.fromJson[WhichCurrencies](Json.toJson(model)) mustBe JsSuccess(model)
      }

      "WholesalerMoneySource and BankMoneySource is none" in {
        val model = WhichCurrencies(Seq("USD", "MNO", "PQR"), usesForeignCurrencies = Some(false), None, None, false)
        Json.fromJson[WhichCurrencies](Json.toJson(model)) mustBe JsSuccess(model)
      }

      "usesForeignCurrency is missing" in {
        val model = WhichCurrencies(Seq("GBP"), None, None, None, false)
        Json.fromJson[WhichCurrencies](Json.toJson(model)) mustBe JsSuccess(model)
      }
    }

    "convert des model to frontend model" in {
      val msbCe = MsbCeDetailsR7(Some(true),
        Some(CurrencySourcesR7(
          None,
          Some(CurrencyWholesalerDetails(
            true,
            Some(List("CurrencyWholesalerNames"))
          )),
          true
        )),
        "11234567890",
        Some(CurrSupplyToCust(List("GBP", "XYZ", "ABC")))
      )

      val convertedModel = Some(WhichCurrencies(List("GBP", "XYZ", "ABC"), usesForeignCurrencies = Some(true), None, Some(WholesalerMoneySource("CurrencyWholesalerNames")), true))

      WhichCurrencies.convMsbCe(Some(msbCe)) must be(convertedModel)

    }

    "convert des model to frontend model" when {

      "CurrSupplyToCust empty" in {
        val msbCe = MsbCeDetailsR7(Some(true),
          Some(CurrencySourcesR7(
            None,
            Some(CurrencyWholesalerDetails(
              true,
              Some(List("CurrencyWholesalerNames"))
            )),
            true
          )),
          "11234567890",
          None
        )

        val convertedModel = Some(WhichCurrencies(List.empty, usesForeignCurrencies = Some(true), None, Some(WholesalerMoneySource("CurrencyWholesalerNames")), true))

        WhichCurrencies.convMsbCe(Some(msbCe)) must be(convertedModel)
      }

      "dealInPhysCurrencies is missing, but contains data" in {

        val msbCe = MsbCeDetailsR7(None,
          Some(CurrencySourcesR7(
            None,
            Some(CurrencyWholesalerDetails(
              true,
              Some(List("CurrencyWholesalerNames"))
            )),
            true
          )),
          "11234567890",
          None
        )

        val convertedModel = Some(WhichCurrencies(List.empty, Some(true), None, Some(WholesalerMoneySource("CurrencyWholesalerNames")), true))

        WhichCurrencies.convMsbCe(Some(msbCe)) must be(convertedModel)
      }

      "dealInPhysCurrencies is missing and contains no data" in {

        val msbCe = MsbCeDetailsR7(None,
          Some(CurrencySourcesR7(
            None,
            None,
            false
          )),
          "11234567890",
          None
        )

        val convertedModel = Some(WhichCurrencies(List.empty, Some(false), None, None, false))

        WhichCurrencies.convMsbCe(Some(msbCe)) must be(convertedModel)
      }

    }

    "convert des model to frontend model when input is none" in {
      WhichCurrencies.convMsbCe(None) must be(None)
    }

    "convert des model to frontend model when bankNames empty" in {
      val desModel = Some(MSBBankDetails(
        false,
        Some(List.empty)
      ))
      WhichCurrencies.convMSBBankDetails(desModel) must be(None)
    }

    "convert des model to frontend model when bankNames is none" in {
      val desModel = Some(MSBBankDetails(
        false,
        None
      ))
      WhichCurrencies.convMSBBankDetails(desModel) must be(None)
    }

    "convert des model to frontend model when valid input is supplied" in {
      val desModel = Some(MSBBankDetails(
        true,
        Some(Seq("bank1", "bank2"))
      ))
      WhichCurrencies.convMSBBankDetails(desModel) must be(Some(BankMoneySource("bank1")))
    }

    "convert des model to frontend model when currencyWholesalersNames is none" in {
      val desModel = Some(CurrencyWholesalerDetails(
        false,
        None
      ))
      WhichCurrencies.convWholesalerDetails(desModel) must be(None)
      WhichCurrencies.convWholesalerDetails(None) must be(None)
    }

    "convert des model to frontend model when currencyWholesalersNames list is empty" in {
      val desModel = Some(CurrencyWholesalerDetails(
        false,
        Some(Seq.empty)
      ))
      WhichCurrencies.convWholesalerDetails(desModel) must be(None)
    }
  }
}
