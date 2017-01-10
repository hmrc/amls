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

import models.des.responsiblepeople.PassportDetail
import play.api.data.validation.ValidationError
import play.api.libs.json._

sealed trait PassportType

case class UKPassport(passportNumberUk: String) extends PassportType
case class NonUKPassport(passportNumberNonUk: String) extends PassportType
case object NoPassport extends PassportType

object PassportType {


  implicit val jsonReads : Reads[PassportType] = {
    import play.api.libs.json.Reads.StringReads
      (__ \ "passportType").read[String].flatMap[PassportType] {
        case "01" =>
          (__ \ "ukPassportNumber").read[String] map {
            UKPassport(_)
          }
        case "02" =>
          (__ \ "nonUKPassportNumber").read[String] map {
            NonUKPassport(_)
          }
        case "03" => Reads(_ => JsSuccess(NoPassport))
        case _ =>
          Reads(_ => JsError((JsPath \ "passportType") -> ValidationError("error.invalid")))
      }
  }

  implicit val jsonWrites = Writes[PassportType] {
    case UKPassport(ukNumber) =>  Json.obj(
      "passportType" -> "01",
      "ukPassportNumber" -> ukNumber
    )
    case NonUKPassport(nonUKNumber) =>  Json.obj(
      "passportType" -> "02",
      "nonUKPassportNumber" -> nonUKNumber
    )
    case NoPassport => Json.obj("passportType" -> "03")
  }

  implicit def conv(passportDetails: Option[PassportDetail]): PassportType = {
    passportDetails match {
      case Some(pDtls) => pDtls.ukPassport match {
        case true => UKPassport(pDtls.passportNumber.ukPassportNumber.getOrElse(""))
        case false =>NonUKPassport(pDtls.passportNumber.nonUkPassportNumber.getOrElse(""))
      }
      case None => NoPassport
    }
  }
}
