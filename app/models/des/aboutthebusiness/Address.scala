/*
 * Copyright 2024 HM Revenue & Customs
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

import models.fe.businessdetails._
import play.api.Logger
import play.api.libs.json.{Json, OFormat}

case class Address(addressLine1: String,
                   addressLine2: Option[String],
                   addressLine3: Option[String],
                   addressLine4: Option[String],
                   country: String,
                   postcode: Option[String],
                   addressChangeDate: Option[String] = None
                  )

object Address {
  implicit val format: OFormat[Address] = Json.format[Address]
  lazy val logger: Logger = Logger(this.getClass)

  private val postcodeRegex = "^[A-Za-z]{1,2}[0-9][0-9A-Za-z]?\\s?[0-9][A-Za-z]{2}$"

  private def convertEmptyOrInvalidToNone(str: String) = {
    (str.nonEmpty, str.matches(postcodeRegex)) match {
      case (true, true) => Some(str)
      case _ => {
        logger.warn("[Address][Invalid postcode not sent to DES]")
        None
      }
    }
  }

  private val maxAddressLineLength = 35

  private def removeAmpersands(address: Address): Address = {
    def removeFromLine(addressLine: Option[String]) = {
      addressLine map {
        line => line.replaceAll("&", "and").take(maxAddressLineLength)
      }
    }

    address.copy(addressLine1 = removeFromLine(Some(address.addressLine1)).getOrElse(""),
      addressLine2 = removeFromLine(address.addressLine2),
      addressLine3 = removeFromLine(address.addressLine3),
      addressLine4 = removeFromLine(address.addressLine4)
    )

  }

  implicit def convert(registeredOffice: RegisteredOffice): Address = {
    registeredOffice match {
      case x: RegisteredOfficeUK => removeAmpersands(Address(x.addressLine1, x.addressLine2, x.addressLine3, x.addressLine4, "GB",
        convertEmptyOrInvalidToNone(x.postCode), x.dateOfChange))
      case y: RegisteredOfficeNonUK => removeAmpersands(Address(y.addressLine1, y.addressLine2, y.addressLine3, y.addressLine4, y.country, None, y.dateOfChange))
    }
  }

  implicit def convertAlternateAddress(model: Option[CorrespondenceAddress]): Address =
    model match {
      case Some(UKCorrespondenceAddress(_, _, addressLine1, addressLine2, addressLine3, addressLine4, postCode)) =>
        removeAmpersands(Address(addressLine1, addressLine2, addressLine3, addressLine4, "GB", convertEmptyOrInvalidToNone(postCode)))
      case Some(NonUKCorrespondenceAddress(_, _, addressLine1, addressLine2, addressLine3, addressLine4, country)) =>
        removeAmpersands(Address(addressLine1, addressLine2, addressLine3, addressLine4, country, None))
      case None =>
        Address("", None, None, None, "", None)
    }
}
