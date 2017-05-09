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

import connectors.{AmendVariationDESConnector, SubscriptionStatusDESConnector, ViewDESConnector}
import models.des
import models.des.responsiblepeople.{MsbOrTcsp, RPExtra, ResponsiblePersons}
import models.des.tradingpremises._
import models.des.{AmendVariationRequest, DesConstants, ReadStatusResponse}
import org.joda.time.{LocalDate, LocalDateTime}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, OneServerPerSuite, PlaySpec}
import play.api.{Application, Mode}
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import play.api.libs.json.{JsResult, JsValue}
import play.api.test.FakeApplication
import repositories.FeeResponseRepository
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendVariationServiceSpec extends PlaySpec with OneAppPerSuite with MockitoSugar with ScalaFutures with IntegrationPatience {

  val successValidate:JsResult[JsValue] = mock[JsResult[JsValue]]

  object TestAmendVariationService extends AmendVariationService {
    override private[services] val amendVariationDesConnector = mock[AmendVariationDESConnector]
    override private[services] val viewStatusDesConnector: SubscriptionStatusDESConnector = mock[SubscriptionStatusDESConnector]
    override private[services] val feeResponseRepository: FeeResponseRepository = mock[FeeResponseRepository]
    override private[services] val viewDesConnector: ViewDESConnector = mock[ViewDESConnector]

    override private[services] def validateResult(request: AmendVariationRequest) = successValidate
  }

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
    Some(3456.12)
  )

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
    unchangedExtra
  )

  val statusResponse = ReadStatusResponse(new LocalDateTime(), "Approved", None, None, None, Some(new LocalDate(2017, 4, 30)), false)
  val amlsRegistrationNumber = "XAAW00000567890"
  val amlsRegForHalfYears = "XAAW00000567891"

  implicit val hc = HeaderCarrier()

  "AmendVariationService" must {

    when{successValidate.isSuccess} thenReturn true

    when {
      TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegistrationNumber))(any(), any(), any())
    } thenReturn Future.successful(statusResponse)

    val premises: Option[AgentBusinessPremises] = Some(mock[AgentBusinessPremises])
    when(premises.get.agentDetails).thenReturn(None)


    "return a successful response" in {

      val request = mock[des.AmendVariationRequest]

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), premises)

      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))
      when(request.tradingPremises).thenReturn(tradingPremises)

      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any(), any())
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any(), any())
      } thenReturn Future.successful(response)

      when(TestAmendVariationService.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

      whenReady(TestAmendVariationService.update(amlsRegistrationNumber, request)) {
        result =>
          result mustEqual models.fe.AmendVariationResponse.convert(response)
      }
    }

    "return a successful response with 1 added zero rated agent TP" in {

      when {
        TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegForHalfYears))(any(), any(), any())
      } thenReturn Future.successful(statusResponse.copy(currentRegYearEndDate = Some(new LocalDate(2016, 11, 11))))

      val agentPremises = mock[AgentPremises]

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), Some(AgentBusinessPremises(true, Some(Seq(AgentDetails("", None, None, None,
        agentPremises, Some("2016-11-01"), None, None, Some("Added"), None))))))

      val responseWithZeroRatedTPs = models.fe.AmendVariationResponse.convert(response).copy(zeroRatedTradingPremises = 1,addedFullYearTradingPremises = 2)

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)
      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))


      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegForHalfYears), eqTo(request))(any(), any(), any(), any())
      } thenReturn Future.successful(response.copy(premiseFYNumber = Some(2)))

      whenReady(TestAmendVariationService.update(amlsRegForHalfYears, request)) {
        result =>
          result mustEqual responseWithZeroRatedTPs
      }

    }

    "evaluate isBusinessReferenceChanged when api5 data is same as api6 " in {
      TestAmendVariationService.isBusinessReferenceChanged(DesConstants.AmendVariationRequestModel, DesConstants.SubscriptionViewModelForRp) must be(false)
    }

    "compare and update api6 request with api5 1" in {

      val viewModel = DesConstants.SubscriptionViewModelAPI5

      when {
        TestAmendVariationService.viewDesConnector.view(eqTo(amlsRegistrationNumber))(any(), any())
      } thenReturn Future.successful(viewModel)

      val testRequest = DesConstants.updateAmendVariationCompleteRequest1.copy(
        tradingPremises = DesConstants.testAmendTradingPremisesAPI6.copy(
          DesConstants.ownBusinessPremisesTPR7
        )
      )

      whenReady(TestAmendVariationService.compareAndUpdate(DesConstants.amendVariationRequest1, amlsRegistrationNumber)) {
        updatedRequest =>
          updatedRequest must be(testRequest)
      }
    }

  }
}
