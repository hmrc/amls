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

package models.fe.tradingpremises

import play.api.libs.json.{Reads, Writes}

case class Address(
                  addressLine1: String,
                  addressLine2: String,
                  addressLine3: Option[String],
                  addressLine4: Option[String],
                  postcode: String,
                  dateOfChange: Option[String] = None
                  )
object Address {

  implicit val reads: Reads[Address] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (
      (__ \ "addressLine1").read[String] and
        (__ \ "addressLine2").read[String] and
        (__ \ "addressLine3").readNullable[String] and
        (__ \ "addressLine4").readNullable[String] and
        (__ \ "postcode").read[String] and
        (__ \ "addressDateOfChange").readNullable[String]
      )(Address.apply _)
  }

  implicit val writes: Writes[Address] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (
    (__ \ "addressLine1").write[String] and
      (__ \ "addressLine2").write[String] and
      (__ \ "addressLine3").writeNullable[String] and
      (__ \ "addressLine4").writeNullable[String] and
      (__ \ "postcode").write[String] and
      (__ \ "addressDateOfChange").writeNullable[String]
    ) (unlift(Address.unapply))
  }

  implicit def convert(address: models.des.tradingpremises.Address): Address = {
    Address(address.addressLine1,
      address.addressLine2,
      address.addressLine3,
      address.addressLine4,
      address.postcode.getOrElse(""))
  }
}
