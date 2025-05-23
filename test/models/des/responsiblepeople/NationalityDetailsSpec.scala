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

package models.des.responsiblepeople

import models.fe.responsiblepeople._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec

import java.time.LocalDate

class NationalityDetailsSpec extends PlaySpec with GuiceOneAppPerSuite {

  "ResponsiblePeople" should {
    "convert frontend model to des model for UkResidence" in {
      val rp = ResponsiblePeople(
        personResidenceType = Some(PersonResidenceType(UKResidence("nino"), "GB", "GB")),
        ukPassport = Some(UKPassportYes("AA111111A")),
        dateOfBirth = Some(DateOfBirth(LocalDate.of(1990, 2, 24)))
      )
      NationalityDetails.convert(rp) must be(
        Some(
          NationalityDetails(
            true,
            Some(IdDetail(Some(UkResident("nino")), None, Some("1990-02-24"))),
            rp.personResidenceType map {
              _.countryOfBirth
            },
            rp.personResidenceType map {
              _.nationality
            }
          )
        )
      )
    }
  }

  "ResponsiblePeople" should {
    "convert frontend model to des model for NonUkResidence" in {
      val rp = ResponsiblePeople(
        personResidenceType = Some(PersonResidenceType(NonUKResidence, "GB", "GB")),
        ukPassport = Some(UKPassportYes("AA111111A")),
        dateOfBirth = Some(DateOfBirth(LocalDate.of(1990, 2, 24)))
      )
      NationalityDetails.convert(rp) must be(
        Some(
          NationalityDetails(
            false,
            Some(
              IdDetail(nonUkResident =
                Some(
                  NonUkResident(
                    Some("1990-02-24"),
                    true,
                    Some(PassportDetail(true, PassportNum(Some("AA111111A"), None)))
                  )
                )
              )
            ),
            rp.personResidenceType map {
              _.countryOfBirth
            },
            rp.personResidenceType map {
              _.nationality
            }
          )
        )
      )
    }
  }
}
