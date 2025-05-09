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

package models.fe.businessactivities

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class TransactionTypesSpec extends PlaySpec {

  "The TransactionTypes model" must {

    "serialise to Json correctly" in {
      val model = TransactionTypes(Set(Paper, DigitalSpreadsheet, DigitalSoftware("Some software")))

      Json.toJson(model) mustBe Json.obj(
        "types"    -> Seq("01", "02", "03"),
        "software" -> "Some software"
      )
    }

  }

}
