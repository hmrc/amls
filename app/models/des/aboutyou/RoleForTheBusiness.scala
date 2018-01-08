/*
 * Copyright 2018 HM Revenue & Customs
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

import models.fe.declaration.{Other, ExternalAccountant, RoleType, RoleWithinBusiness}
import play.api.libs.json.Json

case class RoleForTheBusiness(externalAccountant: Boolean,
                              other: Boolean,
                              specifyOtherRoleForBusiness: Option[String])

object RoleForTheBusiness {
  implicit val format = Json.format[RoleForTheBusiness]

  def convertForBusiness(frontendModel:RoleWithinBusiness): RoleForTheBusiness = {

    val things = Some(frontendModel).fold[Set[RoleType]](Set.empty)(x => x.roles)

    things.foldLeft(
      RoleForTheBusiness(false,false,None)){
      (result, roleType) =>
        roleType match {
          case ExternalAccountant => result.copy(externalAccountant = true)
          case Other(details) => result.copy(other = true, specifyOtherRoleForBusiness = Some(details))
        }
    }
  }
}
