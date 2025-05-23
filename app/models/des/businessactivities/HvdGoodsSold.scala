/*
 * Copyright 2024 HM Revenue & Customs
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

import models.fe.hvd._
import play.api.libs.json.{Json, OFormat}

case class HvdGoodsSold(
  alcohol: Boolean,
  tobacco: Boolean,
  antiques: Boolean,
  cars: Boolean,
  otherMotorVehicles: Boolean,
  caravans: Boolean,
  jewellery: Boolean,
  gold: Boolean,
  scrapMetals: Boolean,
  mobilePhones: Boolean,
  clothing: Boolean,
  other: Boolean,
  specifyOther: Option[String],
  howGoodsAreSold: Option[HowGoodsAreSold]
)

object HvdGoodsSold {
  implicit val format: OFormat[HvdGoodsSold] = Json.format[HvdGoodsSold]

  implicit def conv(hvd: Option[models.fe.hvd.Hvd]): Option[HvdGoodsSold] =
    hvd match {
      case Some(data) => conv1(data)
      case None       => None
    }

  // scalastyle:off  cyclomatic.complexity
  implicit def conv1(hvd: models.fe.hvd.Hvd): Option[HvdGoodsSold] = {

    val products = hvd.products.fold[Set[ItemType]](Set.empty)(x => x.items)

    val hvdGoodsSold  = products.foldLeft(
      HvdGoodsSold(
        alcohol = false,
        tobacco = false,
        antiques = false,
        cars = false,
        otherMotorVehicles = false,
        caravans = false,
        jewellery = false,
        gold = false,
        scrapMetals = false,
        mobilePhones = false,
        clothing = false,
        other = false,
        None,
        None
      )
    ) { (result, productType) =>
      productType match {
        case Alcohol            => result.copy(alcohol = true)
        case Tobacco            => result.copy(tobacco = true)
        case Antiques           => result.copy(antiques = true)
        case Cars               => result.copy(cars = true)
        case OtherMotorVehicles => result.copy(otherMotorVehicles = true)
        case Caravans           => result.copy(caravans = true)
        case Jewellery          => result.copy(jewellery = true)
        case Gold               => result.copy(gold = true)
        case ScrapMetals        => result.copy(scrapMetals = true)
        case MobilePhones       => result.copy(mobilePhones = true)
        case Clothing           => result.copy(clothing = true)
        case Other(dtls)        => result.copy(other = true, specifyOther = Some(dtls))
      }
    }
    val CompleteModel = hvdGoodsSold.copy(howGoodsAreSold = hvd.howWillYouSellGoods)
    Some(CompleteModel)
  }
}
