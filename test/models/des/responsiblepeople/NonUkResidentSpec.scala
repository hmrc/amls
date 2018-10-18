/*
 * Copyright 2018 HM Revenue & Customs
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
import org.joda.time.LocalDate
import org.scalatestplus.play.PlaySpec

class NonUkResidentSpec extends PlaySpec {

  "NonUkResident" should {
    "convert frontend model to des model for UKPassport" in {
      // scalastyle:off magic.number
      val rp = ResponsiblePeople(
        personResidenceType = Some(PersonResidenceType(NonUKResidence, "GB", "GB")),
        ukPassport = Some(UKPassportYes("AA111111A")),
        dateOfBirth = Some(DateOfBirth(new LocalDate(1990, 2, 24)))
      )
      NonUkResident.convert(rp) must be(Some(IdDetail(None, Some(NonUkResident(Some("1990-02-24"),
        true, Some(PassportDetail(true, PassportNum(Some("AA111111A"), None))))))))
    }

    "convert frontend model to des model for NonUKPassport" in {
      val rp = ResponsiblePeople(
        personResidenceType = Some(PersonResidenceType(NonUKResidence, "GB", "GB")),
        nonUKPassport = Some(NonUKPassportYes("1234612124646")),
        dateOfBirth = Some(DateOfBirth(new LocalDate(1990, 2, 24)))
      )
      NonUkResident.convert(rp) must be(Some(IdDetail(None, Some(NonUkResident(Some("1990-02-24"),
        true, Some(PassportDetail(false, PassportNum(None, Some("1234612124646")))))))))
    }

    "convert frontend model to des model for NonUKPassport with no DOB" in {
      val rp = ResponsiblePeople(
        personResidenceType = Some(PersonResidenceType(NonUKResidence, "GB", "GB")),
        nonUKPassport = Some(NonUKPassportYes("1234612124646")),
        dateOfBirth = None
      )
      NonUkResident.convert(rp) must be(Some(IdDetail(None, Some(NonUkResident(None,
        true, Some(PassportDetail(false, PassportNum(None, Some("1234612124646")))))))))
    }

    "convert frontend model to des model for NoPassport" when {
      "nonUkPassport is NoPassport" in {
        val rp = ResponsiblePeople(
          personResidenceType = Some(PersonResidenceType(NonUKResidence, "GB", "GB")),
          nonUKPassport = Some(NoPassport),
          dateOfBirth = Some(DateOfBirth(new LocalDate(1990, 2, 24)))
        )

        NonUkResident.convert(rp) must be(Some(IdDetail(None, Some(NonUkResident(Some("1990-02-24"), false, None)))))
      }

      "nonUkPassport is None" in {
        val rp = ResponsiblePeople(
          personResidenceType = Some(PersonResidenceType(NonUKResidence, "GB", "GB")),
          nonUKPassport = None,
          dateOfBirth = Some(DateOfBirth(new LocalDate(1990, 2, 24)))
        )

        NonUkResident.convert(rp) must be(Some(IdDetail(None, Some(NonUkResident(Some("1990-02-24"), false, None)))))
      }
    }
  }
}

