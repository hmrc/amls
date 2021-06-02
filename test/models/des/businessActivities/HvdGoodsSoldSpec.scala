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

package models.des.businessActivities

import models.des.businessactivities.{HowGoodsAreSold, HvdGoodsSold}
import models.fe.hvd._
import org.scalatestplus.play.PlaySpec

class HvdGoodsSoldSpec extends PlaySpec {

  "HvdGoodsSold" should {

    val DefaultProducts = Products(Set(Alcohol, Tobacco, Other("Details")))
    val DefaultExciseGoods = ExciseGoods(true)
    val DefaultHowWillYouSellGoods = HowWillYouSellGoods(Seq(Retail, Wholesale, Auction))

    val HvdModel = Hvd(cashPayment = None,
      products = Some(DefaultProducts),
      exciseGoods = Some(DefaultExciseGoods),
      linkedCashPayment = None,
      howWillYouSellGoods = Some(DefaultHowWillYouSellGoods)
    )

    "successfully convert hvd frontend model to HvdGoodsSold" in {

      HvdGoodsSold.conv(Some(HvdModel)) must be(Some(HvdGoodsSold(true, true,false,false,
        false,false,false,false,false,false,false,true, Some("Details"), Some(HowGoodsAreSold(true,true,true)))))
    }

    "successfully convert hvd frontend model to HvdGoodsSold when HowWillYouSellGoods id none" in {
      val DefaultProducts = Products(Set(OtherMotorVehicles, Caravans, ScrapMetals, MobilePhones, Clothing))
      val DefaultExciseGoods = ExciseGoods(true)

      val HvdModel = Hvd(cashPayment = None,
        products = Some(DefaultProducts),
        exciseGoods = Some(DefaultExciseGoods),
        linkedCashPayment = None,
        howWillYouSellGoods = None
      )

      HvdGoodsSold.conv(Some(HvdModel)) must be(Some(HvdGoodsSold(false,false,false,false,true,true,false,false,true,true,true,false,None,None)))
    }

    "successfully convert the data model" in {
      HvdGoodsSold.conv(Some(Hvd())) must be(Some(HvdGoodsSold(false,false,false,false,false,false,false,false,false,false,false,false,None,None)))
    }

  }

}
