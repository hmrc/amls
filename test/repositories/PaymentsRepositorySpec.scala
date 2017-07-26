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

package repositories

import java.time.LocalDateTime

import models.PaymentStatuses.Successful
import models.TaxTypes.`other`
import models.{Card, Payment, Provider}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, WordSpec}
import reactivemongo.bson.BSONObjectID
import repository.EmbeddedMongo

import scala.concurrent.ExecutionContext.Implicits.global

class PaymentsRepositorySpec extends WordSpec with EmbeddedMongo with ScalaFutures with Matchers with IntegrationPatience {

  val _id = "biuh98huiu"
  val ref = "ref"
  val desc = "desc"
  val url = "url"

  val amountInPence = 100
  val commissionInPence = 20
  val totalInPence = 120

  val id = "uihuibhjbui"
  val name = "providerName"
  val providerRef = "providerRef"

  val now = LocalDateTime.now()

  private val db = connection("payments")
  private val paymentRepository = new PaymentsRepository(() => db)
  private val testPayment = Payment(
    _id,
    other,
    ref,
    desc,
    amountInPence,
    commissionInPence,
    totalInPence,
    url,
    Some(Card(
      models.CardTypes.`visa-debit`,
      Some(20.00)
    )),
    Map.empty,
    Some(Provider(name, providerRef)),
    Some(now),
    Successful
  )

  "PaymentRepository" should {
    "insert new payment" in {
      val result = for {
        payment <- paymentRepository.insert(testPayment)
        storedPayment <- {
          paymentRepository.findById(payment._id)
        }
      } yield storedPayment
      whenReady(result) { p =>
        p shouldBe 'defined
        p.get shouldEqual testPayment
      }
    }
  }

}
