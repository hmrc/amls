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

package models.des.businessactivities

import models.fe.hvd.{Alcohol, Tobacco}
import play.api.libs.json.Json

case class HvdAlcoholTobacco (dutySuspExAtGoods: Boolean)

object HvdAlcoholTobacco {

  implicit val format = Json.format[HvdAlcoholTobacco]

  implicit def covn(model: Option[models.fe.hvd.Hvd]): Option[HvdAlcoholTobacco] = {

    model match {
      case Some(data) if(data.products.contains(Alcohol) || data.products.contains(Tobacco)) =>
        Some(HvdAlcoholTobacco(data.exciseGoods.fold[Boolean](false)(x=>x.exciseGoods)))
      case _ => None
    }
  }
}
