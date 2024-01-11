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

package models.des

import play.api.libs.json._

sealed trait WithdrawalReason

object WithdrawalReason {

  case object OutOfscope extends WithdrawalReason
  case object NotTradingInOwnRight extends WithdrawalReason
  case object UnderAnotherSupervisor extends WithdrawalReason
  case object JoinedAWRSGroup extends WithdrawalReason
  case object Other extends WithdrawalReason

  implicit val jsonServiceReads: Reads[WithdrawalReason] =
    Reads {
      case JsString("Out of scope") => JsSuccess(OutOfscope)
      case JsString("Not trading in own right") => JsSuccess(NotTradingInOwnRight)
      case JsString("Under another supervisor") => JsSuccess(UnderAnotherSupervisor)
      case JsString("Joined AWRS Group") => JsSuccess(JoinedAWRSGroup)
      case JsString("Other, please specify") => JsSuccess(Other)
      case _ => JsError((JsPath \ "withdrawalReason") -> JsonValidationError("error.invalid"))
    }

  implicit val jsonServiceWrites: Writes[WithdrawalReason] =
    Writes[WithdrawalReason] {
      case OutOfscope => JsString("Out of scope")
      case NotTradingInOwnRight => JsString("Not trading in own right")
      case UnderAnotherSupervisor => JsString("Under another supervisor")
      case JoinedAWRSGroup => JsString("Joined AWRS Group")
      case Other => JsString("Other, please specify")
    }
}

case class WithdrawSubscriptionRequest (acknowledgementReference: String,
                                        withdrawalDate: String,
                                        withdrawalReason: WithdrawalReason,
                                        withdrawalReasonOthers: Option[String]
                                       )

object WithdrawSubscriptionRequest {

  implicit val format: OFormat[WithdrawSubscriptionRequest] = Json.format[WithdrawSubscriptionRequest]
}
