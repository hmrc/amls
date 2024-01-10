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

import models.des.responsiblepeople.{OtherDetails, PositionInBusiness, ResponsiblePersons}

import java.time.LocalDate
import play.api.libs.json._
import utils.CommonMethods

case class Positions(positions: Set[PositionWithinBusiness], startDate: Option[LocalDate])

sealed trait PositionWithinBusiness

case object BeneficialOwner extends PositionWithinBusiness

case object Director extends PositionWithinBusiness

case object InternalAccountant extends PositionWithinBusiness

case object NominatedOfficer extends PositionWithinBusiness

case object Partner extends PositionWithinBusiness

case object SoleProprietor extends PositionWithinBusiness

case object DesignatedMember extends PositionWithinBusiness

case class Other(value: String) extends PositionWithinBusiness

object PositionWithinBusiness {

  implicit val jsonReads: Reads[PositionWithinBusiness] =
    Reads {
      case JsString("01") => JsSuccess(BeneficialOwner)
      case JsString("02") => JsSuccess(Director)
      case JsString("03") => JsSuccess(InternalAccountant)
      case JsString("04") => JsSuccess(NominatedOfficer)
      case JsString("05") => JsSuccess(Partner)
      case JsString("06") => JsSuccess(SoleProprietor)
      case JsString("07") => JsSuccess(DesignatedMember)
      case JsObject(m) if m.contains("other") => JsSuccess(Other(m("other").as[String]))
      case _ => JsError((JsPath \ "positions") -> JsonValidationError("error.invalid"))
    }

  implicit val jsonWrites: Writes[PositionWithinBusiness] = Writes[PositionWithinBusiness] {
    case BeneficialOwner => JsString("01")
    case Director => JsString("02")
    case InternalAccountant => JsString("03")
    case NominatedOfficer => JsString("04")
    case Partner => JsString("05")
    case SoleProprietor => JsString("06")
    case DesignatedMember => JsString("07")
    case Other(v) => Json.obj("other" -> v)
  }
}

object Positions {
  implicit val formats: OFormat[Positions] = Json.format[Positions]

  implicit def conv(desRp: ResponsiblePersons): Option[Positions] = {

    desRp.positionInBusiness match {
      case Some(positions) if convPositions(positions).nonEmpty => Some(Positions(positions, desRp.startDate map {
        date => LocalDate.parse(date)
      }))
      case _ => None
    }
  }

  private def extractOther(d: Option[OtherDetails]): Option[PositionWithinBusiness] = for {
    p <- d
    hasOther <- p.other
    otherValue <- p.otherDetails if hasOther
  } yield Other(otherValue)

  implicit def convPositions(position: PositionInBusiness): Set[PositionWithinBusiness] = {

    val positions = Set(
      CommonMethods.getSpecificType[PositionWithinBusiness](position.soleProprietor.fold(false)(_.nominatedOfficer), NominatedOfficer),
      CommonMethods.getSpecificType[PositionWithinBusiness](position.soleProprietor.fold(false)(_.soleProprietor), SoleProprietor),
      CommonMethods.getSpecificType[PositionWithinBusiness](position.partnership.fold(false)(_.nominatedOfficer), NominatedOfficer),
      CommonMethods.getSpecificType[PositionWithinBusiness](position.partnership.fold(false)(_.partner), Partner),
      CommonMethods.getSpecificType[PositionWithinBusiness](position.corpBodyOrUnInCorpBodyOrLlp.fold(false)(_.beneficialOwner), BeneficialOwner),
      CommonMethods.getSpecificType[PositionWithinBusiness](position.corpBodyOrUnInCorpBodyOrLlp.fold(false)(_.director), Director),
      CommonMethods.getSpecificType[PositionWithinBusiness](position.corpBodyOrUnInCorpBodyOrLlp.fold(false)(_.designatedMember.getOrElse(false)),
        DesignatedMember),
      CommonMethods.getSpecificType[PositionWithinBusiness](position.corpBodyOrUnInCorpBodyOrLlp.fold(false)(_.nominatedOfficer), NominatedOfficer),
      extractOther(position.partnership),
      extractOther(position.soleProprietor),
      extractOther(position.corpBodyOrUnInCorpBodyOrLlp)
    ).flatten

    positions
  }
}
