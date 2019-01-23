/*
 * Copyright 2019 HM Revenue & Customs
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
import play.api.data.validation.ValidationError
import play.api.libs.json._

case class BusinessActivities(businessActivities: Set[BusinessActivity], dateOfChange: Option[String] = None)

sealed trait BusinessActivity

case object AccountancyServices extends BusinessActivity

case object BillPaymentServices extends BusinessActivity

case object EstateAgentBusinessService extends BusinessActivity

case object HighValueDealing extends BusinessActivity

case object MoneyServiceBusiness extends BusinessActivity

case object TrustAndCompanyServices extends BusinessActivity

case object TelephonePaymentService extends BusinessActivity


object BusinessActivity {

  implicit val jsonActivityReads: Reads[BusinessActivity] = Reads {
    case JsString("01") => JsSuccess(AccountancyServices)
    case JsString("02") => JsSuccess(BillPaymentServices)
    case JsString("03") => JsSuccess(EstateAgentBusinessService)
    case JsString("04") => JsSuccess(HighValueDealing)
    case JsString("05") => JsSuccess(MoneyServiceBusiness)
    case JsString("06") => JsSuccess(TrustAndCompanyServices)
    case JsString("07") => JsSuccess(TelephonePaymentService)
    case _ => JsError((JsPath \ "businessActivities") -> ValidationError("error.invalid"))
  }

  implicit val jsonActivityWrite = Writes[BusinessActivity] {
    case AccountancyServices => JsString("01")
    case BillPaymentServices => JsString("02")
    case EstateAgentBusinessService => JsString("03")
    case HighValueDealing => JsString("04")
    case MoneyServiceBusiness => JsString("05")
    case TrustAndCompanyServices => JsString("06")
    case TelephonePaymentService => JsString("07")
  }
}

object BusinessActivities {

  implicit val formats = Json.format[BusinessActivities]
  def getMSB(msb: Boolean): Option[BusinessActivity] = {
    msb match {
      case true => Some(MoneyServiceBusiness)
      case false => None
    }
  }

  def getHVD(hvd: Boolean): Option[BusinessActivity] = {
    hvd match {
      case true => Some(HighValueDealing)
      case false => None
    }
  }
  def getASP(asp: Boolean): Option[BusinessActivity] = {
    asp match {
      case true => Some(AccountancyServices)
      case false => None
    }
  }
  def getTCSP(tcsp: Boolean): Option[BusinessActivity] = {
    tcsp match {
      case true => Some(TrustAndCompanyServices)
      case false => None
    }
  }
  def getEAB(eab: Boolean): Option[BusinessActivity] = {
    eab match {
      case true => Some(EstateAgentBusinessService)
      case false => None
    }
  }
  def getBpsp(bpsp: Boolean): Option[BusinessActivity] = {
    bpsp match {
      case true => Some(BillPaymentServices)
      case false => None
    }
  }
  def getTditpsp(tditpsp: Boolean): Option[BusinessActivity] = {
    tditpsp match {
      case true => Some(TelephonePaymentService)
      case false => None
    }
  }

  implicit def conv(activities: Option[MlrActivitiesAppliedFor]): BusinessActivities = {

    activities match {
      case Some(mlrActivitiesAppliedFor) => BusinessActivities(Set(getMSB(mlrActivitiesAppliedFor.msb), getHVD(mlrActivitiesAppliedFor.hvd),
        getASP(mlrActivitiesAppliedFor.asp), getTCSP(mlrActivitiesAppliedFor.tcsp),
        getEAB(mlrActivitiesAppliedFor.eab), getBpsp(mlrActivitiesAppliedFor.bpsp), getTditpsp(mlrActivitiesAppliedFor.tditpsp)).flatten)
      case _ => BusinessActivities(Set.empty)
    }

  }
}
