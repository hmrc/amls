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

package models.des.aboutthebusiness

import models.fe.businessdetails._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class PreviouslyRegisteredViewSpec extends PlaySpec {

  "PreviouslyRegistered" must {

    val Contact = ContactingYou("123456789", "afa@test.com")
    val Office = RegisteredOfficeUK("1", "2", None, None, "NE3 0QQ")

    "deserialise PreviouslyRegistered model" when {
      "given option yes" in {

        val json = Json.obj(
          "amlsRegistered" -> true,
          "mlrRegNumber" -> "12345678",
          "prevRegForMlr" -> false)
        val mlr = PreviouslyRegisteredMLRView(true, Some("12345678"), false, None)

        PreviouslyRegisteredMLRView.format.reads(json) must be(JsSuccess(mlr))
      }

      "given option no" in {

        val json = Json.obj(
          "amlsRegistered" -> false,
          "prevMlrRegNumber" -> "123456789123654",
          "prevRegForMlr" -> true)
        val mlr = PreviouslyRegisteredMLRView(false, None, true, Some("123456789123654"))

        PreviouslyRegisteredMLRView.format.reads(json) must be(JsSuccess(mlr))
      }
    }

    "serialise PreviouslyRegistered model" when {
      "given option yes" in {

        val json = Json.obj(
          "amlsRegistered" -> true,
          "mlrRegNumber" -> "12345678",
          "prevRegForMlr" -> false)
        val mlr = PreviouslyRegisteredMLRView(true, Some("12345678"), false, None)

        PreviouslyRegisteredMLRView.format.reads(json) must be(JsSuccess(mlr))
      }

      "given option no" in {

        val mlr = PreviouslyRegisteredMLRView(false, None, true, Some("123456789123654"))
        val json = Json.obj(
          "amlsRegistered" -> false,
          "prevMlrRegNumber" -> "123456789123654",
          "prevRegForMlr" -> true)

        PreviouslyRegisteredMLRView.format.reads(json) must be(JsSuccess(mlr))
      }

      "successfully evaluate api5 and api6 data BusinessReferencesAll" in {

        val viewBusinessReferencesAll = PreviouslyRegisteredMLRView(false,
          None,
          false,
          None)

        val desBusinessReferencesAll = PreviouslyRegisteredMLRView(false,
          None,
          false,
          None)

        viewBusinessReferencesAll.equals(desBusinessReferencesAll) must be(true)

      }


      "successfully evaluate api5 and api6 data BusinessReferencesAll when data is changed" in {

        val viewBusinessReferencesAll = PreviouslyRegisteredMLRView(false,
          None,
          false,
          None)

        val desBusinessReferencesAll = PreviouslyRegisteredMLRView(false,
          None,
          true,
          Some("123456789456321"))

        viewBusinessReferencesAll.equals(desBusinessReferencesAll) must be(false)

      }


      "successfully evaluate api5 and api6 data BusinessReferencesAll when data is changed1" in {

        val viewBusinessReferencesAll = PreviouslyRegisteredMLRView(false,
          None,
          true,
          Some("123212312456877"))

        val desBusinessReferencesAll = PreviouslyRegisteredMLRView(false,
          None,
          true,
          Some("123456789456321"))

        viewBusinessReferencesAll.equals(desBusinessReferencesAll) must be(false)

      }

      "successfully evaluate api5 and api6 data BusinessReferencesAll when data is changed2" in {

        val viewBusinessReferencesAll = PreviouslyRegisteredMLRView(true,
          None,
          true,
          Some("123212312456877"))

        val desBusinessReferencesAll = PreviouslyRegisteredMLRView(false,
          None,
          true,
          Some("123456789456321"))

        viewBusinessReferencesAll.equals(desBusinessReferencesAll) must be(false)

      }
    }
  }
}
