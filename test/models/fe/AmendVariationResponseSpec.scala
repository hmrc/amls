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

package models.fe

import models.des._
import models.des.aboutyou.AboutYouRelease7
import models.des.tradingpremises._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.FakeApplication;

class AmendVariationResponseSpec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.phase-2-changes" -> false))

  val amendvariationRequest = AmendVariationRequest(
    acknowledgementReference = "ackRef",
    changeIndicators = ChangeIndicators(
      tradingPremises = true
    ),
    amlsMessageType = "msgType",
    businessDetails = DesConstants.testBusinessDetails,
    businessContactDetails = DesConstants.testViewBusinessContactDetails,
    businessReferencesAll = None,
    businessReferencesAllButSp = None,
    businessReferencesCbUbLlp = None,
    businessActivities = DesConstants.testBusinessActivities,
    tradingPremises = TradingPremises(
      ownBusinessPremises = Some(OwnBusinessPremises(
        ownBusinessPremises = true,
        ownBusinessPremisesDetails = Some(Seq(
          OwnBusinessPremisesDetails(
            tradingName = Some("COMPANY NAME 925"),
            businessAddress = Address(
              addressLine1 = "M House 0002",
              addressLine2 = "Grange 0002",
              addressLine3 = Some("Telford 0002"),
              addressLine4 = Some("Shropshire"),
              country = "GB",
              postcode = Some("TF3 4ER")
            ),
            residential = false,
            msb = Msb(false, true, false, false, false),
            hvd = Hvd(false),
            asp = Asp(false),
            tcsp = Tcsp(false),
            eab = Eab(false),
            bpsp = Bpsp(false),
            tditpsp = Tditpsp(false),
            startDate = "2001-01-01",
            endDate = Some("9999-12-31"),
            lineId = Some("000001"),
            status = Some("Updated"),
            dateChangeFlag = Some(false)
          ),
          OwnBusinessPremisesDetails(
            tradingName = Some("trade3"),
            businessAddress = Address(
              addressLine1 = "add3",
              addressLine2 = "add3d",
              addressLine3 = None,
              addressLine4 = None,
              country = "GB",
              postcode = Some("e3 4rg")
            ),
            residential = false,
            msb = Msb(true, false, false, false, false),
            hvd = Hvd(false),
            asp = Asp(false),
            tcsp = Tcsp(false),
            eab = Eab(false),
            bpsp = Bpsp(false),
            tditpsp = Tditpsp(false),
            startDate = "2018-02-01",
            endDate = Some("9999-12-31"),
            status = Some("Added"),
            dateChangeFlag = Some(false)
          )
        ))
      )),
      agentBusinessPremises = Some(AgentBusinessPremises(
        agentBusinessPremises = true,
        agentDetails = Some(Seq(AgentDetails(
          agentLegalEntity = "Partnership",
          agentLegalEntityName = Some("test partner"),
          dateOfBirth = None,
          agentPremises = AgentPremises(
            tradingName = "trade2",
            businessAddress = Address(
              addressLine1 = "add2",
              addressLine2 = "add2",
              addressLine3 = None,
              addressLine4 = None,
              country = "GB",
              postcode = Some("gr4 5th")
            ),
            residential = false,
            msb = Msb(false,true,false,false,false),
            hvd = Hvd(false),
            asp = Asp(false),
            tcsp = Tcsp(false),
            eab = Eab(false),
            bpsp = Bpsp(false),
            tditpsp = Tditpsp(false),
            startDate = None
          ),
          startDate = Some("2017-01-12"),
          dateChangeFlag = Some(false),
          status = Some("Added")
        )))
      ))
    ),
    bankAccountDetails = None,
    msb = None,
    hvd = None,
    asp = None,
    aspOrTcsp = None,
    tcspAll = None,
    tcspTrustCompFormationAgt = None,
    eabAll = None,
    eabResdEstAgncy = None,
    responsiblePersons = None,
    extraFields = ExtraFields(
      declaration = Declaration(true),
      filingIndividual = AboutYouRelease7(None, true, None, None),
      etmpFields = None
    )
  )

  "AmendVariationResponse for renewalAmendment" must {
    "convert a DES response to a AmendVariationResponse" in {

      val fpFee = 100
      val fpFeeRate = 100
      val premiseFYFeeRate = 115
      val premiseFee = 345
      val totalFees = 445
      val difference = 330
      val premiseHYFeeRate = 57.5

      val paymentReference = "XY002610108134"
      val etmpFormBundleNumber = "082000004607"
      val processingDate = "2017-07-18T09:49:25Z"

      val testApprovalNumbers = Some(10)
      val testApprovalFeeRate = Some(BigDecimal(20))
      val testApprovalCheckFee = Some(BigDecimal(30))


      AmendVariationResponse.convert(amendvariationRequest, true, models.des.AmendVariationResponse(
        processingDate = processingDate,
        etmpFormBundleNumber = etmpFormBundleNumber,
        fpNumbers = Some(1),
        fpFeeRate = Some(fpFeeRate),
        fpFee = Some(fpFee),
        responsiblePersonNotCharged = Some(1),
        premiseFYNumber = Some(3),
        premiseFYFeeRate = Some(premiseFYFeeRate),
        premiseHYFeeRate = Some(premiseHYFeeRate),
        premiseFee = Some(premiseFee),
        totalFees = Some(totalFees),
        paymentReference = Some(paymentReference),
        difference = Some(difference),
        registrationFee = None,
        premiseFYTotal = None,
        premiseHYNumber = None,
        premiseHYTotal = None,
        approvalNumbers = testApprovalNumbers,
        approvalCheckFeeRate = testApprovalFeeRate,
        approvalCheckFee = testApprovalCheckFee

      )) mustBe AmendVariationResponse(
        processingDate = processingDate,
        etmpFormBundleNumber = etmpFormBundleNumber,
        registrationFee = 0,
        fpFee = Some(fpFee),
        fpFeeRate = Some(fpFeeRate),
        premiseFee = premiseFee,
        premiseFeeRate = Some(premiseFYFeeRate),
        totalFees = totalFees,
        paymentReference = Some(paymentReference),
        difference = Some(difference),
        addedResponsiblePeople = 1,
        addedFullYearTradingPremises = 3,
        addedResponsiblePeopleFitAndProper = 1,
        approvalNumbers = testApprovalNumbers,
        approvalCheckFeeRate = testApprovalFeeRate,
        approvalCheckFee = testApprovalCheckFee
      )
    }
  }
}

