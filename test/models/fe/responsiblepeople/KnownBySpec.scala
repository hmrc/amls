/*
 * Copyright 2021 HM Revenue & Customs
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

package models.fe.responsiblepeople

import models.des.responsiblepeople.OthrNamesOrAliasesDetails
import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class KnownBySpec extends PlaySpec with MustMatchers {

  "The KnownBy model" must {
    "convert to the correct frontend model" when {
      "given the DES model" in {
        val desModel = OthrNamesOrAliasesDetails(otherNamesOrAliases = true, Some(Seq("some", "name")))

        KnownBy.conv(Some(desModel)) mustBe Some(KnownBy(hasOtherNames = true, Some("some name")))
      }

      "given a DES model where the flag is false" in {
        val desModel = OthrNamesOrAliasesDetails(otherNamesOrAliases = false, None)

        KnownBy.conv(Some(desModel)) mustBe Some(KnownBy.noOtherNames)
      }

      "given None" in {
        KnownBy.conv(None) mustBe Some(KnownBy(hasOtherNames = false, None))
      }
    }

    "serialise to the correct JSON" in {
      val model = KnownBy(true, Some("name"))

      Json.toJson(model) mustBe Json.obj(
        "hasOtherNames" -> true,
        "otherNames" -> "name"
      )
    }

    "deserialise to the correct model" in {
      val json = Json.obj(
        "hasOtherNames" -> true,
        "otherNames" -> "jimbob"
      )

      json.as[KnownBy] mustBe KnownBy(true, Some("jimbob"))
    }
  }

}
