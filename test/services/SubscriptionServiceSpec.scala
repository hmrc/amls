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

import config.AppConfig
import connectors.{EnrolmentStoreConnector, GovernmentGatewayAdminConnector, SubscribeDESConnector}
import exceptions.{DuplicateSubscriptionException, HttpStatusException}
import generators.AmlsReferenceNumberGenerator
import models._
import models.des.SubscriptionRequest
import models.des.aboutthebusiness.{Address, BusinessContactDetails}
import models.des.responsiblepeople.{RPExtra, ResponsiblePersons}
import models.des.tradingpremises.TradingPremises
import models.fe.{SubscriptionFees, SubscriptionResponse}
import org.joda.time.DateTime
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsResult, JsValue, Json}
import play.api.test.Helpers._
import repositories.FeesRepository
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TestFixture extends MockitoSugar with AmlsReferenceNumberGenerator {

  val successValidate: JsResult[JsValue] = mock[JsResult[JsValue]]
  val duplicateSubscriptionMessage = "Business Partner already has an active AMLS Subscription with MLR Ref Number"

  class MockSubscriptionService extends SubscriptionService(
    desConnector = mock[SubscribeDESConnector],
    ggConnector = mock[GovernmentGatewayAdminConnector],
    enrolmentStoreConnector = mock[EnrolmentStoreConnector],
    auditConnector = mock[AuditConnector],
    config = mock[AppConfig]
  ) {
    override private[services] def validateResult(request: SubscriptionRequest): JsResult[JsValue] = successValidate

    override private[services] val feeResponseRepository: FeesRepository = mock[FeesRepository]
  }

  val connector = new MockSubscriptionService

  val response = des.SubscriptionResponse(
    etmpFormBundleNumber = "111111",
    amlsRefNo = amlsRegistrationNumber,
    Some(150.00),
    Some(100.0),
    300.0,
    550.0,
    "XA353523452345",
    approvalNumbers = Some(100),
    approvalCheckFeeRate = Some(100.0),
    approvalCheckFee = Some(100.0)
  )

  val businessAddressPostcode = "TEST POSTCODE"
  val contactDetails = mock[BusinessContactDetails]
  val address = mock[Address]

  when(contactDetails.businessAddress) thenReturn address
  when(address.postcode) thenReturn Some(businessAddressPostcode)
  when(successValidate.isSuccess) thenReturn true

  val request = mock[des.SubscriptionRequest]
  when(request.businessContactDetails) thenReturn contactDetails


  val safeId = "safeId"

  implicit val hc = HeaderCarrier()
}

class SubscriptionServiceSpec extends PlaySpec with MockitoSugar with ScalaFutures with IntegrationPatience with OneAppPerSuite {

