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

package models.fe.moneyservicebusiness

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsSuccess

class SendMoneyToOtherCountrySpec extends PlaySpec {

  "SendMoneyToOtherCountry" should {

    "Json Validation" must {

      "Successfully read/write Json data" in {

        SendMoneyToOtherCountry.format.reads(
          SendMoneyToOtherCountry.format.writes(SendMoneyToOtherCountry(false))
        ) must be(JsSuccess(SendMoneyToOtherCountry(false)))

      }
    }

    "return none when input is none" in {
      SendMoneyToOtherCountry.convMsbMt(None) must be(None)
    }
  }
}
