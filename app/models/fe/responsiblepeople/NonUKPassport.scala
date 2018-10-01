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

sealed trait NonUKPassport

case class NonUKPassportYes(nonUKPassportNumber: String) extends NonUKPassport
case object NoPassport extends NonUKPassport

object NonUKPassport {

  implicit val jsonReads: Reads[NonUKPassport] =
    (__ \ "nonUKPassport").read[Boolean] flatMap {
      case true => (__ \ "nonUKPassportNumber").read[String] map NonUKPassportYes.apply
      case false => Reads(_ => JsSuccess(NoPassport))
    }

  implicit val jsonWrites = Writes[NonUKPassport] {
    case NonUKPassportYes(value) => Json.obj(
      "nonUKPassport" -> true,
      "nonUKPassportNumber" -> value
    )
    case NoPassport => Json.obj("nonUKPassport" -> false)
  }

  implicit def conv(responsiblePersons: ResponsiblePersons): Option[NonUKPassport] = {
    val passportDetail: Option[PassportDetail] = for {
      nd <- responsiblePersons.nationalityDetails
      id <- nd.idDetails
      non <- id.nonUkResident
      passport <- non.passportDetails
    } yield passport

    val nonUkPassport: NonUKPassport = passportDetail.map(passport => {
      passport.passportNumber.nonUkPassportNumber match {
        case Some(number) => NonUKPassportYes(number)
        case _ => NoPassport
      }
    }).getOrElse(NoPassport)

    if (responsiblePersons.nationalityDetails.exists(a => a.areYouUkResident)) {
      None
    } else {
      Some(nonUkPassport)
    }
  }
}
