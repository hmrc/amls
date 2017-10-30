/*
 * Copyright 2017 HM Revenue & Customs
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

import models.des.DesConstants
import models.fe.hvd.PercentageOfCashPaymentOver15000.First
import org.joda.time.LocalDate
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class HvdSpec extends PlaySpec with TableDrivenPropertyChecks{

  private val DefaultCashPayment = CashPaymentYes(new LocalDate(1956, 2, 15))
  private val DefaultProducts = Products(Set(Other("Details")))
  private val DefaultExciseGoods = ExciseGoods(true)
  private val DefaultLinkedCashPayment = LinkedCashPayments(true)
  private val DefaultHowWillYouSellGoods = HowWillYouSellGoods(Seq(Retail, Wholesale, Auction))
  private val DefaultPercentageOfCashPaymentOver15000 = First
  private val paymentMethods = PaymentMethods(courier = true, direct = true, true, Some("foo"))
  private val DefaultReceiveCashPayments = ReceiveCashPayments(true, Some(paymentMethods))

  val NewCashPayment = CashPaymentNo
  val NewProducts = Products(Set(Other("Details")))
  val NewExciseGoods = ExciseGoods(true)
  val NewLinkedCashPayment = LinkedCashPayments(true)

  "hvd" must {

    val completeJson = Json.obj(
      "cashPayment" -> Json.obj(
        "acceptedAnyPayment" -> true,
        "paymentDate" -> new LocalDate(1956, 2, 15)),
      "products" -> Json.obj(
        "products" -> Json.arr("12"),
        "otherDetails" -> "Details"),
      "exciseGoods" -> Json.obj("exciseGoods" -> true),
      "linkedCashPayment" -> Json.obj("linkedCashPayments" -> true),
      "howWillYouSellGoods" -> Json.obj("salesChannels" -> Json.arr("Retail", "Wholesale", "Auction")),
      "receiveCashPayments" -> true,
      "cashPaymentMethods" -> Json.obj("courier" -> true, "direct" -> true, "other" -> true, "details" -> "foo"),
      "percentageOfCashPaymentOver15000" -> Json.obj("percentage" -> "01"),
      "dateOfChange" -> "2016-02-24"
    )

    val completeModel = Hvd(cashPayment = Some(DefaultCashPayment),
      products = Some(DefaultProducts),
      exciseGoods = Some(DefaultExciseGoods),
      linkedCashPayment = Some(DefaultLinkedCashPayment),
      howWillYouSellGoods = Some(DefaultHowWillYouSellGoods),
      receiveCashPayments = Some(true),
      cashPaymentMethods = Some(paymentMethods),
      percentageOfCashPaymentOver15000 = Some(DefaultPercentageOfCashPaymentOver15000),
      dateOfChange = Some("2016-02-24")
    )

    "Serialise as expected" in {
      Json.toJson(completeModel) must be(completeJson)
    }

    "Deserialise as expected" in {
      completeJson.as[Hvd] must be(completeModel)
    }

    "converting the des subscription model with values must yield a frontend Hvd model with values" in {
      val testHvd = Hvd(
        cashPayment = Some(CashPaymentYes(new LocalDate(2001, 1, 1))),
        products = DesConstants.testBusinessActivities,
        exciseGoods = DesConstants.testBusinessActivities,
        howWillYouSellGoods = DesConstants.testBusinessActivities,
        percentageOfCashPaymentOver15000 = DesConstants.testHvd,
        receiveCashPayments = Hvd.convPayments(DesConstants.testHvd),
        cashPaymentMethods = Hvd.convPaymentMethods(DesConstants.testHvd),
        linkedCashPayment = DesConstants.testHvd
      )

      Hvd.conv(DesConstants.SubscriptionViewModel) must be(Some(testHvd))
    }

    "converting the des subscription model with no hvd model will return none" in {
      Hvd.conv(DesConstants.SubscriptionViewModel.copy(hvd = None)) must be(None)
    }
  }
}
