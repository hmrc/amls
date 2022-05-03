/*
 * Copyright 2022 HM Revenue & Customs
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

package models.des.businessactivities

import models.fe.businessmatching._
import play.api.libs.json.Json

case class MlrActivitiesAppliedFor(msb: Boolean, hvd: Boolean, asp: Boolean, tcsp: Boolean, eab: Boolean, bpsp: Boolean, tditpsp: Boolean, amp: Boolean)

object MlrActivitiesAppliedFor {

  implicit val format = Json.format[MlrActivitiesAppliedFor]

  // scalastyle:off

  implicit def conv(bm: BusinessMatching): Option[MlrActivitiesAppliedFor] = {

    val activities = bm.activities.businessActivities
     val mlrActivities = activities.foldLeft[MlrActivitiesAppliedFor](MlrActivitiesAppliedFor(false, false, false, false, false, false, false, false))((result, activity) =>
       activity match {
          case MoneyServiceBusiness => result.copy(msb = true)
          case HighValueDealing => result.copy(hvd = true)
          case AccountancyServices => result.copy(asp = true)
          case TrustAndCompanyServices => result.copy(tcsp = true)
          case EstateAgentBusinessService => result.copy(eab = true)
          case BillPaymentServices => result.copy(bpsp = true)
          case TelephonePaymentService => result.copy(tditpsp = true)
          case ArtMarketParticipant => result.copy(amp = true)
        }
      )
    Some(mlrActivities)
  }
  // scalastyle:on

}
