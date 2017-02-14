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

import play.api.libs.json.Json

case class AboutYouRelease7(individualDetails: Option[IndividualDetails] = None,
                            employedWithinBusiness: Boolean,
                            roleWithinBusiness: Option[RolesWithinBusiness] = None,
                            roleForTheBusiness: Option[RoleForTheBusiness] = None)

object AboutYouRelease7 {
  implicit val format = Json.format[AboutYouRelease7]


  private def rolesWithinBusinessConvert(person: models.fe.declaration.AddPerson): models.des.aboutyou.RolesWithinBusiness = {
    import models.fe.declaration._

    person.roleWithinBusiness.roles.foldLeft(
      RolesWithinBusiness(false, false, false, false, false, false, false, false, None)) {
      (result, roleType) =>
        roleType match {
          case BeneficialShareholder => result.copy(beneficialShareholder = true)
          case Director => result.copy(director = true)
          case Partner => result.copy(partner = true)
          case InternalAccountant => result.copy(internalAccountant = true)
          case SoleProprietor => result.copy(soleProprietor = true)
          case NominatedOfficer => result.copy(nominatedOfficer = true)
          case DesignatedMember => result.copy(designatedMember = true)
          case Other(details) => result.copy(other = true, specifyOtherRoleInBusiness = Some(details))
          case _ => result
        }
    }
  }

  private def roleForTheBusinessConvert(person: models.fe.declaration.AddPerson): models.des.aboutyou.RoleForTheBusiness = {
    import models.fe.declaration._

    person.roleWithinBusiness.roles.foldLeft(
      RoleForTheBusiness(false, false, None)) {
      (result, roleType) =>
        roleType match {
          case ExternalAccountant => result.copy(externalAccountant = true)
          case Other(details) => result.copy(other = true, specifyOtherRoleForBusiness = Some(details))
          case _ => result
        }
    }
  }

  implicit def convert(person: models.fe.declaration.AddPerson): AboutYouRelease7 = {

    import models.fe.declaration._

    val withinBusiness = !person.roleWithinBusiness.roles.contains(ExternalAccountant)

    val rolesWithinBusiness = rolesWithinBusinessConvert(person)

    val roleForTheBusiness = roleForTheBusinessConvert(person)

    AboutYouRelease7(
      Some(IndividualDetails(person.firstName, person.middleName, person.lastName)),
      withinBusiness,
      Some(rolesWithinBusiness),
      Some(roleForTheBusiness)
    )
  }


  implicit def convertToRelease7(old: models.des.aboutyou.Aboutyou): AboutYouRelease7 = {

    val convertWithinFromOld: RolesWithinBusiness = {
      val roleWithin = old.roleWithinBusiness.foldLeft(
        RolesWithinBusiness(false, false, false, false, false, false, false, false, None)) {
        (result, roleString) =>
          roleString match {
            case "Beneficial Shareholder" => result.copy(beneficialShareholder = true)
            case "Director" => result.copy(director = true)
            case "Partner" => result.copy(partner = true)
            case "Internal Accountant" => result.copy(internalAccountant = true)
            case "Sole Proprietor" => result.copy(soleProprietor = true)
            case "Nominated Officer" => result.copy(nominatedOfficer = true)
            case "Designated Member" => result.copy(designatedMember = true)
            case _ => result
          }
      }

      old.specifyOtherRoleInBusiness match {
        case Some(x) => roleWithin.copy(other = true, specifyOtherRoleInBusiness = Some(x))
        case _ => roleWithin
      }
    }

    val convertForFromOld: RoleForTheBusiness = {
      val roleFor = old.roleForTheBusiness.foldLeft(
        RoleForTheBusiness(false, false, None)) {
        (result, roleString) =>
          roleString match {
            case "External Accountant" => result.copy(externalAccountant = true)
            case _ => result
          }
      }

      old.specifyOtherRoleForBusiness match {
        case Some(x) => roleFor.copy(other = true, specifyOtherRoleForBusiness = Some(x))
        case _ => roleFor
      }

    }

    AboutYouRelease7(old.individualDetails,
      old.employedWithinBusiness,
      Some(convertWithinFromOld),
      Some(convertForFromOld)
    )
  }

}
