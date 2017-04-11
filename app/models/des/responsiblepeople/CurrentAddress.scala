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
import play.api.Logger
import play.api.libs.json.Json

case class CurrentAddress (address: AddressWithChangeDate)

object CurrentAddress {
  implicit val format = Json.format[CurrentAddress]

  private val postcodeRegex = "^[A-Za-z]{1,2}[0-9][0-9A-Za-z]?\\s?[0-9][A-Za-z]{2}$"

  private def convertEmptyOrInvalidToNone(str: String) = {
    (str.nonEmpty,str.matches(postcodeRegex))   match {
      case (true,true) => Some(str)
      case _ => None
    }
  }

  implicit def convPersonAddressOption(addrHistory: Option[ResponsiblePersonCurrentAddress]): Option[CurrentAddress] = {
    addrHistory match {
      case Some(data) => data
      case _ => None
    }
  }

  implicit def convPersonAddress(addrHistory: ResponsiblePersonCurrentAddress): Option[CurrentAddress] = {
    addrHistory.personAddress match {
      case uk:PersonAddressUK => Some(CurrentAddress(AddressWithChangeDate(uk.addressLine1, uk.addressLine2, uk.addressLine3,
        uk.addressLine4, "GB" ,convertEmptyOrInvalidToNone(uk.postCode), addrHistory.dateOfChange)))
      case nonUk:PersonAddressNonUK => Some(CurrentAddress(AddressWithChangeDate(nonUk.addressLineNonUK1, nonUk.addressLineNonUK2, nonUk.addressLineNonUK3,
        nonUk.addressLineNonUK4, nonUk.country, None, addrHistory.dateOfChange)))
    }
  }
}
