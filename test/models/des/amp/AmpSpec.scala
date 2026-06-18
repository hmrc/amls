/*
 * Copyright 2026 HM Revenue & Customs
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

package models.des.amp

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AmpSpec extends AnyWordSpec with Matchers {

  "Amp.getPercentage" should {

    "return 20 for zeroToTwenty" in {
      Amp.getPercentage(Some("zeroToTwenty")) shouldBe 20
    }

    "return 40 for twentyOneToForty" in {
      Amp.getPercentage(Some("twentyOneToForty")) shouldBe 40
    }

    "return 60 for fortyOneToSixty" in {
      Amp.getPercentage(Some("fortyOneToSixty")) shouldBe 60
    }

    "return 80 for sixtyOneToEighty" in {
      Amp.getPercentage(Some("sixtyOneToEighty")) shouldBe 80
    }

    "return 100 for eightyOneToOneHundred" in {
      Amp.getPercentage(Some("eightyOneToOneHundred")) shouldBe 100
    }

    "return 0 for an unknown value" in {
      Amp.getPercentage(Some("invalid")) shouldBe 0
    }

    "return 0 when None is supplied" in {
      Amp.getPercentage(None) shouldBe 0
    }
  }
}
