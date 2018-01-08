/*
 * Copyright 2018 HM Revenue & Customs
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

package models.fe.businessmatching

import models.des.businessactivities.MsbServicesCarriedOut
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class MsbServicesSpec extends PlaySpec {

  "MsbServices" must {

    "round trip through Json correctly" in {

      val data = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal, CurrencyExchange))
      val js = Json.toJson(data)

      js.as[MsbServices] mustEqual data
    }

    "convert des to frontend successfully when inputs are false" in {
      MsbServices.conv(Some(MsbServicesCarriedOut(false, false, false, false, false))) must be(None)
    }

    "convert des to frontend successfully for the valid input" in {
      MsbServices.conv(Some(MsbServicesCarriedOut(true, true, true, true, true))) must be(Some(MsbServices(Set(TransmittingMoney,
        CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))))
    }

    "convert des to frontend successfully when input is none" in {
      MsbServices.conv(None) must be(None)
    }
  }
}
