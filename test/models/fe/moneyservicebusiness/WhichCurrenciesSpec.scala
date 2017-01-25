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

package models.fe.moneyservicebusiness

import models.des.msb._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class WhichCurrenciesSpec extends PlaySpec {

  "WhichCurrencies" must {

    "round trip through Json correctly" in {

      val model = WhichCurrencies(Seq("USD", "MNO", "PQR"), true, Some(BankMoneySource("Bank names")), Some(WholesalerMoneySource("wholesaler names")), true)
      Json.fromJson[WhichCurrencies](Json.toJson(model)) mustBe JsSuccess(model)
    }

    "round trip through Json correctly" when {
      "customerMoneySource is false" in {

        val model = WhichCurrencies(Seq("USD", "MNO", "PQR"), true, Some(BankMoneySource("Bank names")), Some(WholesalerMoneySource("wholesaler names")), false)
        Json.fromJson[WhichCurrencies](Json.toJson(model)) mustBe JsSuccess(model)
      }

      "WholesalerMoneySource and BankMoneySource is none" in {
        val model = WhichCurrencies(Seq("USD", "MNO", "PQR"), false, None, None, false)
        Json.fromJson[WhichCurrencies](Json.toJson(model)) mustBe JsSuccess(model)
      }
    }

    "convert des model to frontend model" in {
      val msbCe = MsbCeDetails(
        CurrencySources(
         None,
          Some(CurrencyWholesalerDetails(
            true,
            Some(List("CurrencyWholesalerNames"))
          )),
          true,
          "11234567890",
          Some(CurrSupplyToCust(List("GBP", "XYZ", "ABC")))
        ),
        dealInPhysCurrencies = Some(true)
      )

      val convertedModel = Some(WhichCurrencies(List("GBP", "XYZ", "ABC"), true, None, Some(WholesalerMoneySource("CurrencyWholesalerNames")), true))

      WhichCurrencies.convMsbCe(Some(msbCe)) must be(convertedModel)

    }

    "convert des model to frontend model when CurrSupplyToCust empty" in {
      val msbCe = MsbCeDetails(
        CurrencySources(
          None,
          Some(CurrencyWholesalerDetails(
            true,
            Some(List("CurrencyWholesalerNames"))
          )),
          true,
          "11234567890",
          None
        ),
        dealInPhysCurrencies = Some(true)
      )

      val convertedModel = Some(WhichCurrencies(List.empty, true, None, Some(WholesalerMoneySource("CurrencyWholesalerNames")), true))

      WhichCurrencies.convMsbCe(Some(msbCe)) must be(convertedModel)

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
        Some(Seq("bank1","bank2"))
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
