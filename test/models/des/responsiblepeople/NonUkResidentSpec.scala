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

import models.fe.responsiblepeople._
import org.joda.time.LocalDate
import org.scalatestplus.play.PlaySpec

class NonUkResidentSpec extends PlaySpec {

  "NonUkResident" should {
    "successfully convert frontend model to des model for UKPassport" in {
      // scalastyle:off magic.number
      val residence = NonUKResidence(new LocalDate(1990,2,24))
      val rp = ResponsiblePeople(
        personResidenceType = Some(PersonResidenceType(residence, "GB", "GB")),
        passportType = Some(UKPassport("AA111111A"))
      )
      NonUkResident.convert(rp) must be(Some(IdDetail(None,Some(NonUkResident("1990-02-24",
        true,Some(PassportDetail(true,PassportNum(Some("AA111111A"),None))))))))
    }

    "successfully convert frontend model to des model for NonUKPassport" in {
      val residence = NonUKResidence(new LocalDate(1990,2,24))
      val rp = ResponsiblePeople(
        personResidenceType = Some(PersonResidenceType(residence, "GB", "GB")),
        passportType = Some(NonUKPassport("1234612124646"))
      )
      NonUkResident.convert(rp) must be(Some(IdDetail(None,Some(NonUkResident("1990-02-24",
        true,Some(PassportDetail(false,PassportNum(None,Some("1234612124646")))))))))
    }

    "successfully convert frontend model to des model for NoPassport" in {
      val residence = NonUKResidence(new LocalDate(1990,2,24))
      val rp = ResponsiblePeople(
        personResidenceType = Some(PersonResidenceType(residence, "GB", "GB")),
        passportType = Some(NoPassport)
      )
      NonUkResident.convert(rp) must be(Some(IdDetail(None,Some(NonUkResident("1990-02-24",false,None)))))
    }
  }

}
