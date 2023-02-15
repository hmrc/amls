/*
 * Copyright 2023 HM Revenue & Customs
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

package models.fe.businessactivities

import models.des.businessactivities.BusinessActivitiesAll
import play.api.libs.json.Json

case class HowManyEmployees(employeeCount: String, employeeCountAMLSSupervision: String)


object HowManyEmployees {

  implicit val formats = Json.format[HowManyEmployees]

  def conv(activityDtls: BusinessActivitiesAll): Option[HowManyEmployees] = {
    (activityDtls.noOfEmployees, activityDtls.noOfEmployeesForMlr) match {
      case (Some(noOfEmployees), Some(noOfEmployeesForMlr)) => Some(HowManyEmployees(noOfEmployees, noOfEmployeesForMlr))
      case _ => None
    }
  }
}
