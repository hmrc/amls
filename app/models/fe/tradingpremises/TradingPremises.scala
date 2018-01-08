/*
 * Copyright 2018 HM Revenue & Customs
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

import models.des.tradingpremises.{AgentDetails, OwnBusinessPremisesDetails, TradingPremises => DesTradingPremises}
import play.api.libs.json.Json

case class TradingPremises(
                            registeringAgentPremises: Option[RegisteringAgentPremises] = None,
                            yourTradingPremises: YourTradingPremises,
                            businessStructure: Option[BusinessStructure] = None,
                            agentName: Option[AgentName] = None,
                            agentCompanyDetails: Option[AgentCompanyDetails] = None,
                            agentPartnership: Option[AgentPartnership] = None,
                            whatDoesYourBusinessDoAtThisAddress: WhatDoesYourBusinessDo,
                            msbServices: Option[MsbServices] = None,
                            lineId: Option[Int] = None,
                            status: Option[String] = None,
                            endDate: Option[ActivityEndDate] = None,
                            removalReason: Option[String] = None,
                            removalReasonOther: Option[String] = None
                          )

object TradingPremises {

  implicit val format = Json.format[TradingPremises]

  implicit def convAgentPremises(agentDetails: AgentDetails): TradingPremises = {
    val tmp =
      TradingPremises(
        Some(RegisteringAgentPremises(true)),
        agentDetails,
        agentDetails.agentLegalEntity,
        None,
        None,
        None,
        agentDetails.agentPremises,
        agentDetails.agentPremises.msb,
        agentDetails.lineId,
        agentDetails.status,
        removalReason = agentDetails.removalReason,
        removalReasonOther = agentDetails.removalReasonOther
      )

    tmp.businessStructure.map {
      case BusinessStructure.SoleProprietor => tmp.copy(agentName = agentDetails)
      case BusinessStructure.IncorporatedBody => tmp.copy(agentCompanyDetails = agentDetails)
      case BusinessStructure.LimitedLiabilityPartnership => tmp.copy(agentCompanyDetails = agentDetails)
      case BusinessStructure.Partnership => tmp.copy(agentPartnership = agentDetails.agentLegalEntityName)
      case BusinessStructure.UnincorporatedBody => tmp
    }.getOrElse(tmp)
  }

  def convOwnPremises(ownPremises: OwnBusinessPremisesDetails): TradingPremises = {
    TradingPremises(Some(RegisteringAgentPremises(false)),
      ownPremises,
      None,
      None,
      None,
      None,
      ownPremises,
      ownPremises.msb,
      ownPremises.lineId,
      ownPremises.status
    )
  }

  implicit def conv(tradingPremises: DesTradingPremises): Option[Seq[TradingPremises]] = {
    val `empty` = Seq.empty[TradingPremises]
    val agentPremises: Seq[TradingPremises] = tradingPremises.agentBusinessPremises match {
      case Some(agentBusinessPremises) => agentBusinessPremises.agentDetails match {
        case Some(data) => data.map(x => convAgentPremises(x))
        case _ => Seq.empty
      }
      case None => Seq.empty
    }

    val ownPremises: Seq[TradingPremises] = tradingPremises.ownBusinessPremises match {
      case Some(ownBusinessPremises) => ownBusinessPremises.ownBusinessPremisesDetails match {
        case Some(data) => data.map(x => convOwnPremises(x))
        case _ => Seq.empty
      }
      case None => Seq.empty
    }
    val premises = Seq(agentPremises, ownPremises).flatten
    premises match {
      case `empty` => None
      case _ => Some(premises)
    }
  }

}
