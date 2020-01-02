/*
 * Copyright 2020 HM Revenue & Customs
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

class UkResidentSpec extends PlaySpec {

    "UkResident" should {
        "convert frontend model to des model for UkResidence with DOB" in {
            UkResident.convert(UKResidence("nino"), Some(DateOfBirth(new LocalDate(1990, 2, 24))))must be(
                Some(IdDetail(Some(UkResident("nino")), dateOfBirth = Some("1990-02-24"))))
        }

        "convert frontend model to des model for UkResidence without DOB" in {
            UkResident.convert(UKResidence("nino"), None) must be(Some(IdDetail(Some(UkResident("nino")))))
        }
    }

}
