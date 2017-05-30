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

package models.des

import models.des
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsNumber, JsObject, JsString}

class SubscriptionResponseSpec extends PlaySpec {

  "SubscriptionResponse" must {
    "Serialise correctly with registration fee" in {

      val response = des.SubscriptionResponse(
        etmpFormBundleNumber = "111111",
        amlsRefNo = "XAML00000567890",
        Some(150.00),
        Some(100.0),
        300.0,
        550.0,
        "XA353523452345"
      )

      SubscriptionResponse.format.writes(response) must be(JsObject(Seq(("etmpFormBundleNumber", JsString("111111")), ("amlsRefNo", JsString("XAML00000567890")),
        ("registrationFee", JsNumber(150)), ("fpFee", JsNumber(100)), ("premiseFee", JsNumber(300)), ("totalFees", JsNumber(550)),
        ("paymentReference", JsString("XA353523452345")))))

    }

    "Serialise correctly without registration fee" in {

      val response = des.SubscriptionResponse(
        etmpFormBundleNumber = "111111",
        amlsRefNo = "XAML00000567890",
        None,
        Some(100.0),
        300.0,
        550.0,
        "XA353523452345"
      )

      SubscriptionResponse.format.writes(response) must be(JsObject(Seq(("etmpFormBundleNumber", JsString("111111")), ("amlsRefNo", JsString("XAML00000567890")),
         ("fpFee", JsNumber(100)), ("premiseFee", JsNumber(300)), ("totalFees", JsNumber(550)),
        ("paymentReference", JsString("XA353523452345")))))

    }
  }

}
