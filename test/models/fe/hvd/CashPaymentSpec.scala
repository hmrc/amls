/*
 * Copyright 2021 HM Revenue & Customs
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
import org.joda.time.LocalDate
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

class CashPaymentSpec extends PlaySpec with MockitoSugar {

  "CashPaymentSpec" should {
    // scalastyle:off
    val DefaultCashPaymentYes = CashPaymentYes(new LocalDate(1990, 2, 24))

    "JSON validation" must {

      "successfully validate given an enum value" in {

        Json.fromJson[CashPayment](Json.obj("acceptedAnyPayment" -> false)) must
          be(JsSuccess(CashPaymentNo))
      }

      "successfully validate given an `Yes` value" in {

        val json = Json.obj("acceptedAnyPayment" -> true, "paymentDate" ->"1990-02-24")

        Json.fromJson[CashPayment](json) must
          be(JsSuccess(CashPaymentYes(new LocalDate(1990, 2, 24)), JsPath \ "paymentDate"))
      }

      "fail to validate when given an empty `Yes` value" in {

        val json = Json.obj("acceptedAnyPayment" -> true)

        Json.fromJson[CashPayment](json) must
          be(JsError((JsPath \ "paymentDate") -> JsonValidationError("error.path.missing")))
      }

      "Successfully read and write Json data" in {

        CashPayment.jsonReads.reads(CashPayment.jsonWrites.writes(DefaultCashPaymentYes)) must be(
          JsSuccess(CashPaymentYes(new LocalDate(1990, 2, 24)), JsPath \ "paymentDate"))
      }

      "write the correct value" in {
        import play.api.libs.json.JodaWrites.DefaultJodaLocalDateWrites

        Json.toJson(CashPaymentNo: CashPayment) must
          be(Json.obj("acceptedAnyPayment" -> false))

        Json.toJson(DefaultCashPaymentYes: CashPayment) must
          be(Json.obj(
            "acceptedAnyPayment" -> true,
            "paymentDate" -> new LocalDate(1990, 2, 24)
          ))
      }

      "convert to CashPaymentNo if is false" in {
        CashPayment.conv(DesConstants.testHvd.copy(cashPaymentsAccptOvrThrshld = false)) must be(Some(CashPaymentNo))
      }
    }
  }

}
