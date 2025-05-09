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

class TransactionsInNext12MonthsSpec extends PlaySpec {

  "TransactionsInNext12Months" should {

    "Json Validation" must {

      "Successfully read/write Json data" in {

        TransactionsInNext12Months.format.reads(
          TransactionsInNext12Months.format.writes(TransactionsInNext12Months("12345678963"))
        ) must be(JsSuccess(TransactionsInNext12Months("12345678963")))

      }
    }

    "return none when input is none" in {
      TransactionsInNext12Months.convMsbMt(None) must be(None)
    }
  }
}
