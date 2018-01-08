/*
 * Copyright 2018 HM Revenue & Customs
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

import models.des.SubscriptionView
import play.api.libs.json.Json


case class Hvd (cashPayment: Option[CashPayment] = None,
                products: Option[Products] = None,
                exciseGoods:  Option[ExciseGoods] = None,
                howWillYouSellGoods: Option[HowWillYouSellGoods] = None,
                percentageOfCashPaymentOver15000: Option[PercentageOfCashPaymentOver15000] = None,
                receiveCashPayments: Option[Boolean] = None,
                cashPaymentMethods: Option[PaymentMethods] = None,
                linkedCashPayment: Option[LinkedCashPayments] = None,
                dateOfChange: Option[String] = None
               ) {
}

object Hvd {

  implicit val format = Json.format[Hvd]

  implicit def default(hvd: Option[Hvd]): Hvd =
    hvd.getOrElse(Hvd())

  def convPayments(hvd: models.des.hvd.Hvd): Option[Boolean] = {
    hvd.hvdFromUnseenCustDetails.map(h => h.hvdFromUnseenCustomers)
  }

  def convPaymentMethods(hvd: models.des.hvd.Hvd): Option[PaymentMethods] = {
    hvd.hvdFromUnseenCustDetails.flatMap(h => h.receiptMethods)
  }

  implicit def conv(view: SubscriptionView): Option[Hvd] = {
    view.hvd match {
      case Some(hvd) => Some(Hvd(
        hvd,
        view.businessActivities,
        view.businessActivities,
        view.businessActivities,
        hvd,
        convPayments(hvd),
        convPaymentMethods(hvd),
        hvd
      ))
      case None => None
    }
  }

}
