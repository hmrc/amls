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

import models.fe.businessmatching.{BusinessAppliedForPSRNumberYes, ChequeCashingNotScrapMetal, MsbServices, TransmittingMoney}
import models.fe.moneyservicebusiness._
import org.scalatest.MustMatchers
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.FakeApplication

class MsbCeDetailsSpec extends PlaySpec with OneAppPerSuite {

  override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> true))

  "MsbCeDetails" should {

    "convert to frontend MSB model to correct Msb Des model when Bank details is none" in {

      val msbCeDetails = Some(MsbCeDetails(CurrencySources(None,
        None,true,"12345678963",Some(CurrSupplyToCust(List("USD", "MNO", "PQR")))), Some("true")))

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

      val msbCeDetails = Some(MsbCeDetails(CurrencySources(None, None, reSellCurrTakenIn = false, "", None), dealInPhysCurrencies = Some("false")))

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
  }

}

class MsbCeDetailsSpecRelease7 extends PlaySpec with MustMatchers with OneAppPerSuite {

  override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  "MsbCeDetails" should {

    "set always physical currency flag to none" when {

      "the release 7 toggle is low" must {

        val msbCeDetails = Some(MsbCeDetails(CurrencySources(None,
          None, reSellCurrTakenIn = true,"12345678963",Some(CurrSupplyToCust(List("USD", "MNO", "PQR")))), None))

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

    }

  }

}
