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

package models.fe.supervision

import models.des.businessactivities.MlrActivitiesAppliedFor
import models.des.supervision.AspOrTcsp

case class Supervision(anotherBody: Option[AnotherBody] = None,
                       professionalBodyMember: Option[ProfessionalBodyMember] = None,
                       professionalBodies: Option[BusinessTypes] = None,
                       professionalBody: Option[ProfessionalBody] = None)

object Supervision {

  import play.api.libs.json._

  implicit val formats: OFormat[Supervision] = Json.format[Supervision]

  /**
    * Converts from the ETMP 'supervision' model to our frontend model.
    *
    * This mostly converts from the ETMP model to the frontend model for Supervision.
    *
    * The ETMP model may not be available if the user has answered 'no' to all of the Supervision
    * questions on the frontend. If maybeAspOrTcsp is None, and the submission activities include TCSP or ASP, then
    * we know that the user must have selected 'no' for all of the Supervision questions.
    * Otherwise, either the converted model should be returned, or None if there's no Supervision data to convert from
    * and the activites don't include either ASP or TCSP.
    *
    * @param maybeAspOrTcsp  The ETMP supervision model
    * @param maybeActivities The activities that have been applied for as part of the submission data
    * @return The Supervision model after having been converted from ETMP's supervision model
    */
  def convertFrom(maybeAspOrTcsp: Option[AspOrTcsp], maybeActivities: Option[MlrActivitiesAppliedFor]): Option[Supervision] =
    (maybeAspOrTcsp, maybeActivities) match {
      case (None, Some(activities)) if activities.tcsp || activities.asp =>
        Some(Supervision(Some(AnotherBodyNo), Some(ProfessionalBodyMemberNo), None, Some(ProfessionalBodyNo)))

      case (Some(aspOrTcsp), _) =>
        Some(Supervision(
          AnotherBody.conv(aspOrTcsp.supervisionDetails),
          ProfessionalBodyMember.conv(aspOrTcsp.professionalBodyDetails),
          BusinessTypes.conv(aspOrTcsp.professionalBodyDetails),
          ProfessionalBody.conv(aspOrTcsp.professionalBodyDetails)
        ))

      case _ => None
    }

}
