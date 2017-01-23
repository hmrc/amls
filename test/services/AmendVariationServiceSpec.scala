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
import models.des.{AmendVariationRequest, DesConstants, ReadStatusResponse, StringOrInt}
import models.des.responsiblepeople.{MsbOrTcsp, RPExtra, ResponsiblePersons}
import models.des.tradingpremises.{Asp, _}
import org.joda.time.{LocalDate, LocalDateTime}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import repositories.FeeResponseRepository
import uk.gov.hmrc.play.http.HeaderCarrier
import utils.StatusConstants

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendVariationServiceSpec extends PlaySpec with MockitoSugar with ScalaFutures with IntegrationPatience {


  object TestAmendVariationService extends AmendVariationService {
    override private[services] val amendVariationDesConnector = mock[AmendVariationDESConnector]
    override private[services] val viewStatusDesConnector: SubscriptionStatusDESConnector = mock[SubscriptionStatusDESConnector]
    override private[services] val feeResponseRepository: FeeResponseRepository = mock[FeeResponseRepository]
    override private[services] val viewDesConnector: ViewDESConnector = mock[ViewDESConnector]
  }

  val response = des.AmendVariationResponse(
    processingDate = "2016-09-17T09:30:47Z",
    etmpFormBundleNumber = "111111",
    Some(1301737.96d),
    Some(231.42d),
    Some(870458d),
    Some(2172427.38),
    Some("string"),
    Some(12345.65d)
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
    unchangedExtra
  )

  val statusResponse = ReadStatusResponse(new LocalDateTime(), "Approved", None, None, Some(new LocalDate(2017, 4, 30)), false)
  val amlsRegistrationNumber = "XAAW00000567890"
  val amlsRegForHalfYears = "XAAW00000567891"

  implicit val hc = HeaderCarrier()

  "AmendVariationService" must {

    when {
      TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegistrationNumber))(any(), any())
    } thenReturn Future.successful(statusResponse)

    val premises: Option[AgentBusinessPremises] = Some(mock[AgentBusinessPremises])
    when(premises.get.agentDetails).thenReturn(None)


    "return a successful response" in {

      val request = mock[des.AmendVariationRequest]

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), premises)

      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))
      when(request.tradingPremises).thenReturn(tradingPremises)

      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any())
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      when(TestAmendVariationService.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

      whenReady(TestAmendVariationService.update(amlsRegistrationNumber, request)) {
        result =>
          result mustEqual response
      }
    }

    "return a successful response with added RPs" in {

      val request = mock[des.AmendVariationRequest]
      val responseWithFullYearRPsAndTPs = response.copy(addedResponsiblePeople = Some(1))
      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), premises)

      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons.copy(extra = addedExtra))))
      when(request.tradingPremises).thenReturn(tradingPremises)
      when(TestAmendVariationService.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegistrationNumber, request)) {
        result =>
          result mustEqual response.copy(addedResponsiblePeopleFitAndProper = Some(1))
      }

    }

    "return a successful response with 1 responsible person fit and proper" in {

      val request = mock[des.AmendVariationRequest]
      val responseWithFullYearRPsAndTPs = response.copy(addedResponsiblePeople = Some(1))
      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), premises)

      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons.copy(msbOrTcsp=Some(MsbOrTcsp(true)), extra = addedExtra))))

      when(request.tradingPremises).thenReturn(tradingPremises)

      when(TestAmendVariationService.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegistrationNumber, request)) {
        result =>
          result mustEqual response.copy(addedResponsiblePeopleFitAndProper = Some(1))
      }
    }

    "return a successful response with 1 responsible person with Msb or Tscp" in {

      val request = mock[des.AmendVariationRequest]
      val responseWithFullYearRPsAndTPs = response.copy(addedResponsiblePeople = Some(1))
      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), premises)

      println(">>")

      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons.copy(msbOrTcsp=Some(MsbOrTcsp(false)), extra = addedExtra))))

      when(request.tradingPremises).thenReturn(tradingPremises)

      when(TestAmendVariationService.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegistrationNumber, request)) {
        result =>
          result mustEqual response.copy(addedResponsiblePeople = Some(1))
      }
    }

    "return a successful response with added TPs" in {

      val ownBusinessPremisesDetails: OwnBusinessPremisesDetails = mock[OwnBusinessPremisesDetails]
      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(ownBusinessPremisesDetails)))), premises)
      val responseWithFullYearRPsAndTPs = response.copy(addedFullYearTradingPremises = Some(1))

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)

      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))

      when(TestAmendVariationService.feeResponseRepository.insert(any())).thenReturn(Future.successful(true))

      when {
        ownBusinessPremisesDetails.status
      } thenReturn Some("Added")
      when(ownBusinessPremisesDetails.startDate).thenReturn("2016-05-10")

      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegistrationNumber, request)) {
        result =>
          result mustEqual response.copy(addedFullYearTradingPremises = Some(1))
      }


    }

    "return a successful response with added TPs last full year day" in {

      val ownBusinessPremisesDetails: OwnBusinessPremisesDetails = mock[OwnBusinessPremisesDetails]
      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(ownBusinessPremisesDetails)))), premises)
      val responseWithFullYearRPsAndTPs = response.copy(addedFullYearTradingPremises = Some(1))

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)

      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))

      when {
        ownBusinessPremisesDetails.status
      } thenReturn Some("Added")
      when(ownBusinessPremisesDetails.startDate).thenReturn("2016-10-31")

      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegistrationNumber, request)) {
        result =>
          result mustEqual response.copy(addedFullYearTradingPremises = Some(1))
      }


    }

    "return a successful response with added agent TPs" in {

      val ownBusinessPremisesDetails: OwnBusinessPremisesDetails = mock[OwnBusinessPremisesDetails]
      val agentPremises = mock[AgentPremises]
      when(agentPremises.startDate).thenReturn("2016-02-02")
      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), Some(AgentBusinessPremises(true, Some(Seq(AgentDetails("", None, agentPremises, Some("Added"), None))))))
      val responseWithFullYearRPsAndTPs = response.copy(addedFullYearTradingPremises = Some(1))

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)

      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))

      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegistrationNumber), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegistrationNumber, request)) {
        result =>
          result mustEqual response.copy(addedFullYearTradingPremises = Some(1))
      }


    }

    "return a successful response with 1 added half-year TP" in {

      when {
        TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegForHalfYears))(any(), any())
      } thenReturn Future.successful(statusResponse.copy(currentRegYearEndDate = Some(new LocalDate(2016, 11, 11))))

      val ownBusinessPremisesDetails: OwnBusinessPremisesDetails = mock[OwnBusinessPremisesDetails]
      when(ownBusinessPremisesDetails.startDate).thenReturn("2016-08-10")
      when {
        ownBusinessPremisesDetails.status
      } thenReturn Some("Added")

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(ownBusinessPremisesDetails)))), premises)
      val responseWithFullYearRPsAndTPs = response.copy(addedFullYearTradingPremises = Some(1))

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)
      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))


      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegForHalfYears), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegForHalfYears, request)) {
        result =>
          result mustEqual response.copy(halfYearlyTradingPremises = Some(1))
      }


    }

    "return a successful response with 1 added half-year TP on first eligible day" in {

      when {
        TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegForHalfYears))(any(), any())
      } thenReturn Future.successful(statusResponse.copy(currentRegYearEndDate = Some(new LocalDate(2017, 4, 30))))

      val ownBusinessPremisesDetails: OwnBusinessPremisesDetails = mock[OwnBusinessPremisesDetails]
      when(ownBusinessPremisesDetails.startDate).thenReturn("2016-11-01")
      when {
        ownBusinessPremisesDetails.status
      } thenReturn Some("Added")

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(ownBusinessPremisesDetails)))), premises)
      val responseWithFullYearRPsAndTPs = response.copy(addedFullYearTradingPremises = Some(1))

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)
      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))


      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegForHalfYears), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegForHalfYears, request)) {
        result =>
          result mustEqual response.copy(halfYearlyTradingPremises = Some(1))
      }


    }

    "return a successful response with 1 added half-year TP on last eligible day" in {

      when {
        TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegForHalfYears))(any(), any())
      } thenReturn Future.successful(statusResponse.copy(currentRegYearEndDate = Some(new LocalDate(2017, 4, 30))))

      val ownBusinessPremisesDetails: OwnBusinessPremisesDetails = mock[OwnBusinessPremisesDetails]
      when(ownBusinessPremisesDetails.startDate).thenReturn("2017-03-31")
      when {
        ownBusinessPremisesDetails.status
      } thenReturn Some("Added")

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(ownBusinessPremisesDetails)))), premises)
      val responseWithFullYearRPsAndTPs = response.copy(addedFullYearTradingPremises = Some(1))

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)
      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))


      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegForHalfYears), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegForHalfYears, request)) {
        result =>
          result mustEqual response.copy(halfYearlyTradingPremises = Some(1))
      }


    }

    "return a successful response with 1 added half-year agent TP" in {

      when {
        TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegForHalfYears))(any(), any())
      } thenReturn Future.successful(statusResponse.copy(currentRegYearEndDate = Some(new LocalDate(2016, 11, 11))))

      val agentPremises = mock[AgentPremises]

      when(agentPremises.startDate).thenReturn("2016-08-10")

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), Some(AgentBusinessPremises(true, Some(Seq(AgentDetails("", None, agentPremises, Some("Added"), None))))))



      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)
      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))


      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegForHalfYears), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegForHalfYears, request)) {
        result =>
          result mustEqual response.copy(halfYearlyTradingPremises = Some(1))
      }


    }

    "return a successful response with 1 added zero rated TP" in {

      when {
        TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegForHalfYears))(any(), any())
      } thenReturn Future.successful(statusResponse.copy(currentRegYearEndDate = Some(new LocalDate(2016, 11, 11))))

      val ownBusinessPremisesDetails: OwnBusinessPremisesDetails = mock[OwnBusinessPremisesDetails]
      when(ownBusinessPremisesDetails.startDate).thenReturn("2016-11-01")
      when {
        ownBusinessPremisesDetails.status
      } thenReturn Some("Added")

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(ownBusinessPremisesDetails)))), premises)
      val responseWithZeroRatedTPs = response.copy(zeroRatedTradingPremises = Some(1))

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)
      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))


      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegForHalfYears), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegForHalfYears, request)) {
        result =>
          result mustEqual responseWithZeroRatedTPs
      }


    }

    "return a successful response with 1 added zero rated TP on first eligble day" in {

      when {
        TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegForHalfYears))(any(), any())
      } thenReturn Future.successful(statusResponse.copy(currentRegYearEndDate = Some(new LocalDate(2017, 4, 30))))

      val ownBusinessPremisesDetails: OwnBusinessPremisesDetails = mock[OwnBusinessPremisesDetails]
      when(ownBusinessPremisesDetails.startDate).thenReturn("2017-04-01")
      when {
        ownBusinessPremisesDetails.status
      } thenReturn Some("Added")

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(ownBusinessPremisesDetails)))), premises)
      val responseWithZeroRatedTPs = response.copy(zeroRatedTradingPremises = Some(1))

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)
      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))


      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegForHalfYears), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegForHalfYears, request)) {
        result =>
          result mustEqual responseWithZeroRatedTPs
      }


    }

    "return a successful response with 1 added zero rated agent TP" in {

      when {
        TestAmendVariationService.viewStatusDesConnector.status(eqTo(amlsRegForHalfYears))(any(), any())
      } thenReturn Future.successful(statusResponse.copy(currentRegYearEndDate = Some(new LocalDate(2016, 11, 11))))

      val agentPremises = mock[AgentPremises]

      when(agentPremises.startDate).thenReturn("2016-11-01")

      val tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, None)), Some(AgentBusinessPremises(true, Some(Seq(AgentDetails("", None, agentPremises, Some("Added"), None))))))

      val responseWithZeroRatedTPs = response.copy(zeroRatedTradingPremises = Some(1))

      val request = mock[des.AmendVariationRequest]

      when(request.tradingPremises).thenReturn(tradingPremises)
      when(request.responsiblePersons).thenReturn(Some(Seq(unchangedResponsiblePersons)))


      when {
        TestAmendVariationService.amendVariationDesConnector.amend(eqTo(amlsRegForHalfYears), eqTo(request))(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(TestAmendVariationService.update(amlsRegForHalfYears, request)) {
        result =>
          result mustEqual responseWithZeroRatedTPs
      }

    }

    "successfully evaluate isBusinessReferenceChanged when api5 data is same as api6 " in {
      TestAmendVariationService.isBusinessReferenceChanged(DesConstants.SubscriptionViewModelForRp, DesConstants.AmendVariationRequestModel) must be(false)

    }

    "successfully compare and update api6 request with api5 data" when {
      "user has deleted a record, added one new record and has not changed one record of responsible people" in {
        val viewModel = DesConstants.SubscriptionViewStatusRP

        val testRequest = DesConstants.amendStatusAmendVariationRP.copy(
          businessActivities = DesConstants.testBusinessActivities.copy(
            all = Some(DesConstants.testBusinessActivitiesAll.copy(
              DateChangeFlag = Some(false)
            ))
          ),
          aspOrTcsp = Some(DesConstants.testAspOrTcsp.copy(
            supervisionDetails = Some(DesConstants.testSupervisionDetails.copy(
              supervisorDetails = Some(DesConstants.testSupervisorDetails.copy(
                dateChangeFlag = Some(false)
              ))
            ))
          )))

        when {
          TestAmendVariationService.viewDesConnector.view(eqTo(amlsRegistrationNumber))(any())
        } thenReturn Future.successful(viewModel)


        whenReady(TestAmendVariationService.compareAndUpdate(DesConstants.updateAmendVariationRequestRP, amlsRegistrationNumber)) {
          updatedRequest =>
            updatedRequest must be(testRequest)
        }
      }
    }

    "successfully compare and update api6 request with api5 data" when {
      "user has deleted a record, added a new record, modified one record and not changed one record of trading premises" in {
        val viewModel = DesConstants.SubscriptionViewStatusTP

        val testRequest = DesConstants.amendStatusAmendVariationTP.copy(
          businessActivities = DesConstants.testBusinessActivities.copy(
            all = Some(DesConstants.testBusinessActivitiesAll.copy(
              DateChangeFlag = Some(false)
            ))
          ),
          aspOrTcsp = Some(DesConstants.testAspOrTcsp.copy(
            supervisionDetails = Some(DesConstants.testSupervisionDetails.copy(
              supervisorDetails = Some(DesConstants.testSupervisorDetails.copy(
                dateChangeFlag = Some(false)
              ))
            ))
          ))
        )

        when {
          TestAmendVariationService.viewDesConnector.view(eqTo(amlsRegistrationNumber))(any())
        } thenReturn Future.successful(viewModel)

        whenReady(TestAmendVariationService.compareAndUpdate(DesConstants.amendStatusDesVariationRequestTP, amlsRegistrationNumber)) {
          updatedRequest =>
            updatedRequest must be(testRequest)
        }
      }
    }

    "successfully compare and update api6 request with api5 1" in {
      val viewModel = DesConstants.SubscriptionViewModelAPI5
      when {
        TestAmendVariationService.viewDesConnector.view(eqTo(amlsRegistrationNumber))(any())
      } thenReturn Future.successful(viewModel)

      whenReady(TestAmendVariationService.compareAndUpdate(DesConstants.amendVariationRequest1, amlsRegistrationNumber)) {
        updatedRequest =>
          updatedRequest must be(DesConstants.updateAmendVariationCompleteRequest1)
      }
    }

    "successfully update own business start date changed flag when start date is amended" in {
      val viewModel = DesConstants.SubscriptionViewModelAPI5

      val data = OwnBusinessPremisesDetails(
        "OwnBusinessTradingName",
        Address("OwnBusinessAddressLine1",
          "OwnBusinessAddressLine2",
          Some("OwnBusinessAddressLine3"),
          Some("OwnBusinessAddressLine4"),
          "GB",
          Some("YY1 1YY")),
        false,
        Msb(false, false, false, false, false),
        Hvd(false),
        Asp(false),
        Tcsp(true),
        Eab(true),
        Bpsp(true),
        Tditpsp(false),
        "2001-05-01",
        None,
        Some(StringOrInt(444444)),
        Some(StatusConstants.Unchanged),
        None,
        None,
        None
      )

      val expectedData = data.copy(dateChangeFlag = Some(true), status = Some(StatusConstants.Updated))

      when {
        TestAmendVariationService.viewDesConnector.view(eqTo(amlsRegistrationNumber))(any())
      } thenReturn Future.successful(viewModel)

      val modelWithchangedStartDate = DesConstants.updateAmendVariationCompleteRequest1.copy(
        tradingPremises = DesConstants.testAmendTradingPremisesAPI6.copy(
          ownBusinessPremises = Some(OwnBusinessPremises(true, Some(Seq(data))))))

      whenReady(TestAmendVariationService.compareAndUpdate(modelWithchangedStartDate, amlsRegistrationNumber)) {
        updatedRequest =>
          updatedRequest.tradingPremises.ownBusinessPremises must be(Some(OwnBusinessPremises(true, Some(Seq(expectedData)))))
      }

    }

    "successfully update agent premises start date changed flag when start date is amended" in {
      val viewModel = DesConstants.SubscriptionViewModelAPI5

      val agentPremisesData = AgentPremises("string",
        des.tradingpremises.Address("string", "string", Some("string"), Some("string"), "GB", Some("string")), true,
        Msb(false, false, false, false, false),
        models.des.tradingpremises.Hvd(false),
        Asp(false),
        Tcsp(false),
        Eab(true),
        Bpsp(true),
        Tditpsp(false),
        "2008-01-01",
        None,
        None,
        None)

      val agentDetailsData = AgentDetails("Sole Proprietor", Some("entity name"), agentPremisesData, Some(StatusConstants.Unchanged), Some(StringOrInt(111111)), None)

      val agentPremisesExpectedData = agentPremisesData.copy(dateChangeFlag = Some(true))
      val agentDetailsExpectedData = agentDetailsData.copy(status = Some(StatusConstants.Updated), agentPremises = agentPremisesExpectedData)

      when {
        TestAmendVariationService.viewDesConnector.view(eqTo(amlsRegistrationNumber))(any())
      } thenReturn Future.successful(viewModel)

      val modelWithChangedStartDate = DesConstants.updateAmendVariationCompleteRequest1.copy(
        tradingPremises = DesConstants.testAmendTradingPremisesAPI6.copy(
          agentBusinessPremises = Some(AgentBusinessPremises(true, Some(Seq(agentDetailsData))))))

      whenReady(TestAmendVariationService.compareAndUpdate(modelWithChangedStartDate, amlsRegistrationNumber)) {
        updatedRequest =>
          updatedRequest.tradingPremises.agentBusinessPremises must be(Some(AgentBusinessPremises(true, Some(Seq(agentDetailsExpectedData)))))
      }



    }
  }
}
