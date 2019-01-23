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

package models.des.msb

import models.fe.moneyservicebusiness._
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.FakeApplication

class MsbAllDetailsSpec extends PlaySpec with OneAppPerSuite{

  "MsbAllDetails" should {

    "convert to  frontend MSB model to correct Msb Des model when ExpectedThroughput is None" in {
      val msbAllDetails = Some(MsbAllDetails(None, false, None, false))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(None,
        None)
      MsbAllDetails.conv(msbModel) must be(msbAllDetails)
    }

    "convert to  frontend MSB model to correct Msb Des model when ExpectedThroughput is First" in {
      val msbAllDetails = Some(MsbAllDetails(Some("£0-£15k"), false, None, false))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.First))
      MsbAllDetails.conv(msbModel) must be(msbAllDetails)
    }

    "convert to  frontend MSB model to correct Msb Des model when ExpectedThroughput is Third" in {
      val msbAllDetails = Some(MsbAllDetails(Some("£50k-£100k"), false, None, true))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Third),
        None,
        Some(IdentifyLinkedTransactions(true)),
        None,
        None,
        Some(BranchesOrAgents(false, None))
      )
      MsbAllDetails.conv(msbModel) must be(msbAllDetails)
    }

    "convert to  frontend MSB model to correct Msb Des model whenExpectedThroughput is Fourth" in {
      val msbAllDetails = Some(MsbAllDetails(Some("£100k-£250k"), true, Some(CountriesList(List("GB"))), true))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Fourth),
        None,
        Some(IdentifyLinkedTransactions(true)),
        None,
        None,
        Some(BranchesOrAgents(true, Some(Seq("GB"))))
      )
      MsbAllDetails.conv(msbModel) must be(msbAllDetails)
    }

    "convert to  frontend MSB model to correct Msb Des model whenExpectedThroughput is Fifth" in {
      val msbAllDetails = Some(MsbAllDetails(Some("£250k-£1m"), false, None, false))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Fifth))
      MsbAllDetails.conv(msbModel) must be(msbAllDetails)
    }

    "convert to  frontend MSB model to correct Msb Des model whenExpectedThroughput is Sixth" in {
      val msbAllDetails = Some(MsbAllDetails(Some("£1m-10m"), false, None, false))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Sixth))
      MsbAllDetails.conv(msbModel) must be(msbAllDetails)
    }


    "convert to  frontend MSB model to correct Msb Des model whenExpectedThroughput is Seventh" in {
      val msbAllDetails = Some(MsbAllDetails(Some("£10m+"), false, None, false))

      val msbModel = models.fe.moneyservicebusiness.MoneyServiceBusiness(
        Some(ExpectedThroughput.Seventh))
      MsbAllDetails.conv(msbModel) must be(msbAllDetails)
    }
  }

}
