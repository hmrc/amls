/*
 * Copyright 2020 HM Revenue & Customs
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
import models.des.businessactivities.{BusinessActivityDetails, ExpectedAMLSTurnover}
import models.des.msb.{CountriesList, MsbAllDetails}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import utils.AckRefGenerator

class AmendVariationRequestSpec extends PlaySpec with OneAppPerSuite {

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  val release7BusinessActivities = DesConstants.testBusinessActivities.copy(
    all = Some(DesConstants.testBusinessActivitiesAll.copy(
      businessActivityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£50k-£100k"))))
    ))
  )

  val release7Msb = DesConstants.testMsb.copy(
    msbAllDetails = Some(MsbAllDetails(
      Some("£50k-£100k"),
      true,
      Some(CountriesList(List("AD", "GB"))),
      true)
    )
  )
  "Phase 2 toggle is on" when {
    "Trust or company formation agent" when {
      "convert frontend model to des model for amendment" in {
        implicit val mt = Amendment
        implicit val requestType = RequestType.Amendment
        AmendVariationRequest.convert(feSubscriptionReq.copy(
          tradingPremisesSection = TradingPremisesSection.tradingPremisesOnlyAgentModel)
        ) must be(
          convertedDesModelRelease7.copy(amlsMessageType = "Amendment", tradingPremises = DesConstants.tradingPremisesAPI6Release7)
        )
      }

      "convert frontend model to des model for variation" in {
        implicit val mt = Variation
        implicit val requestType = RequestType.Variation
        AmendVariationRequest.convert(feSubscriptionReq) must be(
          convertedDesModelRelease7.copy(amlsMessageType = "Variation")
        )
      }
    }

    "Not trust or company formation agent" when {
      "convert without tcspTrustCompFormationAgt for amendment" in {
        implicit val mt = Amendment
        implicit val requestType = RequestType.Amendment
        AmendVariationRequest.convert(feSubscriptionReqNoFormationAgt.copy(
          tradingPremisesSection = TradingPremisesSection.tradingPremisesOnlyAgentModel)
        ).tcspTrustCompFormationAgt must be(None)
      }

      "convert without tcspTrustCompFormationAgt for variation" in {
        implicit val mt = Variation
        implicit val requestType = RequestType.Variation
        AmendVariationRequest.convert(feSubscriptionReqNoFormationAgt).tcspTrustCompFormationAgt must be(None)
      }
    }
  }
  def feSubscriptionReq = {
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

  def feSubscriptionReqNoFormationAgt = feSubscriptionReq.copy(tcspSection = ASPTCSPSection.TcspModelForViewNoCompanyFormationAgent)

  def convertedDesModel = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    DesConstants.testChangeIndicators,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    Some(PreviouslyRegisteredMLRView(false,
      None,
      false,
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
    Some(DesConstants.testResponsiblePersonsForRpAPI6Phase2),
    Some(DesConstants.testAmp),
    DesConstants.extraFields
  )


  def convertedDesModelRelease7 = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    DesConstants.testChangeIndicators,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    Some(PreviouslyRegisteredMLRView(false,
      None,
      false,
      None)),
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    release7BusinessActivities,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(release7Msb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRelease7RpAPI6Phase2),
    Some(DesConstants.testAmp),
    DesConstants.extraFields
  )

  val newEtmpField = Some(EtmpFields(Some("2016-09-17T09:30:47Z"), Some("2016-10-17T09:30:47Z"), Some("2016-11-17T09:30:47Z"), Some("2016-12-17T09:30:47Z")))
  val newChangeIndicator = ChangeIndicators(true, true, true, false, false)
  def newExtraFields = ExtraFields(DesConstants.testDeclaration, DesConstants.testFilingIndividual, newEtmpField)

  def updateAmendVariationRequest = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    newChangeIndicator,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    Some(PreviouslyRegisteredMLRView(false,
      None,
      false,
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
    newExtraFields
  )
}
