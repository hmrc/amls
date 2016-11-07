/*
 * Copyright 2016 HM Revenue & Customs
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

object Aboutyou{
  implicit val format = Json.format[Aboutyou]

  implicit def convert(person: models.fe.declaration.AddPerson): Aboutyou = {
    person.roleWithinBusiness match {
        case Other(y) => Aboutyou(person, false, None, None, Some("Other"), Some(y))
        case extAcct if(extAcct equals ExternalAccountant) => Aboutyou(person, false, None, None, Some(extAcct))
        case intType => Aboutyou(person, true, Some(intType), None, Some("Other"), None)
      }
    }

  implicit def roleConvert(role:RoleWithinBusiness): String ={
    role match{
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
}
