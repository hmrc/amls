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

package utils

import config.AmlsConfig
import models.des.{AmendVariationRequest, SubscriptionView}
import models.des.tradingpremises._

trait TradingPremisesUpdateHelper {

  def updateWithTradingPremises(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {
    desRequest.setTradingPremises(
      tradingPremisesWithStatus(desRequest.tradingPremises, viewResponse.tradingPremises)
    )
  }

  private def tradingPremisesWithStatus(desTradingPremises: TradingPremises, viewTradingPremises: TradingPremises): TradingPremises = {
    val updatedAgentBusinessPremises = getAgentDetailsWithStatus(desTradingPremises, viewTradingPremises)
    val updatedOwnBusinessPremises = getOwnPremisesWithStatus(desTradingPremises, viewTradingPremises)

    desTradingPremises.copy(agentBusinessPremises = updatedAgentBusinessPremises, ownBusinessPremises = updatedOwnBusinessPremises)
  }

  private def getAgentDetailsWithStatus(desTradingPremises: TradingPremises, viewTradingPremises: TradingPremises): Option[AgentBusinessPremises] = {
    val (agentWithLineIds, agentWithoutLineIds) = desTradingPremises.agentBusinessPremises match {
      case Some(agentBusinessPremises) => desTradingPremises.agentBusinessPremises.get.
        agentDetails.fold[(Seq[AgentDetails], Seq[AgentDetails])]((Seq.empty, Seq.empty))(agent => agent.partition(_.lineId.isDefined))
      case None => (Nil, Nil)
    }

    val updatedAgentStatus = agentWithLineIds.map(agentDtls => updateAgentStatus(agentDtls, viewTradingPremises))
    val updatedAgentStatusWithoutLineId =
      agentWithoutLineIds.map(x => x.copy(status = Some(StatusConstants.Added), dateChangeFlag = Some(false)))

    val addAgentList = updatedAgentStatus ++ updatedAgentStatusWithoutLineId

    desTradingPremises.agentBusinessPremises.map {
      agentBusinessPremises => agentBusinessPremises.copy(agentDetails = Some(addAgentList))
    }
  }

  private def getOwnPremisesWithStatus(desTradingPremises: TradingPremises, viewTradingPremises: TradingPremises): Option[OwnBusinessPremises] = {

    val (ownWithLineIds, ownWithoutLineIds) = desTradingPremises.ownBusinessPremises match {
      case Some(ownBusinessPremises) => ownBusinessPremises.ownBusinessPremisesDetails.
        fold[(Seq[OwnBusinessPremisesDetails], Seq[OwnBusinessPremisesDetails])]((Seq.empty, Seq.empty))(own => own.partition(_.lineId.isDefined))
      case None => (Nil, Nil)
    }

    val updatedOwnStatus = ownWithLineIds.map(own => updateOwnPremisesStatus(own, viewTradingPremises))
    val updatedOwnStatusWithoutLineId =
      ownWithoutLineIds.map(x => x.copy(status = Some(StatusConstants.Added), dateChangeFlag = Some(false)))
    val addOwnList = updatedOwnStatus ++ updatedOwnStatusWithoutLineId

    desTradingPremises.ownBusinessPremises.map {
      ownBusinessPremises => ownBusinessPremises.copy(ownBusinessPremisesDetails = Some(addOwnList))
    }
  }

  private def updateOwnPremisesStatus(ownDetails: OwnBusinessPremisesDetails, viewTradingPremises: TradingPremises): OwnBusinessPremisesDetails = {
    val viewOwnDtls: Option[OwnBusinessPremisesDetails] = viewTradingPremises.ownBusinessPremises flatMap {
      _.ownBusinessPremisesDetails flatMap {
        _.find(x => x.lineId.equals(ownDetails.lineId))
      }
    }
    viewOwnDtls map { bpDetails =>
      val updatedStatus = updateOwnPremisesStatus(ownDetails, bpDetails)
      val startDateChangeFlag = updateOwnPremisesStartDateFlag(ownDetails, bpDetails)
      ownDetails.copy(status = Some(updatedStatus), dateChangeFlag = startDateChangeFlag)
    } getOrElse ownDetails.copy(status = Some(StatusConstants.Unchanged), dateChangeFlag = Some(false))
  }

  private def updateOwnPremisesStatus(ownDetails: OwnBusinessPremisesDetails, viewOwnDtls: OwnBusinessPremisesDetails) = {
    ownDetails.status match {
      case Some(StatusConstants.Deleted) => StatusConstants.Deleted
      case _ => ownDetails.equals(viewOwnDtls) match {
        case true => StatusConstants.Unchanged
        case false => StatusConstants.Updated
      }
    }
  }

  private def updateOwnPremisesStartDateFlag(ownDetails: OwnBusinessPremisesDetails, viewOwnDtls: OwnBusinessPremisesDetails) = {
    ownDetails.startDate match {
      case _ if !ownDetails.status.contains(StatusConstants.Deleted) =>
        Some(!ownDetails.startDate.equals(viewOwnDtls.startDate))
      case _ => Some(false)
    }
  }

  private def updateAgentStatus(agentDetails: AgentDetails, viewTradingPremises: TradingPremises): AgentDetails = {
    viewTradingPremises.agentBusinessPremises.fold(agentDetails) {
      _.agentDetails.fold(agentDetails) {
        _.find(x => x.lineId.equals(agentDetails.lineId)).fold(agentDetails) { viewAgent =>
          val updatedStatus = updateAgentDetailsStatus(agentDetails, viewAgent)
          val startDateChangeFlag = updateAgentDetailsDateOfChangeFlag(agentDetails, viewAgent)
          agentDetails.copy(status = Some(updatedStatus), dateChangeFlag = startDateChangeFlag)
        }
      }
    }
  }

  private def updateAgentDetailsStatus(agentDetails: AgentDetails, viewAgent: AgentDetails) = {
    agentDetails.status match {
      case Some(StatusConstants.Deleted) => StatusConstants.Deleted
      case _ => agentDetails.equals(viewAgent) match {
        case true => StatusConstants.Unchanged
        case false => StatusConstants.Updated
      }
    }
  }

  private def updateAgentDetailsDateOfChangeFlag(agentDetails: AgentDetails, viewAgent: AgentDetails) = {
    agentDetails.startDate match {
      case _ if !agentDetails.status.contains(StatusConstants.Deleted) =>
        Some(!agentDetails.startDate.equals(viewAgent.startDate))
      case _ => Some(false)
    }
  }

}
