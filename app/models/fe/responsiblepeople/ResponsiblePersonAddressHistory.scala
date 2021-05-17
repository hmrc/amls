/*
 * Copyright 2021 HM Revenue & Customs
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
import models.fe.responsiblepeople.TimeAtAddress._
import play.api.libs.json.Json

case class ResponsiblePersonAddressHistory(currentAddress: Option[ResponsiblePersonCurrentAddress] = None,
                                           additionalAddress: Option[ResponsiblePersonAddress] = None,
                                           additionalExtraAddress: Option[ResponsiblePersonAddress] = None) {

  def currentAddress(add: ResponsiblePersonCurrentAddress): ResponsiblePersonAddressHistory =
    this.copy(currentAddress = Some(add))

  def additionalAddress(add: ResponsiblePersonAddress): ResponsiblePersonAddressHistory =
    this.copy(additionalAddress = Some(add))

  def additionalExtraAddress(add: ResponsiblePersonAddress): ResponsiblePersonAddressHistory =
    this.copy(additionalExtraAddress = Some(add))
}

object ResponsiblePersonAddressHistory {

  implicit val format = Json.format[ResponsiblePersonAddressHistory]

  def convTimeAtAddress(timeAt: String): TimeAtAddress = {
    timeAt match {
      case "0-6 months" => ZeroToFiveMonths
      case "7-12 months" => SixToElevenMonths
      case "1-3 years" => OneToThreeYears
      case "3+ years" => ThreeYearsPlus
      case  "" => Empty
    }
  }

  def convAddress(addr: Address): PersonAddress = {
    addr.postcode match {
      case Some(postcode) => PersonAddressUK(addr.addressLine1, addr.addressLine2,addr.addressLine3,addr.addressLine4, postcode)
      case None => PersonAddressNonUK(addr.addressLine1, addr.addressLine2,addr.addressLine3,addr.addressLine4, addr.country)
    }
  }

  def convAddress(addr: AddressWithChangeDate): PersonAddress = {
    addr.postcode match {
      case Some(postcode) => PersonAddressUK(addr.addressLine1, addr.addressLine2,addr.addressLine3,addr.addressLine4, postcode)
      case None => PersonAddressNonUK(addr.addressLine1, addr.addressLine2,addr.addressLine3,addr.addressLine4, addr.country)
    }
  }

  def getAddressAndTime(addressDetails: Option[AddressUnderThreeYears], timeAtAddress: Option[String]): Option[ResponsiblePersonAddress] = {
    val address = addressDetails.map(x => convAddress(x.address))
    val timeAt = timeAtAddress.map(x =>convTimeAtAddress(x))
    (address, timeAt) match {
      case(Some(addr), Some(time)) => Some(ResponsiblePersonAddress(addr, time))
      case _ => None
    }
  }

  def getAddressAndTimeForCurrentAddress(addressDetails: Option[CurrentAddress], timeAtAddress: Option[String]): Option[ResponsiblePersonCurrentAddress] = {
    val address = addressDetails.map(x => convAddress(x.address))
    val timeAt = timeAtAddress.map(x =>convTimeAtAddress(x))
    (address, timeAt) match {
      case(Some(addr), Some(time)) => Some(ResponsiblePersonAddress(addr, time))
      case _ => None
    }
  }




  implicit def conv(rp: ResponsiblePersons): Option[ResponsiblePersonAddressHistory] = {
    val addressHistory = ResponsiblePersonAddressHistory(getAddressAndTimeForCurrentAddress(rp.currentAddressDetails, rp.timeAtCurrentAddress),
      getAddressAndTime(rp.addressUnderThreeYears, rp.timeAtAddressUnderThreeYears),
      getAddressAndTime(rp.addressUnderOneYear, rp.timeAtAddressUnderOneYear))

    addressHistory match {
      case ResponsiblePersonAddressHistory(None, None, None) => None
      case _ => Some(addressHistory)
    }
  }
}
