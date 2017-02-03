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
}


case class RoleForTheBusiness(externalAccountant: Boolean,
                              other: Boolean,
                              otherSpecify: Option[String])

object RoleForTheBusiness {
  implicit val format = Json.format[RoleForTheBusiness]
}
