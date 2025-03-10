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

package models.fe.hvd

import models.des.hvd.ReceiptMethods
import play.api.libs.json.{Json, OFormat}

case class PaymentMethods(courier: Boolean, direct: Boolean, other: Boolean, details: Option[String])

object PaymentMethods {
  implicit val format: OFormat[PaymentMethods] = Json.format[PaymentMethods]

  implicit def conv(method: Option[ReceiptMethods]): Option[PaymentMethods] =
    method match {
      case Some(payment) =>
        Some(
          PaymentMethods(
            payment.receiptMethodViaCourier,
            payment.receiptMethodDirectBankAct,
            payment.specifyOther.nonEmpty,
            payment.specifyOther
          )
        )
      case None          => None
    }
}
