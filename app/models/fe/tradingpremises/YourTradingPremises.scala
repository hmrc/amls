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

package models.fe.tradingpremises


import models.des.tradingpremises.{AgentDetails, OwnBusinessPremisesDetails}
import org.joda.time.LocalDate
import play.api.libs.json.{Reads, Writes}
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._

case class YourTradingPremises(
                                tradingName: String,
                                tradingPremisesAddress: Address,
                                startDate: LocalDate,
                                isResidential: Boolean,
                                tradingNameChangeDate: Option[String] = None
                              )

object YourTradingPremises {

  implicit val reads: Reads[YourTradingPremises] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (
      (__ \ "tradingName").read[String] and
        __.read[Address] and
        (__ \ "startDate").read[LocalDate] and
        (__ \ "isResidential").read[Boolean] and
        (__ \ "tradingNameChangeDate").readNullable[String]
      ) (YourTradingPremises.apply _)
  }

  implicit val writes: Writes[YourTradingPremises] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (
      (__ \ "tradingName").write[String] and
        __.write[Address] and
        (__ \ "startDate").write[LocalDate] and
        (__ \ "isResidential").write[Boolean] and
        (__ \ "tradingNameChangeDate").writeNullable[String]
      ) (unlift(YourTradingPremises.unapply))
  }

  implicit def conv(agentDetails: AgentDetails): YourTradingPremises = {
    val agentPremises = agentDetails.agentPremises

    val startDate = agentDetails.startDate

    YourTradingPremises(agentPremises.tradingName,
      agentPremises.businessAddress,
      LocalDate.parse(startDate.getOrElse("")),
      agentPremises.residential)
  }

  implicit def conv(ownPremises: OwnBusinessPremisesDetails): YourTradingPremises = {
    YourTradingPremises(ownPremises.tradingName.getOrElse(""),
      ownPremises.businessAddress,
      LocalDate.parse(ownPremises.startDate),
      ownPremises.residential)
  }
}
