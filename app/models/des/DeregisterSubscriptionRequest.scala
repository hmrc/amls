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

package models.des

import play.api.libs.json._

sealed trait DeregistrationReason

object DeregistrationReason {

  case object Ceasedtrading extends DeregistrationReason
  case object HVDPolicyOfNotAcceptingHighValueCashPayments extends DeregistrationReason
  case object OutOfScope extends DeregistrationReason
  case object NotTradingInOwnRight extends DeregistrationReason
  case object UnderAnotherSupervisor extends DeregistrationReason
  case object ChangeOfLegalEntity extends DeregistrationReason
  case object Other extends DeregistrationReason

  implicit val jsonServiceReads: Reads[DeregistrationReason] =
    Reads {
      case JsString("Ceased Trading") => JsSuccess(Ceasedtrading)
      case JsString("HVD - policy of not accepting high value cash payments") => JsSuccess(HVDPolicyOfNotAcceptingHighValueCashPayments)
      case JsString("Out of scope") => JsSuccess(OutOfScope)
      case JsString("Not trading in own right") => JsSuccess(NotTradingInOwnRight)
      case JsString("Under another supervisor") => JsSuccess(UnderAnotherSupervisor)
      case JsString("Change of Legal Entity") => JsSuccess(ChangeOfLegalEntity)
      case JsString("Other, please specify") => JsSuccess(Other)
      case _ => JsError((JsPath \ "deregistrationReason") -> JsonValidationError("error.invalid"))
    }

  implicit val jsonServiceWrites =
    Writes[DeregistrationReason] {
      case Ceasedtrading => JsString("Ceased Trading")
      case HVDPolicyOfNotAcceptingHighValueCashPayments => JsString("HVD - policy of not accepting high value cash payments")
      case OutOfScope => JsString("Out of scope")
      case NotTradingInOwnRight => JsString("Not trading in own right")
      case UnderAnotherSupervisor => JsString("Under another supervisor")
      case ChangeOfLegalEntity => JsString("Change of Legal Entity")
      case Other => JsString("Other, please specify")
    }
}

case class DeregisterSubscriptionRequest (acknowledgementReference: String,
                                          deregistrationDate: String,
                                          deregistrationReason: DeregistrationReason,
                                          deregReasonOther: Option[String]
                                       )

object DeregisterSubscriptionRequest {

  implicit val format = Json.format[DeregisterSubscriptionRequest]
}
