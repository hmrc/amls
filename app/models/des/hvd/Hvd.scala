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

package models.des.hvd

import models.fe.hvd.PercentageOfCashPaymentOver15000._
import models.fe.hvd.{PercentageOfCashPaymentOver15000, CashPaymentNo, CashPaymentYes, CashPayment}
import play.api.libs.json.Json

case class Hvd (
                 cashPaymentsAccptOvrThrshld: Boolean,
                 dateOfTheFirst: Option[String],
                 sysAutoIdOfLinkedCashPymts: Boolean,
                 hvPercentageTurnover: Option[Int],
                 hvdFromUnseenCustDetails: Option[HvdFromUnseenCustDetails]
               )

object Hvd {

  implicit val format = Json.format[Hvd]

  private val Twenty = 20
  private val Forty = 40
  private val Sixty = 60
  private val Eighty = 80
  private val Hundred = 100

  def getCashPayment(cashPayment: Option[CashPayment]): (Boolean, Option[String]) = {
    cashPayment match {
      case Some(data) =>  data match {
        case CashPaymentYes(date) => (true, Some(date.toString))
        case CashPaymentNo => (false, None)
      }
      case None => (false, None)
    }
  }

  implicit def conv(hvd: models.fe.hvd.Hvd): Hvd = {
      val (cashPayment, paymentDate) = getCashPayment(hvd.cashPayment)
      val sysLinkedCashPayment = hvd.linkedCashPayment.fold(false)(x => x.linkedCashPayments)

      Hvd(cashPayment, paymentDate, sysLinkedCashPayment, hvd.percentageOfCashPaymentOver15000, hvd.receiveCashPayments)
  }

  implicit def percentageCashPayment(model: Option[PercentageOfCashPaymentOver15000]): Option[Int] = {
    model match {
      case Some(data) => data match {
        case First => Some(Twenty)
        case Second => Some(Forty)
        case Third => Some(Sixty)
        case Fourth => Some(Eighty)
        case Fifth => Some(Hundred)
      }
      case None => None
    }

  }
}
