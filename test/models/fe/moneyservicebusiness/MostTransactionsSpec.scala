/*
 * Copyright 2016 HM Revenue & Customs
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

import models.des.msb.{CountriesList, IpspServicesDetails, MsbMtDetails}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsPath, JsSuccess, Json}

class MostTransactionsSpec extends PlaySpec {

  "MostTransactions" must {

    "roundtrip through json" in {

      val model: MostTransactions = MostTransactions(Seq("GB"))

      Json.fromJson[MostTransactions](Json.toJson(model)) mustEqual JsSuccess(model, JsPath \ "mostTransactionsCountries" )
    }
  }

  "MostTransaction conversion" when {
    "There are countries in the list" must {
      "return a Correct model" in {
        MostTransactions.convMsbMt(Some(
          MsbMtDetails(
            applyForFcapsrRegNo = false,
            fcapsrRefNo = None,
            ipspServicesDetails = IpspServicesDetails(false, None),
            informalFundsTransferSystem = false,
            noOfMoneyTrnsfrTransNxt12Mnths = None,
            countriesLrgstMoneyAmtSentTo = None,
            countriesLrgstTranscsSentTo = Some(CountriesList(Seq("Country1", "Country2", "Country3"))
            )
          )
        )) must be (Some(MostTransactions(Seq("Country1", "Country2", "Country3"))))
      }
    }

    "The countries list is empty" must {
      "return a None" in {
        MostTransactions.convMsbMt(Some(
          MsbMtDetails(
            applyForFcapsrRegNo = false,
            fcapsrRefNo = None,
            ipspServicesDetails = IpspServicesDetails(false, None),
            informalFundsTransferSystem = false,
            noOfMoneyTrnsfrTransNxt12Mnths = None,
            countriesLrgstMoneyAmtSentTo = None,
            countriesLrgstTranscsSentTo = Some(CountriesList(Seq.empty[String])
            )
          )
        )) must be (None)
      }
    }

    "There is no list of countries" must {
        "return a None" in {
          MostTransactions.convMsbMt(Some(
            MsbMtDetails(
              applyForFcapsrRegNo = false,
              fcapsrRefNo = None,
              ipspServicesDetails = IpspServicesDetails(false, None),
              informalFundsTransferSystem = false,
              noOfMoneyTrnsfrTransNxt12Mnths = None,
              countriesLrgstMoneyAmtSentTo = None,
              countriesLrgstTranscsSentTo = None
            )
          )) must be (None)
        }
    }
  }
}
