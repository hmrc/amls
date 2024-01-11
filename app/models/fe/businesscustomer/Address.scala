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

package models.fe.businesscustomer

import models.des.aboutthebusiness.{Address => DesAddress}
import play.api.libs.json.{Json, OFormat}

case class Address(line_1: String, line_2: Option[String], line_3: Option[String], line_4: Option[String], postcode: Option[String], country: String)

object Address {
  implicit val format: OFormat[Address] = Json.format[Address]

  implicit def conv(addr: DesAddress): Address = {
    Address(addr.addressLine1, addr.addressLine2, addr.addressLine3, addr.addressLine4, addr.postcode, addr.country)
  }
}
