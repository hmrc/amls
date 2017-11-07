/*
 * Copyright 2017 HM Revenue & Customs
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

package models.des.responsiblepeople

import models.ResponsiblePeopleSection
import org.scalatestplus.play.PlaySpec

class PreviousNameDetailsSpec extends PlaySpec {

  "PreviousNameDetails" should {
    "successfully convert frontend model to des model" in {
      // scalastyle:off magic.number
      PreviousNameDetails.from(ResponsiblePeopleSection.model.get.head) mustBe
        Some(PreviousNameDetails(true, Some(PersonName(Some("fname"), Some("mname"), Some("lname"))), Some("1990-02-24")))
    }
  }
}
