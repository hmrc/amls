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

package audit

import exceptions.HttpStatusException
import models.des._
import play.api.libs.json._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions._
import uk.gov.hmrc.play.audit.model.{DataEvent, ExtendedDataEvent}
import utils._

object SubscriptionEvent {
  def apply(safeId: String, request: SubscriptionRequest, response: SubscriptionResponse)
           (implicit hc: HeaderCarrier, reqW: Writes[SubscriptionRequest], resW: Writes[SubscriptionResponse]): ExtendedDataEvent = {
    ExtendedDataEvent(
      auditSource = AuditHelper.appName,
      auditType = "applicationSubmitted",
      tags = hc.toAuditTags("Subscription", "N/A"),
      detail = Json.toJson(request).as[JsObject]
        ++ Json.toJson(hc.toAuditDetails()).as[JsObject]
        ++ JsObject(Map("amlsRegistrationNumber" -> JsString(response.amlsRefNo)))
        ++ JsObject(Map("paymentReference" -> JsString(response.paymentReference)))
        ++ JsObject(Map("safeId" -> JsString(safeId)))
        ++ Json.toJson(response).as[JsObject]
    )
  }
}

object SubscriptionFailedEvent {
  def apply(safeId: String, request: SubscriptionRequest, ex: HttpStatusException)
           (implicit hc: HeaderCarrier, reqW: Writes[SubscriptionRequest]): ExtendedDataEvent = {
    ExtendedDataEvent(
      auditSource = AuditHelper.appName,
      auditType = "applicationSubmissionFailed",
      tags = hc.toAuditTags("Subscription", "N/A"),
      detail = Json.toJson(request).as[JsObject]
        ++ Json.toJson(hc.toAuditDetails()).as[JsObject]
        ++ JsObject(Map("safeId" -> JsString(safeId)))
        ++ JsObject(Map("reason" -> JsString(ex.body.getOrElse("No body found"))))
    )
  }
}

object SubscriptionValidationFailedEvent {
  def apply(safeId: String, request: SubscriptionRequest, validationResults: Seq[JsObject])
           (implicit hc: HeaderCarrier, reqW: Writes[SubscriptionRequest]): ExtendedDataEvent = {
    ExtendedDataEvent(
      auditSource = AuditHelper.appName,
      auditType = "applicationSubmissionFailedValidation",
      tags = hc.toAuditTags("Subscription", "N/A"),
      detail = Json.obj(
        "request" -> request,
        "validationResults" -> validationResults,
        "safeId" -> safeId
      )
    )
  }
}

object AmendmentEvent {
  def apply(amlsRegistrationNumber: String, request: AmendVariationRequest, response: AmendVariationResponse)
           (implicit hc: HeaderCarrier): ExtendedDataEvent = {

    val inputAuditType = request.amlsMessageType match {
      case "Amendment" => "amendmentSubmitted"
      case "Variation" => "variationSubmitted"
      case "Renewal" => "renewalSubmitted"
      case "Renewal Amendment" => "renewalAmendmentSubmitted"
      case _ => throw new Exception("Amls Message type is missing")
    }

    val auditModel = AmendVariationAuditModel(amlsRegistrationNumber,
      response,
      request.acknowledgementReference,
      request.businessDetails.typeOfLegalEntity,
      request.changeIndicators,
      request
    )
    val requiredInfo = Json.toJson(auditModel)

    ExtendedDataEvent(
      auditSource = AuditHelper.appName,
      auditType = inputAuditType,
      tags = hc.toAuditTags("Amendment", "N/A"),
      detail = requiredInfo.as[JsObject]
        ++ Json.toJson(hc.toAuditDetails()).as[JsObject]
    )
  }
}

object AmendmentEventFailed {
  def apply(amlsRegistrationNumber: String, request: AmendVariationRequest, ex: HttpStatusException)
           (implicit hc: HeaderCarrier): ExtendedDataEvent = {

    val inputAuditType = request.amlsMessageType match {
      case "Amendment" => "amendmentFailed"
      case "Variation" => "variationFailed"
      case "Renewal" => "renewalFailed"
      case "Renewal Amendment" => "renewalAmendmentfailed"
      case _ => throw new Exception("Amls Message type is missing")
    }

    ExtendedDataEvent(
      auditSource = AuditHelper.appName,
      auditType = inputAuditType,
      tags = hc.toAuditTags("Amendment", "N/A"),
      detail = Json.toJson(request).as[JsObject]
        ++ Json.toJson(hc.toAuditDetails()).as[JsObject]
        ++ JsObject(Map("amlsRegistrationNumber" -> JsString(amlsRegistrationNumber)))
        ++ JsObject(Map("reason" -> JsString(ex.body.getOrElse("No body found"))))
    )
  }
}

object AmendVariationValidationFailedEvent {
  def apply(amlsReferenceNumber: String, request: AmendVariationRequest, validationResults: Seq[JsObject])
           (implicit hc: HeaderCarrier, reqW: Writes[AmendVariationRequest]) = {
    ExtendedDataEvent(
      // NOTE: Use auditSource and auditType when searching splunk for failures.
      auditSource = AuditHelper.appName,
      auditType = "amendVariationValidationFailedEvent",
      tags = hc.toAuditTags("Amendment", "N/A"),
      detail = Json.toJson(hc.toAuditDetails()).as[JsObject] ++
        Json.obj(
          "request" -> request,
          "validationResults" -> validationResults,
          "amlsRefNo" -> amlsReferenceNumber
        )
    )
  }
}

object WithdrawSubscriptionEvent {
  def apply(amlsRegistrationNumber: String, request: WithdrawSubscriptionRequest, response: WithdrawSubscriptionResponse)
           (implicit hc: HeaderCarrier): DataEvent =
    DataEvent(
      auditSource = AuditHelper.appName,
      auditType = "OutboundCall",
      tags = hc.toAuditTags("WithdrawSubscription", "N/A"),
      detail = hc.toAuditDetails() ++ Map(
        "amlsRegistrationNumber" -> amlsRegistrationNumber,
        "request" -> Json.toJson(request).toString,
        "response" -> Json.toJson(response).toString)
    )
}

object DeregisterSubscriptionEvent {
  def apply(amlsRegistrationNumber: String, request: DeregisterSubscriptionRequest, response: DeregisterSubscriptionResponse)
           (implicit hc: HeaderCarrier): DataEvent =
    DataEvent(
      auditSource = AuditHelper.appName,
      auditType = "OutboundCall",
      tags = hc.toAuditTags("DeregisterSubscription", "N/A"),
      detail = hc.toAuditDetails() ++ Map(
        "amlsRegistrationNumber" -> amlsRegistrationNumber,
        "request" -> Json.toJson(request).toString,
        "response" -> Json.toJson(response).toString)
    )
}
