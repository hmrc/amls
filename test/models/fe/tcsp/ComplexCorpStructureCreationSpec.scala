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

package models.fe.tcsp

import models.des.DesConstants
import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec

class ComplexCorpStructureCreationSpec extends PlaySpec with MustMatchers {

  "The ComplexCorpStructureCreation model" when {
    "given a valid model" must {
      "return the form values" when {
        "for json" when {
          "complexCorpStructureCreation is 'yes'" in {
            val model = ComplexCorpStructureCreationYes
            val result = ComplexCorpStructureCreation.jsonWrite.writes(model).toString()
            val expected = "{\"complexCorpStructureCreation\":true}"

            result mustBe expected
          }
          "complexCorpStructureCreation is 'no'" in {
            val model = ComplexCorpStructureCreationNo
            val result = ComplexCorpStructureCreation.jsonWrite.writes(model).toString()
            val expected = "{\"complexCorpStructureCreation\":false}"

            result mustBe expected
          }
        }
      }

      "converting the des subscription model must yield a frontend TCSP model" in {
        ComplexCorpStructureCreation.conv(DesConstants.SubscriptionViewModel) must
          be(Some(ComplexCorpStructureCreationYes))
      }
    }
  }
}
