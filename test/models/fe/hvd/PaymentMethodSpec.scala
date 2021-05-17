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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsSuccess

class PaymentMethodSpec extends PlaySpec {

  "PaymentMethod" must {
    "roundtrip through json" in {
      val data = PaymentMethods(courier = true, direct = true, other = true, Some("foo"))
      PaymentMethods.format.reads(PaymentMethods.format.writes(data)) mustEqual JsSuccess(data)
    }
    "convert to None given no payment receipt value" in {
      PaymentMethods.conv(None) must be(None)
    }
  }
}
