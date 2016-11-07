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

import play.api.data.mapping._
import play.api.libs.functional.Monoid
import play.api.libs.json.{Writes, _}
import models.des.hvd.{Hvd=> DesHvd}

case class ReceiveCashPayments(paymentMethods: Option[PaymentMethods])

sealed trait ReceiveCashPayments0 {

  private implicit def rule[A]
  (implicit
   b: Path => Rule[A, Boolean],
   aR: Path => Rule[A, A],
   paymentMethodsR: Rule[A, PaymentMethods]
  ): Rule[A, ReceiveCashPayments] =
    From[A] { __ =>

      import utils.MappingUtils.Implicits.RichRule

      val booleanR = b andThen { _ withMessage "error.required.hvd.receive.cash.payments" }

      (__ \ "receivePayments").read(booleanR).flatMap[Option[PaymentMethods]] {
        case true =>
          // Ideally compose would repath here
          (__ \ "paymentMethods").read[A] compose paymentMethodsR.repath((Path \ "paymentMethods") ++ _) fmap Some.apply
        case false =>
          Rule(_ => Success(None))
      } fmap ReceiveCashPayments.apply
    }

  private implicit def opW[I, O]
  (implicit
   mon: Monoid[O],
   w: Write[I, O]
  ): Write[Option[I], O] =
    Write {
      case Some(i) =>
        w.writes(i)
      case None =>
        mon.identity
    }

  private implicit def write[A]
  (implicit
   mon: Monoid[A],
   b: Path => Write[Boolean, A],
   aW: Path => Write[A, A],
   paymentMethodsW: Write[Option[PaymentMethods], A]
  ): Write[ReceiveCashPayments, A] =
    To[A] { __ =>

      import utils.MappingUtils.Implicits.RichWrite

      (
        (__ \ "receivePayments").write[Boolean].contramap[Option[_]] {
          case Some(_) => true
          case None => false
        } and
          (__ \ "paymentMethods").write[A].andThen(paymentMethodsW)
        )(a => (a.paymentMethods, a.paymentMethods))
    }


  val jsonR: Reads[ReceiveCashPayments] = {
    import play.api.data.mapping.json.Rules.{pickInJson => _, _}
    import utils.JsonMapping.{genericJsonR, pickInJson}
    implicitly[Reads[ReceiveCashPayments]]
  }

  val jsonW: Writes[ReceiveCashPayments] = {
    import utils.JsonMapping.genericJsonW
    import play.api.data.mapping.json.Writes._
    implicitly[Writes[ReceiveCashPayments]]
  }
}

object ReceiveCashPayments {

  private object Cache extends ReceiveCashPayments0

  implicit val jsonR = Cache.jsonR
  implicit val jsonW = Cache.jsonW


  implicit def conv(hvd: DesHvd): Option[ReceiveCashPayments] = {

    hvd.hvdFromUnseenCustDetails match{
      case Some(unseen) => Some(ReceiveCashPayments(unseen.receiptMethods))
      case None => None
    }

  }

}
