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
import play.api.data.mapping.{Failure, Path, Success}
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsPath, JsSuccess}

class ExciseGoodsSpec extends PlaySpec {

  "ExciseGoods" should {
    "Json Validation" must {
      "successfully read and write json data" in {
        ExciseGoods.format.reads(ExciseGoods.format.writes(ExciseGoods(true))) must be(JsSuccess(ExciseGoods(true),
          JsPath \ "exciseGoods"))
      }
    }
    "convert to None if hvdAlcoholTobacco is None" in {
      ExciseGoods.conv(DesConstants.testBusinessActivities.copy(hvdAlcoholTobacco = None)) must be(None)
    }
  }
}
