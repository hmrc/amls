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

import models.fe.businessmatching.{BusinessAppliedForPSRNumberYes, ChequeCashingNotScrapMetal, TransmittingMoney, MsbServices}
import models.fe.moneyservicebusiness._
import org.scalatestplus.play.PlaySpec

class MsbCeDetailsSpec extends PlaySpec {

  "MsbCeDetails" should {

    "convert to  frontend MSB model to correct Msb Des model when Bank details is none" in {

      val msbCeDetails = Some(MsbCeDetails(CurrencySources(None,
        None,true,"12345678963",Some(CurrSupplyToCust(List("USD", "MNO", "PQR"))))))

      val businessUseAnIPSP = BusinessUseAnIPSPNo
      val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")
      val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "PQR"),
        None,
        None, true)
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

    "convert to  frontend MSB model to correct Msb Des model when whichCurrencies is none" in {

      val msbCeDetails = Some(MsbCeDetails(CurrencySources(None,
        None,false,"",None)))

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
