/*
 * Copyright 2019 HM Revenue & Customs
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

import models.des.DesConstants
import models.des.msb.{CountriesList, MsbAllDetails}
import models.fe.businessmatching.{ChequeCashingNotScrapMetal, MsbServices, TransmittingMoney}
import models.fe.moneyservicebusiness.ExpectedThroughput.Third
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication

class MoneyServiceBusinessSpec extends PlaySpec with MoneyServiceBusinessTestData with OneAppPerSuite {

  "MoneyServiceBusiness" should {

    "have an implicit conversion from Option which" when {

      "called with None" should {

        "return a default version of MoneyServiceBusiness" in {

          val res: MoneyServiceBusiness = None
          res must be(emptyModel)
        }
      }

      "called with a concrete value" should {
        "return the value passed in extracted from the option" in {
          val res: MoneyServiceBusiness = Some(completeModel)
          res must be(completeModel)
        }
      }
    }

    "Serialise to expected Json" when {
      "model is complete" in {
        Json.toJson(completeModel) must be(completeJson)
      }
    }

    "Deserialise from Json as expected" when {
      "model is complete" in {
        completeJson.as[MoneyServiceBusiness] must be(completeModel)
      }
    }

    "convert des msb to frontend msb model" in {

      val convertedMsb = Some(MoneyServiceBusiness(
        Some(Third),Some(BusinessUseAnIPSPYes("IPSPName1","IPSPMLRRegNo1")),
        Some(IdentifyLinkedTransactions(true)),
        Some(SendMoneyToOtherCountry(true)),Some(FundsTransfer(true)),
        Some(BranchesOrAgents(true, Some(List("AD", "GB")))),
        Some(TransactionsInNext12Months("11111111111")),
        Some(CETransactionsInNext12Months("11234567890")),
        Some(SendTheLargestAmountsOfMoney("GB",Some("AD"), None)),
        Some(MostTransactions(List("AD", "GB"))),
        Some(WhichCurrencies(List("GBP", "XYZ", "ABC"), Some(true), Some(BankMoneySource("BankNames1")), Some(WholesalerMoneySource("CurrencyWholesalerNames")), true)),
        Some(FXTransactionsInNext12Months("234234234"))
      ))

      val release7SubscriptionViewModel = DesConstants.SubscriptionViewModel.copy(msb = Some(DesConstants.testMsb.copy(
        msbAllDetails = Some(MsbAllDetails(
          Some("£50k-£100k"),
          true,
          Some(CountriesList(List("AD", "GB"))),
          true)
        ))))

      MoneyServiceBusiness.conv(release7SubscriptionViewModel) must be(convertedMsb)

    }

    "evaluate getMsbAll whn input is none" in {
      MoneyServiceBusiness.getMsbAll(None) must be(None)
    }

    "evaluate getMsbMtDetails whn input is none" in {
      MoneyServiceBusiness.getMsbMtDetails(None) must be(None)
    }

    "evaluate getMsbCeDetails whn input is none" in {
      MoneyServiceBusiness.getMsbCeDetails(None) must be(None)
    }

    "evaluate getMsbFxDetails whn input is none" in {
      MoneyServiceBusiness.getMsbFxDetails(None) must be(None)
    }

  }
}


trait MoneyServiceBusinessTestData {
  private val msbService = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal))
  private val businessUseAnIPSP = BusinessUseAnIPSPYes("name", "123456789123456")
  private val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")

  val completeModel = MoneyServiceBusiness(
    throughput = Some(ExpectedThroughput.Second),
    businessUseAnIPSP = Some(businessUseAnIPSP),
    identifyLinkedTransactions = Some(IdentifyLinkedTransactions(true)),
    sendMoneyToOtherCountry = Some(SendMoneyToOtherCountry(true)),
    fundsTransfer = Some(FundsTransfer(true)),
    branchesOrAgents = Some(BranchesOrAgents(true, Some(Seq("GB")))),
    sendTheLargestAmountsOfMoney = Some(sendTheLargestAmountsOfMoney),
    mostTransactions = Some(MostTransactions(Seq("GB"))),
    transactionsInNext12Months = Some(TransactionsInNext12Months("12345678963")),
    ceTransactionsInNext12Months = Some(CETransactionsInNext12Months("12345678963")),
    fxTransactionsInNext12Months = Some(FXTransactionsInNext12Months("987654321"))
  )

  val emptyModel = MoneyServiceBusiness(None)


  val completeJson = Json.obj(
      "throughput" -> Json.obj("throughput" -> "02"),
      "businessUseAnIPSP" -> Json.obj("useAnIPSP" -> true,
      "name" -> "name",
      "referenceNumber" -> "123456789123456"),
      "identifyLinkedTransactions" -> Json.obj("linkedTxn" -> true),
      "sendMoneyToOtherCountry" -> Json.obj("money" -> true),
      "fundsTransfer" -> Json.obj("transferWithoutFormalSystems" -> true),
      "branchesOrAgents" -> Json.obj("hasCountries" -> true,"countries" ->Json.arr("GB")),
      "transactionsInNext12Months" -> Json.obj("txnAmount" -> "12345678963"),
      "fundsTransfer" -> Json.obj("transferWithoutFormalSystems" -> true),
      "mostTransactions" -> Json.obj("mostTransactionsCountries" -> Seq("GB")),
      "sendTheLargestAmountsOfMoney" -> Json.obj("country_1" ->"GB"),
      "ceTransactionsInNext12Months" -> Json.obj("ceTransaction" -> "12345678963"),
      "fxTransactionsInNext12Months" -> Json.obj("fxTransaction" -> "987654321")
  )

  val emptyJson = Json.obj("msbServices" -> Json.arr())
}
