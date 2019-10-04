/*
 * Copyright 2019 HM Revenue & Customs
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

import audit.AmendVariationValidationFailedEvent
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import config.MicroserviceAuditConnector
import connectors._
import javax.inject.Inject
import models.Fees
import models.des.{AmendVariationResponse => DesAmendVariationResponse, _}
import models.fe.AmendVariationResponse
import play.api.Logger
import play.api.libs.json.Json
import repositories.FeesRepository
import uk.gov.hmrc.http.HeaderCarrier
import utils.{ApiRetryHelper, DateOfChangeUpdateHelper, ResponsiblePeopleUpdateHelper, TradingPremisesUpdateHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AmendVariationService @Inject()(
  private[services] val amendVariationDesConnector: AmendVariationDESConnector,
  private[services] val viewStatusDesConnector: SubscriptionStatusDESConnector,
  private[services] val viewDesConnector: ViewDESConnector,
  private[services] val auditConnector: MicroserviceAuditConnector)
  extends ResponsiblePeopleUpdateHelper with TradingPremisesUpdateHelper with DateOfChangeUpdateHelper {

  private[services] lazy val feeResponseRepository = FeesRepository()
  private[services] val validator: SchemaValidator = new SchemaValidator()

  def t(amendVariationResponse: DesAmendVariationResponse, amlsReferenceNumber: String)(implicit f: (DesAmendVariationResponse, String) => Fees) =
    f(amendVariationResponse, amlsReferenceNumber)

  private[services] lazy val updates: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest] = {
    val transforms: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest] = Set(
      updateWithEtmpFields,
      updateWithTradingPremises,
      updateWithResponsiblePeople
    )

    val release7Transforms: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest] = Set(updateWithHvdDateOfChangeFlag,
      updateWithSupervisorDateOfChangeFlag,
      updateWithBusinessActivitiesDateOfChangeFlag)
    transforms ++ release7Transforms

  }

  def compareAndUpdate(desRequest: AmendVariationRequest, amlsRegistrationNumber: String)(
    implicit hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[AmendVariationRequest] = {
    viewDesConnector.view(amlsRegistrationNumber).map { viewResponse =>

      val updatedRequest = updateRequest(desRequest, viewResponse)

      Logger.debug(s"[AmendVariationService][compareAndUpdate] MSB - viewResponse.msb: ${viewResponse.msb}")
      Logger.debug(s"[AmendVariationService][compareAndUpdate] MSB - desRequest.msb: ${desRequest.msb}")

      Logger.debug(s"[AmendVariationService][compareAndUpdate] HVD - viewResponse.hvd: ${viewResponse.hvd}")
      Logger.debug(s"[AmendVariationService][compareAndUpdate] HVD - desRequest.hvd: ${desRequest.hvd}")

      Logger.debug(s"[AmendVariationService][compareAndUpdate] ASP - viewResponse.asp: ${viewResponse.asp}")
      Logger.debug(s"[AmendVariationService][compareAndUpdate] ASP - desRequest.asp: ${desRequest.asp}")

      Logger.debug(s"[AmendVariationService][compareAndUpdate] ASPOrTCSP - viewResponse.aspOrTcsp: ${viewResponse.aspOrTcsp}")
      Logger.debug(s"[AmendVariationService][compareAndUpdate] ASPOrTCSP - desRequest.aspOrTcsp: ${desRequest.aspOrTcsp}")

      Logger.debug(s"[AmendVariationService][compareAndUpdate] MSB - viewResponse.msb: ${viewResponse.msb}")
      Logger.debug(s"[AmendVariationService][compareAndUpdate] MSB - desRequest.msb: ${desRequest.msb}")

      val desRPs = updateWithResponsiblePeople(desRequest, viewResponse).responsiblePersons
      Logger.debug(s"[AmendVariationService][compareAndUpdate] RP - viewResponse.responsiblePersons: ${viewResponse.responsiblePersons}")
      Logger.debug(s"[AmendVariationService][compareAndUpdate] RP - desRequest.responsiblePersons: ${desRPs}")

      Logger.debug(s"[AmendVariationService][compareAndUpdate] TP - viewResponse.tradingPremises: ${viewResponse.tradingPremises}")
      Logger.debug(s"[AmendVariationService][compareAndUpdate] TP - desRequest.tradingPremises: ${desRequest.tradingPremises}")

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
        !viewResponse.responsiblePersons.equals(desRPs),
        !viewResponse.extraFields.filingIndividual.equals(desRequest.extraFields.filingIndividual)
      ))
    }
  }

  def update
  (amlsRegistrationNumber: String, request: AmendVariationRequest)
  (implicit
   hc: HeaderCarrier,
   ec: ExecutionContext,
   apiRetryHelper: ApiRetryHelper
  ): Future[AmendVariationResponse] = {

    val result = validateResult(request)

    if (!result.isSuccess) {

      // $COVERAGE-OFF$

      val errors = result.fold(invalid = { errors =>
        errors.foldLeft[String]("") {
          (a, b) => a + "," + b._1.toJsonString
        }
      }, valid = identity)

      result.fold(invalid = validationResult => {
        val resultObjects = validationResult.map {
          case (path, messages) => Json.obj(
            "path" -> path.toJsonString,
            "messages" -> messages.map(_.messages)
          )
        }
        Logger.warn(s"[AmendVariationService][update] Schema Validation Failed : amlsReg: $amlsRegistrationNumber")
        auditConnector.sendExtendedEvent(AmendVariationValidationFailedEvent(amlsRegistrationNumber, request, resultObjects))
      }, valid = identity)
    } else {
      Logger.debug(s"[AmendVariationService][update] Schema Validation Passed : amlsReg: $amlsRegistrationNumber")
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

  private[services] def isTcspChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - response.tcspAll: ${response.tcspAll}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - desRequest.tcspAll: ${desRequest.tcspAll}")

    Logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - response.tcspTrustCompFormationAgt: ${response.tcspTrustCompFormationAgt}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isTcspChanged - desRequest.tcspTrustCompFormationAgt: ${desRequest.tcspTrustCompFormationAgt}")
    !(response.tcspAll.equals(desRequest.tcspAll) &&
      response.tcspTrustCompFormationAgt.equals(desRequest.tcspTrustCompFormationAgt))
  }

  private[services] def isEABChanged(desRequest: AmendVariationRequest, response: SubscriptionView) = {
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - response.tcspAll: ${response.tcspAll}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - desRequest.tcspAll: ${desRequest.tcspAll}")

    Logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - response.eabAll: ${response.eabAll}")
    Logger.debug(s"[AmendVariationService][compareAndUpdate] isEABChanged - desRequest.eabAll: ${desRequest.eabAll}")

    !(response.eabAll.equals(desRequest.eabAll) &&
      response.eabResdEstAgncy.equals(desRequest.eabResdEstAgncy))
  }

   private[services] def validateResult(request: AmendVariationRequest) = {
     // $COVERAGE-OFF$
     val stream: InputStream = getClass.getResourceAsStream ("/resources/api6_schema_release_4.1.1.json")
     val lines = scala.io.Source.fromInputStream(stream).getLines
     val linesString = lines.foldLeft[String]("")((x, y) => x.trim ++ y.trim)

      validator.validate(Json.fromJson[SchemaType](Json.parse(linesString.trim)).get, Json.toJson(request))
  }

  private[services] def amendVariationResponse(request: AmendVariationRequest, isRenewalWindow: Boolean, des: DesAmendVariationResponse) =
    AmendVariationResponse.convert(request, isRenewalWindow, des)
}
