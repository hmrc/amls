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

import org.joda.time.{LocalDate, LocalDateTime}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsSuccess, Json}

class ReadStatusResponseSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  val json = Json.parse("""{
               |  "processingDate": "2017-07-18T09:49:18Z",
               |  "formBundleStatus": "Approved",
               |  "currentRegYearStartDate": "2017-06-01",
               |  "currentRegYearEndDate": "2018-12-31",
               |  "renewalConFlag": true,
               |  "renewalSubmissionFlag": true,
               |  "currentAMLSOutstandingBalance": "0.00",
               |  "safeId": "XY0000100095375"
               |}""".stripMargin)

  val model = ReadStatusResponse(
    processingDate = new LocalDateTime(2017,7,18, 9,49,18),
    formBundleStatus = "Approved",
    statusReason = None,
    deRegistrationDate = None ,
    currentRegYearStartDate = Some(new LocalDate(2017,6,1)),
    currentRegYearEndDate = Some(new LocalDate(2018,12,31)),
    renewalConFlag = true,
    renewalSubmissionFlag = Some(true),
    currentAMLSOutstandingBalance = Some("0.00"),
    businessContactNumber = None,
    safeId = Some("XY0000100095375")
  )

  "ReadStatusResponse" must {

    "serialise json to model" in {
      ReadStatusResponse.format.reads(json) mustBe JsSuccess(model)
    }

    "deserialise json to model" in {
      ReadStatusResponse.format.writes(model) mustBe json
    }

    "confirm isRenewalPeriod" when {
      "date today is day after renewal period began" in {
        model.isRenewalPeriod(new LocalDate(2018,12,2)) mustBe false
      }
      "date today is same day as renweal period begins" in {
        model.isRenewalPeriod(new LocalDate(2018,12,1)) mustBe true
      }
    }
    "deny isRenewalPeriod" when {
      "date today is date before renewal period begins" in {
        model.isRenewalPeriod(new LocalDate(2018,11,30)) mustBe true
      }
    }
  }

}
