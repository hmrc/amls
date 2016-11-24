/*
 * Copyright 2016 HM Revenue & Customs
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
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class PercentageOfCashPaymentOver15000Spec extends PlaySpec {

  "PercentageOfCashPaymentOver15000" should {

    "JSON validation" must {

      "successfully validate given an enum value" in {

        Json.fromJson[PercentageOfCashPaymentOver15000](Json.obj("percentage" -> "01")) must
          be(JsSuccess(PercentageOfCashPaymentOver15000.First))

        Json.fromJson[PercentageOfCashPaymentOver15000](Json.obj("percentage" -> "02")) must
          be(JsSuccess(PercentageOfCashPaymentOver15000.Second))

        Json.fromJson[PercentageOfCashPaymentOver15000](Json.obj("percentage" -> "03")) must
          be(JsSuccess(PercentageOfCashPaymentOver15000.Third))

        Json.fromJson[PercentageOfCashPaymentOver15000](Json.obj("percentage" -> "04")) must
          be(JsSuccess(PercentageOfCashPaymentOver15000.Fourth))

        Json.fromJson[PercentageOfCashPaymentOver15000](Json.obj("percentage" -> "05")) must
          be(JsSuccess(PercentageOfCashPaymentOver15000.Fifth))

      }

      "write the correct value" in {

        Json.toJson(PercentageOfCashPaymentOver15000.First) must
          be(Json.obj("percentage" -> "01"))

        Json.toJson(PercentageOfCashPaymentOver15000.Second) must
          be(Json.obj("percentage" -> "02"))

        Json.toJson(PercentageOfCashPaymentOver15000.Third) must
          be(Json.obj("percentage" -> "03"))

        Json.toJson(PercentageOfCashPaymentOver15000.Fourth) must
          be(Json.obj("percentage" -> "04"))

        Json.toJson(PercentageOfCashPaymentOver15000.Fifth) must
          be(Json.obj("percentage" -> "05"))

      }
      "throw error for invalid data" in {
        Json.fromJson[PercentageOfCashPaymentOver15000](Json.obj("percentage" -> "20")) must
          be(JsError(JsPath \ "percentage", ValidationError("error.invalid")))
      }
    }
    "convert to the correct model given a percentageTurnover int" in {
      PercentageOfCashPaymentOver15000.conv(DesConstants.testHvd.copy(hvPercentageTurnover = Some(20))) must be(Some(PercentageOfCashPaymentOver15000.First))
      PercentageOfCashPaymentOver15000.conv(DesConstants.testHvd.copy(hvPercentageTurnover = Some(40))) must be(Some(PercentageOfCashPaymentOver15000.Second))
      PercentageOfCashPaymentOver15000.conv(DesConstants.testHvd.copy(hvPercentageTurnover = Some(60))) must be(Some(PercentageOfCashPaymentOver15000.Third))
      PercentageOfCashPaymentOver15000.conv(DesConstants.testHvd.copy(hvPercentageTurnover = Some(80))) must be(Some(PercentageOfCashPaymentOver15000.Fourth))
      PercentageOfCashPaymentOver15000.conv(DesConstants.testHvd.copy(hvPercentageTurnover = Some(100))) must be(Some(PercentageOfCashPaymentOver15000.Fifth))
    }
    "convert to None given hvPercentageTurnover = None" in {
      PercentageOfCashPaymentOver15000.conv(DesConstants.testHvd.copy(hvPercentageTurnover = None)) must be(None)
    }
  }
}
