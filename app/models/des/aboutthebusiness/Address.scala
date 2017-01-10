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

import models.fe.aboutthebusiness._
import play.api.libs.json.Json

case class Address (addressLine1: String,
                    addressLine2 : String,
                    addressLine3 : Option[String],
                    addressLine4 : Option[String],
                    country : String,
                    postcode : Option[String],
                    addressChangeDate: Option[String] = None
                   )

object Address {
  implicit val format = Json.format[Address]

  implicit def convert(registeredOffice : RegisteredOffice):Address = {
    registeredOffice match {
      case x:RegisteredOfficeUK => Address(x.addressLine1, x.addressLine2, x.addressLine3, x.addressLine4, "GB", Some(x.postCode), x.dateOfChange)
      case y:RegisteredOfficeNonUK =>Address(y.addressLine1, y.addressLine2, y.addressLine3, y.addressLine4, y.country, None, y.dateOfChange)
    }
  }

  implicit def convertAlternateAddress(model: Option[CorrespondenceAddress]): Address =
    model match {
      case Some(UKCorrespondenceAddress(_ , _,addressLine1, addressLine2, addressLine3, addressLine4, postCode)) =>
        Address(addressLine1, addressLine2, addressLine3, addressLine4, "GB", Some(postCode))
      case Some(NonUKCorrespondenceAddress(_ , _,addressLine1, addressLine2, addressLine3, addressLine4, country)) =>
        Address(addressLine1, addressLine2, addressLine3, addressLine4, country, None)
      case None =>
        Address("", "", None, None, "", None)
    }
}
