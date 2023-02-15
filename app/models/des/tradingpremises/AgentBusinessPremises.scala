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

package models.des.tradingpremises

import models.des.RequestType
import play.api.libs.json.Json
import models.fe.tradingpremises.{TradingPremises => FETradingPremises}

case class AgentBusinessPremises(agentBusinessPremises: Boolean, agentDetails: Option[Seq[AgentDetails]])

object AgentBusinessPremises {

  implicit val format = Json.format[AgentBusinessPremises]

  implicit def convert(tradingPremises: Seq[FETradingPremises])(implicit requestType: RequestType): AgentBusinessPremises = {
    val `empty` = Seq.empty[FETradingPremises]
    tradingPremises match {
      case `empty` => AgentBusinessPremises(false, None)
      case _ => AgentBusinessPremises(true, Some(tradingPremises))
    }
  }
}
