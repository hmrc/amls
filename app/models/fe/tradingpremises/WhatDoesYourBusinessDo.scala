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

package models.fe.tradingpremises

import models.des.tradingpremises._
import play.api.data.validation.ValidationError
import play.api.libs.json._
import utils.CommonMethods

sealed trait BusinessActivity

object BusinessActivity{

  case object AccountancyServices extends BusinessActivity
  case object BillPaymentServices extends  BusinessActivity
  case object EstateAgentBusinessService extends BusinessActivity
  case object HighValueDealing extends BusinessActivity
  case object MoneyServiceBusiness extends BusinessActivity
  case object TrustAndCompanyServices extends BusinessActivity
  case object TelephonePaymentService extends BusinessActivity

  implicit val jsonActivityReads: Reads[BusinessActivity] = Reads {
    case JsString("01") => JsSuccess(AccountancyServices)
    case JsString("02") => JsSuccess(BillPaymentServices)
    case JsString("03") => JsSuccess(EstateAgentBusinessService)
    case JsString("04") => JsSuccess(HighValueDealing)
    case JsString("05") => JsSuccess(MoneyServiceBusiness)
    case JsString("06") => JsSuccess(TrustAndCompanyServices)
    case JsString("07") => JsSuccess(TelephonePaymentService)
    case _ => JsError((JsPath \ "activities") -> ValidationError("error.invalid"))
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



case class WhatDoesYourBusinessDo(activities : Set[BusinessActivity], dateOfChange: Option[String] = None)

object WhatDoesYourBusinessDo {
  implicit val format = Json.format[WhatDoesYourBusinessDo]

  def convMsb(msb: Msb): Option[BusinessActivity] = {
    msb match {
      case Msb(false,false,false,false,false) => None
      case Msb(_,_,_,_,_) => Some(BusinessActivity.MoneyServiceBusiness)
    }
  }

  implicit def conv(agentPremises: AgentPremises) : WhatDoesYourBusinessDo = {

    val businessActivities = Set(CommonMethods.getSpecificType[BusinessActivity](agentPremises.asp.asp, BusinessActivity.AccountancyServices),
      CommonMethods.getSpecificType[BusinessActivity](agentPremises.bpsp.bpsp, BusinessActivity.BillPaymentServices),
      CommonMethods.getSpecificType[BusinessActivity](agentPremises.eab.eab, BusinessActivity.EstateAgentBusinessService),
      CommonMethods.getSpecificType[BusinessActivity](agentPremises.hvd.hvd, BusinessActivity.HighValueDealing),
      convMsb(agentPremises.msb),
      CommonMethods.getSpecificType[BusinessActivity](agentPremises.tcsp.tcsp, BusinessActivity.TrustAndCompanyServices),
      CommonMethods.getSpecificType[BusinessActivity](agentPremises.tditpsp.tditpsp, BusinessActivity.TelephonePaymentService)
    ).flatten

    WhatDoesYourBusinessDo(businessActivities)
  }

  implicit def conv(ownPremises: OwnBusinessPremisesDetails) : WhatDoesYourBusinessDo = {

    val businessActivities = Set(CommonMethods.getSpecificType[BusinessActivity](ownPremises.asp.asp, BusinessActivity.AccountancyServices),
      CommonMethods.getSpecificType[BusinessActivity](ownPremises.bpsp.bpsp, BusinessActivity.BillPaymentServices),
      CommonMethods.getSpecificType[BusinessActivity](ownPremises.eab.eab, BusinessActivity.EstateAgentBusinessService),
      CommonMethods.getSpecificType[BusinessActivity](ownPremises.hvd.hvd, BusinessActivity.HighValueDealing),
      convMsb(ownPremises.msb),
      CommonMethods.getSpecificType[BusinessActivity](ownPremises.tcsp.tcsp, BusinessActivity.TrustAndCompanyServices),
      CommonMethods.getSpecificType[BusinessActivity](ownPremises.tditpsp.tditpsp, BusinessActivity.TelephonePaymentService)
    ).flatten

    WhatDoesYourBusinessDo(businessActivities)
  }

}
