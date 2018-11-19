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

package services

import java.io.InputStream
import audit.AmendVariationValidationFailedEvent
import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import config.{AmlsConfig, MicroserviceAuditConnector}
import connectors._
import models.Fees
import models.des.{AmendVariationResponse => DesAmendVariationResponse, _}
import models.fe.AmendVariationResponse
import play.api.Logger
import play.api.libs.json.{JsResult, JsValue, Json}
import repositories.FeesRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.{BackOffHelper, DateOfChangeUpdateHelper, ResponsiblePeopleUpdateHelper, TradingPremisesUpdateHelper}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

trait AmendVariationService extends ResponsiblePeopleUpdateHelper with TradingPremisesUpdateHelper with DateOfChangeUpdateHelper {

  private[services] def amendVariationDesConnector: AmendVariationDESConnector

  private[services] def viewStatusDesConnector: SubscriptionStatusDESConnector

  private[services] def feeResponseRepository: FeesRepository

  private[services] def viewDesConnector: ViewDESConnector

  private[services] val validator: SchemaValidator = new SchemaValidator()

  private[services] def validateResult(request: AmendVariationRequest): JsResult[JsValue]

  private[services] def amendVariationResponse(request: AmendVariationRequest, isRenewalWindow: Boolean, des: DesAmendVariationResponse): AmendVariationResponse

  private[services] val auditConnector: AuditConnector

  val phase2Changes: Boolean = false

  val stream: InputStream = getClass.getResourceAsStream (if (phase2Changes) "/resources/api6_schema_release_3.0.0.json" else "/resources/API6_Request.json")
  val lines = scala.io.Source.fromInputStream(stream).getLines
  val linesString = lines.foldLeft[String]("")((x, y) => x.trim ++ y.trim)

  def t(amendVariationResponse: DesAmendVariationResponse, amlsReferenceNumber: String)(implicit f: (DesAmendVariationResponse, String) => Fees) =
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

  def compareAndUpdate(desRequest: AmendVariationRequest, amlsRegistrationNumber: String)(
    implicit hc: HeaderCarrier,
    backOffHelper: BackOffHelper
  ): Future[AmendVariationRequest] = {
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
   ec: ExecutionContext,
   backOffHelper: BackOffHelper
  ): Future[AmendVariationResponse] = {

    val result = validateResult(request)

    if (!result.isSuccess) {
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

        Logger.warn(s"[AmendVariationService][update] Schema Validation Failed : amlsReg: $amlsRegistrationNumber : resultObjects : ${resultObjects}")
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
  override private[services] val feeResponseRepository = FeesRepository()
  override private[services] val amendVariationDesConnector = DESConnector
  override private[services] val viewStatusDesConnector: SubscriptionStatusDESConnector = DESConnector
  override private[services] val viewDesConnector: ViewDESConnector = DESConnector
  override private[services] val auditConnector = MicroserviceAuditConnector

  override private[services] def validateResult(request: AmendVariationRequest) =
    validator.validate(Json.fromJson[SchemaType](Json.parse(linesString.trim.drop(1))).get, Json.toJson(request))

  override private[services] def amendVariationResponse(request: AmendVariationRequest, isRenewalWindow: Boolean, des: DesAmendVariationResponse) =
    AmendVariationResponse.convert(request, isRenewalWindow, des)

  override val phase2Changes = AmlsConfig.phase2Changes
}
