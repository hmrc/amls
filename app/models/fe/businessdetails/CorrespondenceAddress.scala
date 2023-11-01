/*
 * Copyright 2023 HM Revenue & Customs
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

import models.des.aboutthebusiness.AlternativeAddress
import play.api.libs.json.{Reads, Writes}

sealed trait CorrespondenceAddress

case class UKCorrespondenceAddress(
                                    yourName: String,
                                    businessName: String,
                                    addressLine1: String,
                                    addressLine2: Option[String],
                                    addressLine3: Option[String],
                                    addressLine4: Option[String],
                                    postCode: String
                                  ) extends CorrespondenceAddress

case class NonUKCorrespondenceAddress(
                                       yourName: String,
                                       businessName: String,
                                       addressLineNonUK1: String,
                                       addressLineNonUK2: Option[String],
                                       addressLineNonUK3: Option[String],
                                       addressLineNonUK4: Option[String],
                                       country: String
                                     ) extends CorrespondenceAddress

object CorrespondenceAddress {

  implicit val jsonReads: Reads[CorrespondenceAddress] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (__ \ "correspondencePostCode").read[String] andKeep (
      ((__ \ "yourName").read[String] and
        (__ \ "businessName").read[String] and
        (__ \ "correspondenceAddressLine1").read[String] and
        (__ \ "correspondenceAddressLine2").readNullable[String] and
        (__ \ "correspondenceAddressLine3").readNullable[String] and
        (__ \ "correspondenceAddressLine4").readNullable[String] and
        (__ \ "correspondencePostCode").read[String]) (UKCorrespondenceAddress.apply _) map identity[CorrespondenceAddress]
      ) orElse (
      ((__ \ "yourName").read[String] and
        (__ \ "businessName").read[String] and
        (__ \ "correspondenceAddressLine1").read[String] and
        (__ \ "correspondenceAddressLine2").readNullable[String] and
        (__ \ "correspondenceAddressLine3").readNullable[String] and
        (__ \ "correspondenceAddressLine4").readNullable[String] and
        (__ \ "correspondenceCountry").read[String]) (NonUKCorrespondenceAddress.apply _)
      )
  }

  implicit val jsonWrites: Writes[CorrespondenceAddress] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Writes._
    import play.api.libs.json._
    Writes[CorrespondenceAddress] {
      case a: UKCorrespondenceAddress =>
        (
          (__ \ "yourName").write[String] and
            (__ \ "businessName").write[String] and
            (__ \ "correspondenceAddressLine1").write[String] and
            (__ \ "correspondenceAddressLine2").writeNullable[String] and
            (__ \ "correspondenceAddressLine3").writeNullable[String] and
            (__ \ "correspondenceAddressLine4").writeNullable[String] and
            (__ \ "correspondencePostCode").write[String]
          ) (unlift(UKCorrespondenceAddress.unapply)).writes(a)
      case a: NonUKCorrespondenceAddress =>
        (
          (__ \ "yourName").write[String] and
            (__ \ "businessName").write[String] and
            (__ \ "correspondenceAddressLine1").write[String] and
            (__ \ "correspondenceAddressLine2").writeNullable[String] and
            (__ \ "correspondenceAddressLine3").writeNullable[String] and
            (__ \ "correspondenceAddressLine4").writeNullable[String] and
            (__ \ "correspondenceCountry").write[String]
          ) (unlift(NonUKCorrespondenceAddress.unapply)).writes(a)
    }
  }

  implicit def conv(address: Option[AlternativeAddress]): Option[CorrespondenceAddress] = {
    address match {
      case Some(data) => data.address.postcode match {
        case None => Some(NonUKCorrespondenceAddress(data.name,
          data.tradingName,
          data.address.addressLine1,
          data.address.addressLine2,
          data.address.addressLine3,
          data.address.addressLine4,
          data.address.country
        ))
        case _ => Some(UKCorrespondenceAddress(data.name,
          data.tradingName,
          data.address.addressLine1,
          data.address.addressLine2,
          data.address.addressLine3,
          data.address.addressLine4,
          data.address.postcode.getOrElse("")
        ))
      }
      case _ => None
    }
  }

}
