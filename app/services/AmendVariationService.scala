/*
 * Copyright 2016 HM Revenue & Customs
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

package services

import connectors._
import models.des._
import models.des.responsiblepeople.{RPExtra, ResponsiblePersons}
import models.des.tradingpremises._
import org.joda.time.{LocalDate, Months}
import repositories.FeeResponseRepository
import uk.gov.hmrc.play.http.HeaderCarrier
import utils.StatusConstants
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

trait AmendVariationService {

  private[services] def amendVariationDesConnector: AmendVariationDESConnector

  private[services] def viewStatusDesConnector: SubscriptionStatusDESConnector

  private[services] def feeResponseRepository: FeeResponseRepository

  private[services] def viewDesConnector: ViewDESConnector

  def t(amendVariationResponse: AmendVariationResponse, amlsReferenceNumber: String)(implicit f: (AmendVariationResponse, String) => FeeResponse) =
    f(amendVariationResponse, amlsReferenceNumber)

  def update
  (amlsRegistrationNumber: String, request: AmendVariationRequest)
  (implicit
   hc: HeaderCarrier,
   ec: ExecutionContext
  ): Future[AmendVariationResponse] = {
    for {
      response <- amendVariationDesConnector.amend(amlsRegistrationNumber, request)
      inserted <- feeResponseRepository.insert(t(response, amlsRegistrationNumber))
      regStatus <- viewStatusDesConnector.status(amlsRegistrationNumber)
    } yield decorateWithTotals(request, response, regStatus.currentRegYearEndDate)
  }

  private def detailsMatch[T](seqOption: Option[Seq[T]])(implicit statusProvider: StatusProvider[T]) = {

    def statusMatch(status: Option[String]) = status match {
      case Some(status) if status == "Added" => true
      case None => true
      case _ => false
    }

    seqOption match {
      case Some(contained) => contained count {
        detail => statusMatch(statusProvider.getStatus(detail))

      }
      case _ => 0
    }
  }

  private def monthOfRegistration(startDate: LocalDate, currentRegYearEndDate: Option[LocalDate]): Int = {
    currentRegYearEndDate match {
      case Some(endDate) => 12 - Months.monthsBetween(startDate, endDate).getMonths
      case _ => 0
    }
  }

  private def decorateWithTotals(request: AmendVariationRequest, response: AmendVariationResponse,
                                 currentRegYearEndDate: Option[LocalDate]): AmendVariationResponse = {

    def startDateMatcher(startDate: String, monthPredicate: (Int) => Boolean): Boolean = {
      startDate match {
        case "" => false
        case _ => monthPredicate(monthOfRegistration(LocalDate.parse(startDate), currentRegYearEndDate))
      }
    }

    val responsiblePeopleSplit: Option[(Seq[ResponsiblePersons], Seq[ResponsiblePersons])] =
      request.responsiblePersons.map(_.partition(_.msbOrTcsp.fold(true)(x => x.passedFitAndProperTest)))

    val addedResponsiblePeopleCount = detailsMatch(request.responsiblePersons)

    val responsiblePeopleSplitCount = responsiblePeopleSplit match {
      case Some(partition) => partition match {
        case (fp,rp) => (detailsMatch(Some(fp)),detailsMatch(Some(rp)))
        case _ => (0,0)
      }
      case _ => (0,0)
    }

    val addedOwnBusinessTradingPremisesCount = request.tradingPremises.ownBusinessPremises match {
      case Some(ownBusinessPremises) => detailsMatch(ownBusinessPremises.ownBusinessPremisesDetails)
      case None => 0
    }
    val addedAgentTradingPremisesCount = request.tradingPremises.agentBusinessPremises match {
      case Some(agentBusinessPremises) => detailsMatch(agentBusinessPremises.agentDetails)
      case None => 0
    }

    val addedOwnBusinessHalfYearlyTradingPremisesCount = request.tradingPremises.ownBusinessPremises match {
      case Some(ownBusinessPremises) => detailsMatch(ownBusinessPremises.ownBusinessPremisesDetails map {
        obpd => obpd.filter {
          x => startDateMatcher(x.startDate, x => (7 to 11) contains x)
        }
      })
      case None => 0
    }

    val addedAgentHalfYearlyTradingPremisesCount = request.tradingPremises.agentBusinessPremises match {
      case Some(agentBusinessPremises) => detailsMatch(agentBusinessPremises.agentDetails map {
        ad => ad.filter {
          x => startDateMatcher(x.agentPremises.startDate, x => (7 to 11) contains x)
        }
      })
      case None => 0
    }

    val addedOwnBusinessZeroRatedTradingPremisesCount = request.tradingPremises.ownBusinessPremises match {
      case Some(ownBusinessPremises) => detailsMatch(ownBusinessPremises.ownBusinessPremisesDetails map {
        obpd => obpd.filter {
          x => startDateMatcher(x.startDate, x => x == 12)
        }
      })
      case None => 0
    }

    val addedAgentZeroRatedTradingPremisesCount = request.tradingPremises.agentBusinessPremises match {
      case Some(agentBusinessPremises) => detailsMatch(agentBusinessPremises.agentDetails map {
        ad => ad.filter {
          x => startDateMatcher(x.agentPremises.startDate, x => x == 12)
        }
      })
      case None => 0
    }

    decoratedResponse(
      response,
      responsiblePeopleSplitCount._2,
      responsiblePeopleSplitCount._1,
      addedOwnBusinessTradingPremisesCount,
      addedAgentTradingPremisesCount,
      addedOwnBusinessHalfYearlyTradingPremisesCount,
      addedAgentHalfYearlyTradingPremisesCount,
      addedOwnBusinessZeroRatedTradingPremisesCount,
      addedAgentZeroRatedTradingPremisesCount
    )
  }

  private def decoratedResponse(response: AmendVariationResponse,
                                addedResponsiblePeopleCount: Int,
                                addedResponsiblePeopleFitAndProperCount: Int,
                                addedOwnBusinessTradingPremisesCount: Int,
                                addedAgentTradingPremisesCount: Int,
                                addedOwnBusinessHalfYearlyTradingPremisesCount: Int,
                                addedAgentHalfYearlyTradingPremisesCount: Int,
                                addedOwnBusinessZeroRatedTradingPremisesCount: Int,
                                addedAgentZeroRatedTradingPremisesCount: Int): AmendVariationResponse = {
    response.copy(
      addedResponsiblePeople = Some(addedResponsiblePeopleCount),
      addedResponsiblePeopleFitAndProper = Some(addedResponsiblePeopleFitAndProperCount),
      addedFullYearTradingPremises = Some(
        addedOwnBusinessTradingPremisesCount
          + addedAgentTradingPremisesCount
          - addedOwnBusinessHalfYearlyTradingPremisesCount
          - addedAgentHalfYearlyTradingPremisesCount
          - addedOwnBusinessZeroRatedTradingPremisesCount
          - addedAgentZeroRatedTradingPremisesCount
      ),
      halfYearlyTradingPremises = Some(
        addedOwnBusinessHalfYearlyTradingPremisesCount
          + addedAgentHalfYearlyTradingPremisesCount
      ),
      zeroRatedTradingPremises = Some(
        addedOwnBusinessZeroRatedTradingPremisesCount
          + addedAgentZeroRatedTradingPremisesCount
      ))
  }

  def compareAndUpdate(desRequest: AmendVariationRequest, amlsRegistrationNumber: String): Future[AmendVariationRequest] = {
    viewDesConnector.view(amlsRegistrationNumber) map {
      response =>
        val etmpFields = desRequest.extraFields.setEtmpFields(response.extraFields.etmpFields)
        val requestWithExtraField = desRequest.setExtraFields(etmpFields)
        val updatedDesRequestWithRp = requestWithExtraField.setResponsiblePersons(
          updatedRPExtraFields(response.responsiblePersons, requestWithExtraField.responsiblePersons)
        )
        val updatedDesRequestWithTp = tradingPremisesWithStatus(response.tradingPremises, desRequest.tradingPremises)

        val statusUpdatedTP = updatedDesRequestWithRp.copy(tradingPremises = updatedDesRequestWithTp)

        statusUpdatedTP.setChangeIndicator(ChangeIndicators(
          !response.businessDetails.equals(desRequest.businessDetails),
          !response.businessContactDetails.businessAddress.equals(desRequest.businessContactDetails.businessAddress),
          isBusinessReferenceChanged(response, desRequest),
          !response.tradingPremises.equals(desRequest.tradingPremises),
          !response.businessActivities.equals(desRequest.businessActivities),
          !response.bankAccountDetails.equals(desRequest.bankAccountDetails),
          !response.msb.equals(desRequest.msb),
          !response.hvd.equals(desRequest.hvd),
          !response.asp.equals(desRequest.asp),
          !response.aspOrTcsp.equals(desRequest.aspOrTcsp),
          isTcspChanged(response, desRequest),
          isEABChanged(response, desRequest),
          !response.responsiblePersons.equals(updatedDesRequestWithRp.responsiblePersons),
          !response.extraFields.filingIndividual.equals(desRequest.extraFields.filingIndividual)
        ))
    }
  }

  private def updatedRPExtraFields(viewResponsiblePerson: Option[Seq[ResponsiblePersons]],
                                   desResponsiblePerson: Option[Seq[ResponsiblePersons]]): Seq[ResponsiblePersons] = {
    (viewResponsiblePerson, desResponsiblePerson) match {
      case (Some(rp), Some(desRp)) => {
        val (withLineIds, withoutLineIds) = desRp.partition(_.extra.lineId.isDefined)
        val rpWithLineIds = withLineIds.map(updateRpExtraField(_, rp))
        val rpWithoutLineId = withoutLineIds.map(rp => rp.copy(extra = RPExtra(status = Some(StatusConstants.Added))))
        rpWithLineIds ++ rpWithoutLineId
      }
      case _ => desResponsiblePerson.fold[Seq[ResponsiblePersons]](Seq.empty)(x => x)
    }
  }

  private def updateRpExtraField(desRp: ResponsiblePersons, viewResponsiblePersons: Seq[ResponsiblePersons]): ResponsiblePersons = {
    val rpOption = viewResponsiblePersons.find(x => x.extra.lineId.equals(desRp.extra.lineId))
    val viewRp: ResponsiblePersons = rpOption.getOrElse(None)

    val desRPExtra = desRp.extra.copy(
      retestFlag = viewRp.extra.retestFlag,
      testResult = viewRp.extra.testResult,
      testDate = viewRp.extra.testDate
    )
    val desResponsiblePeople = desRp.copy(extra = desRPExtra)

    val updatedStatus = desResponsiblePeople.extra.status match {
      case Some(StatusConstants.Deleted) => StatusConstants.Deleted
      case _ => desResponsiblePeople.equals(viewRp) match {
        case true => StatusConstants.Unchanged
        case false => StatusConstants.Updated
      }
    }

    val statusExtraField = desResponsiblePeople.extra.copy(status = Some(updatedStatus))
    desResponsiblePeople.copy(extra = statusExtraField)
  }

  def tradingPremisesWithStatus(viewTradingPremises: TradingPremises, desTradingPremises: TradingPremises): TradingPremises = {

    val updatedAgentBusinessPremises = getAgentDetailsWithStatus(viewTradingPremises, desTradingPremises)
    val updatedOwnBusinessPremises = getOwnPremisesWithStatus(viewTradingPremises, desTradingPremises)

    desTradingPremises.copy(agentBusinessPremises = updatedAgentBusinessPremises, ownBusinessPremises = updatedOwnBusinessPremises)
  }

  def updateAgentStatus(agentDtls: AgentDetails, viewTradingPremises: TradingPremises): AgentDetails = {
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

  def updateOwnPremisesStatus(ownDtls: OwnBusinessPremisesDetails, viewTradingPremises: TradingPremises): OwnBusinessPremisesDetails = {
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


  def getAgentDetailsWithStatus(viewTradingPremises: TradingPremises, desTradingPremises: TradingPremises): Option[AgentBusinessPremises] = {
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


  def getOwnPremisesWithStatus(viewTradingPremises: TradingPremises, desTradingPremises: TradingPremises): Option[OwnBusinessPremises] = {

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

  def isBusinessReferenceChanged(response: SubscriptionView, desRequest: AmendVariationRequest): Boolean = {
    !(response.businessReferencesAll.equals(desRequest.businessReferencesAll) &&
      response.businessReferencesAllButSp.equals(desRequest.businessReferencesAllButSp) &&
      response.businessReferencesCbUbLlp.equals(desRequest.businessReferencesCbUbLlp))
  }

  private def isTcspChanged(response: SubscriptionView, desRequest: AmendVariationRequest): Boolean = {
    !(response.tcspAll.equals(desRequest.tcspAll) &&
      response.tcspTrustCompFormationAgt.equals(desRequest.tcspTrustCompFormationAgt))
  }

  private def isEABChanged(response: SubscriptionView, desRequest: AmendVariationRequest): Boolean = {
    !(response.eabAll.equals(desRequest.eabAll) &&
      response.eabResdEstAgncy.equals(desRequest.eabResdEstAgncy))
  }
}

object AmendVariationService extends AmendVariationService {
  // $COVERAGE-OFF$
  override private[services] val feeResponseRepository = FeeResponseRepository()
  override private[services] val amendVariationDesConnector = DESConnector
  override private[services] val viewStatusDesConnector: SubscriptionStatusDESConnector = DESConnector
  override private[services] val viewDesConnector: ViewDESConnector = DESConnector
}
