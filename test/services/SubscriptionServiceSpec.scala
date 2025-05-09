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

import config.ApplicationConfig
import connectors.{EnrolmentStoreConnector, GovernmentGatewayAdminConnector, SubscribeDESConnector}
import exceptions.{DuplicateSubscriptionException, HttpStatusException}
import generators.AmlsReferenceNumberGenerator
import models._
import models.des.aboutthebusiness.{Address, BusinessContactDetails}
import models.des.responsiblepeople.{RPExtra, ResponsiblePersons}
import models.des.tradingpremises.TradingPremises
import models.fe.SubscriptionResponse
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsResult, JsValue, Json}
import play.api.test.Helpers._
import repositories.FeesRepository
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.{ApiRetryHelper, SubscriptionRequestValidator}

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TestFixture extends AmlsReferenceNumberGenerator {

  val successValidate: JsResult[JsValue] = mock(classOf[JsResult[JsValue]])
  val duplicateSubscriptionMessage       = "Business Partner already has an active AMLS Subscription with MLR Ref Number"

  class MockSubscriptionService
      extends SubscriptionService(
        mock(classOf[SubscribeDESConnector]),
        mock(classOf[GovernmentGatewayAdminConnector]),
        mock(classOf[EnrolmentStoreConnector]),
        mock(classOf[AuditConnector]),
        mock(classOf[ApplicationConfig]),
        mock(classOf[SubscriptionRequestValidator]),
        mock(classOf[FeesRepository])
      )

  val connector = new MockSubscriptionService

  val response = des.SubscriptionResponse(
    etmpFormBundleNumber = "111111",
    amlsRefNo = amlsRegistrationNumber,
    Some(150.00),
    Some(100.0),
    300.0,
    550.0,
    "XA353523452345",
    approvalCheckNumbers = Some(100),
    approvalCheckFeeRate = Some(100.0),
    approvalCheckFee = Some(100.0)
  )

  val businessAddressPostcode = "TEST POSTCODE"
  val contactDetails          = mock(classOf[BusinessContactDetails])
  val address                 = mock(classOf[Address])

  when(contactDetails.businessAddress) thenReturn address
  when(address.postcode) thenReturn Some(businessAddressPostcode)

  val request = mock(classOf[des.SubscriptionRequest])
  when(request.businessContactDetails) thenReturn contactDetails

  val safeId = "safeId"

  implicit val hc: HeaderCarrier = HeaderCarrier()
}

class SubscriptionServiceSpec extends PlaySpec with ScalaFutures with IntegrationPatience with GuiceOneAppPerSuite {

