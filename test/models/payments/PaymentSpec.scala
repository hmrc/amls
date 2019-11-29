/*
 * Copyright 2019 HM Revenue & Customs
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

package models.payments

import java.time.LocalDateTime

import generators.PaymentGenerator
import models.payapi.PaymentStatuses.{Created, Successful}
import models.payapi.{Payment => PayApiPayment}
import org.scalatest.MustMatchers
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

//noinspection ScalaStyle
class PaymentSpec extends PlaySpec with MustMatchers with PaymentGenerator {

  "The Payment model" when {
    "serialising" must {

      val now = LocalDateTime.now

      val model = Payment(
        "123456789",
        "X12345678",
        "X73289473",
        "X987654321",
        //"A test payment",
        10000,
        Successful,
        now,
        isBacs = Some(true),
        Some(now.plusDays(1))
      )

      val json = Json.obj(
        "_id" -> "123456789",
        "amlsRefNo" -> "X12345678",
        "safeId" -> "X73289473",
        "reference" -> "X987654321",
        "description" -> "A test payment",
        "amountInPence" -> 10000,
        "status" -> "Successful",
        "isBacs" -> true,
        "createdAt" -> now,
        "updatedAt" -> now.plusDays(1)
      )

      "serialise to Json" in {
        Json.toJson(model) mustBe json
      }

      "deserialise from Json" in {
        Json.fromJson[Payment](json) mustBe JsSuccess(model)
      }
    }

    "converting" must {
      "convert from a Pay Api payment" in {
        val payApiModel = payApiPaymentGen.sample.get
        val refNumber = amlsRefNoGen.sample.get
        val safeId = amlsRefNoGen.sample.get
        val now = LocalDateTime.now

        Payment(refNumber, safeId, payApiModel).copy(createdAt = now) mustBe Payment(
          payApiModel.id,
          refNumber,
          safeId,
          payApiModel.reference,
          //payApiModel.description,
          payApiModel.amountInPence,
          payApiModel.status,
          now,
          isBacs = None,
          None
        )
      }

      "convert from a BACS payment request" in {
        val paymentRequest = createBacsPaymentRequestGen.sample.get

        Payment(paymentRequest) match {
          case Payment(_,
          paymentRequest.amlsReference,
          paymentRequest.safeId,
          paymentRequest.paymentReference,
          //_,
          paymentRequest.amountInPence,
          Created,
          _,
          Some(true),
          None
          ) =>
          case _ => fail("The resulting payment object was not expected")
        }
      }
    }
  }
}
