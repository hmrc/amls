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

import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}
import utils.AmlsBaseSpec

class TaxMattersSpec extends PlaySpec with AmlsBaseSpec {

  "DoesAccountantalsoDealWithTax" must {
    "Serialise yes to json correctly" in {
      val expected = Json.obj(
        "manageYourTaxAffairs" -> true
      )
      TaxMatters.jsonWrites.writes(TaxMatters(true)) should be(expected)
    }

    "Serialise no to json correctly" in {
      val expected = Json.obj(
        "manageYourTaxAffairs" -> false
      )
      TaxMatters.jsonWrites.writes(TaxMatters(false)) should be(expected)
    }

    "Deserialise yes from json Correctly" in {
      val json = Json.obj(
        "manageYourTaxAffairs" -> true
      )
      TaxMatters.jsonReads.reads(json) should be(JsSuccess(TaxMatters(true)))
    }

    "Deserialise no from json Correctly" in {
      val json = Json.obj(
        "manageYourTaxAffairs" -> false
      )
      TaxMatters.jsonReads.reads(json) should be(JsSuccess(TaxMatters(false)))
    }

    "Round trip yes" in {
      TaxMatters.jsonReads.reads(
        TaxMatters.jsonWrites.writes(TaxMatters(true))
      ) should be(JsSuccess(TaxMatters(true)))
    }

    "Round trip no" in {
      TaxMatters.jsonReads.reads(
        TaxMatters.jsonWrites.writes(TaxMatters(false))
      ) should be(JsSuccess(TaxMatters(false)))
    }
  }

}
