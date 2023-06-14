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

package models.payments

import models.payapi.PaymentStatus.Created
import models.payapi.{Payment => PayApiPayment, _}
import org.bson.types.ObjectId
import play.api.libs.functional.syntax._
import play.api.libs.json.{OFormat, OWrites, Reads, __}
import play.custom.JsPathSupport.RichJsPath
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.LocalDateTime

case class Payment(_id: String,
                   amlsRefNo: String,
                   safeId: String,
                   reference: String,
                   description: Option[String],
                   amountInPence: Int,
                   status: PaymentStatus,
                   createdAt: LocalDateTime,
                   isBacs: Option[Boolean] = None,
                   updatedAt: Option[LocalDateTime] = None
                  )

object Payment {

  def apply(amlsRegNo: String, safeId: String, apiPayment: PayApiPayment): Payment =
    Payment(
      apiPayment.id,
      amlsRegNo,
      safeId,
      apiPayment.reference.getOrElse(""),
      apiPayment.description,
      apiPayment.amountInPence.getOrElse(0),
      apiPayment.status,
      LocalDateTime.now
    )

  def apply(bacsPaymentRequest: CreateBacsPaymentRequest): Payment =
    Payment(new ObjectId().toString,
      bacsPaymentRequest.amlsReference,
      bacsPaymentRequest.safeId,
      bacsPaymentRequest.paymentReference,
      None,
      bacsPaymentRequest.amountInPence,
      Created,
      LocalDateTime.now,
      isBacs = Some(true)
    )

  implicit val reads: Reads[Payment] =
    (
      (__ \ "_id").read[String] and
        (__ \ "amlsRefNo").read[String] and
        (__ \ "safeId").read[String] and
        (__ \ "reference").read[String] and
        (__ \ "description").readNullable[String] and
        (__ \ "amountInPence").read[Int] and
        (__ \ "status").read[PaymentStatus] and
        (__ \ "createdAt").readCreatedDate and
        (__ \ "isBacs").readNullable[Boolean] and
        (__ \ "updatedAt").readNullable[LocalDateTime]
      ) (Payment(_, _, _, _, _, _, _, _, _, _))

  implicit val writes: OWrites[Payment] =
    (
      (__ \ "_id").write[String] and
        (__ \ "amlsRefNo").write[String] and
        (__ \ "safeId").write[String] and
        (__ \ "reference").write[String] and
        (__ \ "description").writeNullable[String] and
        (__ \ "amountInPence").write[Int] and
        (__ \ "status").write[PaymentStatus] and
        (__ \ "createdAt").write[LocalDateTime](MongoJavatimeFormats.localDateTimeWrites) and
        (__ \ "isBacs").writeNullable[Boolean] and
        (__ \ "updatedAt").writeNullable[LocalDateTime]
      ) (unlift(Payment.unapply))

  implicit val format: OFormat[Payment] = OFormat(reads, writes)
}