class AmendVariationResponsePhase2Spec extends PlaySpec with OneAppPerSuite with MockitoSugar {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.phase-2-changes" -> true))

    val amendvariationRequest = AmendVariationRequest(
      acknowledgementReference = "ackRef",
      changeIndicators = ChangeIndicators(
        tradingPremises = true
      ),
      amlsMessageType = "msgType",
      businessDetails = DesConstants.testBusinessDetails,
      businessContactDetails = DesConstants.testViewBusinessContactDetails,
      businessReferencesAll = None,
      businessReferencesAllButSp = None,
      businessReferencesCbUbLlp = None,
      businessActivities = DesConstants.testBusinessActivities,
      tradingPremises = TradingPremises(
        ownBusinessPremises = Some(OwnBusinessPremises(
          ownBusinessPremises = true,
          ownBusinessPremisesDetails = Some(Seq(
            OwnBusinessPremisesDetails(
            tradingName = Some("COMPANY NAME 925"),
            businessAddress = Address(
              addressLine1 = "M House 0002",
              addressLine2 = "Grange 0002",
              addressLine3 = Some("Telford 0002"),
              addressLine4 = Some("Shropshire"),
              country = "GB",
              postcode = Some("TF3 4ER")
            ),
            residential = false,
            msb = Msb(false, true, false, false, false),
            hvd = Hvd(false),
            asp = Asp(false),
            tcsp = Tcsp(false),
            eab = Eab(false),
            bpsp = Bpsp(false),
            tditpsp = Tditpsp(false),
            startDate = "2001-01-01",
            endDate = Some("9999-12-31"),
            lineId = Some("000001"),
            status = Some("Updated"),
            dateChangeFlag = Some(false)
          ),
            OwnBusinessPremisesDetails(
              tradingName = Some("trade3"),
              businessAddress = Address(
                addressLine1 = "add3",
                addressLine2 = "add3d",
                addressLine3 = None,
                addressLine4 = None,
                country = "GB",
                postcode = Some("e3 4rg")
              ),
              residential = false,
              msb = Msb(true, false, false, false, false),
              hvd = Hvd(false),
              asp = Asp(false),
              tcsp = Tcsp(false),
              eab = Eab(false),
              bpsp = Bpsp(false),
              tditpsp = Tditpsp(false),
              startDate = "2018-02-01",
              endDate = Some("9999-12-31"),
              status = Some("Added"),
              dateChangeFlag = Some(false)
            )
          ))
        )),
        agentBusinessPremises = Some(AgentBusinessPremises(
          agentBusinessPremises = true,
          agentDetails = Some(Seq(AgentDetails(
            agentLegalEntity = "Partnership",
            agentLegalEntityName = Some("test partner"),
            dateOfBirth = None,
            agentPremises = AgentPremises(
              tradingName = "trade2",
              businessAddress = Address(
                addressLine1 = "add2",
                addressLine2 = "add2",
                addressLine3 = None,
                addressLine4 = None,
                country = "GB",
                postcode = Some("gr4 5th")
              ),
              residential = false,
              msb = Msb(false,true,false,false,false),
              hvd = Hvd(false),
              asp = Asp(false),
              tcsp = Tcsp(false),
              eab = Eab(false),
              bpsp = Bpsp(false),
              tditpsp = Tditpsp(false),
              startDate = None
            ),
            startDate = Some("2017-01-12"),
            dateChangeFlag = Some(false),
            status = Some("Added")
          )))
        ))
      ),
      bankAccountDetails = None,
      msb = None,
      hvd = None,
      asp = None,
      aspOrTcsp = None,
      tcspAll = None,
      tcspTrustCompFormationAgt = None,
      eabAll = None,
      eabResdEstAgncy = None,
      responsiblePersons = None,
      extraFields = ExtraFields(
        declaration = Declaration(true),
        filingIndividual = AboutYouRelease7(None, true, None, None),
        etmpFields = None
      )
    )

