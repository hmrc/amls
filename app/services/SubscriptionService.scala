/*
 * Copyright 2024 HM Revenue & Customs
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

import audit.SubscriptionValidationFailedEvent
import config.ApplicationConfig
import connectors.{EnrolmentStoreConnector, GovernmentGatewayAdminConnector, SubscribeDESConnector}
import exceptions.{DuplicateSubscriptionException, HttpExceptionBody, HttpStatusException}
import models.des.SubscriptionRequest
import models.enrolment.AmlsEnrolmentKey
import models.fe.SubscriptionResponse
import models.{Fees, KnownFact, KnownFactsForService}
import play.api.Logging
import play.api.libs.json.JsObject
import play.mvc.Http.Status._
import repositories.FeesRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.{ApiRetryHelper, SubscriptionRequestValidator}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubscriptionService @Inject()(private[services] val desConnector: SubscribeDESConnector,
                                    private[services] val ggConnector: GovernmentGatewayAdminConnector,
                                    private[services] val enrolmentStoreConnector: EnrolmentStoreConnector,
                                    private[services] val auditConnector: AuditConnector,
                                    private[services] val config: ApplicationConfig,
                                    val subscriptionRequestValidator: SubscriptionRequestValidator,
                                    val feeResponseRepository: FeesRepository) extends Logging {

  private val amlsRegistrationNumberRegex = "X[A-Z]ML00000[0-9]{6}$".r

  private def duplicateSubscriptionErrorHandler(request: SubscriptionRequest): PartialFunction[Throwable, Future[SubscriptionResponse]] = {
    case ex @ HttpStatusException(BAD_REQUEST, _) => {
      ex.jsonBody map {
        case body if body.reason.startsWith(Constants.duplicateSubscriptionErrorMessage) =>
          amlsRegistrationNumberRegex
            .findFirstIn(body.reason)
            .fold[Future[SubscriptionResponse]](failResponse(ex, body)) {
              amlsRegNo => {
                logger.warn(s"[SubscriptionService] - Duplicate subscription for $amlsRegNo; failing..")
                failResponse(DuplicateSubscriptionException(ex, amlsRegNo, body.reason), body)
              }
            }
        case body =>
          failResponse(ex, body)
      }
    }.getOrElse(Future.failed(ex))

    case e @ HttpStatusException(status, Some(body)) =>
      logger.warn(s" - Status: $status, Message: $body")
      Future.failed(e)
  }

  def subscribe(safeId: String, request: SubscriptionRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext,
                                                              apiRetryHelper: ApiRetryHelper): Future[SubscriptionResponse] = {

    val validationResult: Either[collection.Seq[JsObject], SubscriptionRequest] = subscriptionRequestValidator.validateRequest(request)

    validationResult match {
      case Left(errors) =>
        logger.warn(s"[SubscriptionService][subscribe] Schema Validation Failed : safeId: $safeId")
        auditConnector.sendExtendedEvent(SubscriptionValidationFailedEvent(safeId, request, errors))
      case Right(_) =>
        logger.debug(s"[SubscriptionService][subscribe] : safeId: $safeId : Validation passed")
    }

    for {
      response <- desConnector.subscribe(safeId, request)
        .map(desResponse => SubscriptionResponse.convert(desResponse))
        .recoverWith {
          duplicateSubscriptionErrorHandler(request)
        }
      _ <- Fees.convertSubscription(response) match {
        case Some(fees) => feeResponseRepository.insert(fees)
        case _ => Future.successful(false)
      }
      _ <- addKnownFacts(safeId, request, response)

    } yield response

  }

  private def getKnownFacts(safeId: String, request: SubscriptionRequest, response: SubscriptionResponse) = {
    val facts = Seq(
      KnownFact("MLRRefNumber", response.amlsRefNo),
      KnownFact("SafeId", safeId)
    )

    if (request.businessContactDetails.businessAddress.postcode.isDefined) {
      KnownFactsForService(facts :+ KnownFact("POSTCODE", request.businessContactDetails.businessAddress.postcode.get))
    } else {
      KnownFactsForService(facts)
    }
  }

  private def failResponse(ex: Throwable, body: HttpExceptionBody) = {
    ex match {
      case e: HttpStatusException => logger.warn(s" - Status: ${e.status}, Message: $body")
      case _ => logger.warn(s" - Exception thrown - Message: $body")
    }

    Future.failed(ex)
  }

  private def addKnownFacts(safeId: String, request: SubscriptionRequest, response: SubscriptionResponse)
                           (implicit hc: HeaderCarrier, ec: ExecutionContext) = {
    (if (config.enrolmentStoreToggle) {
      enrolmentStoreConnector.addKnownFacts(AmlsEnrolmentKey(response.amlsRefNo), getKnownFacts(safeId, request, response))
    } else {
      ggConnector.addKnownFacts(getKnownFacts(safeId, request, response))
    }) map (_ => response) recover {
      case ex => logger.warn("[AddKnownFactsFailed]", ex)
        response
    }
  }

}