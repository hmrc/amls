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

package models.fe.responsiblepeople

import play.api.libs.json.{Reads, Writes}

sealed trait PersonAddress

case class PersonAddressUK(
                            addressLine1: String,
                            addressLine2: String,
                            addressLine3: Option[String],
                            addressLine4: Option[String],
                            postCode: String) extends PersonAddress

case class PersonAddressNonUK(
                               addressLineNonUK1: String,
                               addressLineNonUK2: String,
                               addressLineNonUK3: Option[String],
                               addressLineNonUK4: Option[String],
                               country: String) extends PersonAddress

object PersonAddress {

  implicit val jsonReads: Reads[PersonAddress] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (__ \ "personAddressPostCode").read[String] andKeep (
      (
        (__ \ "personAddressLine1").read[String] and
          (__ \ "personAddressLine2").read[String] and
          (__ \ "personAddressLine3").readNullable[String] and
          (__ \ "personAddressLine4").readNullable[String] and
          (__ \ "personAddressPostCode").read[String]) (PersonAddressUK.apply _) map identity[PersonAddress]
      ) orElse
      (
        (__ \ "personAddressLine1").read[String] and
          (__ \ "personAddressLine2").read[String] and
          (__ \ "personAddressLine3").readNullable[String] and
          (__ \ "personAddressLine4").readNullable[String] and
          (__ \ "personAddressCountry").read[String]) (PersonAddressNonUK.apply _)
  }

  implicit val jsonWrites: Writes[PersonAddress] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Writes._
    import play.api.libs.json._
    Writes[PersonAddress] {
      case a: PersonAddressUK =>
        (
          (__ \ "personAddressLine1").write[String] and
            (__ \ "personAddressLine2").write[String] and
            (__ \ "personAddressLine3").writeNullable[String] and
            (__ \ "personAddressLine4").writeNullable[String] and
            (__ \ "personAddressPostCode").write[String]
          ) (unlift(PersonAddressUK.unapply)).writes(a)
      case a: PersonAddressNonUK =>
        (
          (__ \ "personAddressLine1").write[String] and
            (__ \ "personAddressLine2").write[String] and
            (__ \ "personAddressLine3").writeNullable[String] and
            (__ \ "personAddressLine4").writeNullable[String] and
            (__ \ "personAddressCountry").write[String]
          ) (unlift(PersonAddressNonUK.unapply)).writes(a)
    }
  }
}
