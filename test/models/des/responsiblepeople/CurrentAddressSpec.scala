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

import models.fe.responsiblepeople.TimeAtAddress.OneToThreeYears
import models.fe.responsiblepeople.{PersonAddressNonUK, PersonAddressUK, ResponsiblePersonCurrentAddress}
import org.joda.time.LocalDate
import org.scalatestplus.play.PlaySpec

/**
  * Created by NicoleAvison on 11/01/2017.
  */
class CurrentAddressSpec extends PlaySpec {

  "CurrentAddress" when {
    "given a UK address" must {
      "convert a ResponsiblePersonCurrentAddress to a CurrentAddress" in {

        val testResponsiblePersonCurrentAddress = ResponsiblePersonCurrentAddress(
          PersonAddressUK("line1", "line2", None, None, "AB1 2CD"),
          OneToThreeYears,
          Some("2017-1-1"))

        val testCurrentAddress = CurrentAddress(AddressWithChangeDate("line1", "line2", None, None, "GB", Some("AB1 2CD"),Some("2017-1-1")))

        CurrentAddress.convPersonAddress(testResponsiblePersonCurrentAddress) must be(Some(testCurrentAddress))

      }
    }

    "given a non-UK address" must {
      "convert a ResponsiblePersonCurrentAddress to a CurrentAddress" in {

        val testResponsiblePersonCurrentAddress = ResponsiblePersonCurrentAddress(
          PersonAddressNonUK("line1", "line2", None, None, "Country"),
          OneToThreeYears,
          Some("2017-1-1"))

        val testCurrentAddress = CurrentAddress(AddressWithChangeDate("line1", "line2", None, None, "Country", None, Some("2017-1-1")))

        CurrentAddress.convPersonAddress(testResponsiblePersonCurrentAddress) must be(Some(testCurrentAddress))

      }
    }
  }


}
