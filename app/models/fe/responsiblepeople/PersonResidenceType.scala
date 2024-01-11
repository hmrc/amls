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

package models.fe.responsiblepeople

import models.des.responsiblepeople.NationalityDetails
import play.api.libs.json.{Reads, Writes}

case class PersonResidenceType(isUKResidence: ResidenceType, countryOfBirth: String, nationality: String)

object PersonResidenceType {

  implicit val jsonRead: Reads[PersonResidenceType] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (
      __.read[ResidenceType] and
        (__ \ "countryOfBirth").read[String] and
        (__ \ "nationality").read[String]
      ) (PersonResidenceType.apply _)
  }

  implicit val jsonWrite: Writes[PersonResidenceType] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (
      __.write[ResidenceType] and
        (__ \ "countryOfBirth").write[String] and
        (__ \ "nationality").write[String]
      ) (unlift(PersonResidenceType.unapply))
  }

  implicit def conv(nationality: Option[NationalityDetails]): Option[PersonResidenceType] = {
    nationality match {
      case Some(details) => details
      case None => None
    }
  }

  implicit def convNationality(details: NationalityDetails): Option[PersonResidenceType] = {
    details.idDetails match {
      case Some(idDetail) => {

        val ukResidence: Option[ResidenceType] = idDetail.ukResident.map(x => UKResidence(x.nino))
        val nonUKResidence: Option[ResidenceType] = idDetail.nonUkResident.map(x => NonUKResidence)

        val residenceType = details.areYouUkResident match {
          case true => ukResidence
          case false => nonUKResidence
        }
        residenceType match {
          case Some(resType) => Some(PersonResidenceType(resType, details.countryOfBirth.getOrElse(""), details.nationality.getOrElse("")))
          case _ => None
        }
      }
      case _ => None
    }
  }
}
