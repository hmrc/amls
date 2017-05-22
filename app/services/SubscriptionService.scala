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
import connectors.{DESConnector, GovernmentGatewayAdminConnector, SubscribeDESConnector}
import exceptions.{HttpExceptionBody, HttpStatusException}
import models.{Fees, KnownFact, KnownFactsForService}
import models.des.SubscriptionRequest
import models.fe.{SubscriptionFees, SubscriptionResponse}
import play.api.Logger
import play.api.libs.json.{JsResult, JsValue, Json}
import repositories.FeesRepository
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait SubscriptionService {

  private val BAD_REQUEST: scala.Int = 400

  private val amlsRegistrationNumberRegex = "X[A-Z]ML00000[0-9]{6}$".r

  private[services] def desConnector: SubscribeDESConnector

  private[services] def ggConnector: GovernmentGatewayAdminConnector

  private[services] def feeResponseRepository: FeesRepository

  private[services] val validator: SchemaValidator = SchemaValidator()

  private[services] def validateResult(request: SubscriptionRequest): JsResult[JsValue]

  private val stream: InputStream = getClass.getResourceAsStream("/resources/API4_Request.json")
  private val lines = scala.io.Source.fromInputStream(stream).getLines
  protected[SubscriptionService] val linesString: String = lines.foldLeft[String]("")((x, y) => x.trim ++ y.trim)
  private val duplicateSubscriptionMessage = "Business Partner already has an active AMLS Subscription"


  def subscribe
  (safeId: String, request: SubscriptionRequest)
  (implicit
   hc: HeaderCarrier,
   ec: ExecutionContext
  ): Future[SubscriptionResponse] = {

    val handleExceptionWithBody: PartialFunction[Throwable, Future[SubscriptionResponse]] = {
      case ex@HttpStatusException(BAD_REQUEST, _) => {
        ex.jsonBody map {
          case body if body.reason.startsWith(duplicateSubscriptionMessage) => amlsRegistrationNumberRegex.findFirstIn(body.reason)
            .fold[Future[SubscriptionResponse]](failResponse(ex, body)) {
            amlsRegNo => constructedSubscriptionResponse(amlsRegNo, request)(ec)
          }
          case body =>
            failResponse(ex, body)
        }
      }.getOrElse(Future.failed(ex))

      case e@HttpStatusException(status, Some(body)) =>
        Logger.warn(s" - Status: $status, Message: $body")
        Future.failed(e)
    }

    validateRequest(safeId, request)

    for {
      response <- desConnector.subscribe(safeId, request)
        .map(desResponse => SubscriptionResponse.convert(desResponse))
        .recoverWith {
          handleExceptionWithBody
        }
      _ <- Fees.convert(response) match {
        case Some(fees) => feeResponseRepository.insert(fees)
        case _ => Future.successful(false)
      }
      _ <- ggConnector.addKnownFacts(KnownFactsForService(Seq(
        KnownFact("SafeId", safeId),
        KnownFact("MLRRefNumber", response.amlsRefNo)
      )))
    } yield response
  }

  private def validateRequest(safeId: String, request: SubscriptionRequest) = {
    val result = validateResult(request)
    if (!result.isSuccess) {
      val errors = result.fold(invalid = { errors =>
        errors.foldLeft[String]("") {
          (a, b) => a + "," + b._1.toJsonString
        }
      }, valid = identity)
      Logger.warn(s"[SubscriptionService][subscribe] Schema Validation Failed : safeId: $safeId : Error Paths : $errors")
    } else {
      Logger.debug(s"[SubscriptionService][subscribe] : safeId: $safeId : Validation passed")
    }
  }

  private def failResponse(ex: HttpStatusException, body: HttpExceptionBody) = {
    Logger.warn(s" - Status: ${ex.status}, Message: $body")
    Future.failed(ex)
  }

  private def constructedSubscriptionResponse(amlsRegNo: String, request: SubscriptionRequest)(implicit ec: ExecutionContext) = {

    def tradingPremisesCount = {
      (for {
        agentPremises <- request.tradingPremises.agentBusinessPremises
        agentPremisesDetails <- agentPremises.agentDetails
      } yield agentPremisesDetails.size).getOrElse(0) +
        (for {
          ownPremises <- request.tradingPremises.ownBusinessPremises
          ownBusinessPremisesDetails <- ownPremises.ownBusinessPremisesDetails
        } yield ownBusinessPremisesDetails.size).getOrElse(0)
    }

    def responsiblePersonsCount = {
      request.responsiblePersons.fold(0) { rp => rp.size }
    }

    def responsiblePersonsPassedFitAndProperCount = {
      request.responsiblePersons.fold(0) { rp =>
        rp.count(_.msbOrTcsp.fold(false) {
          _.passedFitAndProperTest
        })
      }
    }

    feeResponseRepository.findLatestByAmlsReference(amlsRegNo) map {
      case Some(fees) => SubscriptionResponse("", amlsRegNo, responsiblePersonsCount,
        responsiblePersonsPassedFitAndProperCount, tradingPremisesCount, Some(SubscriptionFees(fees.paymentReference.getOrElse(""),
          fees.registrationFee, fees.fpFee, None, fees.premiseFee, None, fees.totalFees)), true)
      case None => SubscriptionResponse("", amlsRegNo, responsiblePersonsCount, responsiblePersonsPassedFitAndProperCount, tradingPremisesCount, None, true)
    }

  }
}

object SubscriptionService extends SubscriptionService {
  // $COVERAGE-OFF$
  override private[services] val desConnector = DESConnector
  override private[services] val ggConnector = GovernmentGatewayAdminConnector
  override private[services] val feeResponseRepository = FeesRepository()

  override private[services] def validateResult(request: SubscriptionRequest) = {
    validator.validate(Json.fromJson[SchemaType](Json.parse(linesString.trim.drop(1))).get, Json.toJson(request))
  }
}
