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

package models.des.aboutthebusiness

import models.fe.businessdetails.{CorrespondenceAddress, RegisteredOfficeNonUK, RegisteredOfficeUK, UKCorrespondenceAddress}
import org.scalatestplus.play.PlaySpec


class AddressSpec extends PlaySpec {

  "address" must {



    "convert to uk registered address" in {

      val address = Address (
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "GB",
        Some("AA1 1AA"),
        Some("2016-1-1")
      )

      val registeredOfficeUk = RegisteredOfficeUK(
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "AA1 1AA",
        Some("2016-1-1")
      )

      Address.convert(registeredOfficeUk) must be(address)

    }

    "convert to uk registered address when post code is empty" in {

      val address = Address (
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "GB",
        None,
        Some("2016-1-1")
      )

      val registeredOfficeUk = RegisteredOfficeUK(
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "",
        Some("2016-1-1")
      )

      Address.convert(registeredOfficeUk) must be(address)

    }

    "convert from uk registered office when postcode is invalid format" in {

      val address = Address (
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "GB",
        None,
        Some("2016-1-1")
      )

      val registeredOfficeUk = RegisteredOfficeUK(
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "NOT A POSTCODE",
        Some("2016-1-1")
      )

      Address.convert(registeredOfficeUk) must be(address)


    }

    "convert to uk alternate address when post code is empty" in {

      val address = Address (
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "GB",
        None
      )

      val alternateAddress = Some(UKCorrespondenceAddress(
        "Name",
        "Business Name",
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        ""
      ))

      Address.convertAlternateAddress(alternateAddress) must be(address)

    }

    "convert from uk registered address replacing ampersands" in {

      val address = Address (
        "Hodaway, Hodaway, Hodaway and Hodaw",
        "addressLine2",
        Some("addressLine3"),
        Some("Tyne and Wear"),
        "GB",
        None,
        Some("2016-1-1")
      )

      val registeredOfficeUk = RegisteredOfficeUK(
        "Hodaway, Hodaway, Hodaway & Hodaway",
        "addressLine2",
        Some("addressLine3"),
        Some("Tyne & Wear"),
        "",
        Some("2016-1-1")
      )

      Address.convert(registeredOfficeUk) must be(address)

    }

    "convert to non uk registered address" in {

      val address = Address (
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "france",
        None,
        Some("2016-1-1")
      )

      val registeredOfficeNonUk = RegisteredOfficeNonUK(
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "france",
        Some("2016-1-1")
      )

      Address.convert(registeredOfficeNonUk) must be(address)

    }
  }

}
