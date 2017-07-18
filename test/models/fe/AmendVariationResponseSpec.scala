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

package models.fe

import models.des.{AmendVariationRequest, ChangeIndicators, DesConstants}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec};

class AmendVariationResponseSpec extends PlaySpec with OneAppPerSuite {

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
    tradingPremises = ???
  )

  "AmendVariationResponse" must {
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

      AmendVariationResponse.withZeroRatedTPs(amendvariationRequest, models.des.AmendVariationResponse(
        processingDate = processingDate,
        etmpFormBundleNumber = etmpFormBundleNumber,
        fpNumbers = Some(1),
        fpFeeRate = Some(fpFeeRate),
        fpFee = Some(fpFee),
        fpNumbersNotCharged = Some(1),
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
        premiseHYTotal = None
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
        addedResponsiblePeopleFitAndProper = 1
      )
    }
  }

}
