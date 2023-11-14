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

package services

import audit.AmendVariationValidationFailedEvent
import config.ApplicationConfig
import connectors._
import models.Fees
import models.des.{AmendVariationResponse => DesAmendVariationResponse, _}
import models.fe.AmendVariationResponse
import play.api.Logging
import play.api.libs.json.JsObject
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import repositories.FeesRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AmendVariationService @Inject()(private[services] val amendVariationDesConnector: AmendVariationDESConnector,
                                      private[services] val viewStatusDesConnector: SubscriptionStatusDESConnector,
                                      private[services] val viewDesConnector: ViewDESConnector,
                                      private[services] val auditConnector: AuditConnector,
                                      val amendVariationValidator: AmendVariationValidator,
                                      val feeResponseRepository: FeesRepository,
                                      private[services] val config: ApplicationConfig)(implicit val ec: ExecutionContext)
  extends ResponsiblePeopleUpdateHelper with TradingPremisesUpdateHelper with DateOfChangeUpdateHelper with ChangeIndicatorHelper with Logging {

  def t(amendVariationResponse: DesAmendVariationResponse, amlsReferenceNumber: String)
       (implicit f: (DesAmendVariationResponse, String) => Fees) =
    f(amendVariationResponse, amlsReferenceNumber)

  private[services] lazy val updates: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest] = {
    val transforms: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest] = Set(
      updateWithEtmpFields,
      updateWithTradingPremises,
      updateWithResponsiblePeople
    )

    val release7Transforms: Set[(AmendVariationRequest, SubscriptionView) =>
      AmendVariationRequest] = Set(
      updateWithHvdDateOfChangeFlag,
      updateWithSupervisorDateOfChangeFlag,
      updateWithBusinessActivitiesDateOfChangeFlag
    )
    transforms ++ release7Transforms
  }

  def compareAndUpdate(desRequest: AmendVariationRequest, amlsRegistrationNumber: String)(
    implicit hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[AmendVariationRequest] = {
    viewDesConnector.view(amlsRegistrationNumber).map { viewResponse =>

      val updatedRequest = updateRequest(desRequest, viewResponse)
      val desRPs = updateWithResponsiblePeople(desRequest, viewResponse).responsiblePersons

      updatedRequest.setChangeIndicator(
        changeIndicators = ChangeIndicators(
          businessDetails = !viewResponse.businessDetails.equals(desRequest.businessDetails),
          businessAddress = !viewResponse.businessContactDetails.businessAddress.equals(
            desRequest.businessContactDetails.businessAddress
          ),
          businessReferences = isBusinessReferenceChanged(desRequest, viewResponse),
          tradingPremises = !viewResponse.tradingPremises.equals(desRequest.tradingPremises),
          businessActivities = businessActivitiesChangeIndicator(viewResponse, desRequest),
          bankAccountDetails = !viewResponse.bankAccountDetails.equals(desRequest.bankAccountDetails),
          msb = msbChangedIndicator(viewResponse, desRequest),
          hvd = hvdChangedIndicator(viewResponse, desRequest),
          asp = aspChangedIndicator(viewResponse, desRequest),
          aspOrTcsp = aspOrTcspChangeIndicator(viewResponse, desRequest),
          tcsp = tcspChangedIndicator(viewResponse, desRequest),
          eab = eabChangedIndicator(viewResponse, desRequest),
          amp = ampChangeIndicator(viewResponse, desRequest),
          responsiblePersons = !viewResponse.responsiblePersons.equals(desRPs),
          filingIndividual = !viewResponse.extraFields.filingIndividual.equals(desRequest.extraFields.filingIndividual)
        )
      )
    }
  }

  def update(amlsRegistrationNumber: String, request: AmendVariationRequest)(implicit hc: HeaderCarrier,
                                                                             ec: ExecutionContext,
                                                                             apiRetryHelper: ApiRetryHelper): Future[AmendVariationResponse] = {

    val validationResult: Either[collection.Seq[JsObject], AmendVariationRequest] = amendVariationValidator.validateResult(request)

    validationResult match {
      case Left(errors) =>
        logger.warn(s"[AmendVariationService][update] Schema Validation Failed : amlsReg: $amlsRegistrationNumber")
        auditConnector.sendExtendedEvent(AmendVariationValidationFailedEvent(amlsRegistrationNumber, request, errors.toIndexedSeq))
      case Right(_) =>
        logger.debug(s"[AmendVariationService][update] Schema Validation Passed : amlsReg: $amlsRegistrationNumber")
    }

    for {
      response <- amendVariationDesConnector.amend(amlsRegistrationNumber, request)
      status <- viewStatusDesConnector.status(amlsRegistrationNumber)
      _ <- feeResponseRepository.insert(t(response, amlsRegistrationNumber))
    } yield amendVariationResponse(request, status.isRenewalPeriod(), response)
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

  private[services] def amendVariationResponse(request: AmendVariationRequest, isRenewalWindow: Boolean, des: DesAmendVariationResponse) =
    AmendVariationResponse.convert(request, isRenewalWindow, des)
}
