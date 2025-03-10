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

package models.fe.businessmatching

import models.des.businessactivities.MlrActivitiesAppliedFor
import play.api.libs.json._

case class BusinessActivities(businessActivities: Set[BusinessActivity], dateOfChange: Option[String] = None)

sealed trait BusinessActivity

case object AccountancyServices extends BusinessActivity

case object ArtMarketParticipant extends BusinessActivity

case object BillPaymentServices extends BusinessActivity

case object EstateAgentBusinessService extends BusinessActivity

case object HighValueDealing extends BusinessActivity

case object MoneyServiceBusiness extends BusinessActivity

case object TrustAndCompanyServices extends BusinessActivity

case object TelephonePaymentService extends BusinessActivity

object BusinessActivity {

  implicit val jsonActivityReads: Reads[BusinessActivity] = Reads {
    case JsString("01") => JsSuccess(AccountancyServices)
    case JsString("08") => JsSuccess(ArtMarketParticipant)
    case JsString("02") => JsSuccess(BillPaymentServices)
    case JsString("03") => JsSuccess(EstateAgentBusinessService)
    case JsString("04") => JsSuccess(HighValueDealing)
    case JsString("05") => JsSuccess(MoneyServiceBusiness)
    case JsString("06") => JsSuccess(TrustAndCompanyServices)
    case JsString("07") => JsSuccess(TelephonePaymentService)
    case _              => JsError((JsPath \ "businessActivities") -> JsonValidationError("error.invalid"))
  }

  implicit val jsonActivityWrite: Writes[BusinessActivity] = Writes[BusinessActivity] {
    case AccountancyServices        => JsString("01")
    case ArtMarketParticipant       => JsString("08")
    case BillPaymentServices        => JsString("02")
    case EstateAgentBusinessService => JsString("03")
    case HighValueDealing           => JsString("04")
    case MoneyServiceBusiness       => JsString("05")
    case TrustAndCompanyServices    => JsString("06")
    case TelephonePaymentService    => JsString("07")
  }
}

object BusinessActivities {

  implicit val formats: OFormat[BusinessActivities] = Json.format[BusinessActivities]

  def getActivity[A <: BusinessActivity](activity: A, present: Boolean): Option[BusinessActivity] =
    if (present) {
      Some(activity)
    } else {
      None
    }

  implicit def conv(activities: Option[MlrActivitiesAppliedFor]): BusinessActivities =
    activities match {
      case Some(mlrActivitiesAppliedFor) =>
        BusinessActivities(
          Set(
            getActivity(MoneyServiceBusiness, mlrActivitiesAppliedFor.msb),
            getActivity(HighValueDealing, mlrActivitiesAppliedFor.hvd),
            getActivity(AccountancyServices, mlrActivitiesAppliedFor.asp),
            getActivity(TrustAndCompanyServices, mlrActivitiesAppliedFor.tcsp),
            getActivity(EstateAgentBusinessService, mlrActivitiesAppliedFor.eab),
            getActivity(BillPaymentServices, mlrActivitiesAppliedFor.bpsp),
            getActivity(TelephonePaymentService, mlrActivitiesAppliedFor.tditpsp),
            getActivity(ArtMarketParticipant, mlrActivitiesAppliedFor.amp)
          ).flatten
        )
      case _                             => BusinessActivities(Set.empty)
    }
}
