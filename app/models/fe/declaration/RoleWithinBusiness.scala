/*
 * Copyright 2021 HM Revenue & Customs
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

package models.fe.declaration

import models.des.aboutyou.AboutYouRelease7
import play.api.libs.json.Reads.StringReads
import play.api.libs.json.{Json, Reads, _}
import utils.CommonMethods

case class RoleWithinBusiness(roles: Set[RoleType])

sealed trait RoleType {
  val value: String =
    this match {
      case BeneficialShareholder => "BeneficialShareholder"
      case Director => "Director"
      case Partner => "Partner"
      case InternalAccountant => "InternalAccountant"
      case ExternalAccountant => "ExternalAccountant"
      case SoleProprietor => "SoleProprietor"
      case NominatedOfficer => "NominatedOfficer"
      case DesignatedMember => "DesignatedMember"
      case Other(_) => "Other"
    }
}

case object BeneficialShareholder extends RoleType

case object Director extends RoleType

case object Partner extends RoleType

case object InternalAccountant extends RoleType

case object ExternalAccountant extends RoleType

case object SoleProprietor extends RoleType

case object NominatedOfficer extends RoleType

case object DesignatedMember extends RoleType

case class Other(details: String) extends RoleType

object RoleWithinBusiness {

  implicit val jsonReads: Reads[RoleWithinBusiness] =
    (__ \ "roleWithinBusiness").read[Set[String]].flatMap { x: Set[String] =>
      x.map {
        case "BeneficialShareholder" => Reads(_ => JsSuccess(BeneficialShareholder)) map identity[RoleType]
        case "Director" => Reads(_ => JsSuccess(Director)) map identity[RoleType]
        case "Partner" => Reads(_ => JsSuccess(Partner)) map identity[RoleType]
        case "InternalAccountant" => Reads(_ => JsSuccess(InternalAccountant)) map identity[RoleType]
        case "ExternalAccountant" => Reads(_ => JsSuccess(ExternalAccountant)) map identity[RoleType]
        case "SoleProprietor" => Reads(_ => JsSuccess(SoleProprietor)) map identity[RoleType]
        case "NominatedOfficer" => Reads(_ => JsSuccess(NominatedOfficer)) map identity[RoleType]
        case "DesignatedMember" => Reads(_ => JsSuccess(DesignatedMember)) map identity[RoleType]
        case "Other" =>
          val test = (JsPath \ "roleWithinBusinessOther").read[String].map(Other.apply _)
          test map identity[RoleType]
        case _ =>
          Reads(_ => JsError((JsPath \ "roleWithinBusiness") -> JsonValidationError("error.invalid")))
      }.foldLeft[Reads[Set[RoleType]]](
        Reads[Set[RoleType]](_ => JsSuccess(Set.empty))
      ) {
        (result, data) =>
          data flatMap { m =>
            result.map { n =>
              n + m
            }
          }
      }
    } map RoleWithinBusiness.apply

  implicit val jsonWrite = Writes[RoleWithinBusiness] {
    case RoleWithinBusiness(roles) =>
      Json.obj(
        "roleWithinBusiness" -> (roles map {
          _.value
        }).toSeq
      ) ++ roles.foldLeft[JsObject](Json.obj()) {
        case (m, Other(name)) =>
          m ++ Json.obj("roleWithinBusinessOther" -> name)
        case (m, _) =>
          m
      }
  }

  def convOther(other: Boolean, specifyOther: String): Option[RoleType] =
    other match {
      case true => Some(Other(specifyOther))
      case false => None
    }

  def convert(aboutYou: AboutYouRelease7): RoleWithinBusiness = {

    val withinTheBusinessO = aboutYou.roleWithinBusiness match {
      case Some(roles) => Some(Set(
        CommonMethods.getSpecificType(roles.beneficialShareholder, BeneficialShareholder),
        CommonMethods.getSpecificType(roles.director, Director),
        CommonMethods.getSpecificType(roles.partner, Partner),
        CommonMethods.getSpecificType(roles.internalAccountant, InternalAccountant),
        CommonMethods.getSpecificType(roles.soleProprietor, SoleProprietor),
        CommonMethods.getSpecificType(roles.nominatedOfficer, NominatedOfficer),
        CommonMethods.getSpecificType(roles.designatedMember, DesignatedMember),
        convOther(roles.other, roles.specifyOtherRoleInBusiness.getOrElse(""))).flatten)
      case None => None
    }

    val forTheBusinessO = aboutYou.roleForTheBusiness match {
      case Some(roles) => Some(Set(
        CommonMethods.getSpecificType(roles.externalAccountant, ExternalAccountant),
        convOther(roles.other, roles.specifyOtherRoleForBusiness.getOrElse(""))).flatten)
      case _ => None
    }

    val roleTypesWithinBusiness: Option[Set[RoleType]] =
      for {
        withinTheBusiness <- withinTheBusinessO
        forTheBusiness <- forTheBusinessO
      } yield {
        withinTheBusiness ++ forTheBusiness
      }

    roleTypesWithinBusiness.map(RoleWithinBusiness(_)).getOrElse(RoleWithinBusiness(Set.empty))
  }
}
