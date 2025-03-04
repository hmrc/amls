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

package models.des.msb

import models.BusinessMatchingSection
import models.fe.businessmatching._
import models.fe.moneyservicebusiness.ExpectedThroughput.Third
import models.fe.moneyservicebusiness.{MoneyServiceBusiness => FEMoneyServiceBusiness, _}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec

class MoneyServiceBusinessSpec extends PlaySpec with GuiceOneAppPerSuite {

  "MoneyServiceBusiness" should {

    val feMSb = Some(
      FEMoneyServiceBusiness(
        Some(Third),
        Some(BusinessUseAnIPSPYes("IPSPName1", "IPSPMLRRegNo1")),
        Some(IdentifyLinkedTransactions(true)),
        Some(SendMoneyToOtherCountry(true)),
        Some(FundsTransfer(true)),
        Some(BranchesOrAgents(true, Some(List("AD", "GB")))),
        Some(TransactionsInNext12Months("11111111111")),
        Some(CETransactionsInNext12Months("11234567890")),
        Some(SendTheLargestAmountsOfMoney("GB", Some("AD"), None)),
        Some(MostTransactions(List("AD", "GB"))),
        Some(
          WhichCurrencies(
            List("GBP", "XYZ", "ABC"),
            usesForeignCurrencies = Some(true),
            Some(BankMoneySource("BankNames1")),
            Some(WholesalerMoneySource("CurrencyWholesalerNames")),
            true
          )
        ),
        Some(FXTransactionsInNext12Months("456456456"))
      )
    )

    val msbAllDetails: MsbAllDetails =
      MsbAllDetails(Some("£50k-£100k"), true, Some(CountriesList(List("AD", "GB"))), true)

    val msbMtDetails: MsbMtDetails = MsbMtDetails(
      true,
      Some("123456"),
      IpspServicesDetails(true, Some(List(IpspDetails("IPSPName1", "IPSPMLRRegNo1")))),
      true,
      Some("11111111111"),
      Some(CountriesList(List("GB", "AD"))),
      Some(CountriesList(List("AD", "GB"))),
      Some(false)
    )

    val msbCeDetails: MsbCeDetailsR7 = MsbCeDetailsR7(
      Some(true),
      Some(
        CurrencySourcesR7(
          Some(MSBBankDetails(true, Some(List("BankNames1")))),
          Some(CurrencyWholesalerDetails(true, Some(List("CurrencyWholesalerNames")))),
          true
        )
      ),
      "11234567890",
      Some(CurrSupplyToCust(List("GBP", "XYZ", "ABC")))
    )

    val msbFxDetails: MsbFxDetails = MsbFxDetails("456456456")

    "convert MSB data based on business matching msb services selection of ChequeCashingNotScrapMetal" in {
      val msbService1        = MsbServices(Set(ChequeCashingNotScrapMetal))
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel = Some(models.des.msb.MoneyServiceBusiness(Some(msbAllDetails), None, None, None))

      models.des.msb.MoneyServiceBusiness.conv(feMSb, feBusinessMatching, amendVariation = true) must be(convertedModel)
    }

    "convert MSB data based on business matching msb services selection of ChequeCashingNotScrapMetal and Transmitting Money" in {
      val msbService1        = MsbServices(Set(ChequeCashingNotScrapMetal, TransmittingMoney))
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel =
        Some(models.des.msb.MoneyServiceBusiness(Some(msbAllDetails), Some(msbMtDetails), None, None))

      models.des.msb.MoneyServiceBusiness.conv(feMSb, feBusinessMatching, amendVariation = true) must be(convertedModel)
    }

    "convert MSB data based on selection of ChequeCashingNotScrapMetal, TransmittingMoney, CurrencyExchange, ChequeCashingScrapMetal" in {
      val msbService1        =
        MsbServices(Set(ChequeCashingNotScrapMetal, TransmittingMoney, CurrencyExchange, ChequeCashingScrapMetal))
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel =
        Some(models.des.msb.MoneyServiceBusiness(Some(msbAllDetails), Some(msbMtDetails), Some(msbCeDetails), None))

      models.des.msb.MoneyServiceBusiness.conv(feMSb, feBusinessMatching, amendVariation = true) must be(convertedModel)
    }

    "convert MSB data based on selection of ForeignExchange" in {
      val msbService1        = MsbServices(Set(ForeignExchange))
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel =
        Some(models.des.msb.MoneyServiceBusiness(Some(msbAllDetails), None, None, Some(msbFxDetails)))

      models.des.msb.MoneyServiceBusiness.conv(feMSb, feBusinessMatching, amendVariation = true) must be(convertedModel)
    }

    "convert MSB data based on selection of ForeignExchange, CurrencyExchange" in {
      val msbService1        = MsbServices(Set(ForeignExchange, CurrencyExchange))
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel =
        Some(models.des.msb.MoneyServiceBusiness(Some(msbAllDetails), None, Some(msbCeDetails), Some(msbFxDetails)))

      models.des.msb.MoneyServiceBusiness.conv(feMSb, feBusinessMatching, amendVariation = true) must be(convertedModel)
    }

    "convert MSB data based on selection of ForeignExchange, TransmittingMoney" in {
      val msbService1        = MsbServices(Set(ForeignExchange, TransmittingMoney))
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel =
        Some(models.des.msb.MoneyServiceBusiness(Some(msbAllDetails), Some(msbMtDetails), None, Some(msbFxDetails)))

      models.des.msb.MoneyServiceBusiness.conv(feMSb, feBusinessMatching, amendVariation = true) must be(convertedModel)
    }

    "convert MSB data based on selection of all the option of msb services" in {
      val msbService1        = MsbServices(
        Set(ChequeCashingNotScrapMetal, TransmittingMoney, CurrencyExchange, ChequeCashingScrapMetal, ForeignExchange)
      )
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel = Some(
        models.des.msb.MoneyServiceBusiness(
          Some(msbAllDetails),
          Some(msbMtDetails),
          Some(msbCeDetails),
          Some(msbFxDetails)
        )
      )

      models.des.msb.MoneyServiceBusiness.conv(feMSb, feBusinessMatching, amendVariation = true) must be(convertedModel)
    }

    "send None for psrRefChangeFlag if the submission is not an amendment or variation" in {
      val msbService1        = MsbServices(
        Set(ChequeCashingNotScrapMetal, TransmittingMoney, CurrencyExchange, ChequeCashingScrapMetal, ForeignExchange)
      )
      val feBusinessMatching = BusinessMatchingSection.model.copy(msbServices = Some(msbService1))

      val convertedModel = Some(
        models.des.msb.MoneyServiceBusiness(
          Some(msbAllDetails),
          Some(
            MsbMtDetails(
              true,
              Some("123456"),
              IpspServicesDetails(true, Some(List(IpspDetails("IPSPName1", "IPSPMLRRegNo1")))),
              true,
              Some("11111111111"),
              Some(CountriesList(List("GB", "AD"))),
              Some(CountriesList(List("AD", "GB"))),
              None
            )
          ),
          Some(msbCeDetails),
          Some(msbFxDetails)
        )
      )

      models.des.msb.MoneyServiceBusiness.conv(feMSb, feBusinessMatching, amendVariation = false) must be(
        convertedModel
      )
    }
  }

}
