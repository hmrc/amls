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

package models.fe.businessactivities

import models.des.businessactivities.{MlrActivitiesAppliedFor, MlrAdvisor}
import play.api.libs.json.{Json, OFormat}

case class AccountantForAMLSRegulations(accountantForAMLSRegulations: Boolean)

object AccountantForAMLSRegulations {

  implicit val formats: OFormat[AccountantForAMLSRegulations] = Json.format[AccountantForAMLSRegulations]

  /** Converts an MLR Advisor instance into AccountantForAMLSRegulations. There is some logic here to ensure that the
    * correct data is returned if no advisor data is supplied, which is a real scenario if the application says that the
    * business does ASP, but the user has answered 'no' to the Business Activities question 'Does your business receive
    * advice...?' on the frontend. In this case, ETMP will not return any data for this question. In that case, if the
    * business is not an ASP and there is no data for that question, then it can be assumed that the user answered 'no'.
    * If the business IS an ASP, then None should be returned for this question (in this scenario, the user is never
    * asked to complete that question).
    *
    * @param maybeAdvisor
    *   The MLR Advisor object to convert
    * @param maybeActivities
    *   The MLR activities supplied as part of the application
    * @return
    */
  def convertAccountant(
    maybeAdvisor: Option[MlrAdvisor],
    maybeActivities: Option[MlrActivitiesAppliedFor]
  ): Option[AccountantForAMLSRegulations] =
    (maybeAdvisor, maybeActivities) match {
      case (None, Some(activities)) if !activities.asp => Some(AccountantForAMLSRegulations(false))
      case (None, Some(activities)) if activities.asp  => None
      case (Some(advisor), _)                          => Some(AccountantForAMLSRegulations(advisor.doYouHaveMlrAdvisor))
      case _                                           => None
    }
}
