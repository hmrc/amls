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

package models

import models.des.AmendVariationResponse
import models.fe.SubscriptionResponse
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.custom.JsPathSupport._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

sealed trait ResponseType

case object SubscriptionResponseType extends ResponseType

case object AmendOrVariationResponseType extends ResponseType

object ResponseType {

  import utils.MappingUtils.Implicits._

  implicit val jsonWrites: Writes[ResponseType] = Writes[ResponseType] {
    case SubscriptionResponseType => JsString("SubscriptionReponse")
    case AmendOrVariationResponseType => JsString("AmendOrVariationResponse")
  }

  implicit val jsonReads: Reads[ResponseType] = {
    import play.api.libs.json.Reads.StringReads
    (__).read[String] flatMap {
      case "SubscriptionReponse" => SubscriptionResponseType
      case "AmendOrVariationResponse" => AmendOrVariationResponseType
      case _ =>
        JsonValidationError("error.invalid")
    }
  }
}

case class Fees(responseType: ResponseType,
                amlsReferenceNumber: String,
                registrationFee: BigDecimal = 0,
                fpFee: Option[BigDecimal],
                premiseFee: BigDecimal = 0,
                totalFees: BigDecimal = 0,
                paymentReference: Option[String],
                difference: Option[BigDecimal],
                approvalCheckFeeRate: Option[BigDecimal] = None,
                approvalCheckFee: Option[BigDecimal] = None,
                createdAt: LocalDateTime)

object Fees {
  def convertSubscription(subscriptionResponse: SubscriptionResponse): Option[Fees] = {
    subscriptionResponse.subscriptionFees map {
      feesResponse =>
        Fees(SubscriptionResponseType,
          subscriptionResponse.amlsRefNo,
          feesResponse.registrationFee,
          feesResponse.fpFee,
          feesResponse.premiseFee,
          feesResponse.totalFees,
          Some(feesResponse.paymentReference),
          None,
          feesResponse.approvalCheckFeeRate,
          feesResponse.approvalCheckFee,
          LocalDateTime.now(UTC))
    }
  }

  implicit def convertAmendmentVariation(amendVariationResponse: AmendVariationResponse, amlsReferenceNumber: String): Fees = {
    Fees(AmendOrVariationResponseType,
      amlsReferenceNumber,
      amendVariationResponse.registrationFee.getOrElse(0),
      amendVariationResponse.fpFee,
      amendVariationResponse.premiseFee.getOrElse(0),
      amendVariationResponse.totalFees.getOrElse(0),
      amendVariationResponse.paymentReference,
      amendVariationResponse.difference,
      amendVariationResponse.approvalCheckFeeRate,
      amendVariationResponse.approvalCheckFee,
      LocalDateTime.now(UTC))
  }

  implicit lazy val reads: Reads[Fees] =
    (
      (__ \ "responseType").read[ResponseType] and
        (__ \ "amlsReferenceNumber").read[String] and
        (__ \ "registrationFee").read[BigDecimal] and
        (__ \ "fpFee").readNullable[BigDecimal] and
        (__ \ "premiseFee").read[BigDecimal] and
        (__ \ "totalFees").read[BigDecimal] and
        (__ \ "paymentReference").readNullable[String] and
        (__ \ "difference").readNullable[BigDecimal] and
        (__ \ "approvalCheckFeeRate").readNullable[BigDecimal] and
        (__ \ "approvalCheckFee").readNullable[BigDecimal] and
        (__ \ "createdAt").readLocalDateTime
      ) (Fees.apply _)


  implicit lazy val writes: OWrites[Fees] =
    (
      (__ \ "responseType").write[ResponseType] and
        (__ \ "amlsReferenceNumber").write[String] and
        (__ \ "registrationFee").write[BigDecimal] and
        (__ \ "fpFee").writeNullable[BigDecimal] and
        (__ \ "premiseFee").write[BigDecimal] and
        (__ \ "totalFees").write[BigDecimal] and
        (__ \ "paymentReference").writeNullable[String] and
        (__ \ "difference").writeNullable[BigDecimal] and
        (__ \ "approvalCheckFeeRate").writeNullable[BigDecimal] and
        (__ \ "approvalCheckFee").writeNullable[BigDecimal] and
        (__ \ "createdAt").write[LocalDateTime](MongoJavatimeFormats.localDateTimeWrites)
      ) (unlift(Fees.unapply))

  implicit val format: OFormat[Fees] = OFormat(reads, writes)
}