    "AmendVariationResponse for renewalAmendment" must {
      "convert a DES response to a AmendVariationResponse" in {

        val fpFee = 100
        val fpFeeRate = 100
        val premiseFYFeeRate = 115
        val premiseFee = 345
        val totalFees = 445
        val difference = 330
        val premiseHYFeeRate = 57.5

        val paymentReference = "XY002610108134"
        val etmpFormBundleNumber = "082000004607"
        val processingDate = "2017-07-18T09:49:25Z"

        val testApprovalNumbers = Some(10)
        val testApprovalFeeRate = Some(BigDecimal(20))
        val testApprovalCheckFee = Some(BigDecimal(30))


        AmendVariationResponse.convert(amendvariationRequest, true, models.des.AmendVariationResponse(
          processingDate = processingDate,
          etmpFormBundleNumber = etmpFormBundleNumber,
          fpNumbers = Some(1),
          fpFeeRate = Some(fpFeeRate),
          fpFee = Some(fpFee),
          responsiblePersonNotCharged = Some(1),
          premiseFYNumber = Some(3),
          premiseFYFeeRate = Some(premiseFYFeeRate),
          premiseHYFeeRate = Some(premiseHYFeeRate),
          premiseFee = Some(premiseFee),
          totalFees = Some(totalFees),
          paymentReference = Some(paymentReference),
          difference = Some(difference),
          registrationFee = None,
          premiseFYTotal = None,
          premiseHYNumber = None,
          premiseHYTotal = None,
          approvalNumbers = testApprovalNumbers,
          approvalCheckFeeRate = testApprovalFeeRate,
          approvalCheckFee = testApprovalCheckFee

        )) mustBe AmendVariationResponse(
          processingDate = processingDate,
          etmpFormBundleNumber = etmpFormBundleNumber,
          registrationFee = 0,
          fpFee = Some(fpFee),
          fpFeeRate = Some(fpFeeRate),
          premiseFee = premiseFee,
          premiseFeeRate = Some(premiseFYFeeRate),
          totalFees = totalFees,
          paymentReference = Some(paymentReference),
          difference = Some(difference),
          addedResponsiblePeople = 11,
          addedFullYearTradingPremises = 3,
          addedResponsiblePeopleFitAndProper = 1,
          addedResponsiblePeopleApprovalCheck = 10,
          approvalNumbers = testApprovalNumbers,
          approvalCheckFeeRate = testApprovalFeeRate,
          approvalCheckFee = testApprovalCheckFee
         )
      }
    }
}
