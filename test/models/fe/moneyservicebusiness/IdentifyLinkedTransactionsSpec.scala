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

class IdentifyLinkedTransactionsSpec extends PlaySpec {

  "IdentifyLinkedTransactions" should {

    "Json Validation" must {

      "Successfully read/write Json data" in {

        IdentifyLinkedTransactions.format.reads(
          IdentifyLinkedTransactions.format.writes(IdentifyLinkedTransactions(false))
        ) must be(JsSuccess(IdentifyLinkedTransactions(false)))

      }
    }

    "convert des to fronted" in {
      IdentifyLinkedTransactions.convMsbAll(None) must be(None)

    }
  }
}
