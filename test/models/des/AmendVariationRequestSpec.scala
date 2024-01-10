/*
 * Copyright 2023 HM Revenue & Customs
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

package models.des

import models._
import models.des.aboutthebusiness.PreviouslyRegisteredMLRView
import models.des.businessactivities.{BusinessActivities, BusinessActivityDetails, ExpectedAMLSTurnover}
import models.des.msb.{CountriesList, MoneyServiceBusiness, MsbAllDetails}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import utils.AckRefGenerator

class AmendVariationRequestSpec extends PlaySpec with GuiceOneAppPerTest {

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder().configure(Map("microservice.services.feature-toggle.phase3-release2-la" -> false)).build()
  }

  implicit val ackref: AckRefGenerator = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  val release7BusinessActivities: BusinessActivities = DesConstants.testBusinessActivities.copy(
    all = Some(DesConstants.testBusinessActivitiesAll1.copy(
      businessActivityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£50k-£100k"))))
    ))
  )

  val businessActivitiesLA: BusinessActivities = DesConstants.testBusinessActivitiesLA.copy(
    all = Some(DesConstants.testBusinessActivitiesAll.copy(
      dateChangeFlag = Some(false),
      businessActivityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£50k-£100k"))))
    ))
  )

  val release7Msb: MoneyServiceBusiness = DesConstants.testMsb.copy(
    msbAllDetails = Some(MsbAllDetails(
      Some("£50k-£100k"),
      otherCntryBranchesOrAgents = true,
      Some(CountriesList(List("AD", "GB"))),
      sysLinkedTransIdentification = true)
    )
  )
  "Phase 2 toggle is on" when {
    "Trust or company formation agent" when {

      "convert frontend model to des model for amendment" in {
        implicit val mt: AmlsMessageType = Amendment
        implicit val requestType: RequestType = RequestType.Amendment
        AmendVariationRequest.convert(feSubscriptionReq.copy(
          tradingPremisesSection = TradingPremisesSection.tradingPremisesOnlyAgentModel)
        ) must be(
          convertedDesModelRelease7.copy(amlsMessageType = "Amendment", tradingPremises = DesConstants.tradingPremisesAPI6Release7)
        )
      }

      "convert frontend model to des model for variation" in {
        implicit val mt: AmlsMessageType = Variation
        implicit val requestType: RequestType = RequestType.Variation
        AmendVariationRequest.convert(feSubscriptionReq) must be(
          convertedDesModelRelease7.copy(amlsMessageType = "Variation")
        )
      }

      "convert frontend model with Letting Agent to des model with Letting Agent for variation" in {
        implicit val mt: AmlsMessageType = Variation
        implicit val requestType: RequestType = RequestType.Variation
        AmendVariationRequest.convert(feSubscriptionReqLA) must be(
          convertedDesModelLA.copy(amlsMessageType = "Variation")
        )
      }
    }

    "Not trust or company formation agent" when {
      "convert without tcspTrustCompFormationAgt for amendment" in {
        implicit val mt: AmlsMessageType = Amendment
        implicit val requestType: RequestType = RequestType.Amendment
        AmendVariationRequest.convert(feSubscriptionReqNoFormationAgt.copy(
          tradingPremisesSection = TradingPremisesSection.tradingPremisesOnlyAgentModel)
        ).tcspTrustCompFormationAgt must be(None)
      }

      "convert without tcspTrustCompFormationAgt for variation" in {
        implicit val mt: AmlsMessageType = Variation
        implicit val requestType: RequestType = RequestType.Variation
        AmendVariationRequest.convert(feSubscriptionReqNoFormationAgt).tcspTrustCompFormationAgt must be(None)
      }
    }
  }

  def feSubscriptionReq: fe.SubscriptionRequest = {
    import models.fe.SubscriptionRequest
    SubscriptionRequest(
      BusinessMatchingSection.modelForView,
      EabSection.modelForView,
      TradingPremisesSection.modelForView,
      AboutTheBusinessSection.modelForView,
      BankDetailsSection.modelForView,
      AboutYouSection.modelforView,
      BusinessActivitiesSection.modelForView,
      ResponsiblePeopleSection.modelForViewPhase2,
      ASPTCSPSection.TcspModelForView,
      ASPTCSPSection.AspModelForView,
      MsbSection.modelForView,
      HvdSection.modelForView,
      AmpSection.completeModel,
      SupervisionSection.modelForView
    )
  }

  def feSubscriptionReqLA: fe.SubscriptionRequest = feSubscriptionReq.copy(eabSection = EabSection.modelForViewLA)

  def feSubscriptionReqNoFormationAgt: fe.SubscriptionRequest = feSubscriptionReq.copy(tcspSection = ASPTCSPSection.TcspModelForViewNoCompanyFormationAgent)

  def convertedDesModelRelease7: AmendVariationRequest = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    changeIndicators = DesConstants.testChangeIndicators,
    amlsMessageType = "Amendment",
    businessDetails = DesConstants.testBusinessDetails,
    businessContactDetails = DesConstants.testViewBusinessContactDetails,
    businessReferencesAll = Some(PreviouslyRegisteredMLRView(amlsRegistered = false,
      None,
      prevRegForMlr = false,
      None)),
    businessReferencesAllButSp = Some(DesConstants.testbusinessReferencesAllButSp),
    businessReferencesCbUbLlp = Some(DesConstants.testBusinessReferencesCbUbLlp),
    businessActivities = release7BusinessActivities,
    tradingPremises = DesConstants.testTradingPremisesAPI6,
    bankAccountDetails = DesConstants.testBankDetails,
    msb = Some(release7Msb),
    hvd = Some(DesConstants.testHvd),
    asp = Some(DesConstants.testAsp),
    aspOrTcsp = Some(DesConstants.testAspOrTcsp1),
    tcspAll = Some(DesConstants.testTcspAll),
    tcspTrustCompFormationAgt = Some(DesConstants.testTcspTrustCompFormationAgt),
    eabAll = Some(DesConstants.testEabAll),
    eabResdEstAgncy = Some(DesConstants.testEabResdEstAgncy),
    responsiblePersons = Some(DesConstants.testResponsiblePersonsForRelease7RpAPI6Phase2),
    amp = Some(DesConstants.testAmp),
    lettingAgents = None,
    extraFields = DesConstants.extraFields
  )

  def convertedDesModelLA = convertedDesModelRelease7.copy(
    lettingAgents = Some(DesConstants.testLettingAgents),
    businessActivities = businessActivitiesLA
  )

  val newEtmpField: Option[EtmpFields] = Some(EtmpFields(Some("2016-09-17T09:30:47Z"),
    Some("2016-10-17T09:30:47Z"),
    Some("2016-11-17T09:30:47Z"),
    Some("2016-12-17T09:30:47Z")))

  val newChangeIndicator: ChangeIndicators = ChangeIndicators(businessDetails = true,
    businessAddress = true,
    businessReferences = true,
    tradingPremises = false,
    businessActivities = false)

  def newExtraFields: ExtraFields = ExtraFields(DesConstants.testDeclaration, DesConstants.testFilingIndividual, newEtmpField)

  def updateAmendVariationRequest(): AmendVariationRequest = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    newChangeIndicator,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    Some(PreviouslyRegisteredMLRView(amlsRegistered = false,
      None,
      prevRegForMlr = false,
      None)),
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRpAPI6),
    Some(DesConstants.testAmp),
    None,
    newExtraFields
  )
}
