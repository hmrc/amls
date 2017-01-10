/*
 * Copyright 2017 HM Revenue & Customs
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

package models.fe.tradingpremises

import models.des.tradingpremises.{AgentPremises, OwnBusinessPremisesDetails}
import org.joda.time.LocalDate
import play.api.libs.json.{Reads, Writes}

case class YourTradingPremises(
                                tradingName: String,
                                tradingPremisesAddress: Address,
                                startDate: LocalDate,
                                isResidential: Boolean
                              )

object YourTradingPremises {

  implicit val reads: Reads[YourTradingPremises] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (
      (__ \ "tradingName").read[String] and
        __.read[Address] and
        (__ \ "startDate").read[LocalDate] and
        (__ \ "isResidential").read[Boolean]
      ) (YourTradingPremises.apply _)
  }

  implicit val writes: Writes[YourTradingPremises] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (
      (__ \ "tradingName").write[String] and
        __.write[Address] and
        (__ \ "startDate").write[LocalDate] and
        (__ \ "isResidential").write[Boolean]
      ) (unlift(YourTradingPremises.unapply))
  }

  implicit def conv(agentPremises: AgentPremises): YourTradingPremises = {
    YourTradingPremises(agentPremises.tradingName,
      agentPremises.businessAddress,
      LocalDate.parse(agentPremises.startDate),
      agentPremises.residential)
  }

  implicit def conv(ownPremises: OwnBusinessPremisesDetails): YourTradingPremises = {
    YourTradingPremises(ownPremises.tradingName,
      ownPremises.businessAddress,
      LocalDate.parse(ownPremises.startDate),
      ownPremises.residential)
  }
}
