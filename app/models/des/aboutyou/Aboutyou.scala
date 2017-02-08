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

package models.des.aboutyou

import models.fe.declaration._
import play.api.libs.json.Json

case class Aboutyou(individualDetails: Option[IndividualDetails] = None,
                    employedWithinBusiness: Boolean,
                    roleWithinBusiness: Option[String] = None,
                    specifyOtherRoleInBusiness: Option[String] = None,
                    roleForTheBusiness: Option[String] = None,
                    specifyOtherRoleForBusiness: Option[String] = None) {

}

object Aboutyou {
  implicit val format = Json.format[Aboutyou]

  implicit def convert(person: models.fe.declaration.AddPerson): Aboutyou = {
    person.roleWithinBusiness match {
      case Other(y) => Aboutyou(person, false, None, None, Some("Other"), Some(y))
      case extAcct if (extAcct equals ExternalAccountant) => Aboutyou(person, false, None, None, Some(extAcct))
      case intType => Aboutyou(person, true, Some(intType), None, Some("Other"), None)
    }
  }

  implicit def roleConvert(role: RoleWithinBusiness): String = {
    role match {
      case BeneficialShareholder => "Beneficial Shareholder"
      case Director => "Director"
      case ExternalAccountant => "External Accountant"
      case InternalAccountant => "Internal Accountant"
      case NominatedOfficer => "Nominated officer"
      case Partner => "Partner"
      case SoleProprietor => "Sole proprietor"
      case Other(details) => details
    }
  }

  implicit def convertFromRelease7(aboutYouRelease7: AboutYouRelease7): Aboutyou = {

    val roleWithin: Option[String] = {
      aboutYouRelease7.roleWithinBusiness match {
        case Some(x) if x.beneficialShareholder => Some("Beneficial Shareholder")
        case Some(x) if x.director => Some("Director")
        case Some(x) if x.partner => Some("Partner")
        case Some(x) if x.internalAccountant => Some("Internal Accountant")
        case Some(x) if x.soleProprietor => Some("Sole Proprietor")
        case Some(x) if x.nominatedOfficer => Some("Nominated Officer")
        case Some(x) if x.designatedMember => Some("Designated Member")
        case Some(x) if x.other => Some("Other")
        case _ => None
      }
    }

    val roleFor: Option[String] = {
      aboutYouRelease7.roleForTheBusiness match {
        case Some(x) if x.externalAccountant => Some("External Accountant")
        case Some(x) if x.other => Some("Other")
        case _ => None
      }
    }


    Aboutyou(
      aboutYouRelease7.individualDetails,
      aboutYouRelease7.employedWithinBusiness,
      roleWithin,
      aboutYouRelease7.roleWithinBusiness flatMap (x => x.specifyOtherRoleInBusiness),
      roleFor,
      aboutYouRelease7.roleForTheBusiness flatMap (x => x.otherSpecify)
    )

  }
}


