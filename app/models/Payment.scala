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

package models

import enumeratum.{Enum, EnumEntry}
import java.time.LocalDateTime

import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, _}

object EnumFormat {
  // $COVERAGE-OFF$
  def apply[T <: EnumEntry](e: Enum[T]): Format[T] = Format(
    Reads {
      case JsString(value) => e.withNameOption(value).map(JsSuccess(_))
        .getOrElse(JsError(ValidationError(s"Unknown ${e.getClass.getSimpleName} value: $value", s"error.invalid.${e.getClass.getSimpleName.toLowerCase.replaceAllLiterally("$", "")}")))
      case _ => JsError("Can only parse String")
    },
    Writes(v => JsString(v.entryName))
  )
}

sealed abstract class PaymentStatus(val isFinal: Boolean, val validNextStates: Seq[PaymentStatus] = Seq()) extends EnumEntry

object PaymentStatuses extends Enum[PaymentStatus] {
  case object Created extends PaymentStatus(false, Seq(Sent))
  case object Successful extends PaymentStatus(true)
  case object Sent extends PaymentStatus(false, Seq(Successful, Failed, Cancelled))
  case object Failed extends PaymentStatus(true)
  case object Cancelled extends PaymentStatus(true)

  override def values = findValues
}


sealed abstract class TaxType extends EnumEntry

object TaxTypes extends Enum[TaxType] {
  case object `self-assessment` extends TaxType
  case object `vat` extends TaxType
  case object `epaye` extends TaxType
  case object `p11d` extends TaxType
  case object `other` extends TaxType
  case object `stamp-duty` extends TaxType
  case object `corporation-tax` extends TaxType

  override def values = findValues
}

import models.CardTypes.IsCreditCard

sealed abstract class CardType(val schemeCode: String, val isCreditCard: IsCreditCard) extends EnumEntry

object CardTypes extends Enum[CardType] {

  type IsCreditCard = Boolean

  case object `visa-debit` extends CardType("V", false)
  case object `visa-credit` extends CardType("V", true)
  case object `mastercard-debit` extends CardType("M", false)
  case object `mastercard-credit` extends CardType("M", true)
  case object `visa-electron` extends CardType("V", false)
  case object `maestro` extends CardType("M", false)

  override def values = findValues
}

case class Card(`type`: CardType, creditCardCommissionRate: Option[BigDecimal] = None)

object Card {

  import play.api.libs.functional.syntax._

  implicit val cardTypeFormat = EnumFormat(CardTypes)

  val nonNegativeRateValidator: Reads[BigDecimal] = Reads.of[BigDecimal]
    .filter(ValidationError("Credit Card Commission Rate should be non-negative numbers greater than equal to 0", "error.invalid.rate")
    )(amount => amount >= 0.0)

  implicit val cardRead: Reads[Card] = (
    (__ \ "type").read[CardType] and
      (__ \ "creditCardCommissionRate").readNullable[BigDecimal](nonNegativeRateValidator)
    ) (Card.apply _)

  implicit val cardWrite = Json.writes[Card]
}

case class Payment(
                    _id: String,
                    taxType: TaxType,
                    reference: String,
                    description: String,
                    amountInPence: Int,
                    commissionInPence: Int,
                    totalInPence: Int,
                    returnUrl: String,
                    card: Option[Card],
                    additionalInformation: Map[String, String],
                    provider: Option[Provider],
                    confirmed: Option[LocalDateTime],
                    status: PaymentStatus)

object Payment {
  implicit val taxTypeTypeFormat = EnumFormat(TaxTypes)
  implicit val statusFormat = EnumFormat(PaymentStatuses)
  implicit val providerFormat = Json.format[Provider]
  implicit val paymentOrder = Json.format[PaymentOrder]

  implicit val format = Json.format[Payment]
}

case class Provider(name: String, reference: String)

case class PaymentOrder(id: String, providerName: String, status: PaymentStatus, lastUpdatedOrCreated: LocalDateTime)
