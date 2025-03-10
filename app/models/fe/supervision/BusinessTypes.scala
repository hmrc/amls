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

package models.fe.supervision

import models.des.supervision.{MemberOfProfessionalBody, ProfessionalBodyDetails}
import play.api.libs.json._
import utils.CommonMethods

sealed trait BusinessType {
  val value: String =
    this match {
      case AccountingTechnicians         => "01"
      case CharteredCertifiedAccountants => "02"
      case InternationalAccountants      => "03"
      case TaxationTechnicians           => "04"
      case ManagementAccountants         => "05"
      case InstituteOfTaxation           => "06"
      case Bookkeepers                   => "07"
      case AccountantsIreland            => "08"
      case AccountantsScotland           => "09"
      case AccountantsEnglandandWales    => "10"
      case FinancialAccountants          => "11"
      case AssociationOfBookkeepers      => "12"
      case LawSociety                    => "13"
      case Other(_)                      => "14"
    }
}

case object AccountingTechnicians extends BusinessType

case object CharteredCertifiedAccountants extends BusinessType

case object InternationalAccountants extends BusinessType

case object TaxationTechnicians extends BusinessType

case object ManagementAccountants extends BusinessType

case object InstituteOfTaxation extends BusinessType

case object Bookkeepers extends BusinessType

case object AccountantsIreland extends BusinessType

case object AccountantsScotland extends BusinessType

case object AccountantsEnglandandWales extends BusinessType

case object FinancialAccountants extends BusinessType

case object AssociationOfBookkeepers extends BusinessType

case object LawSociety extends BusinessType

case class Other(businessDetails: String) extends BusinessType

case class BusinessTypes(businessType: Set[BusinessType])

object BusinessTypes {

  implicit val jsonReads: Reads[BusinessTypes] =
    (__ \ "businessType").read[Set[String]].flatMap { x: Set[String] =>
      x.map {
        case "01" => Reads(_ => JsSuccess(AccountingTechnicians)) map identity[BusinessType]
        case "02" => Reads(_ => JsSuccess(CharteredCertifiedAccountants)) map identity[BusinessType]
        case "03" => Reads(_ => JsSuccess(InternationalAccountants)) map identity[BusinessType]
        case "04" => Reads(_ => JsSuccess(TaxationTechnicians)) map identity[BusinessType]
        case "05" => Reads(_ => JsSuccess(ManagementAccountants)) map identity[BusinessType]
        case "06" => Reads(_ => JsSuccess(InstituteOfTaxation)) map identity[BusinessType]
        case "07" => Reads(_ => JsSuccess(Bookkeepers)) map identity[BusinessType]
        case "08" => Reads(_ => JsSuccess(AccountantsIreland)) map identity[BusinessType]
        case "09" => Reads(_ => JsSuccess(AccountantsScotland)) map identity[BusinessType]
        case "10" => Reads(_ => JsSuccess(AccountantsEnglandandWales)) map identity[BusinessType]
        case "11" => Reads(_ => JsSuccess(FinancialAccountants)) map identity[BusinessType]
        case "12" => Reads(_ => JsSuccess(AssociationOfBookkeepers)) map identity[BusinessType]
        case "13" => Reads(_ => JsSuccess(LawSociety)) map identity[BusinessType]
        case "14" =>
          (JsPath \ "specifyOtherBusiness").read[String].map(Other.apply) map identity[BusinessType]
        case _    =>
          Reads(_ => JsError((JsPath \ "businessType") -> JsonValidationError("error.invalid")))
      }.foldLeft[Reads[Set[BusinessType]]](
        Reads[Set[BusinessType]](_ => JsSuccess(Set.empty))
      ) { (start, businessTypeReader) =>
        businessTypeReader flatMap { businessType =>
          start map { businessTypes =>
            businessTypes + businessType
          }
        }
      }
    } map BusinessTypes.apply

  implicit val jsonWrites: Writes[BusinessTypes] = Writes[BusinessTypes] { businessTypes =>
    Json.obj(
      "businessType" -> (businessTypes.businessType map {
        _.value
      }).toSeq
    ) ++ businessTypes.businessType.foldLeft[JsObject](Json.obj()) {
      case (json, Other(name)) => json ++ Json.obj("specifyOtherBusiness" -> name)
      case (json, _)           => json
    }
  }

  def convOther(other: Boolean, specifyOther: String): Option[BusinessType] =
    other match {
      case true  => Some(Other(specifyOther))
      case false => None
    }

  implicit def conv(supDtls: Option[ProfessionalBodyDetails]): Option[BusinessTypes] =
    (for {
      pBodyDtls <- supDtls
      member    <- pBodyDtls.professionalBody
    } yield member.professionalBodyDetails).flatten

  implicit def convProfessionalBodyMember(pBodyMember: Option[MemberOfProfessionalBody]): Option[BusinessTypes] =
    pBodyMember map { member =>
      BusinessTypes(
        Set(
          CommonMethods.getSpecificType[BusinessType](member.associationofAccountingTechnicians, AccountingTechnicians),
          CommonMethods.getSpecificType[BusinessType](
            member.associationofCharteredCertifiedAccountants,
            CharteredCertifiedAccountants
          ),
          CommonMethods
            .getSpecificType[BusinessType](member.associationofInternationalAccountants, InternationalAccountants),
          CommonMethods.getSpecificType[BusinessType](member.associationofTaxationTechnicians, TaxationTechnicians),
          CommonMethods
            .getSpecificType[BusinessType](member.charteredInstituteofManagementAccountants, ManagementAccountants),
          CommonMethods.getSpecificType[BusinessType](member.charteredInstituteofTaxation, InstituteOfTaxation),
          CommonMethods.getSpecificType[BusinessType](member.instituteofCertifiedBookkeepers, Bookkeepers),
          CommonMethods
            .getSpecificType[BusinessType](member.instituteofCharteredAccountantsinIreland, AccountantsIreland),
          CommonMethods
            .getSpecificType[BusinessType](member.instituteofCharteredAccountantsinScotland, AccountantsScotland),
          CommonMethods.getSpecificType[BusinessType](
            member.instituteofCharteredAccountantsofEnglandandWales,
            AccountantsEnglandandWales
          ),
          CommonMethods.getSpecificType[BusinessType](member.instituteofFinancialAccountants, FinancialAccountants),
          CommonMethods
            .getSpecificType[BusinessType](member.internationalAssociationofBookKeepers, AssociationOfBookkeepers),
          CommonMethods.getSpecificType[BusinessType](member.lawSociety, LawSociety),
          convOther(member.other, member.specifyOther.getOrElse(""))
        ).flatten
      )
    }

}
