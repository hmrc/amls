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
import config.{AmlsConfig, MicroserviceAuditConnector}
import connectors._
import javax.inject.Inject
import models.Fees
import models.des.{AmendVariationResponse => DesAmendVariationResponse, _}
import models.fe.AmendVariationResponse
import models.fe.moneyservicebusiness.MoneyServiceBusiness
import models.des.msb
import models.des.tcsp.TcspTrustCompFormationAgt
import models.fe.asp.Asp
import models.fe.businessmatching.BusinessMatching
import models.fe.estateagentbusiness.EstateAgentBusiness
import models.fe.hvd.Hvd
import models.fe.supervision.Supervision
import models.fe.tcsp.{Tcsp, TcspTypes}
import models.fe.tradingpremises.BusinessActivity.HighValueDealing
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

  private def compareMsb(viewResponse: SubscriptionView, desRequest: AmendVariationRequest)  = {
    val api5BM = BusinessMatching.conv(viewResponse)
    val api5Msb = MoneyServiceBusiness.conv(viewResponse)
    val convApi5Msb = models.des.msb.MoneyServiceBusiness.conv(api5Msb, api5BM, amendVariation = true)

    !convApi5Msb.equals(desRequest.msb)
  }

  private def compareHvd(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val api5Hvd = Hvd.conv(viewResponse)
    val convApi5Hvd = models.des.hvd.Hvd.conv(api5Hvd)

    !convApi5Hvd.equals(desRequest.hvd)
  }

  private def compareAsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val api5Asp = Asp.conv(viewResponse)
    val convApi5Asp = models.des.asp.Asp.conv(api5Asp)

    !convApi5Asp.equals(desRequest.asp)
  }

  private def compareTcsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val api5Tcsp = Tcsp.conv(viewResponse)
    val convApi5Tcsp = Some(models.des.tcsp.TcspAll.conv(api5Tcsp))
    val convApi5TcspTypes = Some(models.des.tcsp.TcspTrustCompFormationAgt.conv(api5Tcsp))

    !(convApi5Tcsp.equals(desRequest.tcspAll) &&
      convApi5TcspTypes.equals(desRequest.tcspTrustCompFormationAgt))
  }

  private def compareEab(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val api5Eab = EstateAgentBusiness.conv(viewResponse)
    val convApi5Eab = models.des.estateagentbusiness.EabAll.convert(api5Eab.getOrElse(EstateAgentBusiness()))
    val convApi5EabResdEstAgncy = models.des.estateagentbusiness.EabResdEstAgncy.convert(api5Eab)

    !(convApi5Eab.equals(desRequest.eabAll) &&
      convApi5EabResdEstAgncy.equals(desRequest.eabResdEstAgncy))
  }

  private def compareAspOrTcsp(viewResponse: SubscriptionView, desRequest: AmendVariationRequest) = {
    val api5Supervision = Supervision.convertFrom(viewResponse.aspOrTcsp,
      viewResponse.businessActivities.mlrActivitiesAppliedFor)
    val convApi5AspOrTcsp = models.des.supervision.AspOrTcsp.conv(api5Supervision)

    !convApi5AspOrTcsp.equals(desRequest.aspOrTcsp)
  }

  def compareAndUpdate(desRequest: AmendVariationRequest, amlsRegistrationNumber: String)(
    implicit hc: HeaderCarrier,
    apiRetryHelper: ApiRetryHelper
  ): Future[AmendVariationRequest] = {
    viewDesConnector.view(amlsRegistrationNumber).map { viewResponse =>

      val updatedRequest = updateRequest(desRequest, viewResponse)
      val desRPs = updateWithResponsiblePeople(desRequest, viewResponse).responsiblePersons

      updatedRequest.setChangeIndicator(ChangeIndicators(
        !viewResponse.businessDetails.equals(desRequest.businessDetails),
        !viewResponse.businessContactDetails.businessAddress.equals(desRequest.businessContactDetails.businessAddress),
        isBusinessReferenceChanged(desRequest, viewResponse),
        !viewResponse.tradingPremises.equals(desRequest.tradingPremises),
        !viewResponse.businessActivities.equals(desRequest.businessActivities),
        !viewResponse.bankAccountDetails.equals(desRequest.bankAccountDetails),

        compareMsb(viewResponse, desRequest),
        compareHvd(viewResponse, desRequest),
        compareAsp(viewResponse, desRequest),
        compareTcsp(viewResponse, desRequest),
        compareEab(viewResponse, desRequest),
        compareAspOrTcsp(viewResponse, desRequest),

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

   private[services] def validateResult(request: AmendVariationRequest) = {
     // $COVERAGE-OFF$
     val stream: InputStream = getClass.getResourceAsStream (
       if (AmlsConfig.phase2Changes) "/resources/api6_schema_release_3.0.0.json" else "/resources/API6_Request.json")
     val lines = scala.io.Source.fromInputStream(stream).getLines
     val linesString = lines.foldLeft[String]("")((x, y) => x.trim ++ y.trim)

    if(AmlsConfig.phase2Changes) {
      validator.validate(Json.fromJson[SchemaType](Json.parse(linesString.trim)).get, Json.toJson(request))
    }
    else {
      validator.validate(Json.fromJson[SchemaType](Json.parse(linesString.trim.drop(1))).get, Json.toJson(request))
    }
  }

  private[services] def amendVariationResponse(request: AmendVariationRequest, isRenewalWindow: Boolean, des: DesAmendVariationResponse) =
    AmendVariationResponse.convert(request, isRenewalWindow, des)
}
