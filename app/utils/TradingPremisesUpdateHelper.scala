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

package utils

import models.des.{AmendVariationRequest, SubscriptionView}
import models.des.tradingpremises._

trait TradingPremisesUpdateHelper {

  def updateWithTradingPremises(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {
    desRequest.setTradingPremises(
      tradingPremisesWithStatus(desRequest.tradingPremises, viewResponse.tradingPremises)
    )
  }

  private def getAgentDetailsWithStatus(viewTradingPremises: TradingPremises, desTradingPremises: TradingPremises): Option[AgentBusinessPremises] = {
    val (agentWithLineIds, agentWithoutLineIds) = desTradingPremises.agentBusinessPremises match {
      case Some(agentBusinessPremises) => desTradingPremises.agentBusinessPremises.get.
        agentDetails.fold[(Seq[AgentDetails], Seq[AgentDetails])]((Seq.empty, Seq.empty))(agent => agent.partition(_.lineId.isDefined))
      case None => (Nil, Nil)
    }

    val updatedAgentStatus = agentWithLineIds.map(agentDtls => updateAgentStatus(agentDtls, viewTradingPremises))
    val updatedAgentStatusWithoutLineId = agentWithoutLineIds.map(x => x.copy(status = Some(StatusConstants.Added)))
    val addAgentList = updatedAgentStatus ++ updatedAgentStatusWithoutLineId

    desTradingPremises.agentBusinessPremises.map {
      agentBusinessPremises => agentBusinessPremises.copy(agentDetails = Some(addAgentList))
    }
  }

  private def getOwnPremisesWithStatus(viewTradingPremises: TradingPremises, desTradingPremises: TradingPremises): Option[OwnBusinessPremises] = {

    val (ownWithLineIds, ownWithoutLineIds) = desTradingPremises.ownBusinessPremises match {
      case Some(ownBusinessPremises) => ownBusinessPremises.ownBusinessPremisesDetails.
        fold[(Seq[OwnBusinessPremisesDetails], Seq[OwnBusinessPremisesDetails])]((Seq.empty, Seq.empty))(own => own.partition(_.lineId.isDefined))
      case None => (Nil, Nil)
    }

    val updatedOwnStatus = ownWithLineIds.map(own => updateOwnPremisesStatus(own, viewTradingPremises))
    val updatedOwnStatusWithoutLineId = ownWithoutLineIds.map(x => x.copy(status = Some(StatusConstants.Added)))
    val addOwnList = updatedOwnStatus ++ updatedOwnStatusWithoutLineId

    desTradingPremises.ownBusinessPremises.map {
      ownBusinessPremises => ownBusinessPremises.copy(ownBusinessPremisesDetails = Some(addOwnList))
    }
  }

  private def updateOwnPremisesStatus(ownDtls: OwnBusinessPremisesDetails, viewTradingPremises: TradingPremises): OwnBusinessPremisesDetails = {
    viewTradingPremises.ownBusinessPremises match {
      case Some(ownBusinessPremises) => ownBusinessPremises.ownBusinessPremisesDetails match {
        case Some(agentPremises) => {
          agentPremises.find(x => x.lineId.equals(ownDtls.lineId)) match {
            case Some(viewOwnDtls) =>
              val updatedStatus = ownDtls.status match {
                case Some(StatusConstants.Deleted) => StatusConstants.Deleted
                case _ => ownDtls.equals(viewOwnDtls) match {
                  case true => StatusConstants.Unchanged
                  case false => StatusConstants.Updated
                }
              }
              val startDateChangeFlag = ownDtls.startDate match {
                case date if ownDtls.status != Some(StatusConstants.Deleted) =>
                  !ownDtls.startDate.equals((viewOwnDtls.startDate)) match {
                    case false => None
                    case _ => Some(true)
                  }
                case _ => None
              }
              ownDtls.copy(status = Some(updatedStatus), dateChangeFlag = startDateChangeFlag)
            case _ => ownDtls
          }
        }
        case None => ownDtls
      }
      case None => ownDtls
    }

  }

  private def updateAgentStatus(agentDtls: AgentDetails, viewTradingPremises: TradingPremises): AgentDetails = {
    viewTradingPremises.agentBusinessPremises match {
      case Some(agentBusinessPremises) => agentBusinessPremises.agentDetails match {
        case Some(agentPremises) => {
          agentPremises.find(x => x.lineId.equals(agentDtls.lineId)) match {
            case Some(viewAgent) =>
              val updatedStatus = agentDtls.status match {
                case Some(StatusConstants.Deleted) => StatusConstants.Deleted
                case _ => agentDtls.equals(viewAgent) match {
                  case true => StatusConstants.Unchanged
                  case false => StatusConstants.Updated
                }
              }

              val startDateChangeFlag = agentDtls.agentPremises.startDate match {
                case date if agentDtls.status != Some(StatusConstants.Deleted) =>
                  !agentDtls.agentPremises.startDate.equals((viewAgent.agentPremises.startDate)) match {
                    case false => None
                    case _ => Some(true)
                  }
                case _ => None
              }

              agentDtls.copy(status = Some(updatedStatus), agentPremises = agentDtls.agentPremises.copy(dateChangeFlag = startDateChangeFlag))
            case _ => agentDtls
          }
        }
        case None => agentDtls
      }
      case None => agentDtls
    }

  }

  private def tradingPremisesWithStatus(desTradingPremises: TradingPremises, viewTradingPremises: TradingPremises): TradingPremises = {
    val updatedAgentBusinessPremises = getAgentDetailsWithStatus(viewTradingPremises, desTradingPremises)
    val updatedOwnBusinessPremises = getOwnPremisesWithStatus(viewTradingPremises, desTradingPremises)

    desTradingPremises.copy(agentBusinessPremises = updatedAgentBusinessPremises, ownBusinessPremises = updatedOwnBusinessPremises)
  }

}
