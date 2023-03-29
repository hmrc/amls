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

package models.fe.tcsp

import models.des.DesConstants
import org.scalatestplus.play.PlaySpec
import utils.AmlsBaseSpec

class OnlyOffTheShelfCompsSoldSpec extends PlaySpec with AmlsBaseSpec {

  "The OnlyOffTheShelfCompsSold model" when {
    "given a valid model" must {
      "return the form values" when {
        "for json" when {
          "onlyOffTheShelfCompsSold is 'yes'" in {
            val model = OnlyOffTheShelfCompsSoldYes
            val result = OnlyOffTheShelfCompsSold.jsonWrite.writes(model).toString()
            val expected = "{\"onlyOffTheShelfCompsSold\":true}"

            result mustBe expected
          }
          "onlyOffTheShelfCompsSold is 'no'" in {
            val model = OnlyOffTheShelfCompsSoldNo
            val result = OnlyOffTheShelfCompsSold.jsonWrite.writes(model).toString()
            val expected = "{\"onlyOffTheShelfCompsSold\":false}"

            result mustBe expected
          }
        }
      }

      "converting the des subscription model must yield a frontend TCSP model" in {
        OnlyOffTheShelfCompsSold.conv(DesConstants.SubscriptionViewModel) must
          be(Some(OnlyOffTheShelfCompsSoldYes))
      }

      "converting the des subscription model with no formation agent must yield a frontend TCSP model" in {
        OnlyOffTheShelfCompsSold.conv(DesConstants.SubscriptionViewModelNoFormationAgent) must
          be(Some(OnlyOffTheShelfCompsSoldNo))
      }

      "converting the des subscription model with no formation agent service offered must yield a frontend TCSP model" in {
        OnlyOffTheShelfCompsSold.conv(DesConstants.SubscriptionViewModelNoFormationAgentSvc) must
          be(None)
      }

      "converting the des subscription model with notcsb services must yield a frontend TCSP model" in {
        OnlyOffTheShelfCompsSold.conv(DesConstants.SubscriptionViewModelNoFormationAgentNoTcspServices) must
          be(None)
      }
    }
  }
}
