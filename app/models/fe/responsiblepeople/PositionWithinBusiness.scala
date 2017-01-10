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

import models.des.responsiblepeople.{PositionInBusiness, ResponsiblePersons}
import org.joda.time.LocalDate
import play.api.data.validation.ValidationError
import play.api.libs.json._

case class Positions(positions: Set[PositionWithinBusiness], startDate: Option[LocalDate])

sealed trait PositionWithinBusiness

case object BeneficialOwner extends PositionWithinBusiness
case object Director extends PositionWithinBusiness
case object InternalAccountant extends PositionWithinBusiness
case object NominatedOfficer extends PositionWithinBusiness
case object Partner extends PositionWithinBusiness
case object SoleProprietor extends PositionWithinBusiness

object PositionWithinBusiness {

  implicit val jsonReads: Reads[PositionWithinBusiness] =
    Reads {
      case JsString("01") => JsSuccess(BeneficialOwner)
      case JsString("02") => JsSuccess(Director)
      case JsString("03") => JsSuccess(InternalAccountant)
      case JsString("04") => JsSuccess(NominatedOfficer)
      case JsString("05") => JsSuccess(Partner)
      case JsString("06") => JsSuccess(SoleProprietor)
      case _ => JsError((JsPath \ "positions") -> ValidationError("error.invalid"))
    }

  implicit val jsonWrites = Writes[PositionWithinBusiness] {
    case BeneficialOwner => JsString("01")
    case Director => JsString("02")
    case InternalAccountant => JsString("03")
    case NominatedOfficer => JsString("04")
    case Partner => JsString("05")
    case SoleProprietor => JsString("06")
  }
}

object Positions {
  implicit val formats = Json.format[Positions]

  implicit def conv(desRp: ResponsiblePersons): Option[Positions] = {

    desRp.positionInBusiness match {
      case Some(positions) if(!convPositions(positions).isEmpty) => Some(Positions(positions,desRp.startDate map {
        date => LocalDate.parse(date)
      }))
      case _ => None
    }
  }

  def getBeneficialOwner(beneficialOwner: Boolean): Option[PositionWithinBusiness] = {
    beneficialOwner match {
      case true => Some(BeneficialOwner)
      case false => None
    }
  }

  def getNominatedOfficer(nominatedOfficer: Boolean): Option[PositionWithinBusiness] = {
    nominatedOfficer match {
      case true => Some(NominatedOfficer)
      case false => None
    }
  }

  def getDirector(director: Boolean): Option[PositionWithinBusiness] = {
    director match {
      case true => Some(Director)
      case false => None
    }
  }

  def getInternalAccountant(internalAccountant: Boolean): Option[PositionWithinBusiness] = {
    internalAccountant match {
      case true => Some(InternalAccountant)
      case false => None
    }
  }

  def getPartner(partner: Boolean): Option[PositionWithinBusiness] = {
    partner match {
      case true => Some(Partner)
      case false => None
    }
  }

  def getSoleProprietor(soleProprietor: Boolean): Option[PositionWithinBusiness] = {
    soleProprietor match {
      case true => Some(SoleProprietor)
      case false => None
    }
  }

  implicit def convPositions(position: PositionInBusiness): Set[PositionWithinBusiness] = {
    val `empty` = Set.empty[PositionWithinBusiness]

    val positions = Set(getNominatedOfficer(position.soleProprietor.fold(false)(_.nominatedOfficer)),
      getSoleProprietor(position.soleProprietor.fold(false)(_.soleProprietor)),
      getNominatedOfficer(position.partnership.fold(false)(_.nominatedOfficer)),
      getPartner(position.partnership.fold(false)(_.partner)),
      getBeneficialOwner(position.corpBodyOrUnInCorpBodyOrLlp.fold(false)(_.beneficialOwner)),
      getDirector(position.corpBodyOrUnInCorpBodyOrLlp.fold(false)(_.director)),
      getNominatedOfficer(position.corpBodyOrUnInCorpBodyOrLlp.fold(false)(_.nominatedOfficer))
    ).flatten

    positions
  }
}
