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

import models.fe.businesscustomer.{Address, ReviewDetails}
import models.fe.businessmatching.{MoneyServiceBusiness => BMMoneyServiceBusiness_, _}
import models.fe.moneyservicebusiness._
import org.scalatestplus.play.PlaySpec

class MsbMtDetailsSpec extends PlaySpec {

  "MsbMtDetails" should {

    val msbService = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal))
    val businessAddress = Address("line1", "line2", Some("line3"), Some("line4"), Some("AA1 1AA"), "GB")
    val BusinessActivitiesModel = BusinessActivities(Set(BMMoneyServiceBusiness_, TrustAndCompanyServices, TelephonePaymentService))
    val ReviewDetailsModel = ReviewDetails("BusinessName", BusinessType.UnincorporatedBody, businessAddress, "XE0001234567890")

    "convert to  frontend MSB model to correct Msb Des model when Send money to other country is false" in {
      val msbMtDetails = MsbMtDetails(true,Some("123456"),
        IpspServicesDetails(false, None),
        true,
        Some("12345678963"), None, None)
      val psrNumber = Some(BusinessAppliedForPSRNumberYes("123456"))
      val bm = BusinessMatching(ReviewDetailsModel, BusinessActivitiesModel, msbServices = Some(msbService), None, None, psrNumber)

      val businessUseAnIPSP = BusinessUseAnIPSPNo
      val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")

      val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "EUR"),
        usesForeignCurrencies = true,
        Some(BankMoneySource("Bank names")),
        Some(WholesalerMoneySource("wholesaler names")), customerMoneySource = true)

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
      MsbMtDetails.conv(msbModel, bm) must be(Some(msbMtDetails))
    }

    "convert to  frontend MSB model to correct Msb Des model when Send money to other country is true" in {
      val msbMtDetails = MsbMtDetails(true,Some("123456"),
        None,
        false,
        None, Some(CountriesList(List("GB"))),Some(CountriesList(List("LA","LV"))))

      val msbService = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal))
      val psrNumber = Some(BusinessAppliedForPSRNumberYes("123456"))
      val bm = BusinessMatching(ReviewDetailsModel, BusinessActivitiesModel, msbServices = Some(msbService),None, None, psrNumber)
      val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")

      val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "PQR"),
        usesForeignCurrencies = true,
        Some(BankMoneySource("Bank names")),
        Some(WholesalerMoneySource("wholesaler names")), customerMoneySource = true)

      val mostTransactions = MostTransactions(Seq("LA", "LV"))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Second),
        None,
        Some(IdentifyLinkedTransactions(true)),
        Some(SendMoneyToOtherCountry(true)),
        None,
        Some(BranchesOrAgents(true, Some(Seq("GB")))),
        None,
        Some(CETransactionsInNext12Months("12345678963")),
        Some(sendTheLargestAmountsOfMoney),
        Some(mostTransactions),
        Some(whichCurrencies)
      )
      MsbMtDetails.conv(msbModel, bm) must be(Some(msbMtDetails))
    }


    "convert to  frontend MSB model to correct Msb Des model when fundTransfer is false" in {
      val msbMtDetails = MsbMtDetails(true,Some("123456"),
        IpspServicesDetails(true,Some(List(IpspDetails("name","123456789123456")))),false,
        Some("12345678963"),Some(CountriesList(List("GB"))),Some(CountriesList(List("LA", "LV"))))

      val msbService = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal))

      val bm = BusinessMatching(ReviewDetailsModel, BusinessActivitiesModel, msbServices = Some(msbService),None, None,
        Some(BusinessAppliedForPSRNumberYes("123456")))
      val businessUseAnIPSP = BusinessUseAnIPSPYes("name", "123456789123456")
      val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")

      val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "PQR"),
        usesForeignCurrencies = true,
        Some(BankMoneySource("Bank names")),
        Some(WholesalerMoneySource("wholesaler names")), customerMoneySource = true)

      val mostTransactions = MostTransactions(Seq("LA", "LV"))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Second),
        Some(businessUseAnIPSP),
        Some(IdentifyLinkedTransactions(true)),
        Some(SendMoneyToOtherCountry(true)),
        Some(FundsTransfer(false)),
        Some(BranchesOrAgents(true, Some(Seq("GB")))),
        Some(TransactionsInNext12Months("12345678963")),
        Some(CETransactionsInNext12Months("12345678963")),
        Some(sendTheLargestAmountsOfMoney),
        Some(mostTransactions),
        Some(whichCurrencies)
      )
      MsbMtDetails.conv(msbModel, bm) must be(Some(msbMtDetails))
    }


    "convert to  frontend MSB model to correct Msb Des model when psrNumber option is false" in {
      val msbMtDetails = MsbMtDetails(false,None,
        IpspServicesDetails(true,Some(List(IpspDetails("name","123456789123456")))),false,
        Some("12345678963"),Some(CountriesList(List("GB"))),Some(CountriesList(List("LA", "LV"))))

      val msbService = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal))

      val bm = BusinessMatching(ReviewDetailsModel, BusinessActivitiesModel, msbServices = Some(msbService),None,
        None, Some(BusinessAppliedForPSRNumberNo))
      val businessUseAnIPSP = BusinessUseAnIPSPYes("name", "123456789123456")
      val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")

      val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "PQR"),
        true,
        Some(BankMoneySource("Bank names")),
        Some(WholesalerMoneySource("wholesaler names")), true)

      val mostTransactions = MostTransactions(Seq("LA", "LV"))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Second),
        Some(businessUseAnIPSP),
        Some(IdentifyLinkedTransactions(true)),
        Some(SendMoneyToOtherCountry(true)),
        Some(FundsTransfer(false)),
        Some(BranchesOrAgents(true, Some(Seq("GB")))),
        Some(TransactionsInNext12Months("12345678963")),
        Some(CETransactionsInNext12Months("12345678963")),
        Some(sendTheLargestAmountsOfMoney),
        Some(mostTransactions),
        Some(whichCurrencies)
      )
      MsbMtDetails.conv(msbModel, bm) must be(Some(msbMtDetails))
    }

    "convert to  frontend MSB model to correct Msb Des model when psrNumberModel is None" in {
      val msbMtDetails = MsbMtDetails(false,None,
        IpspServicesDetails(true,Some(List(IpspDetails("name","123456789123456")))),false,
        Some("12345678963"),Some(CountriesList(List("GB"))),Some(CountriesList(List("LA", "LV"))))

      val msbService = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal))
      val businessUseAnIPSP = BusinessUseAnIPSPYes("name", "123456789123456")
      val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")

      val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "PQR"),
        usesForeignCurrencies = true,
        Some(BankMoneySource("Bank names")),
        Some(WholesalerMoneySource("wholesaler names")), customerMoneySource = true)

      val mostTransactions = MostTransactions(Seq("LA", "LV"))

      val bm = BusinessMatching(ReviewDetailsModel, BusinessActivitiesModel, msbServices = Some(msbService),None,
        None, None)

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Second),
        Some(businessUseAnIPSP),
        Some(IdentifyLinkedTransactions(true)),
        Some(SendMoneyToOtherCountry(true)),
        Some(FundsTransfer(false)),
        Some(BranchesOrAgents(true, Some(Seq("GB")))),
        Some(TransactionsInNext12Months("12345678963")),
        Some(CETransactionsInNext12Months("12345678963")),
        Some(sendTheLargestAmountsOfMoney),
        Some(mostTransactions),
        Some(whichCurrencies)
      )
      MsbMtDetails.conv(msbModel, bm) must be(Some(msbMtDetails))
    }
  }
}
