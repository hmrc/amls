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

package models.fe.responsiblepeople

import models.des.responsiblepeople.{UkResident, IdDetail}
import org.joda.time.LocalDate
import play.api.libs.json.{Reads, Writes}

sealed trait ResidenceType

case class UKResidence(nino: String) extends ResidenceType

case class NonUKResidence(
                           dateOfBirth: LocalDate,
                           passportType: PassportType
                         ) extends ResidenceType

object ResidenceType {

  implicit val jsonReads: Reads[ResidenceType] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (__ \ "nino").read[String] fmap UKResidence.apply map identity[ResidenceType] orElse
      (
        (__ \ "dateOfBirth").read[LocalDate] and
          __.read[PassportType]
        ) (NonUKResidence.apply _)
  }

  implicit val jsonWrites: Writes[ResidenceType] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Writes._
    import play.api.libs.json._
    Writes[ResidenceType] {
      case a: UKResidence =>
        Json.obj(
          "nino" -> a.nino
        )
      case a: NonUKResidence =>
        (
          (__ \ "dateOfBirth").write[LocalDate] and
            __.write[PassportType]
          ) (unlift(NonUKResidence.unapply)).writes(a)
    }
  }
}
