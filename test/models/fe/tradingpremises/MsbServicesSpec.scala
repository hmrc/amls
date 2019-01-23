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

package models.fe.tradingpremises

import models.des.tradingpremises.Msb
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json._

class MsbServicesSpec extends PlaySpec {

  "MsbServices" must {

    "round trip through Json correctly" in {

      val data = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal, CurrencyExchange, ForeignExchange))
      val js = Json.toJson(data)

      js.as[MsbServices] mustEqual data
    }

    "fail when on invalid data" in {
      Json.fromJson[MsbServices](Json.obj("msbServices" -> Seq("40"))) must
        be(JsError((JsPath \ "msbServices") (0)  -> ValidationError("error.invalid")))
    }

    "convert msb des model to frontend model" in {
      val feModel = Some(MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal, ForeignExchange)))

      MsbServices.convMsb(Msb(true, false, true, true, true)) must be(feModel)
    }

    "convert msb des model to frontend model when no msb options selected" in {
      MsbServices.convMsb(Msb(false, false, false, false, false)) must be(None)
    }
  }
}
