/*
 * Copyright 2020 HM Revenue & Customs
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

package models.payapi

import java.time.LocalDateTime
import generators.AmlsReferenceNumberGenerator
import models.payapi.TaxTypes.`other`
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class PaymentSpec extends PlaySpec with MockitoSugar with AmlsReferenceNumberGenerator {

  val id = "biuh98huiu"
  val ref = "ref"

  val amountInPence = 100
  val commissionInPence = 20
  val totalInPence = 120
  val name = "providerName"
  val providerRef = "providerRef"

  val now = LocalDateTime.now()

  "Payment" must {
    "serialise to JSON with no description" in {
      Json.toJson(Payment(
        id,
        other,
        ref,
        None,
        amountInPence,
        PaymentStatuses.Successful
      )) must be(Json.obj(
        "id" -> id,
        "taxType" -> "other",
        "reference" -> ref,
        "amountInPence" -> amountInPence,
        "status" -> "Successful"
      ))

    }

    "serialise to JSON with a description" in {
      Json.toJson(Payment(
        id,
        other,
        ref,
        Some("Desc"),
        amountInPence,
        PaymentStatuses.Successful
      )) must be(Json.obj(
        "id" -> id,
        "taxType" -> "other",
        "reference" -> ref,
        "description" -> "Desc",
        "amountInPence" -> amountInPence,
        "status" -> "Successful"
      ))

    }
  }
}
