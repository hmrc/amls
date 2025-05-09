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

import models.fe.hvd._
import org.scalatestplus.play.PlaySpec

class HvdFromUnseenCustDetailsSpec extends PlaySpec {

  "HvdFromUnseenCustDetails" should {
    "successfully convert frontend model to des model" in {
      val paymentMethods = PaymentMethods(courier = true, direct = true, true, Some("foo"))
      val fe             = models.fe.hvd.Hvd(
        receiveCashPayments = Some(true),
        cashPaymentMethods = Some(paymentMethods)
      )

      HvdFromUnseenCustDetails.conv(fe) must be(
        Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))
      )
    }

    "successfully convert frontend model to des model when frontend model is none" in {
      val fe = models.fe.hvd.Hvd(
        receiveCashPayments = Some(false)
      )

      HvdFromUnseenCustDetails.conv(fe) must be(Some(HvdFromUnseenCustDetails(false, None)))
    }

    "successfully convert frontend model to des model when frontend model is none1" in {
      HvdFromUnseenCustDetails.conv(None) must be(None)
    }
  }
}
