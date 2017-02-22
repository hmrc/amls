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

import models.des.msb._
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.FakeApplication

class WhichCurrenciesPreRelease7Spec extends PlaySpec with OneAppPerSuite {

  override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  "WhichCurrencies" must {

    "not set the 'uses foreign currency' flag" in {

      val msbCe = MsbCeDetailsR7( None,
        Some(CurrencySourcesR7(
          None,
          Some(CurrencyWholesalerDetails(
            true,
            Some(List("CurrencyWholesalerNames"))
          )),
          true
        )),
        "11234567890",
        None
      )

      val convertedModel = Some(WhichCurrencies(List.empty, None, None, Some(WholesalerMoneySource("CurrencyWholesalerNames")), true))

      WhichCurrencies.convMsbCe(Some(msbCe)) must be(convertedModel)
    }
  }
}
