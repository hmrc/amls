/*
 * Copyright 2021 HM Revenue & Customs
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

package models.des.msb

import models.fe.moneyservicebusiness.{MoneyServiceBusiness => FeMoneyServiceBusiness, _}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.Logger
import play.api.libs.json.Json

class MsbCeDetailsSpec extends PlaySpec with GuiceOneAppPerSuite {

  "MsbCeDetails" should {

    "convert to frontend MSB model to correct Msb Des model when Bank details is none" in {

      val msbCeDetails = Some(MsbCeDetails(CurrencySources(Some(MSBBankDetails(false,None)),
        Some(CurrencyWholesalerDetails(false, None)), true, "12345678963", Some(CurrSupplyToCust(List("USD", "MNO", "PQR")))), Some(true)))

      val businessUseAnIPSP = BusinessUseAnIPSPNo
      val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")
      val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "PQR"), usesForeignCurrencies = Some(true), None, None, true)
      val mostTransactions = MostTransactions(Seq("LA", "LV"))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Second),
        Some(businessUseAnIPSP),
        Some(IdentifyLinkedTransactions(true)),
        Some(SendMoneyToOtherCountry(false)),
        Some(FundsTransfer(true)),
        Some(BranchesOrAgents(true, Some(Seq("GB")))),
        Some(TransactionsInNext12Months("12345678963")),
        Some(CETransactionsInNext12Months("12345678963")),
        Some(sendTheLargestAmountsOfMoney),
        Some(mostTransactions),
        Some(whichCurrencies)
      )

      MsbCeDetails.conv(msbModel) must be(msbCeDetails)
    }

    "convert to frontend MSB model to correct Msb Des model when whichCurrencies is none" in {

      val msbCeDetails = Some(MsbCeDetails(CurrencySources(None, None, reSellCurrTakenIn = false, "", None), dealInPhysCurrencies = Some(false)))

      val businessUseAnIPSP = BusinessUseAnIPSPNo
      val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")
      val mostTransactions = MostTransactions(Seq("LA", "LV"))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Second),
        Some(businessUseAnIPSP),
        Some(IdentifyLinkedTransactions(true)),
        Some(SendMoneyToOtherCountry(false)),
        Some(FundsTransfer(true)),
        Some(BranchesOrAgents(true, Some(Seq("GB")))),
        Some(TransactionsInNext12Months("12345678963")),
        Some(CETransactionsInNext12Months("12345678963")),
        Some(sendTheLargestAmountsOfMoney),
        Some(mostTransactions),
        None
      )

      MsbCeDetails.conv(msbModel) must be(msbCeDetails)
    }

    "infer the value of dealInPhysCurrencies" when {

      "no value is supplied by the front end and at least one currency source is specified" in {
        val model = FeMoneyServiceBusiness(whichCurrencies =
          Some(WhichCurrencies(Seq("GBP"), None, Some(BankMoneySource("Some bank name")), None, customerMoneySource = false)))

        MsbCeDetails.conv(model) match {
          case Some(x) => x.dealInPhysCurrencies must be(Some(true))
          case _ => Logger.info("No MsbCe Details")
        }
      }

      "no value is supplied by the front end and no currency sources are specified" in {
        val model = FeMoneyServiceBusiness(whichCurrencies =
          Some(WhichCurrencies(Seq("GBP"), None, None, None, customerMoneySource = false)))

        MsbCeDetails.conv(model) match {
          case Some(x) => x.dealInPhysCurrencies must be(Some(false))
          case _ => Logger.info("No MsbCe Details")
        }
      }

    }

    "deserialise the DES response correctly" when {

      val model = MsbCeDetails(CurrencySources(
        Some(MSBBankDetails(banks = true, Some(Seq("BankNames1", "BankNames2")))),
        reSellCurrTakenIn = true,
        antNoOfTransNxt12Mnths = "11234567890",
        currSupplyToCust = Some(CurrSupplyToCust(Seq("GBP", "USD", "INR")))
      ), dealInPhysCurrencies = Some(true))

      "given usesForeignCurrencies as a string" in {

        val json =
          """ {
            |   "dealInPhysCurrencies": "true",
            |   "currencySources": {
            |     "bankDetails": {
            |       "banks":true,
            |       "bankNames":["BankNames1","BankNames2"]
            |     },
            |     "reSellCurrTakenIn":true,
            |     "antNoOfTransNxt12Mnths":"11234567890",
            |     "currSupplyToCust":{
            |       "currency":["GBP","USD","INR"]
            |     }
            |   }
            | }
          """.stripMargin

        Json.parse(json).as[MsbCeDetails] must be(model)

      }

      "given dealInPhysCurrencies as a boolean" in {

        val json =
          """ {
            |   "dealInPhysCurrencies": true,
            |   "currencySources": {
            |     "bankDetails": {
            |       "banks":true,
            |       "bankNames":["BankNames1","BankNames2"]
            |     },
            |     "reSellCurrTakenIn":true,
            |     "antNoOfTransNxt12Mnths":"11234567890",
            |     "currSupplyToCust":{
            |       "currency":["GBP","USD","INR"]
            |     }
            |   }
            | }
          """.stripMargin

        Json.parse(json).as[MsbCeDetails] must be(model)
      }

    }

  }

}
