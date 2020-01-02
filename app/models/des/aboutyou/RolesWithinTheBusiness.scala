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

package models.des.aboutyou

import models.fe.declaration._
import play.api.libs.json.Json

case class RolesWithinBusiness(beneficialShareholder: Boolean,
                               director: Boolean,
                               partner: Boolean,
                               internalAccountant: Boolean,
                               soleProprietor: Boolean,
                               nominatedOfficer: Boolean,
                               designatedMember: Boolean,
                               other: Boolean,
                               specifyOtherRoleInBusiness: Option[String]
                              )

object RolesWithinBusiness {
  implicit val format = Json.format[RolesWithinBusiness]

  def convertWithinBusiness(frontendModel: RoleWithinBusiness): RolesWithinBusiness = {

    val things = Some(frontendModel).fold[Set[RoleType]](Set.empty)(x => x.roles)

    things.foldLeft(
      RolesWithinBusiness(false,false,false,false,false,false,false,false,None)){
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
          case _ => throw new MatchError(this)
        }
    }
  }
}


