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

package models.des.hvd

import models.fe.hvd.PercentageOfCashPaymentOver15000._
import models.fe.hvd._
import play.api.libs.json.Json

case class Hvd(
                cashPaymentsAccptOvrThrshld: Boolean,
                dateOfTheFirst: Option[String],
                dateChangeFlag: Option[Boolean],
                sysAutoIdOfLinkedCashPymts: Boolean,
                hvPercentageTurnover: Option[Int],
                hvdFromUnseenCustDetails: Option[HvdFromUnseenCustDetails]
              )

object Hvd {

  implicit val format = Json.format[Hvd]

  private val Zero = 0
  private val Twenty = 20
  private val Forty = 40
  private val Sixty = 60
  private val Eighty = 80
  private val Hundred = 100

  def getCashPayment(cashPayment: Option[CashPayment]): (Boolean, Option[String]) = {
    cashPayment match {
      case Some(data) => data match {
        case CashPaymentYes(date) => (true, Some(date.toString))
        case CashPaymentNo => (false, None)
      }
      case None => (false, None)
    }
  }

  implicit def conv(hvdOpt: Option[models.fe.hvd.Hvd]): Option[Hvd] =
    hvdOpt match {
      case Some(models.fe.hvd.Hvd(None, None, None, None, None, None, None, None, None)) => None
      case hvd: Option[models.fe.hvd.Hvd] => hvd.map {
        hvdResult =>
          val (cashPayment, paymentDate) = getCashPayment(hvdResult.cashPayment)
          val sysLinkedCashPayment = hvdResult.linkedCashPayment.fold(false)(x => x.linkedCashPayments)
          Hvd(cashPayment, paymentDate, None, sysLinkedCashPayment, hvdResult.percentageOfCashPaymentOver15000, hvdResult)
      }
      case _ => None
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
      case None => Some(Zero)
    }

  }
}
