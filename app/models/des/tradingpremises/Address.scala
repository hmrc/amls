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

  implicit def convert(address: models.fe.tradingpremises.Address): Address = {
    Address(address.addressLine1, address.addressLine2, address.addressLine3, address.addressLine4, "GB", Some(address.postcode), address.dateOfChange)
  }
}
