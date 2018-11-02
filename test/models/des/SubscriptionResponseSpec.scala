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

package models.des

import models.{des, fe}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsNumber, JsObject, JsString}
import play.api.test.FakeApplication

class SubscriptionResponseSpec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.phase-2-changes" -> false))

  "SubscriptionResponse" must {
    "Serialise correctly with registration fee" in {

      val response = des.SubscriptionResponse(
        etmpFormBundleNumber = "111111",
        amlsRefNo = "XAML00000567890",
        Some(150.00),
        Some(100.0),
        300.0,
        550.0,
        "XA353523452345",
        approvalCheckNumbers = Some(100),
        approvalCheckFeeRate = Some(100.0),
        approvalCheckFee = Some(100.0)
      )

      SubscriptionResponse.format.writes(response) must be(JsObject(Seq(
        ("etmpFormBundleNumber", JsString("111111")),
        ("amlsRefNo", JsString("XAML00000567890")),
        ("registrationFee", JsNumber(150)),
        ("fpFee", JsNumber(100)),
        ("premiseFee", JsNumber(300)),
        ("totalFees", JsNumber(550)),
        ("paymentReference", JsString("XA353523452345")),
        ("approvalCheckNumbers", JsNumber(100)),
        ("approvalCheckFeeRate", JsNumber(100.0)),
        ("approvalCheckFee", JsNumber(100.0)))))
    }

    "Serialise correctly without registration fee" in {

      val response = des.SubscriptionResponse(
        etmpFormBundleNumber = "111111",
        amlsRefNo = "XAML00000567890",
        None,
        Some(100.0),
        300.0,
        550.0,
        "XA353523452345",
        approvalCheckNumbers = Some(100),
        approvalCheckFeeRate = Some(100.0),
        approvalCheckFee = Some(100.0)
      )

      SubscriptionResponse.format.writes(response) must be(JsObject(Seq(
        ("etmpFormBundleNumber", JsString("111111")),
        ("amlsRefNo", JsString("XAML00000567890")),
        ("fpFee", JsNumber(100)),
        ("premiseFee", JsNumber(300)),
        ("totalFees", JsNumber(550)),
        ("paymentReference", JsString("XA353523452345")),
        ("approvalCheckNumbers", JsNumber(100)),
        ("approvalCheckFeeRate", JsNumber(100.0)),
        ("approvalCheckFee", JsNumber(100.0)))))
    }
  }
}

class SubscriptionResponsePhase2Spec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.phase-2-changes" -> true))

  "SubscriptionResponse" must {
    "Serialise correctly with registration fee" in {

      val response = des.SubscriptionResponse(
        etmpFormBundleNumber = "111111",
        amlsRefNo = "XAML00000567890",
        Some(150.00),
        Some(100.0),
        300.0,
        550.0,
        "XA353523452345",
        responsiblePersonNotCharged = Some(1),
        approvalCheckNumbers = Some(100),
        approvalCheckFeeRate = Some(100.0),
        approvalCheckFee = Some(100.0)
      )

      des.SubscriptionResponse.format.writes(response) must be(JsObject(Seq(
        ("etmpFormBundleNumber", JsString("111111")),
        ("amlsRefNo", JsString("XAML00000567890")),
        ("registrationFee", JsNumber(150)),
        ("fpFee", JsNumber(100)),
        ("premiseFee", JsNumber(300)),
        ("totalFees", JsNumber(550)),
        ("paymentReference", JsString("XA353523452345")),
        ("responsiblePersonNotCharged", JsNumber(1)),
        ("approvalCheckNumbers", JsNumber(100)),
        ("approvalCheckFeeRate", JsNumber(100.0)),
        ("approvalCheckFee", JsNumber(100.0)))))

    }

    "Serialise correctly without registration fee" in {

      val response = des.SubscriptionResponse(
        etmpFormBundleNumber = "111111",
        amlsRefNo = "XAML00000567890",
        None,
        Some(100.0),
        300.0,
        550.0,
        "XA353523452345",
        responsiblePersonNotCharged = Some(1),
        approvalCheckNumbers = Some(100),
        approvalCheckFeeRate = Some(100.0),
        approvalCheckFee = Some(100.0)
      )

      des.SubscriptionResponse.format.writes(response) must be(JsObject(Seq(
        ("etmpFormBundleNumber", JsString("111111")),
        ("amlsRefNo", JsString("XAML00000567890")),
        ("fpFee", JsNumber(100)),
        ("premiseFee", JsNumber(300)),
        ("totalFees", JsNumber(550)),
        ("paymentReference", JsString("XA353523452345")),
        ("responsiblePersonNotCharged", JsNumber(1)),
        ("approvalCheckNumbers", JsNumber(100)),
        ("approvalCheckFeeRate", JsNumber(100.0)),
        ("approvalCheckFee", JsNumber(100.0)))))
    }

    "provide correct number of fit and proper responsible people to be charged" in {

      val response = des.SubscriptionResponse(
        etmpFormBundleNumber = "111111",
        amlsRefNo = "XAML00000567890",
        registrationFee = Some(150.00),
        fpFee = Some(100.0),
        premiseFee = 300.0,
        totalFees = 550.0,
        paymentReference = "XA353523452345",
        fpNumbers = Some(5),
        fpFeeRate = Some(40.0),
        responsiblePersonNotCharged = Some(2),
        approvalCheckNumbers = Some(0),
        approvalCheckFeeRate = Some(100.0),
        approvalCheckFee = Some(100.0)
      )

      val feResponse = fe.SubscriptionResponse.convert(response)

      feResponse.addedResponsiblePeopleFitAndProper mustBe 5
      feResponse.addedResponsiblePeopleApprovalCheck mustBe 0

    }

    "provide correct number of responsible people who need to pay approval check" in {

      val response = des.SubscriptionResponse(
        etmpFormBundleNumber = "111111",
        amlsRefNo = "XAML00000567890",
        registrationFee = Some(150.00),
        fpFee = Some(100.0),
        premiseFee = 300.0,
        totalFees = 550.0,
        paymentReference = "XA353523452345",
        fpNumbers = Some(0),
        fpFeeRate = Some(40.0),
        responsiblePersonNotCharged = Some(2),
        approvalCheckNumbers = Some(5),
        approvalCheckFeeRate = Some(100.0),
        approvalCheckFee = Some(100.0)
      )

      val feResponse = fe.SubscriptionResponse.convert(response)

      feResponse.addedResponsiblePeopleApprovalCheck mustBe 5
      feResponse.addedResponsiblePeopleFitAndProper mustBe 0

    }
  }
}
