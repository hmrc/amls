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

package models.fe.businessdetails

import models.des.aboutthebusiness.{Address => DesAddress}
import play.api.libs.json.{Json, Reads, Writes}

sealed trait RegisteredOffice

case class RegisteredOfficeUK(
  addressLine1: String,
  addressLine2: Option[String] = None,
  addressLine3: Option[String] = None,
  addressLine4: Option[String] = None,
  postCode: String,
  dateOfChange: Option[String] = None
) extends RegisteredOffice

case class RegisteredOfficeNonUK(
  addressLine1: String,
  addressLine2: Option[String] = None,
  addressLine3: Option[String] = None,
  addressLine4: Option[String] = None,
  country: String,
  dateOfChange: Option[String] = None
) extends RegisteredOffice

object RegisteredOffice {

  implicit val jsonReads: Reads[RegisteredOffice] = {
    import play.api.libs.json._
    import play.api.libs.json.Reads._
    import play.api.libs.functional.syntax._
    (
      (__ \ "postCode").read[String] andKeep
        (
          (__ \ "addressLine1").read[String] and
            (__ \ "addressLine2").readNullable[String] and
            (__ \ "addressLine3").readNullable[String] and
            (__ \ "addressLine4").readNullable[String] and
            (__ \ "postCode").read[String] and
            (__ \ "dateOfChange").readNullable[String]
        )(RegisteredOfficeUK.apply _) map identity[RegisteredOffice]
    ) orElse
      (
        (__ \ "addressLineNonUK1").read[String] and
          (__ \ "addressLineNonUK2").readNullable[String] and
          (__ \ "addressLineNonUK3").readNullable[String] and
          (__ \ "addressLineNonUK4").readNullable[String] and
          (__ \ "country").read[String] and
          (__ \ "dateOfChange").readNullable[String]
      )(RegisteredOfficeNonUK.apply _)
  }

  implicit val jsonWrites: Writes[RegisteredOffice] = Writes[RegisteredOffice] {
    case m: RegisteredOfficeUK    =>
      Json.obj(
        "addressLine1" -> m.addressLine1,
        "addressLine2" -> m.addressLine2,
        "addressLine3" -> m.addressLine3,
        "addressLine4" -> m.addressLine4,
        "postCode"     -> m.postCode
      )
    case m: RegisteredOfficeNonUK =>
      Json.obj(
        "addressLineNonUK1" -> m.addressLine1,
        "addressLineNonUK2" -> m.addressLine2,
        "addressLineNonUK3" -> m.addressLine3,
        "addressLineNonUK4" -> m.addressLine4,
        "country"           -> m.country
      )
  }

  implicit def conv(address: DesAddress): RegisteredOffice =
    address.postcode match {
      case None =>
        RegisteredOfficeNonUK(
          address.addressLine1,
          address.addressLine2,
          address.addressLine3,
          address.addressLine4,
          address.country
        )
      case _    =>
        RegisteredOfficeUK(
          address.addressLine1,
          address.addressLine2,
          address.addressLine3,
          address.addressLine4,
          address.postcode.getOrElse("")
        )
    }
}
