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

package models.des.tradingpremises

import models.des.RequestType
import play.api.libs.json.Json
import models.fe.tradingpremises.{TradingPremises => FETradingPremises}

case class TradingPremises(
                            ownBusinessPremises: Option[OwnBusinessPremises],
                            agentBusinessPremises: Option[AgentBusinessPremises]
                          )

object TradingPremises {

  implicit val format = Json.format[TradingPremises]

  implicit def convert(tradingPremises: Seq[FETradingPremises])(implicit requestType: RequestType): TradingPremises = {
    val (agent, own) = tradingPremises.partition(_.registeringAgentPremises.fold(false)(x => x.agentPremises))
    TradingPremises(Some(own), Some(agent))
  }

  implicit def convert(tradingPremises: Option[Seq[FETradingPremises]])(implicit requestType: RequestType): TradingPremises = {
    convert(tradingPremises.getOrElse(Seq.empty))
  }
}
