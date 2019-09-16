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

import connectors.{AmendVariationDESConnector, SubscriptionStatusDESConnector, ViewDESConnector}
import generators.AmlsReferenceNumberGenerator
import models.des
import models.des.responsiblepeople.{RPExtra, ResponsiblePersons}
import models.des.tradingpremises._
import models.des.{AmendVariationRequest, DesConstants, ReadStatusResponse}
import models.fe.AmendVariationResponse
import org.joda.time.{LocalDate, LocalDateTime}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsResult, JsValue}
import repositories.FeesMongoRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.ApiRetryHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendVariationServiceSpec extends PlaySpec
  with OneAppPerSuite
  with MockitoSugar
  with ScalaFutures
  with IntegrationPatience
  with AmlsReferenceNumberGenerator {

  val successValidate:JsResult[JsValue] = mock[JsResult[JsValue]]

  val feAmendVariationResponse = AmendVariationResponse(
    processingDate = "2016-09-17T09:30:47Z",
    etmpFormBundleNumber = "111111",
    1301737.96,
    Some(1),
    Some(115.0d),
    231.42,
    Some(0),
    123.12,
    None,
    None
  )

  val feeRepo: FeesMongoRepository = mock[FeesMongoRepository]

  class TestAmendVariationService extends AmendVariationService(
    mock[AmendVariationDESConnector],
    mock[SubscriptionStatusDESConnector],
    mock[ViewDESConnector],
    mock[AuditConnector],
    feeRepo
  ) {
    override private[services] def validateResult(request: AmendVariationRequest) = successValidate
    override private[services] def amendVariationResponse(
      request: AmendVariationRequest,
      isRenewalWindow: Boolean,
      des: models.des.AmendVariationResponse) = feAmendVariationResponse
  }

  val avs = new TestAmendVariationService

  val response = des.AmendVariationResponse(
    processingDate = "2016-09-17T09:30:47Z",
    etmpFormBundleNumber = "111111",
    Some(1301737.96d),
    Some(1),
    Some(115.0d),
    Some(231.42d),
    Some(0),
    None,
    None,
    None,
    None,
    None,
    None,
    Some(870458d),
    Some(2172427.38),
    Some("string"),
    Some(3456.12),
    Some(100),
    Some(100.0),
    Some(100.0)
  )

  val statusResponse = ReadStatusResponse(new LocalDateTime(), "Approved", None, None, None, Some(new LocalDate(2017, 4, 30)), false)

  val unchangedExtra: RPExtra = RPExtra(status = Some("Unchanged"))
  val addedExtra: RPExtra = RPExtra(status = Some("Added"))

  val unchangedResponsiblePersons = ResponsiblePersons(
    None,
    None,
    None,
    None,
    Some("0-6 months"),
    None,
    Some("7-12 months"),
    None,
    Some("1-3 years"),
    None,
    None,
    true,
    Some("Some training"),
    true,
    Some("test"),
    None,
    None,
    None,
    extra = unchangedExtra
  )

  val amlsRegForHalfYears = amlsRefNoGen.sample.get

  implicit val hc = HeaderCarrier()

  "AmendVariationService" must {


    when {
      successValidate.isSuccess
    } thenReturn true

    when {
      avs.viewStatusDesConnector.status(eqTo(amlsRegistrationNumber))(any(), any(), any(), any())
    } thenReturn Future.successful(statusResponse)

    val premises: Option[AgentBusinessPremises] = Some(mock[AgentBusinessPremises])

    when {
      premises.get.agentDetails
    } thenReturn None

    "return a successful response" in {

      val request = mock[des.AmendVariationRequest]
      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), premises)

      when{
        request.responsiblePersons
      } thenReturn Some(Seq(unchangedResponsiblePersons))

      when {
        request.tradingPremises
      } thenReturn tradingPremises

      when {
        avs.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any(), any(), any())
        avs.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any(), any(), any())
      } thenReturn Future.successful(response)

      when{
        avs.feeResponseRepository.insert(any())
      } thenReturn Future.successful(true)

      whenReady(avs.update(amlsRegistrationNumber, request)(hc, global, apiRetryHelper = mock[ApiRetryHelper])) {
        result =>
          result mustEqual feAmendVariationResponse
      }
    }

    "evaluate isBusinessReferenceChanged when api5 data is same as api6 " in {
      avs.isBusinessReferenceChanged(DesConstants.AmendVariationRequestModel, DesConstants.SubscriptionViewModelForRp) must be(false)
    }

    "compare and update api6 request with api5 1" in {

      val viewModel = DesConstants.SubscriptionViewModelAPI5

      when {
        avs.viewDesConnector.view(eqTo(amlsRegistrationNumber))(any(), any(), any())
      } thenReturn Future.successful(viewModel)

      val testRequest = DesConstants.updateAmendVariationCompleteRequest1.copy(
        tradingPremises = DesConstants.testAmendTradingPremisesAPI6.copy(
          DesConstants.ownBusinessPremisesTPR7
        )
      )

      whenReady(avs.compareAndUpdate(
        DesConstants.amendVariationRequest1, amlsRegistrationNumber)(hc, apiRetryHelper = mock[ApiRetryHelper])
      ) {
        updatedRequest =>
          updatedRequest must be(testRequest)
      }
    }

  }
}
