/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.Json
import utils.AmlsBaseSpec

class HowWillYouSellGoodsSpec extends PlaySpec with AmlsBaseSpec {

  val fullData = HowWillYouSellGoods(Seq(Wholesale, Retail, Auction))
  val fullForm = Map(
    "salesChannels[]" -> Seq("Wholesale", "Retail", "Auction")
  )

  "How will You Sell Goods" should {
    "Round trip through Json" in {
      Json.toJson(fullData).as[HowWillYouSellGoods] must be(fullData)
    }
    "convert to None when hvdGoodsSold is None" in {
      val testModel = DesConstants.testBusinessActivities.copy(hvdGoodsSold = None)
      HowWillYouSellGoods.convHowWillYouSellGoods(testModel) must be(None)
    }
    "convert to None when howGoodsAreSold is None" in {
      val testModel = DesConstants.testBusinessActivities.copy(hvdGoodsSold = Some(DesConstants.testHvdGoodsSold.copy(howGoodsAreSold = None)))
      HowWillYouSellGoods.convHowWillYouSellGoods(testModel) must be(None)
    }
    "convert auction to None if given false" in {
      HowWillYouSellGoods.convAuction(false) must be(None)
    }
    "convert retail to None if given false" in {
      HowWillYouSellGoods.convRetail(false) must be(None)
    }
    "convert wholesale to None if given false" in {
      HowWillYouSellGoods.convWholesale(false) must be(None)
    }

  }
}
