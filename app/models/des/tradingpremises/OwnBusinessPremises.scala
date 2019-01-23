/*
 * Copyright 2019 HM Revenue & Customs
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

package models.des.tradingpremises

import play.api.libs.json.Json
import models.fe.tradingpremises.{TradingPremises => FETradingPremises}

case class OwnBusinessPremises(
                                ownBusinessPremises: Boolean,
                                ownBusinessPremisesDetails: Option[Seq[OwnBusinessPremisesDetails]]
                              )

object OwnBusinessPremises {

  implicit val format = Json.format[OwnBusinessPremises]

  implicit def convert(tradingPremises: Seq[FETradingPremises]): OwnBusinessPremises = {
    val `empty` = Seq.empty[FETradingPremises]
    tradingPremises match {
      case `empty` =>
        OwnBusinessPremises(false, None)
      case _ =>
        OwnBusinessPremises(true, Some(tradingPremises))
    }
  }
}
