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

package models.payapi

import java.time.LocalDateTime

import generators.AmlsReferenceNumberGenerator
import models.payapi.PaymentStatuses.Successful
import models.payapi.TaxTypes.`other`
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class PaymentSpec extends PlaySpec with MockitoSugar with AmlsReferenceNumberGenerator {

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

  "Payment" must {
    "serialise to JSON" in {
      Json.toJson(Payment(
        _id,
        Some(amlsRegistrationNumber),
        other,
        ref,
        desc,
        amountInPence,
        commissionInPence,
        totalInPence,
        url,
        Some(Card(
          CardTypes.`visa-debit`,
          Some(20.00)
        )),
        Map.empty,
        Some(Provider(name, providerRef)),
        Some(now),
        Successful,
        Some(now)
      )) must be(Json.obj(
        "_id" -> _id,
        "amlsRefNo" -> amlsRegistrationNumber,
        "taxType" -> "other",
        "reference" -> ref,
        "description" -> desc,
        "amountInPence" -> amountInPence,
        "commissionInPence" -> commissionInPence,
        "totalInPence" -> totalInPence,
        "returnUrl" -> url,
        "card" -> Json.obj(
          "type" -> "visa-debit",
          "creditCardCommissionRate" -> 20.00
        ),
        "additionalInformation" -> Json.obj(),
        "provider" -> Json.obj(
          "name" -> name,
          "reference" -> providerRef
        ),
        "confirmed" -> Json.toJson(now),
        "status" -> "Successful",
        "createdAt" -> Json.toJson(now)
      ))

    }
  }

}
