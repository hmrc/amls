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

package models.fe.responsiblepeople

import models.des.responsiblepeople.{PreviousNameDetails, PersonName => DesPersonName}
import org.joda.time.LocalDate
import org.scalatestplus.play.PlaySpec

class PreviousNameSpec extends PlaySpec {

  "PreviousName" must {
    "Json Read/write with PreviousName details" in {
      val previousName = PreviousName(true, Some("fname"), Some("mname"), Some("lname"))

      PreviousName.format.reads(PreviousName.format.writes(previousName))
    }

    "convert des name details to frontend" in {

      val previousNameDetails = Some(PreviousNameDetails(
        true,
        Some(DesPersonName(Some("first name"), Some("middle name"), Some("last name"))),
        Some("2001-01-01")
      ))

      val previousName = PreviousName(true, Some("first name"), Some("middle name"), Some("last name"))
      PreviousName.conv(previousNameDetails) must be(Some(previousName))
    }

    "convert des name details to frontend when previousNameDetails is none" in {
      PreviousName.conv(None) mustBe Some(PreviousName.noPreviousName)
    }

    "convert des name details to frontend when previousNameDetails has none values" in {
      val previousNameDetails = Some(PreviousNameDetails(
        false,
        None,
        None
      ))

      PreviousName.conv(previousNameDetails) mustBe Some(PreviousName.noPreviousName)
    }
  }
}
