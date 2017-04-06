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

package services

import java.io.InputStream

import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import config.AmlsConfig
import connectors._
import models.des._
import models.des.responsiblepeople.ResponsiblePersons
import org.joda.time.{LocalDate, Months}
import play.api.Logger
import play.api.libs.json.Json
import repositories.FeeResponseRepository
import uk.gov.hmrc.play.http.HeaderCarrier
import utils.{DateOfChangeUpdateHelper, ResponsiblePeopleUpdateHelper, TradingPremisesUpdateHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

trait AmendVariationService extends ResponsiblePeopleUpdateHelper with TradingPremisesUpdateHelper with DateOfChangeUpdateHelper {

  private[services] def amendVariationDesConnector: AmendVariationDESConnector

  private[services] def viewStatusDesConnector: SubscriptionStatusDESConnector

  private[services] def feeResponseRepository: FeeResponseRepository

  private[services] def viewDesConnector: ViewDESConnector

  private val validator = new SchemaValidator()

  val stream: InputStream = getClass.getResourceAsStream("/resources/API6_Request.json")
  val lines = scala.io.Source.fromInputStream(stream).getLines
  val linesString = lines.foldLeft[String]("")((x, y) => x.trim ++ y.trim)


  def t(amendVariationResponse: AmendVariationResponse, amlsReferenceNumber: String)(implicit f: (AmendVariationResponse, String) => FeeResponse) =
    f(amendVariationResponse, amlsReferenceNumber)

  private[services] lazy val updates: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest] = {
    val transforms: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest] = Set(
      updateWithEtmpFields,
      updateWithTradingPremises,
      updateWithResponsiblePeople
    )
    if (AmlsConfig.release7) {
      val release7Transforms: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest] = Set(updateWithHvdDateOfChangeFlag,
        updateWithSupervisorDateOfChangeFlag,
        updateWithBusinessActivitiesDateOfChangeFlag)
      transforms ++ release7Transforms
    } else {
      transforms
    }
  }

  def compareAndUpdate(desRequest: AmendVariationRequest, amlsRegistrationNumber: String): Future[AmendVariationRequest] = {
    viewDesConnector.view(amlsRegistrationNumber).map { viewResponse =>

      val updatedRequest = updateRequest(desRequest, viewResponse)

      updatedRequest.setChangeIndicator(ChangeIndicators(
        !viewResponse.businessDetails.equals(desRequest.businessDetails),
        !viewResponse.businessContactDetails.businessAddress.equals(desRequest.businessContactDetails.businessAddress),
        isBusinessReferenceChanged(desRequest, viewResponse),
        !viewResponse.tradingPremises.equals(desRequest.tradingPremises),
        !viewResponse.businessActivities.equals(desRequest.businessActivities),
        !viewResponse.bankAccountDetails.equals(desRequest.bankAccountDetails),
        !viewResponse.msb.equals(desRequest.msb),
        !viewResponse.hvd.equals(desRequest.hvd),
        !viewResponse.asp.equals(desRequest.asp),
        !viewResponse.aspOrTcsp.equals(desRequest.aspOrTcsp),
        isTcspChanged(desRequest, viewResponse),
        isEABChanged(desRequest, viewResponse),
        !viewResponse.responsiblePersons.equals(updateWithResponsiblePeople(desRequest, viewResponse).responsiblePersons),
        !viewResponse.extraFields.filingIndividual.equals(desRequest.extraFields.filingIndividual)
      ))
    }
  }

  def update
  (amlsRegistrationNumber: String, request: AmendVariationRequest)
  (implicit
   hc: HeaderCarrier,
   ec: ExecutionContext
  ): Future[AmendVariationResponse] = {
    import com.eclipsesource.schema._
    val validateResult = validator.validate(Json.fromJson[SchemaType](Json.parse(linesString.trim.drop(1))).get, Json.toJson(request))
    if (!validateResult.isSuccess) {
      val errors = validateResult.fold(invalid = { errors =>
        errors.foldLeft[String]("") {
          (a, b) => a + "," + b._1.toJsonString
        }
      }, valid = { post => post })
      Logger.warn(s"[AmendVariationService][update] Schema Validation Failed : amlsReg: $amlsRegistrationNumber : Error Paths : ${errors}")
    } else {
      Logger.debug(s"[AmendVariationService][update] Schema Validation Passed : amlsReg: $amlsRegistrationNumber")
    }
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
        case (fp, rp) => (detailsMatch(Some(fp)), detailsMatch(Some(rp)))
        case _ => (0, 0)
      }
      case _ => (0, 0)
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
        obpd =>
          obpd.filter {
            x => startDateMatcher(x.startDate, x => (7 to 11) contains x)
          }
      })
      case None => 0
    }

    val addedAgentHalfYearlyTradingPremisesCount = request.tradingPremises.agentBusinessPremises match {
      case Some(agentBusinessPremises) => detailsMatch(agentBusinessPremises.agentDetails map {
        ad =>
          ad.filter {
            x => startDateMatcher(x.startDate.getOrElse(x.agentPremises.startDate.getOrElse("")), x => (7 to 11) contains x)
          }
      })
      case None => 0
    }

    val addedOwnBusinessZeroRatedTradingPremisesCount = request.tradingPremises.ownBusinessPremises match {
      case Some(ownBusinessPremises) => detailsMatch(ownBusinessPremises.ownBusinessPremisesDetails map {
        obpd =>
          obpd.filter {
            x => startDateMatcher(x.startDate, x => x == 12)
          }
      })
      case None => 0
    }

    val addedAgentZeroRatedTradingPremisesCount = request.tradingPremises.agentBusinessPremises match {
      case Some(agentBusinessPremises) => detailsMatch(agentBusinessPremises.agentDetails map {
        ad =>
          ad.filter {
            x => startDateMatcher(x.startDate.getOrElse(x.agentPremises.startDate.getOrElse("")), x => x == 12)
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

    val registrationFees = response.registrationFee.getOrElse(BigDecimal(0))
    val premisesFees = response.premiseFee.getOrElse(BigDecimal(0))
    val totalFees = response.totalFees.getOrElse(BigDecimal(0))

    response.copy(
      registrationFee = Some(registrationFees),
      premiseFee = Some(premisesFees),
      totalFees = Some(totalFees),
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

  private[services] def updateRequest(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {

    def update(request: AmendVariationRequest, updateF: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest]): AmendVariationRequest = {
      if (updateF.size < 1) {
        request
      } else {
        if (updateF.size == 1) {
          updateF.head(request, viewResponse)
        } else {
          val updated = updateF.head(request, viewResponse)
          update(updated, updateF.tail)
        }
      }
    }

    update(desRequest, updates)

  }

  private[services] def updateWithEtmpFields(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {
    val etmpFields = desRequest.extraFields.setEtmpFields(viewResponse.extraFields.etmpFields)
    desRequest.setExtraFields(etmpFields)
  }

  private[services] def isBusinessReferenceChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    !(response.businessReferencesAll.equals(desRequest.businessReferencesAll) &&
      response.businessReferencesAllButSp.equals(desRequest.businessReferencesAllButSp) &&
      response.businessReferencesCbUbLlp.equals(desRequest.businessReferencesCbUbLlp))
  }

  private[services] def isTcspChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    !(response.tcspAll.equals(desRequest.tcspAll) &&
      response.tcspTrustCompFormationAgt.equals(desRequest.tcspTrustCompFormationAgt))
  }

  private[services] def isEABChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
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