  "SubscriptionService subscribe" must {
    "return a successful response" which {
      "connects to EnrolmentStore" which {

        "returns full response" in new TestFixture {

          val knownFacts = KnownFactsForService(
            Seq(
              KnownFact("MLRRefNumber", response.amlsRefNo),
              KnownFact("SafeId", safeId),
              KnownFact("POSTCODE", businessAddressPostcode)
            )
          )

          reset(connector.ggConnector)

          when {
            connector.subscriptionRequestValidator.validateRequest(request)
          } thenReturn Right(request)

          when {
            connector.desConnector
              .subscribe(ArgumentMatchers.eq(safeId), ArgumentMatchers.eq(request))(any(), any(), any(), any(), any())
          } thenReturn Future.successful(response)

          when {
            connector.enrolmentStoreConnector.addKnownFacts(any(), ArgumentMatchers.eq(knownFacts))(any(), any())
          } thenReturn Future.successful(mock(classOf[HttpResponse]))

          when {
            connector.feeResponseRepository.insert(any())
          } thenReturn Future.successful(true)

          when {
            connector.config.enrolmentStoreToggle
          } thenReturn true

          whenReady(connector.subscribe(safeId, request)(hc, global, apiRetryHelper = mock(classOf[ApiRetryHelper]))) {
            result =>
              result mustEqual SubscriptionResponse.convert(response)
              verify(connector.enrolmentStoreConnector, times(1))
                .addKnownFacts(any(), ArgumentMatchers.eq(knownFacts))(any(), any())
          }
        }

        "returns duplicate response with amlsregno and there are no stored fees" in new TestFixture {

          reset(connector.ggConnector)

          val errorMessage          = s"$duplicateSubscriptionMessage $amlsRegistrationNumber"
          val exceptionBody: String = Json.obj("reason" -> errorMessage).toString

          when {
            connector.subscriptionRequestValidator.validateRequest(request)
          } thenReturn Right(request)

          when {
            connector.desConnector
              .subscribe(ArgumentMatchers.eq(safeId), ArgumentMatchers.eq(request))(any(), any(), any(), any(), any())
          } thenReturn Future.failed(HttpStatusException(BAD_REQUEST, Some(exceptionBody)))

          when(connector.feeResponseRepository.findLatestByAmlsReference(any())).thenReturn(Future.successful(None))

          when {
            connector.config.enrolmentStoreToggle
          } thenReturn true

          when(request.responsiblePersons).thenReturn(
            Some(
              Seq(
                ResponsiblePersons(
                  None,
                  None,
                  None,
                  None,
                  None,
                  None,
                  None,
                  None,
                  None,
                  None,
                  None,
                  false,
                  None,
                  false,
                  None,
                  None,
                  Some(false),
                  None,
                  extra = RPExtra(None, None, None, None, None, None, None)
                )
              )
            )
          )

          when(request.tradingPremises).thenReturn(mock(classOf[TradingPremises]))
          when(request.tradingPremises.ownBusinessPremises).thenReturn(None)
          when(request.tradingPremises.agentBusinessPremises).thenReturn(None)

          val ex: DuplicateSubscriptionException = intercept[DuplicateSubscriptionException] {
            await(connector.subscribe(safeId, request)(hc, global, apiRetryHelper = mock(classOf[ApiRetryHelper])))
          }

          ex.amlsRegNumber mustBe amlsRegistrationNumber
          ex.message mustBe errorMessage
          ex.cause mustBe HttpStatusException(BAD_REQUEST, Some(exceptionBody))

          verify(connector.enrolmentStoreConnector, never).addKnownFacts(any(), any())(any(), any())
        }

        "returns duplicate response with amlsregno and there are stored fees" in new TestFixture {

          val errorMessage          = s"$duplicateSubscriptionMessage $amlsRegistrationNumber"
          val exceptionBody: String = Json.obj("reason" -> errorMessage).toString

          reset(connector.ggConnector)

          val jsonBody = Json.obj("reason" -> (duplicateSubscriptionMessage + " " + amlsRegistrationNumber)).toString

          when {
            connector.subscriptionRequestValidator.validateRequest(request)
          } thenReturn Right(request)

          when {
            connector.desConnector
              .subscribe(ArgumentMatchers.eq(safeId), ArgumentMatchers.eq(request))(any(), any(), any(), any(), any())
          } thenReturn Future.failed(HttpStatusException(BAD_REQUEST, Some(jsonBody)))

          val knownFacts = KnownFactsForService(
            Seq(
              KnownFact("MLRRefNumber", amlsRegistrationNumber),
              KnownFact("SafeId", safeId),
              KnownFact("POSTCODE", businessAddressPostcode)
            )
          )

          when {
            connector.enrolmentStoreConnector.addKnownFacts(any(), ArgumentMatchers.eq(knownFacts))(any(), any())
          } thenReturn Future.successful(mock(classOf[HttpResponse]))

          when {
            connector.config.enrolmentStoreToggle
          } thenReturn true

          when(connector.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

          when {
            connector.feeResponseRepository.findLatestByAmlsReference(any())
          } thenReturn Future.successful(
            Some(
              Fees(
                SubscriptionResponseType,
                amlsRegistrationNumber,
                500,
                Some(50),
                115,
                1000,
                Some("PaymentRef"),
                None,
                Some(BigDecimal(20)),
                Some(BigDecimal(30)),
                LocalDateTime.now()
              )
            )
          )

          when(request.responsiblePersons).thenReturn(None)
          when(request.tradingPremises).thenReturn(mock(classOf[TradingPremises]))
          when(request.tradingPremises.ownBusinessPremises).thenReturn(None)
          when(request.tradingPremises.agentBusinessPremises).thenReturn(None)

          val ex: DuplicateSubscriptionException = intercept[DuplicateSubscriptionException] {
            await(connector.subscribe(safeId, request)(hc, global, apiRetryHelper = mock(classOf[ApiRetryHelper])))
          }

          ex.amlsRegNumber mustBe amlsRegistrationNumber
          ex.message mustBe errorMessage
          ex.cause mustBe HttpStatusException(BAD_REQUEST, Some(exceptionBody))
        }

      }
    }

    "return a failed future" when {
      "bad request is returned" when {
        "jsonbody is returned but does not contain an amlsregno" in new TestFixture {

          reset(connector.ggConnector)

          val jsonBody = Json.obj("reason" -> duplicateSubscriptionMessage).toString

          when {
            connector.subscriptionRequestValidator.validateRequest(request)
          } thenReturn Right(request)

          when {
            connector.desConnector
              .subscribe(ArgumentMatchers.eq(safeId), ArgumentMatchers.eq(request))(any(), any(), any(), any(), any())
          } thenReturn Future.failed(HttpStatusException(BAD_REQUEST, Some(jsonBody)))

          whenReady(
            connector.subscribe(safeId, request)(hc, global, apiRetryHelper = mock(classOf[ApiRetryHelper])).failed
          ) { case ex @ HttpStatusException(status, _) =>
            status mustEqual BAD_REQUEST
            ex.jsonBody.get.reason must equal(duplicateSubscriptionMessage)

          }
        }

        "no body is returned" in new TestFixture {

          reset(connector.ggConnector)

          when {
            connector.subscriptionRequestValidator.validateRequest(request)
          } thenReturn Right(request)

          when {
            connector.desConnector
              .subscribe(ArgumentMatchers.eq(safeId), ArgumentMatchers.eq(request))(any(), any(), any(), any(), any())
          } thenReturn Future.failed(HttpStatusException(BAD_REQUEST, None))

          whenReady(
            connector.subscribe(safeId, request)(hc, global, apiRetryHelper = mock(classOf[ApiRetryHelper])).failed
          ) { case ex @ HttpStatusException(status, _) =>
            status mustEqual BAD_REQUEST
            ex.jsonBody must equal(None)

          }
        }
      }

      "another exception is returned" in new TestFixture {

        reset(connector.ggConnector)

        when {
          connector.subscriptionRequestValidator.validateRequest(request)
        } thenReturn Right(request)

        when {
          connector.desConnector
            .subscribe(ArgumentMatchers.eq(safeId), ArgumentMatchers.eq(request))(any(), any(), any(), any(), any())
        } thenReturn Future.failed(HttpStatusException(BAD_GATEWAY, None))

        whenReady(
          connector.subscribe(safeId, request)(hc, global, apiRetryHelper = mock(classOf[ApiRetryHelper])).failed
        ) { case ex @ HttpStatusException(status, body) =>
          status mustEqual BAD_GATEWAY
          ex.jsonBody must equal(None)

        }
      }
    }

  }

  it must {
    "return full response" which {
      "connects to GGAdminConnector" when {
        "enrolment-store-toggle is switched to false" in new TestFixture {

          val knownFacts = KnownFactsForService(
            Seq(
              KnownFact("MLRRefNumber", response.amlsRefNo),
              KnownFact("SafeId", safeId),
              KnownFact("POSTCODE", businessAddressPostcode)
            )
          )

          reset(connector.ggConnector)

          when {
            connector.subscriptionRequestValidator.validateRequest(request)
          } thenReturn Right(request)

          when {
            connector.desConnector
              .subscribe(ArgumentMatchers.eq(safeId), ArgumentMatchers.eq(request))(any(), any(), any(), any(), any())
          } thenReturn Future.successful(response)

          when {
            connector.ggConnector.addKnownFacts(ArgumentMatchers.eq(knownFacts))(any(), any())
          } thenReturn Future.successful(mock(classOf[HttpResponse]))

          when {
            connector.enrolmentStoreConnector.addKnownFacts(any(), ArgumentMatchers.eq(knownFacts))(any(), any())
          } thenReturn Future.successful(mock(classOf[HttpResponse]))

          when {
            connector.feeResponseRepository.insert(any())
          } thenReturn Future.successful(true)

          when {
            connector.config.enrolmentStoreToggle
          } thenReturn false

          whenReady(connector.subscribe(safeId, request)(hc, global, apiRetryHelper = mock(classOf[ApiRetryHelper]))) {
            result =>
              result mustEqual SubscriptionResponse.convert(response)
              verify(connector.ggConnector, times(1)).addKnownFacts(ArgumentMatchers.eq(knownFacts))(any(), any())
          }
        }
      }
    }
  }
}
