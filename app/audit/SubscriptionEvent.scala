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

package audit

import models.des._
import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.play.audit.model.DataEvent
import uk.gov.hmrc.play.audit.AuditExtensions._
import uk.gov.hmrc.play.config.AppName
import uk.gov.hmrc.play.http.HeaderCarrier

object SubscriptionEvent {
  def apply
  (safeId: String, request: SubscriptionRequest, response: SubscriptionResponse)
  (implicit
   hc: HeaderCarrier,
   reqW: Writes[SubscriptionRequest],
   resW: Writes[SubscriptionResponse]
  ): DataEvent =
    DataEvent(
      auditSource = AppName.appName,
      auditType = "OutboundCall",
      tags = hc.toAuditTags("Subscription", "N/A"),
      detail = hc.toAuditDetails() ++ Map(
        "safeId" -> safeId,
        "request" -> Json.toJson(request).toString,
        "response" -> Json.toJson(response).toString,
        "paymentReference" -> response.paymentReference,
        "amlsRegistrationNumber" -> response.amlsRefNo
      )
    )
}

object AmendmentEvent {
  def apply
  (amlsRegistrationNumber: String, request: AmendVariationRequest, response: AmendVariationResponse)
  (implicit
   hc: HeaderCarrier,
   reqW: Writes[SubscriptionRequest],
   resW: Writes[SubscriptionResponse]
  ): DataEvent =
    DataEvent(
      auditSource = AppName.appName,
      auditType = "OutboundCall",
      tags = hc.toAuditTags("Amendment", "N/A"),
      detail = hc.toAuditDetails() ++ Map(
        "amlsRegistrationNumber" -> amlsRegistrationNumber,
        "request" -> Json.toJson(request).toString,
        "response" -> Json.toJson(response).toString)
        ++ {
        response.paymentReference match {
          case Some(paymentReference) => Map("paymentReference" -> paymentReference)
          case _ => Map.empty
        }
      }
    )
}


object WithdrawSubscriptionEvent {
  def apply
  (amlsRegistrationNumber: String, request: WithdrawSubscriptionRequest, response: WithdrawSubscriptionResponse)
  (implicit
   hc: HeaderCarrier): DataEvent =
    DataEvent(
      auditSource = AppName.appName,
      auditType = "OutboundCall",
      tags = hc.toAuditTags("WithdrawSubscription", "N/A"),
      detail = hc.toAuditDetails() ++ Map(
        "amlsRegistrationNumber" -> amlsRegistrationNumber,
        "request" -> Json.toJson(request).toString,
        "response" -> Json.toJson(response).toString)
    )
}

object DeregisterSubscriptionEvent {
  def apply
  (amlsRegistrationNumber: String, request: DeregisterSubscriptionRequest, response: DeregisterSubscriptionResponse)
  (implicit
   hc: HeaderCarrier): DataEvent =
    DataEvent(
      auditSource = AppName.appName,
      auditType = "OutboundCall",
      tags = hc.toAuditTags("DeregisterSubscription", "N/A"),
      detail = hc.toAuditDetails() ++ Map(
        "amlsRegistrationNumber" -> amlsRegistrationNumber,
        "request" -> Json.toJson(request).toString,
        "response" -> Json.toJson(response).toString)
    )
}