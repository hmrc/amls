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

package models.des.aboutthebusiness

import models.fe.aboutthebusiness.{RegisteredOfficeNonUK, RegisteredOfficeUK}
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
        Some("postcode"),
        Some("2016-1-1")
      )

      val registeredOfficeUk = RegisteredOfficeUK(
        "addressLine1",
        "addressLine2",
        Some("addressLine3"),
        Some("addressLine4"),
        "postcode",
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
