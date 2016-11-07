/*
 * Copyright 2016 HM Revenue & Customs
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

import models.fe.responsiblepeople.{PersonAddressNonUK, PersonAddressUK, ResponsiblePersonAddress}
import play.api.libs.json.Json

case class AddressUnderThreeYears (address: Address)

object AddressUnderThreeYears {
  implicit val format = Json.format[AddressUnderThreeYears]

  implicit def convPersonAddressOption(addrHistory: Option[ResponsiblePersonAddress]): Option[AddressUnderThreeYears] = {
    addrHistory match {
      case Some(data) => data
      case _ => None
    }
  }

  implicit def convPersonAddress(addrHistory: ResponsiblePersonAddress): Option[AddressUnderThreeYears] = {
    addrHistory.personAddress match {
      case uk:PersonAddressUK => Some(AddressUnderThreeYears(Address(uk.addressLine1, uk.addressLine2, uk.addressLine3,
        uk.addressLine4, "GB" ,Some(uk.postCode))))
      case nonUk:PersonAddressNonUK => Some(AddressUnderThreeYears(Address(nonUk.addressLineNonUK1, nonUk.addressLineNonUK2, nonUk.addressLineNonUK3,
        nonUk.addressLineNonUK4, nonUk.country, None)))
    }
  }
}
