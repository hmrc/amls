/*
 * Copyright 2020 HM Revenue & Customs
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

package models.des.supervision

import models.fe.supervision._
import play.api.libs.json.Json

case class MemberOfProfessionalBody (
                                    associationofAccountingTechnicians: Boolean,
                                    associationofCharteredCertifiedAccountants: Boolean,
                                    associationofInternationalAccountants: Boolean,
                                    associationofTaxationTechnicians: Boolean,
                                    charteredInstituteofManagementAccountants: Boolean,
                                    charteredInstituteofTaxation: Boolean,
                                    instituteofCertifiedBookkeepers: Boolean,
                                    instituteofCharteredAccountantsinIreland: Boolean,
                                    instituteofCharteredAccountantsinScotland: Boolean,
                                    instituteofCharteredAccountantsofEnglandandWales: Boolean,
                                    instituteofFinancialAccountants: Boolean,
                                    internationalAssociationofBookKeepers: Boolean,
                                    lawSociety: Boolean,
                                    other: Boolean,
                                    specifyOther: Option[String]
                                  )

object MemberOfProfessionalBody {

  implicit val format = Json.format[MemberOfProfessionalBody]

  // scalastyle:off

  implicit def convBusinessTypes(members: BusinessTypes): Option[MemberOfProfessionalBody] = {

    val memberOfProfessionalBody = members.businessType.foldLeft[MemberOfProfessionalBody](MemberOfProfessionalBody(
      false, false, false, false, false, false, false,
      false, false, false, false, false, false, false,
      None
    ))((result, businessType) =>
      businessType match {
        case AccountingTechnicians => result.copy(associationofAccountingTechnicians = true)
        case CharteredCertifiedAccountants => result.copy(associationofCharteredCertifiedAccountants = true)
        case InternationalAccountants => result.copy(associationofInternationalAccountants = true)
        case TaxationTechnicians => result.copy(associationofTaxationTechnicians = true)
        case ManagementAccountants => result.copy(charteredInstituteofManagementAccountants = true)
        case InstituteOfTaxation => result.copy(charteredInstituteofTaxation = true)
        case Bookkeepers => result.copy(instituteofCertifiedBookkeepers = true)
        case AccountantsIreland => result.copy(instituteofCharteredAccountantsinIreland = true)
        case AccountantsScotland => result.copy(instituteofCharteredAccountantsinScotland = true)
        case AccountantsEnglandandWales => result.copy(instituteofCharteredAccountantsofEnglandandWales = true)
        case FinancialAccountants => result.copy(instituteofFinancialAccountants = true)
        case AssociationOfBookkeepers => result.copy(internationalAssociationofBookKeepers = true)
        case LawSociety => result.copy(lawSociety = true)
        case Other(details) => result.copy(other = true, specifyOther = Some(details))
      }
    )

    Some(memberOfProfessionalBody)
  }
  // scalastyle:on

}
