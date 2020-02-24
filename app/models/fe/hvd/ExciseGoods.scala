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

case class ExciseGoods(exciseGoods: Boolean)

object ExciseGoods {

  implicit val format = Json.format[ExciseGoods]

  implicit def conv(ba: BusinessActivities): Option[ExciseGoods] = {
    (ba.hvdAlcoholTobacco, ba.hvdGoodsSold) match {
      case (Some(goods), _) => Some(ExciseGoods(goods.dutySuspExAtGoods))
      case (None, hvdGoodsSold) => hvdGoodsSold match {
        case Some(x) if x.tobacco || x.alcohol => Some(ExciseGoods(false))
        case None => None
      }
      case _ => None
    }
  }
}
