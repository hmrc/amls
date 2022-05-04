/*
 * Copyright 2022 HM Revenue & Customs
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

package models.fe.hvd

import org.joda.time.{LocalDate}
import models.des.hvd.{Hvd=> DesHvd}
import play.api.libs.json.Reads

sealed trait CashPayment

case class CashPaymentYes(paymentDate: LocalDate) extends CashPayment

case object CashPaymentNo extends CashPayment

object CashPayment {

  implicit val jsonReads: Reads[CashPayment] = {
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    import play.api.libs.json.JodaReads.DefaultJodaLocalDateReads

    (__ \ "acceptedAnyPayment").read[Boolean] flatMap {
      case true => (__ \ "paymentDate").read[LocalDate] map CashPaymentYes.apply
      case false => Reads(_ => JsSuccess(CashPaymentNo))
    }
  }

  implicit val jsonWrites = {
    import play.api.libs.json.Writes._
    import play.api.libs.json._

    Writes[CashPayment] {
      case CashPaymentYes(b) => Json.obj(
        "acceptedAnyPayment" -> true,
        "paymentDate" -> b.toString
      )
      case CashPaymentNo => Json.obj("acceptedAnyPayment" -> false)
    }
  }

  implicit def conv(hvd: DesHvd): Option[CashPayment] = {
    hvd.cashPaymentsAccptOvrThrshld match {
      case true => Some(CashPaymentYes(new LocalDate(hvd.dateOfTheFirst.getOrElse(""))))
      case false => Some(CashPaymentNo)
    }
  }
}
