/*
 * Copyright 2019 HM Revenue & Customs
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

package utils

import models.des.DesConstants
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}


class AmendVariationHelperSpec extends PlaySpec with MockitoSugar with ScalaFutures with OneAppPerSuite {

  val amendVariationHelper = new AmendVariationHelper

  "AmendVariationHelper" must {
    "set change indicators correctly" must {

      // MSB
      "user does not have msb" when {
         "msb section has not changed" must {
           val api5 = DesConstants.inAPI5NoMsb
           val api6 = DesConstants.outApi6NoMsb

           "not set msb change indicator" in {
             amendVariationHelper.msbChangedIndicator(api5, api6) mustBe false
           }
         }
        "msb section has changed" must {
          val api5 = DesConstants.inAPI5NoMsb
          val api6 = DesConstants.outApi6NoMsbWithMsbChange

          "set msb change indicator" in {
            amendVariationHelper.msbChangedIndicator(api5, api6) mustBe true
          }
        }
      }
      "user has msb" when {
        "msb section has not changed" must {
          val api5 = DesConstants.inAPI5
          val api6 = DesConstants.outApi6NoMsb

          "not set msb change indicator" in {
            amendVariationHelper.msbChangedIndicator(api5, api6) mustBe false
          }
        }
        "msb section has changed" must {
          val api5 = DesConstants.inAPI5
          val api6 = DesConstants.outApi6NoMsbWithMsbChange

          "set msb change indicator" in {
            amendVariationHelper.msbChangedIndicator(api5, api6) mustBe true
          }
        }
      }

      // HVD
      "user does not have hvd" when {
        "hvd section has not changed" must {
          val api5 = DesConstants.inAPI5NoHvd
          val api6 = DesConstants.outApi6NoHvd

          "not set hvd change indicator" in {
            amendVariationHelper.hvdChangedIndicator(api5, api6) mustBe false
          }
        }
        "hvd section has changed" must {
          val api5 = DesConstants.inAPI5NoHvd
          val api6 = DesConstants.outApi6NoHvdWithHvdChange

          "set hvd change indicator" in {
            amendVariationHelper.hvdChangedIndicator(api5, api6) mustBe true
          }
        }
      }
      "user has hvd" when {
        "hvd section has not changed" must {
          val api5 = DesConstants.inAPI5
          val api6 = DesConstants.outApi6NoHvd

          "not set hvd change indicator" in {
            amendVariationHelper.hvdChangedIndicator(api5, api6) mustBe false
          }
        }
        "hvd section has changed" must {
          val api5 = DesConstants.inAPI5
          val api6 = DesConstants.outApi6NoHvdWithHvdChange

          "set hvd change indicator" in {
            amendVariationHelper.hvdChangedIndicator(api5, api6) mustBe true
          }
        }
      }

      // ASP
      "user does not have asp" when {
        "asp section has not changed" must {
          "not set asp change indicator" in {

          }
        }
        "asp section has changed" must {
          "set asp change indicator" in {

          }
        }
      }
      "user has asp" when {
        "asp section has not changed" must {
          "not set asp change indicator" in {

          }
        }
        "asp section has changed" must {
          "set asp change indicator" in {

          }
        }
      }

      // TCSP
      "user does not have tcsp" when {
        "tcsp section has not changed" must {
          "not set tcsp change indicator" in {

          }
        }
        "tcsp section has changed" must {
          "set tcsp change indicator" in {

          }
        }
        "tcsp formation agent section has changed" must {
          "set tcsp change indicator" in {

          }
        }
      }
      "user has tcsp" when {
        "tcsp section has not changed" must {
          "not set tcsp change indicator" in {

          }
        }
        "tcsp section has changed" must {
          "set tcsp change indicator" in {

          }
        }
      }

      // EAB
      "user does not have eab" when {
        "eab section has not changed" must {
          "not set eab change indicator" in {

          }
        }
        "eab section has changed" must {
          "set eab change indicator" in {

          }
        }
        "eab residential estate agency section has changed" must {
          "set eab change indicator" in {

          }
        }
      }
      "user has eab" when {
        "eab section has not changed" must {
          "not set eab change indicator" in {

          }
        }
        "eab section has changed" must {
          "set eab change indicator" in {

          }
        }
      }

    }
  }
}
