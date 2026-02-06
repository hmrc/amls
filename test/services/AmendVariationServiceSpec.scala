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

import com.google.inject.Inject
import config.ApplicationConfig
import connectors.{AmendVariationDESConnector, SubscriptionStatusDESConnector, ViewDESConnector}
import generators.AmlsReferenceNumberGenerator
import models.des
import models.des.responsiblepeople.{RPExtra, ResponsiblePersons}
import models.des.tradingpremises._
import models.des.{AmendVariationRequest, DesConstants, ReadStatusResponse}
import models.fe.AmendVariationResponse
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsResult, JsValue}
import repositories.FeesRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.{AmendVariationValidator, ApiRetryHelper}

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.{ExecutionContext, Future}

class AmendVariationServiceSpec @Inject() (implicit val ec: ExecutionContext)
    extends PlaySpec
    with GuiceOneAppPerSuite
    with ScalaFutures
    with IntegrationPatience
    with AmlsReferenceNumberGenerator {

  val successValidate: JsResult[JsValue] = mock(classOf[JsResult[JsValue]])

  val feAmendVariationResponse: AmendVariationResponse = AmendVariationResponse(
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

  val feeRepo: FeesRepository = mock(classOf[FeesRepository])

  class TestAmendVariationService
      extends AmendVariationService(
        mock(classOf[AmendVariationDESConnector]),
        mock(classOf[SubscriptionStatusDESConnector]),
        mock(classOf[ViewDESConnector]),
        mock(classOf[AuditConnector]),
        mock(classOf[AmendVariationValidator]),
        feeRepo,
        mock(classOf[ApplicationConfig])
      ) {
    def validateResult: JsResult[JsValue] = mock(classOf[JsResult[JsValue]])

    override private[services] def amendVariationResponse(
      request: AmendVariationRequest,
      isRenewalWindow: Boolean,
      des: models.des.AmendVariationResponse
    ) = feAmendVariationResponse
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

  val statusResponse =
    ReadStatusResponse(LocalDateTime.now, "Approved", None, None, None, Some(LocalDate.of(2017, 4, 30)), false)

  val unchangedExtra: RPExtra = RPExtra(status = Some("Unchanged"))
  val addedExtra: RPExtra     = RPExtra(status = Some("Added"))

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
    Some(false),
    None,
    Some(false),
    None,
    extra = unchangedExtra
  )

  val amlsRegForHalfYears: String = amlsRefNoGen.sample.get

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "AmendVariationService" must {
    val request = mock(classOf[des.AmendVariationRequest])

    when {
      avs.amendVariationValidator.validateResult(request)
    } thenReturn Right(request)

    when {
      avs.viewStatusDesConnector.status(ArgumentMatchers.eq(amlsRegistrationNumber))(any(), any(), any(), any())
    } thenReturn Future.successful(statusResponse)

    val premises: Option[AgentBusinessPremises] = Some(mock(classOf[AgentBusinessPremises]))

    when {
      premises.get.agentDetails
    } thenReturn None

    "return a successful response" in {
      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), premises)

      when {
        request.responsiblePersons
      } thenReturn Some(Seq(unchangedResponsiblePersons))

      when {
        request.tradingPremises
      } thenReturn tradingPremises

      when {
        avs.amendVariationDesConnector.amend(ArgumentMatchers.eq(amlsRegistrationNumber), ArgumentMatchers.eq(request))(
          any(),
          any(),
          any(),
          any(),
          any()
        )
        avs.amendVariationDesConnector.amend(ArgumentMatchers.eq(amlsRegistrationNumber), ArgumentMatchers.eq(request))(
          any(),
          any(),
          any(),
          any(),
          any()
        )
      } thenReturn Future.successful(response)

      when {
        avs.feeResponseRepository.insert(any())
      } thenReturn Future.successful(true)

      whenReady(
        avs.update(amlsRegistrationNumber, request)(hc, ec, apiRetryHelper = mock(classOf[ApiRetryHelper]))
      ) { result =>
        result mustEqual feAmendVariationResponse
      }
    }

    "evaluate isBusinessReferenceChanged when api5 data is same as api6 " in {
      avs.isBusinessReferenceChanged(
        DesConstants.AmendVariationRequestModel,
        DesConstants.SubscriptionViewModelForRp
      ) must be(false)
    }

    "compare and update api6 request with api5 1" in {

      val viewModel = DesConstants.SubscriptionViewModelAPI5

      when {
        avs.viewDesConnector.view(ArgumentMatchers.eq(amlsRegistrationNumber))(any(), any(), any())
      } thenReturn Future.successful(viewModel)

      val testRequest = DesConstants.updateAmendVariationCompleteRequest1.copy(
        tradingPremises = DesConstants.testAmendTradingPremisesAPI6.copy(
          DesConstants.ownBusinessPremisesTPR7
        )
      )

      whenReady(
        avs.compareAndUpdate(DesConstants.amendVariationRequest1, amlsRegistrationNumber)(
          hc,
          apiRetryHelper = mock(classOf[ApiRetryHelper])
        )
      ) { updatedRequest =>
        updatedRequest must be(testRequest)
      }
    }
  }
}
