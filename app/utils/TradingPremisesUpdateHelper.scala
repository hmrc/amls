package utils

import models.des.{AmendVariationRequest, SubscriptionView}
import models.des.tradingpremises._

trait TradingPremisesUpdateHelper {

  def updateWithTradingPremises(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {
    desRequest.setTradingPremises(
      tradingPremisesWithStatus(viewResponse.tradingPremises, desRequest.tradingPremises)
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
              ownDtls.copy(status = Some(updatedStatus))
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
              agentDtls.copy(status = Some(updatedStatus))
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
