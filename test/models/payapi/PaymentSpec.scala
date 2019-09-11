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

package models.payapi

import java.time.LocalDateTime

import generators.AmlsReferenceNumberGenerator
import models.payapi.PaymentStatuses.Successful
import models.payapi.TaxTypes.`other`
import org.scalatest.mockito.MockitoSugar
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
        other,
        ref,
        desc,
        amountInPence,
        url,
        PaymentStatuses.Successful
      )) must be(Json.obj(
        "_id" -> _id,
        "taxType" -> "other",
        "reference" -> ref,
        "description" -> desc,
        "amountInPence" -> amountInPence,
        "returnUrl" -> url,
        "status" -> "Successful"
      ))

    }
  }

}
