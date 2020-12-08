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

package models.fe.hvd

import models.des.DesConstants
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsSuccess

class ExciseGoodsSpec extends PlaySpec {

  "ExciseGoods" should {
    "Json Validation" must {
      "successfully read and write json data" in {
        ExciseGoods.format.reads(ExciseGoods.format.writes(ExciseGoods(true))) must be(JsSuccess(ExciseGoods(true)))
      }
    }

    "convert to false if hvdAlcoholTobacco is None but hvd and has alcohol or tobacco" in {
      ExciseGoods.conv(DesConstants.testBusinessActivities.copy(hvdAlcoholTobacco = None)) must be(Some(ExciseGoods(false)))
    }

    "convert to None if hvdAlcoholTobacco is None but hvd and does not have alcohol or tobacco" in {
      ExciseGoods.conv(DesConstants.testBusinessActivitiesNoAlcoholOrTobacco.copy(hvdAlcoholTobacco = None)) must be(None)
    }

    "convert to None if hvdAlcoholTobacco is None" in {
      ExciseGoods.conv(DesConstants.testBusinessActivities.copy(hvdAlcoholTobacco = None,hvdGoodsSold = None)) must be(None)
    }

  }
}