  "SubscriptionService subscribe" must {
    "return a successful response" which {
      "connects to EnrolmentStore" which {

        "returns full response" in new TestFixture {

          val knownFacts = KnownFactsForService(Seq(
            KnownFact("MLRRefNumber", response.amlsRefNo),
            KnownFact("SafeId", safeId),
            KnownFact("POSTCODE", businessAddressPostcode)
          ))

          reset(connector.ggConnector)

          when {
            successValidate.isSuccess
          } thenReturn true

          when {
            connector.desConnector.subscribe(eqTo(safeId), eqTo(request))(any(), any(), any(), any())
          } thenReturn Future.successful(response)

          when {
            connector.enrolmentStoreConnector.addKnownFacts(any(), eqTo(knownFacts))(any(), any())
          } thenReturn Future.successful(mock[HttpResponse])

          when {
            connector.feeResponseRepository.insert(any())
          } thenReturn Future.successful(true)

          when {
            connector.config.enrolmentStoreToggle
          } thenReturn true

          whenReady(connector.subscribe(safeId, request)) {
            result =>
              result mustEqual SubscriptionResponse.convert(response)
              verify(connector.enrolmentStoreConnector, times(1)).addKnownFacts(any(), eqTo(knownFacts))(any(), any())
          }
        }

        "returns duplicate response with amlsregno and there are no stored fees" in new TestFixture {

          reset(connector.ggConnector)

          val errorMessage = s"$duplicateSubscriptionMessage $amlsRegistrationNumber"
          val exceptionBody: String = Json.obj("reason" -> errorMessage).toString
          val subscriptionResponse = SubscriptionResponse("", amlsRegistrationNumber, 1, 0, 0, 0, 0, None, previouslySubmitted = true)

          when {
            connector.desConnector.subscribe(eqTo(safeId), eqTo(request))(any(), any(), any(), any())
          } thenReturn Future.failed(HttpStatusException(BAD_REQUEST, Some(exceptionBody)))

          when(connector.feeResponseRepository.findLatestByAmlsReference(any())).thenReturn(Future.successful(None))

          when {
            connector.config.enrolmentStoreToggle
          } thenReturn true

          when(request.responsiblePersons).thenReturn(Some(Seq(ResponsiblePersons(
            None, None, None, None, None, None, None, None, None, None, None, false, None, false, None, None, None, None, extra = RPExtra(None, None, None, None, None, None, None))
          )))

          when(request.tradingPremises).thenReturn(mock[TradingPremises])
          when(request.tradingPremises.ownBusinessPremises).thenReturn(None)
          when(request.tradingPremises.agentBusinessPremises).thenReturn(None)

          val ex: DuplicateSubscriptionException = intercept[DuplicateSubscriptionException] {
            await(connector.subscribe(safeId, request))
          }

          ex.amlsRegNumber mustBe amlsRegistrationNumber
          ex.message mustBe errorMessage
          ex.cause mustBe HttpStatusException(BAD_REQUEST, Some(exceptionBody))

          verify(connector.enrolmentStoreConnector, never).addKnownFacts(any(), any())(any(), any())
        }

        "returns duplicate response with amlsregno and there are stored fees" in new TestFixture {

          reset(connector.ggConnector)

          val jsonBody = Json.obj("reason" -> (duplicateSubscriptionMessage + amlsRegistrationNumber)).toString

          val subscriptionResponse = SubscriptionResponse(
            "", amlsRegistrationNumber, 0, 0, 0, 0, 0, Some(SubscriptionFees("PaymentRef", 500, Some(50), None, 115, None, 1000,
              Some(BigDecimal(20)), Some(BigDecimal(30)))), previouslySubmitted = true
          )

          when {
            successValidate.isSuccess
          } thenReturn false

          when {
            connector.desConnector.subscribe(eqTo(safeId), eqTo(request))(any(), any(), any(), any())
          } thenReturn Future.failed(HttpStatusException(BAD_REQUEST, Some(jsonBody)))

          val knownFacts = KnownFactsForService(Seq(
            KnownFact("MLRRefNumber", amlsRegistrationNumber),
            KnownFact("SafeId", safeId),
            KnownFact("POSTCODE", businessAddressPostcode)
          ))

          when {
            connector.enrolmentStoreConnector.addKnownFacts(any(), eqTo(knownFacts))(any(), any())
          } thenReturn Future.successful(mock[HttpResponse])

          when {
            connector.config.enrolmentStoreToggle
          } thenReturn true

          when(connector.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

          when{
            connector.feeResponseRepository.findLatestByAmlsReference(any())
          } thenReturn Future.successful(Some(Fees(SubscriptionResponseType, amlsRegistrationNumber, 500, Some(50), 115, 1000,
            Some("PaymentRef"), None, Some(BigDecimal(20)), Some(BigDecimal(30)), new DateTime())))

          when(request.responsiblePersons).thenReturn(None)
          when(request.tradingPremises).thenReturn(mock[TradingPremises])
          when(request.tradingPremises.ownBusinessPremises).thenReturn(None)
          when(request.tradingPremises.agentBusinessPremises).thenReturn(None)

          whenReady(connector.subscribe(safeId, request)) {
            result =>
              result mustEqual subscriptionResponse
              verify(connector.enrolmentStoreConnector, times(1)).addKnownFacts(any(), eqTo(knownFacts))(any(), any())
          }
        }

      }
    }

    "return a failed future" when {
      "bad request is returned" when {
        "jsonbody is returned but does not contain an amlsregno" in new TestFixture {

          reset(connector.ggConnector)

          val jsonBody = Json.obj("reason" -> duplicateSubscriptionMessage).toString

          when {
            connector.desConnector.subscribe(eqTo(safeId), eqTo(request))(any(), any(), any(), any())
          } thenReturn Future.failed(HttpStatusException(BAD_REQUEST, Some(jsonBody)))


          whenReady(connector.subscribe(safeId, request).failed) {
            case ex@HttpStatusException(status, _) =>
              status mustEqual BAD_REQUEST
              ex.jsonBody.get.reason must equal(duplicateSubscriptionMessage)

          }
        }

        "no body is returned" in new TestFixture {

          reset(connector.ggConnector)

          when {
            connector.desConnector.subscribe(eqTo(safeId), eqTo(request))(any(), any(), any(), any())
          } thenReturn Future.failed(HttpStatusException(BAD_REQUEST, None))

          whenReady(connector.subscribe(safeId, request).failed) {
            case ex@HttpStatusException(status, _) =>
              status mustEqual BAD_REQUEST
              ex.jsonBody must equal(None)

          }
        }
      }

      "another exception is returned" in new TestFixture {

        reset(connector.ggConnector)

        when {
          connector.desConnector.subscribe(eqTo(safeId), eqTo(request))(any(), any(), any(), any())
        } thenReturn Future.failed(HttpStatusException(BAD_GATEWAY, None))

        whenReady(connector.subscribe(safeId, request).failed) {
          case ex@HttpStatusException(status, body) =>
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

          val knownFacts = KnownFactsForService(Seq(
            KnownFact("MLRRefNumber", response.amlsRefNo),
            KnownFact("SafeId", safeId),
            KnownFact("POSTCODE", businessAddressPostcode)
          ))

          reset(connector.ggConnector)

          when {
            successValidate.isSuccess
          } thenReturn true

          when {
            connector.desConnector.subscribe(eqTo(safeId), eqTo(request))(any(), any(), any(), any())
          } thenReturn Future.successful(response)

          when {
            connector.ggConnector.addKnownFacts(eqTo(knownFacts))(any(), any())
          } thenReturn Future.successful(mock[HttpResponse])

          when {
            connector.enrolmentStoreConnector.addKnownFacts(any(), eqTo(knownFacts))(any(), any())
          } thenReturn Future.successful(mock[HttpResponse])

          when {
            connector.feeResponseRepository.insert(any())
          } thenReturn Future.successful(true)

          when {
            connector.config.enrolmentStoreToggle
          } thenReturn false

          whenReady(connector.subscribe(safeId, request)) {
            result =>
              result mustEqual SubscriptionResponse.convert(response)
              verify(connector.ggConnector, times(1)).addKnownFacts(eqTo(knownFacts))(any(), any())
          }
        }
      }
    }
  }
}