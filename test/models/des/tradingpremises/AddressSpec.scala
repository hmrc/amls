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

package models.des.tradingpremises

import org.scalatestplus.play.PlaySpec

class AddressSpec extends PlaySpec {

  "convert to fe address to des Address when post code is empty" in {

    val desAddress = Address (
      "addressLine1",
      "addressLine2",
      Some("addressLine3"),
      Some("addressLine4"),
      "GB",
      None
    )

    val feAddress = models.fe.tradingpremises.Address(
      "addressLine1",
      "addressLine2",
      Some("addressLine3"),
      Some("addressLine4"),
      ""
    )
    Address.convert(feAddress) must be(desAddress)
  }

  "convert to fe address to des Address when post code is not empty" in {

    val desAddress = Address (
      "addressLine1",
      "addressLine2",
      Some("addressLine3"),
      Some("addressLine4"),
      "GB",
      Some("postcode")
    )

    val feAddress = models.fe.tradingpremises.Address(
      "addressLine1",
      "addressLine2",
      Some("addressLine3"),
      Some("addressLine4"),
      "postcode"
    )
    Address.convert(feAddress) must be(desAddress)
  }


}
