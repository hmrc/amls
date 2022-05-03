/*
 * Copyright 2022 HM Revenue & Customs
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

import models.des.responsiblepeople._
import org.scalatestplus.play.PlaySpec

class ResidenceTypeSpec extends PlaySpec {

  "ResidenceType" must {

    "convert des.NationalityDetails to fe.ResidenceType" in {
      val desModel = Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            Some("2001-01-01"),
            true,
            Some(PassportDetail(
              true,
              PassportNum(Some("AA1111111"), None)
            ))
          ))
        )),
        Some("AD"),
        Some("AD")
      ))

      val feModel = Some(NonUKResidence)

      ResidenceType.conv(desModel) must be(feModel)
    }
  }

}
