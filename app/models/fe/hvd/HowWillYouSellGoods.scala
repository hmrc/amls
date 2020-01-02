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

import models.des.businessactivities.BusinessActivities
import play.api.libs.json.Json

case class HowWillYouSellGoods(salesChannels : Seq[SalesChannel])

object HowWillYouSellGoods {

  implicit val formats = Json.format[HowWillYouSellGoods]

  def convRetail(retail: Boolean): Option[SalesChannel] =
    retail match {
      case true => Some(Retail)
      case false => None
    }

  def convWholesale(wholesale: Boolean): Option[SalesChannel] =
    wholesale match {
      case true => Some(Wholesale)
      case false => None
    }

  def convAuction(auction: Boolean): Option[SalesChannel] =
    auction match {
      case true => Some(Auction)
      case false => None
    }

  implicit def convHowWillYouSellGoods(ba: BusinessActivities): Option[HowWillYouSellGoods] = {

    ba.hvdGoodsSold match {
      case Some(hvdGoodsSold) => hvdGoodsSold.howGoodsAreSold match {

        case Some(goods) => Some(HowWillYouSellGoods(Seq(
          convRetail(goods.retail),
          convWholesale(goods.wholesale),
          convAuction(goods.auction)).flatten))

        case None => None
      }
      case None => None
    }
  }

}
