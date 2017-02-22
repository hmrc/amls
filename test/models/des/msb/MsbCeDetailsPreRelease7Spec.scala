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

package models.des.msb

import models.fe.moneyservicebusiness._
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication

class MsbCeDetailsPreRelease7Spec extends PlaySpec with OneAppPerSuite {

  override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  "MsbCeDetails" should {

    "always set usesForeignCurrencies to None" when {

      "there is a full set of data" in {

        val msbCeDetails = Some(MsbCeDetails(CurrencySources(None,
          None, reSellCurrTakenIn = true, "12345678963", Some(CurrSupplyToCust(List("USD", "MNO", "PQR")))), None))

        val businessUseAnIPSP = BusinessUseAnIPSPNo
        val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")
        val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "PQR"), None, None, None, true)
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

      "dealInPhysCurrencies is missing" in {

        val msbCe = MsbCeDetailsR7(None,
          Some(CurrencySourcesR7(
            None,
            Some(CurrencyWholesalerDetails(true, Some(Seq("Some wholesaler")))),
            false
          )),
          "11234567890",
          None
        )

        val convertedModel = Some(WhichCurrencies(List.empty, None, None, Some(WholesalerMoneySource("Some wholesaler")), false))

        WhichCurrencies.convMsbCe(Some(msbCe)) must be(convertedModel)
      }

    }

    "deserialize the json properly" when {

      val model = MsbCeDetails(CurrencySources(
        Some(MSBBankDetails(banks = true, Some(Seq("BankNames1", "BankNames2")))),
        reSellCurrTakenIn = true,
        antNoOfTransNxt12Mnths = "11234567890",
        currSupplyToCust = Some(CurrSupplyToCust(Seq("GBP", "USD", "INR")))
      ), dealInPhysCurrencies = None)

      "given a JSON packet where dealInPhysCurrencies is missing" in {

        val json =
          """ {
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