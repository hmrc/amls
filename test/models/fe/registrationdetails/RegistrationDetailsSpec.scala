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

package models.fe.registrationdetails

import models.des.registrationdetails.{Individual, LLP, Organisation, RegistrationDetails => DesRegistrationDetails}
import org.joda.time.LocalDate
import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec

class RegistrationDetailsSpec extends PlaySpec with MustMatchers {

  "The RegistrationDetails model" must {
    "serialise to Json" when {
      "given an 'Individual' des model" in {
        //noinspection ScalaStyle
        val model = DesRegistrationDetails(true, Individual("Forename", None, "Surname"))

        RegistrationDetails.convert(model) mustBe RegistrationDetails(
          companyName = "Forename Surname",
          isIndividual = true
        )
      }

      "given an 'organisation' des model" in {
        val model = DesRegistrationDetails(false, Organisation("Test Company Name", isAGroup = Some(false), Some(LLP)))

        RegistrationDetails.convert(model) mustBe RegistrationDetails(
          companyName = "Test Company Name",
          isIndividual = false
        )
      }
    }
  }
}
