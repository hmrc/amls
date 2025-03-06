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

package models.des

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class ChangeIndicatorsSpec extends PlaySpec with GuiceOneAppPerSuite {

  "ChangeIndicators" must {
    "serialize correctly" in {

      val json = Json.parse("""{
    "businessDetails": false,
    "businessAddress": false,
    "businessReferences": true,
    "tradingPremises": true,
    "businessActivities": true,
    "bankAccountDetails": true,
    "msb": {
    "msb": true
    },
    "hvd": {
    "hvd": false
    },
    "asp": {
    "asp": true
    },
    "aspOrTcsp": {
    "aspOrTcsp": false
    },
    "tcsp": {
    "tcsp": true
    },
    "eab": {
    "eab": true
    },
    "amp": {
    "amp": true
    },
    "responsiblePersons": false,
    "filingIndividual": true
  }""")

      val changeIndicators =
        ChangeIndicators(false, false, true, true, true, true, true, false, true, false, true, true, true, false, true)

      ChangeIndicators.format.writes(changeIndicators) must be(json)

    }
  }

}
