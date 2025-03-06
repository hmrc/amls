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

package models.des.hvd

import models.fe.hvd.{Hvd => FEHvd, _}
import models.fe.hvd.PercentageOfCashPaymentOver15000.{Fifth, First, Fourth, Third}

import java.time.LocalDate
import org.scalatestplus.play.PlaySpec

class HvdSpec extends PlaySpec {

  "Hvd" should {

    "successfully convert frontend model to valid des model" in {
      // scalastyle:off magic.number
      val DefaultCashPayment                      = CashPaymentYes(LocalDate.of(1956, 2, 15))
      val DefaultExciseGoods                      = ExciseGoods(true)
      val DefaultLinkedCashPayment                = LinkedCashPayments(true)
      val DefaultPercentageOfCashPaymentOver15000 = Third
      val paymentMethods                          = PaymentMethods(courier = true, direct = true, true, Some("foo"))

      val completeModel = FEHvd(
        Some(DefaultCashPayment),
        exciseGoods = Some(DefaultExciseGoods),
        linkedCashPayment = Some(DefaultLinkedCashPayment),
        receiveCashPayments = Some(true),
        cashPaymentMethods = Some(paymentMethods),
        percentageOfCashPaymentOver15000 = Some(DefaultPercentageOfCashPaymentOver15000)
      )

      Hvd.conv(Some(completeModel)) must be(
        Some(
          Hvd(
            true,
            Some("1956-02-15"),
            None,
            true,
            Some(60),
            Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))
          )
        )
      )
    }

    "successfully convert frontend model to valid des model with cashpayment no" in {
      val DefaultCashPayment                      = CashPaymentNo
      val DefaultExciseGoods                      = ExciseGoods(true)
      val DefaultLinkedCashPayment                = LinkedCashPayments(true)
      val DefaultPercentageOfCashPaymentOver15000 = First
      val paymentMethods                          = PaymentMethods(courier = true, direct = true, true, Some("foo"))

      val completeModel = FEHvd(
        Some(DefaultCashPayment),
        exciseGoods = Some(DefaultExciseGoods),
        linkedCashPayment = Some(DefaultLinkedCashPayment),
        receiveCashPayments = Some(true),
        cashPaymentMethods = Some(paymentMethods),
        percentageOfCashPaymentOver15000 = Some(DefaultPercentageOfCashPaymentOver15000),
        dateOfChange = Some("1999-1-1")
      )

      Hvd.conv(Some(completeModel)) must be(
        Some(
          Hvd(
            false,
            None,
            None,
            true,
            Some(20),
            Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))
          )
        )
      )
    }

    "successfully convert frontend model to valid des model with cashpayment option is none" in {
      val DefaultExciseGoods                      = ExciseGoods(true)
      val DefaultLinkedCashPayment                = LinkedCashPayments(true)
      val DefaultPercentageOfCashPaymentOver15000 = Fourth
      val paymentMethods                          = PaymentMethods(courier = true, direct = true, true, Some("foo"))

      val completeModel = FEHvd(
        None,
        exciseGoods = Some(DefaultExciseGoods),
        linkedCashPayment = Some(DefaultLinkedCashPayment),
        receiveCashPayments = Some(true),
        cashPaymentMethods = Some(paymentMethods),
        percentageOfCashPaymentOver15000 = Some(DefaultPercentageOfCashPaymentOver15000)
      )

      Hvd.conv(Some(completeModel)) must be(
        Some(
          Hvd(
            false,
            None,
            None,
            true,
            Some(80),
            Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))
          )
        )
      )
    }

    "successfully convert frontend model to valid des model with PercentageOfCashPaymentOver15000 option is Fifth" in {
      val DefaultExciseGoods                      = ExciseGoods(true)
      val DefaultLinkedCashPayment                = LinkedCashPayments(true)
      val DefaultPercentageOfCashPaymentOver15000 = Fifth
      val paymentMethods                          = PaymentMethods(courier = true, direct = true, true, Some("foo"))

      val completeModel = FEHvd(
        None,
        exciseGoods = Some(DefaultExciseGoods),
        linkedCashPayment = Some(DefaultLinkedCashPayment),
        receiveCashPayments = Some(true),
        cashPaymentMethods = Some(paymentMethods),
        percentageOfCashPaymentOver15000 = Some(DefaultPercentageOfCashPaymentOver15000)
      )

      Hvd.conv(Some(completeModel)) must be(
        Some(
          Hvd(
            false,
            None,
            None,
            true,
            Some(100),
            Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))
          )
        )
      )
    }

    "successfully convert frontend model to valid des model with PercentageOfCashPaymentOver15000 option is not given" in {
      val DefaultExciseGoods       = ExciseGoods(true)
      val DefaultLinkedCashPayment = LinkedCashPayments(true)
      val paymentMethods           = PaymentMethods(courier = true, direct = true, true, Some("foo"))

      val completeModel = FEHvd(
        None,
        exciseGoods = Some(DefaultExciseGoods),
        linkedCashPayment = Some(DefaultLinkedCashPayment),
        receiveCashPayments = Some(true),
        cashPaymentMethods = Some(paymentMethods),
        percentageOfCashPaymentOver15000 = None
      )

      Hvd.conv(Some(completeModel)) must be(
        Some(
          Hvd(
            false,
            None,
            None,
            true,
            Some(0),
            Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))
          )
        )
      )
    }

    "successfully convert frontend model to valid des model with percentageCashPayment option is none" in {
      val DefaultExciseGoods       = ExciseGoods(true)
      val DefaultLinkedCashPayment = LinkedCashPayments(true)
      val paymentMethods           = PaymentMethods(courier = true, direct = true, true, Some("foo"))

      val completeModel = FEHvd(
        None,
        exciseGoods = Some(DefaultExciseGoods),
        linkedCashPayment = Some(DefaultLinkedCashPayment),
        receiveCashPayments = Some(true),
        cashPaymentMethods = Some(paymentMethods),
        percentageOfCashPaymentOver15000 = None
      )

      Hvd.conv(Some(completeModel)) must be(
        Some(
          Hvd(
            false,
            None,
            None,
            true,
            Some(0),
            Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))
          )
        )
      )
    }

    "successfully convert frontend model to valid des model with LinkedCashPayment option is none" in {
      val DefaultExciseGoods = ExciseGoods(true)
      val paymentMethods     = PaymentMethods(courier = true, direct = true, true, Some("foo"))

      val completeModel = FEHvd(
        None,
        exciseGoods = Some(DefaultExciseGoods),
        linkedCashPayment = None,
        receiveCashPayments = Some(true),
        cashPaymentMethods = Some(paymentMethods),
        percentageOfCashPaymentOver15000 = None
      )

      Hvd.conv(Some(completeModel)) must be(
        Some(
          Hvd(
            false,
            None,
            None,
            false,
            Some(0),
            Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))
          )
        )
      )
    }

  }
}
