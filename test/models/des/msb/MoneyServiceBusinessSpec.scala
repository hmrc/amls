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

import models.BusinessMatchingSection
import models.fe.businessmatching._
import models.fe.moneyservicebusiness.ExpectedThroughput.Third
import models.fe.moneyservicebusiness.{MoneyServiceBusiness => FEMoneyServiceBusiness, _}
import org.scalatestplus.play.PlaySpec

class MoneyServiceBusinessSpec extends  PlaySpec {


  "MoneyServiceBusiness" should {

    val feMSb = Some(FEMoneyServiceBusiness(
      Some(Third),Some(BusinessUseAnIPSPYes("IPSPName1","IPSPMLRRegNo1")),
      Some(IdentifyLinkedTransactions(true)),
      Some(SendMoneyToOtherCountry(true)),Some(FundsTransfer(true)),
      Some(BranchesOrAgents(true, Some(List("AD", "GB")))),
      Some(TransactionsInNext12Months("11111111111")),
      Some(CETransactionsInNext12Months("11234567890")),
      Some(SendTheLargestAmountsOfMoney("GB",Some("AD"),None)),Some(MostTransactions(List("AD", "GB"))),
      Some(WhichCurrencies(List("GBP", "XYZ", "ABC"), WhichCurrencies.usesForeignCurrencies, Some(BankMoneySource("BankNames1")),
        Some(WholesalerMoneySource("CurrencyWholesalerNames")),true))))

    "Convert MSB data based on business matching msb services selection of ChequeCashingNotScrapMetal" in {
      val msbService1 = MsbServices(Set(ChequeCashingNotScrapMetal))
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel = Some(MoneyServiceBusiness(Some(MsbAllDetails(Some("999999"),
        true,Some(CountriesList(List("AD", "GB"))), true)),
        None,
        None,None))

      MoneyServiceBusiness.conv(feMSb, feBusinessMatching) must be(convertedModel)

    }

    "Convert MSB data based on business matching msb services selection of ChequeCashingNotScrapMetal and Trasmitting Money" in {
      val msbService1 = MsbServices(Set(ChequeCashingNotScrapMetal, TransmittingMoney))
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel = Some(MoneyServiceBusiness(Some(MsbAllDetails(Some("999999"),
        true,Some(CountriesList(List("AD", "GB"))), true)),
        Some(MsbMtDetails(true,Some("123456"),IpspServicesDetails(true,Some(List(IpspDetails("IPSPName1","IPSPMLRRegNo1")))),
          true,Some("11111111111"),Some(CountriesList(List("GB", "AD"))),
          Some(CountriesList(List("AD", "GB"))))),
        None,None))

      MoneyServiceBusiness.conv(feMSb, feBusinessMatching) must be(convertedModel)

    }

    "Convert MSB data based on selection of all the option of msb services" in {
      val msbService1 = MsbServices(Set(ChequeCashingNotScrapMetal, TransmittingMoney, CurrencyExchange, ChequeCashingScrapMetal))
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel = Some(MoneyServiceBusiness(Some(MsbAllDetails(Some("999999"),
        true,Some(CountriesList(List("AD", "GB"))), true)),
        Some(MsbMtDetails(true,Some("123456"),IpspServicesDetails(true,Some(List(IpspDetails("IPSPName1","IPSPMLRRegNo1")))),
          true,Some("11111111111"),Some(CountriesList(List("GB", "AD"))),
          Some(CountriesList(List("AD", "GB"))))),
        Some(MsbCeDetails(CurrencySources(Some(MSBBankDetails(true,Some(List("BankNames1")))),
          Some(CurrencyWholesalerDetails(true,Some(List("CurrencyWholesalerNames")))),true,"11234567890",
          Some(CurrSupplyToCust(List("GBP", "XYZ", "ABC")))), dealInPhysCurrencies = Some(true))),None))

      MoneyServiceBusiness.conv(feMSb, feBusinessMatching) must be(convertedModel)
    }
  }

}
