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

sealed trait ResidenceType

case class UKResidence(nino: String) extends ResidenceType

case object NonUKResidence extends ResidenceType

object ResidenceType {

  implicit val jsonReads: Reads[ResidenceType] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (__ \ "nino").read[String] fmap UKResidence.apply map identity[ResidenceType] orElse {
      Reads(_ => JsSuccess(NonUKResidence)) map identity[ResidenceType]
    }
  }

  implicit val jsonWrites: Writes[ResidenceType] = {
    import play.api.libs.json.Writes._
    import play.api.libs.json._
    Writes[ResidenceType] {
      case UKResidence(nino) =>
        Json.obj(
          "nino" -> nino
        )
      case NonUKResidence    =>
        Json.obj(
          "isUKResidence" -> "false"
        )
    }
  }

  implicit def conv(nationality: Option[NationalityDetails]): Option[ResidenceType] =
    nationality match {
      case Some(details) => details
      case None          => None
    }

  implicit def conv(nationalityDetails: NationalityDetails): Option[ResidenceType] =
    nationalityDetails.idDetails match {
      case Some(idDetail) =>
        val ukResidence: Option[ResidenceType]    = idDetail.ukResident.map(x => UKResidence(x.nino))
        val nonUKResidence: Option[ResidenceType] = idDetail.nonUkResident.map(x => NonUKResidence)

        nationalityDetails.areYouUkResident match {
          case true  => ukResidence
          case false => nonUKResidence
        }
      case _              => None
    }
}
