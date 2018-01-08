/*
 * Copyright 2018 HM Revenue & Customs
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

package models.des.businessactivities

import models.fe.hvd.{HowWillYouSellGoods, Auction, Wholesale, Retail}
import play.api.libs.json.Json

case class HowGoodsAreSold(retail: Boolean,
                           wholesale: Boolean,
                           auction: Boolean)

object HowGoodsAreSold {
  implicit val format = Json.format[HowGoodsAreSold]

  implicit def conv(model: Option[HowWillYouSellGoods]): Option[HowGoodsAreSold] = {
    model match {
      case Some(data) => data
      case None => None
    }
  }

  implicit def conv1(model: HowWillYouSellGoods): Option[HowGoodsAreSold] = {
    val howWillYouSellGoods = model.salesChannels.foldLeft(HowGoodsAreSold(false, false, false)) {
      (result, channel) =>
        channel match {
          case Retail => result.copy(retail = true)
          case Wholesale => result.copy(wholesale = true)
          case Auction => result.copy(auction = true)
        }
    }
    Some(howWillYouSellGoods)
  }
}
