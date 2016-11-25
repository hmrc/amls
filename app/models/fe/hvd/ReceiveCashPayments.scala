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

import play.api.libs.json.{Writes, _}
import models.des.hvd.{Hvd=> DesHvd}


case class ReceiveCashPayments(receivePayments:Boolean, paymentMethods: Option[PaymentMethods])


object ReceiveCashPayments {

  implicit val format = Json.format[ReceiveCashPayments]

  implicit def conv(hvd: DesHvd): Option[ReceiveCashPayments] = {

    hvd.hvdFromUnseenCustDetails match{
      case Some(unseen) => Some(ReceiveCashPayments(unseen.receiptMethods.isDefined, unseen.receiptMethods))
      case None => None
    }

  }

}
