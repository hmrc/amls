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

import generators.PaymentGenerator
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{Matchers, WordSpec}
import repository.EmbeddedMongo

import scala.concurrent.ExecutionContext.Implicits.global

class PaymentsRepositorySpec extends WordSpec with EmbeddedMongo with ScalaFutures with Matchers with IntegrationPatience with PaymentGenerator {

  private val db = connection("payments")
  private val testRepo = new PaymentRepository(() => db)
  private val testPayment = paymentGen.sample.get

  "PaymentRepository" should {
    "insert new payment" in {
      val result = for {
        _ <- testRepo.insert(testPayment)
        storedPayment <- testRepo.findAll()
      } yield storedPayment
      whenReady(result) ( p =>
        p.exists( payment =>
          payment._id.equals(testPayment._id)
        )
      )
    }
    "findLatestByAmlsReference" should {
      "return None if the amlsRefNo does not exist in the database" in {
        whenReady(testRepo.findLatestByAmlsReference("asdfghjkl"))(r => r shouldBe None)
      }
    }
  }

}
