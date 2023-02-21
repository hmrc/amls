/*
 * Copyright 2023 HM Revenue & Customs
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

package models.enrolment

import generators.AmlsReferenceNumberGenerator
import models.{KnownFactsForService, KnownFact => GGKNownFact}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class KnownFactsSpec extends PlaySpec with AmlsReferenceNumberGenerator {

  "The model" must {
    "serialize to the correct Json" in {
      val model = KnownFacts(Set(
        KnownFact("Postcode", "TF2 6NU"),
        KnownFact("NINO", "AB123456X")
      ))

      val expectedJson = Json.obj(
        "verifiers" -> Json.arr(
          Json.obj("key" -> "Postcode", "value" -> "TF2 6NU"),
          Json.obj("key" -> "NINO", "value" -> "AB123456X")
        )
      )

      Json.toJson(model) mustBe expectedJson
    }

    "convert from legacy KnownFacts model" which {
      "filters identifier MLRRefNumber" in {

        val legacyModel = KnownFactsForService(Seq(
          GGKNownFact("MLRRefNumber", amlsRegistrationNumber),
          GGKNownFact("Postcode", "TF2 6NU"),
          GGKNownFact("NINO", "AB123456X")
        ))

        val currentModel = KnownFacts(Set(
          KnownFact("Postcode", "TF2 6NU"),
          KnownFact("NINO", "AB123456X")
        ))

        KnownFacts.conv(legacyModel) must be(currentModel)

      }
    }
  }


}
