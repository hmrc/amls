/*
 * Copyright 2016 HM Revenue & Customs
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

import play.api.libs.json
import play.api.libs.json.{Reads, __, Json, Format, JsObject, JsSuccess, JsValue, JsResult}
import models.des.hvd.{Hvd=> DesHvd}
import play.api.libs.functional.syntax._


case class ReceiveCashPayments(receivePayments:Boolean, paymentMethods: Option[PaymentMethods])


object ReceiveCashPayments {

  private def emptyObjectReads[A] (implicit r : Reads[A]) : Reads[Option[A]] = Reads { x =>
    if (x == Json.obj()) {
      JsSuccess(None).asInstanceOf[JsResult[Option[A]]]
    } else {
      JsSuccess(Some(x.as[A])).asInstanceOf[JsResult[Option[A]]]
    }
  }

  private val jsReads : Reads[ReceiveCashPayments] =(
      (__ \ "receivePayments").read[Boolean] ~
      (__ \ "paymentMethods")
        .readNullable[Option[PaymentMethods]](emptyObjectReads[PaymentMethods])
        .map(a => a.flatten)
    )(ReceiveCashPayments.apply _)

  private val jsWrites = Json.writes[ReceiveCashPayments]

  implicit val format = Format(jsReads, jsWrites)

  implicit def conv(hvd: DesHvd): Option[ReceiveCashPayments] = {

    hvd.hvdFromUnseenCustDetails match{
      case Some(unseen) => Some(ReceiveCashPayments(unseen.receiptMethods.isDefined, unseen.receiptMethods))
      case None => None
    }
  }
}
