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

import models.des.msb.{CountriesList, IpspDetails, IpspServicesDetails, MsbMtDetails}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsSuccess

class SendTheLargestAmountsOfMoneySpec extends PlaySpec {


  "SendTheLargestAmountsOfMoney" must {

    "JSON validation" must {
      "successfully validate givcen values" in {
        val data = SendTheLargestAmountsOfMoney("GB")

        SendTheLargestAmountsOfMoney.format.reads(SendTheLargestAmountsOfMoney.format.writes(data)) must
          be(JsSuccess(data))
      }
     }

    "convert des to frontend model" in {
      val msbMtDetails = Some(MsbMtDetails(
        true,
        Some("123456"),
        IpspServicesDetails(
          true,
          Some(List(IpspDetails("IPSPName1", "IPSPMLRRegNo1"), IpspDetails("IPSPName2", "IPSPMLRRegNo2")))
        ),
        true,
        Some("11111111111"),
        Some(CountriesList(List("GB", "AD"))),
        Some(CountriesList(List("AD", "GB")))
      ))
      SendTheLargestAmountsOfMoney.convMsbMt(msbMtDetails) must be(Some(SendTheLargestAmountsOfMoney("GB",Some("AD"),None)))
    }

    "convert des to frontend model when no countries listed" in {
      val msbMtDetails = Some(MsbMtDetails(
        true,
        Some("123456"),
        IpspServicesDetails(
          true,
          Some(List(IpspDetails("IPSPName1", "IPSPMLRRegNo1"), IpspDetails("IPSPName2", "IPSPMLRRegNo2")))
        ),
        true,
        Some("11111111111"),
        None,
        None)
      )
      SendTheLargestAmountsOfMoney.convMsbMt(msbMtDetails) must be(None)
    }
  }
}
