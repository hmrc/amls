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

import models.des.businessactivities.BusinessActivities
import play.api.data.mapping._
import play.api.libs.json.{Reads, Writes}
import utils.TraversableValidators
import utils.MappingUtils.Implicits._


case class HowWillYouSellGoods(channels : Seq[SalesChannel])

trait HowWillYouSellGoods0 {
  private implicit def rule[A]
  (implicit
   a: Path => RuleLike[A, Seq[String]]
  ) = From[A] { __ =>
    (__ \ "salesChannels")
      .read(TraversableValidators.minLengthR[Seq[String]](1))
      .withMessage("error.required.hvd.how-will-you-sell-goods").fmap { s =>
      HowWillYouSellGoods(s.map {
        case "Retail" => Retail
        case "Wholesale" => Wholesale
        case "Auction" => Auction
      })
    }
  }

  private implicit def write[A]
  (implicit
   a: Path => WriteLike[Seq[String], A]) = To[A] { __ =>
    (__ \ "salesChannels").write[Seq[String]].contramap { hwysg: HowWillYouSellGoods =>
      hwysg.channels.map {
        case Retail => "Retail"
        case Wholesale => "Wholesale"
        case Auction => "Auction"
      }
    }
  }

  val jsonR: Reads[HowWillYouSellGoods] = {
    import play.api.data.mapping.json.Rules.{JsValue => _, pickInJson => _, _}
    import utils.JsonMapping._
    implicitly[Reads[HowWillYouSellGoods]]
  }


  val jsonW: Writes[HowWillYouSellGoods] = {
    import play.api.data.mapping.json.Writes._
    import utils.JsonMapping._
    implicitly[Writes[HowWillYouSellGoods]]

  }
}

object HowWillYouSellGoods {

  private object Cache extends HowWillYouSellGoods0

  implicit val jsonR: Reads[HowWillYouSellGoods] = Cache.jsonR
  implicit val jsonW: Writes[HowWillYouSellGoods] = Cache.jsonW

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
