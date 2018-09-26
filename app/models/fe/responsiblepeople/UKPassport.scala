/*
 * Copyright 2018 HM Revenue & Customs
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

import models.des.responsiblepeople.{PassportDetail, ResponsiblePersons}
import play.api.libs.json._

sealed trait UKPassport

case class UKPassportYes(ukPassportNumber: String) extends UKPassport

case object UKPassportNo extends UKPassport

object UKPassport {

  implicit val jsonReads: Reads[UKPassport] =
    (__ \ "ukPassport").read[Boolean] flatMap {
      case true => (__ \ "ukPassportNumber").read[String] map UKPassportYes.apply
      case false => Reads(_ => JsSuccess(UKPassportNo))
    }

  implicit val jsonWrites = Writes[UKPassport] {
    case UKPassportYes(value) => Json.obj(
      "ukPassport" -> true,
      "ukPassportNumber" -> value
    )
    case UKPassportNo => Json.obj("ukPassport" -> false)
  }

  implicit def conv(responsiblePersons: ResponsiblePersons): Option[UKPassport] = {
    val blah: Option[Option[PassportDetail]] = for {
      nd <- responsiblePersons.nationalityDetails
      id <- nd.idDetails
      non <- id.nonUkResident
    } yield non.passportDetails

    val thing2: UKPassport = if (blah.isDefined && blah.get.isDefined) {
      UKPassportYes(blah.get.get.passportNumber.ukPassportNumber.getOrElse(""))
    } else {
      UKPassportNo
    }

    Some(thing2)
  }

}
