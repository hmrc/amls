/*
 * Copyright 2021 HM Revenue & Customs
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
import models.des.amp.Amp
import models.des.asp.Asp
import models.des.businessactivities.{AmpServices, BusinessActivities, MlrActivitiesAppliedFor}
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy, LettingAgents}
import models.des.hvd.Hvd
import models.des.msb.{MoneyServiceBusiness, MsbAllDetails}
import models.des.tcsp.{TcspAll, TcspTrustCompFormationAgt}
import models.des.{AmendVariationRequest, SubscriptionView}
import org.mockito.Mockito._

class ChangeIndicatorHelperSpec extends AmlsBaseSpec with ChangeIndicatorHelper {

  "ChangeIndicatorHelper" must {
    "set change indicators correctly" must {
      val api5 = mock[SubscriptionView]
      val api6 = mock[AmendVariationRequest]
      val businessActivities = mock[BusinessActivities]

      // MSB
      "msb" when {
        val msbSection = mock[MoneyServiceBusiness]

        "user does not have msb" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val noMsb = mlrActivities.copy(amp = true)

          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(noMsb)

          "msb section has not changed" must {
            "not set msb change indicator" in {
              when(api5.msb) thenReturn Some(msbSection)
              when(api6.msb) thenReturn Some(msbSection)
              msbChangedIndicator(api5, api6) mustBe (false)
            }
          }
          "msb section has changed" must {
            val updatedMsbSection = msbSection.copy(msbAllDetails = Some(mock[MsbAllDetails]))

            "set msb change indicator" in {
              when(api6.msb) thenReturn Some(updatedMsbSection)
              msbChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }
        "user has msb" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val hasMsb = mlrActivities.copy(msb = true)
          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(hasMsb)

          "msb section has not changed" must {
            "not set msb change indicator" in {
              when(api5.msb) thenReturn Some(msbSection)
              when(api6.msb) thenReturn Some(msbSection)
              msbChangedIndicator(api5, api6) mustBe (false)
            }
          }
          "msb section has changed" must {
            val updatedMsbSection = msbSection.copy(msbAllDetails = Some(mock[MsbAllDetails]))

            "set msb change indicator" in {
              when(api6.msb) thenReturn Some(updatedMsbSection)
              msbChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }
      }

      // HVD
      "hvd" when {
        val hvdSection = mock[Hvd]

        "user does not have hvd" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val noHvd = mlrActivities.copy(amp = true)

          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(noHvd)

          "section has not changed" must {
            "not set hvd change indicator" in {
              when(api5.hvd) thenReturn Some(hvdSection)
              when(api6.hvd) thenReturn Some(hvdSection)
              hvdChangedIndicator(api5, api6) mustBe (false)
            }
          }
          "section has changed" must {
            val updatedHvdSection = hvdSection.copy(cashPaymentsAccptOvrThrshld = true)

            "set hvd change indicator" in {
              when(api6.hvd) thenReturn Some(updatedHvdSection)
              hvdChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }
        "user has hvd" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val hasHvd = mlrActivities.copy(hvd = true)
          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(hasHvd)

          "section has not changed" must {
            "not set hvd change indicator" in {
              when(api5.hvd) thenReturn Some(hvdSection)
              when(api6.hvd) thenReturn Some(hvdSection)
              hvdChangedIndicator(api5, api6) mustBe (false)
            }
          }
          "section has changed" must {
            val updatedHvdSection = hvdSection.copy(cashPaymentsAccptOvrThrshld = true)

            "set hvd change indicator" in {
              when(api6.hvd) thenReturn Some(updatedHvdSection)
              hvdChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }
      }

      // AMP
      "amp" when {
        val ampSection = mock[Amp]
        val ampServices = mock[AmpServices]

        "section has not changed" must {
          "not set amp change indicator" in {
            val ampServices = mock[AmpServices]
            val businessActivities = mock[BusinessActivities]

            when(api5.amp) thenReturn Some(ampSection)
            when(api6.amp) thenReturn Some(ampSection)

            when(api5.businessActivities) thenReturn businessActivities
            when(api6.businessActivities) thenReturn businessActivities

            when(api5.businessActivities.ampServicesCarriedOut) thenReturn Some(ampServices)
            when(api6.businessActivities.ampServicesCarriedOut) thenReturn Some(ampServices)

            ampChangeIndicator(api5, api6) mustBe (false)
          }
        }

        "section has changed" must {
          "set amp change indicator" in {
            val updatedAmpSection = ampSection.copy(sysAutoIdOfLinkedTransactions = true)
            val ampServices = mock[AmpServices]
            val businessActivities = mock[BusinessActivities]

            when(api5.amp) thenReturn Some(ampSection)
            when(api6.amp) thenReturn Some(updatedAmpSection)

            when(api5.businessActivities) thenReturn businessActivities
            when(api6.businessActivities) thenReturn businessActivities

            when(api5.businessActivities.ampServicesCarriedOut) thenReturn Some(ampServices)
            when(api6.businessActivities.ampServicesCarriedOut) thenReturn Some(ampServices)

            ampChangeIndicator(api5, api6) mustBe (true)
          }
        }

        "section not changed amp services have changed" must {
          "set amp change indicator" in {
            val updatedAmpServices = ampServices.copy(privateDealer = true)
            val businessActivities = mock[BusinessActivities]
            val updatedBusinessActivities = mock[BusinessActivities]

            when(api5.amp) thenReturn Some(ampSection)
            when(api6.amp) thenReturn Some(ampSection)

            when(api5.businessActivities) thenReturn businessActivities
            when(api6.businessActivities) thenReturn updatedBusinessActivities

            when(businessActivities.ampServicesCarriedOut) thenReturn Some(ampServices)
            when(updatedBusinessActivities.ampServicesCarriedOut) thenReturn Some(updatedAmpServices)

            ampChangeIndicator(api5, api6) mustBe (false)
          }
        }
      }

      // ASP
      "asp" when {
        val aspSection = mock[Asp]

        "user does not have asp" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val noAsp = mlrActivities.copy(amp = true)

          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(noAsp)

          "asp section has not changed" must {
            "not set asp change indicator" in {
              when(api5.asp) thenReturn Some(aspSection)
              when(api6.asp) thenReturn Some(aspSection)
              aspChangedIndicator(api5, api6) mustBe (false)
            }
          }
          "asp section has changed" must {
            val updatedAspSection = aspSection.copy(regHmrcAgtRegSchTax = true)

            "set asp change indicator" in {
              when(api6.asp) thenReturn Some(updatedAspSection)
              msbChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }
        "user has asp" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val hasAsp = mlrActivities.copy(asp = true)
          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(hasAsp)

          "asp section has not changed" must {
            "not set asp change indicator" in {
              when(api5.asp) thenReturn Some(aspSection)
              when(api6.asp) thenReturn Some(aspSection)
              aspChangedIndicator(api5, api6) mustBe (false)
            }
          }
          "asp section has changed" must {
            val updatedAspSection = aspSection.copy(regHmrcAgtRegSchTax = true)

            "set asp change indicator" in {
              when(api6.asp) thenReturn Some(updatedAspSection)
              msbChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }
      }

      // TCSP
      "tcsp" when {
        val tcspSection = mock[TcspAll]
        val tcspTrustCompFormationAgt = mock[TcspTrustCompFormationAgt]
        val fullTcspTrustCompFormationAgt = tcspTrustCompFormationAgt.copy(onlyOffTheShelfCompsSold = true,
          complexCorpStructureCreation = true)

        "user does not have tcsp" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val noTcsp = mlrActivities.copy(amp = true)

          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(noTcsp)
          when(api5.tcspTrustCompFormationAgt) thenReturn Some(fullTcspTrustCompFormationAgt)
          when(api6.tcspTrustCompFormationAgt) thenReturn Some(fullTcspTrustCompFormationAgt)

          "tcsp section has not changed" must {
            "not set tcsp change indicator" in {
              when(api5.tcspAll) thenReturn Some(tcspSection)
              when(api6.tcspAll) thenReturn Some(tcspSection)

              tcspChangedIndicator(api5, api6) mustBe (false)
            }
          }

          "tcsp section has changed" must {
            val updatedTcspSection = tcspSection.copy(anotherTcspServiceProvider = true)

            "set tcsp change indicator" in {
              when(api5.tcspAll) thenReturn Some(tcspSection)
              when(api6.tcspAll) thenReturn Some(updatedTcspSection)
              tcspChangedIndicator(api5, api6) mustBe (true)
            }
          }

          "tcsp formation agent section has changed" must {
            val updatedTcspTrustCompFormationAgt = tcspTrustCompFormationAgt.copy(onlyOffTheShelfCompsSold = true)

            "set tcsp change indicator" in {
              when(api6.tcspTrustCompFormationAgt) thenReturn Some(updatedTcspTrustCompFormationAgt)
              tcspChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }

        "user has tcsp" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val hasTcsp = mlrActivities.copy(tcsp = true)

          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(hasTcsp)
          when(api5.tcspTrustCompFormationAgt) thenReturn Some(fullTcspTrustCompFormationAgt)
          when(api6.tcspTrustCompFormationAgt) thenReturn Some(fullTcspTrustCompFormationAgt)

          "tcsp section has not changed" must {
            "not set tcsp change indicator" in {
              when(api5.tcspAll) thenReturn Some(tcspSection)
              when(api6.tcspAll) thenReturn Some(tcspSection)
              tcspChangedIndicator(api5, api6) mustBe (false)
            }
          }

          "tcsp section has changed" must {
            val updatedTcspSection = tcspSection.copy(anotherTcspServiceProvider = true)

            "set tcsp change indicator" in {
              when(api5.tcspAll) thenReturn Some(tcspSection)
              when(api6.tcspAll) thenReturn Some(updatedTcspSection)
              tcspChangedIndicator(api5, api6) mustBe (true)
            }
          }
          "tcsp formation agent section has changed" must {
            val updatedTcspTrustCompFormationAgt = tcspTrustCompFormationAgt.copy(onlyOffTheShelfCompsSold = true)

            "set tcsp change indicator" in {
              when(api6.tcspTrustCompFormationAgt) thenReturn Some(updatedTcspTrustCompFormationAgt)
              tcspChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }
      }

      // EAB
      "eab" when {
        val eabSection = mock[EabAll]
        val eabResdEstAgncy = mock[EabResdEstAgncy]
        val eabLettingAgents = mock[LettingAgents]
        val fullEabResdEstAgncy = eabResdEstAgncy.copy(regWithRedressScheme = true, whichRedressScheme = Some("foo"))

        "user does not have eab" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val noEab = mlrActivities.copy(amp = true)

          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(noEab)
          when(api5.eabResdEstAgncy) thenReturn Some(fullEabResdEstAgncy)
          when(api6.eabResdEstAgncy) thenReturn Some(fullEabResdEstAgncy)

          "eab section has not changed" must {
            "not set eab change indicator" in {
              when(api5.eabAll) thenReturn Some(eabSection)
              when(api6.eabAll) thenReturn Some(eabSection)
              eabChangedIndicator(api5, api6) mustBe (false)
            }
          }

          "section has changed" must {
            val updatedEabSection = eabSection.copy(estateAgencyActProhibition = true)

            "set tcsp change indicator" in {
              when(api5.eabAll) thenReturn Some(eabSection)
              when(api6.eabAll) thenReturn Some(updatedEabSection)
              eabChangedIndicator(api5, api6) mustBe (true)
            }
          }

          "eab residential estate agency section has changed" must {
            val updatedEabResdEstAgncy = eabResdEstAgncy.copy(regWithRedressScheme = true)

            "set tcsp change indicator" in {
              when(api6.eabResdEstAgncy) thenReturn Some(updatedEabResdEstAgncy)
              eabChangedIndicator(api5, api6) mustBe (true)
            }
          }

          "eab letting agents section has changed" must {
            val updatedEabLettingAgent = eabLettingAgents.copy(clientMoneyProtection = Some(true))

            "set tcsp change indicator" in {
              when(api6.lettingAgents) thenReturn Some(updatedEabLettingAgent)
              eabChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }

        "user has eab" when {
          val mlrActivities = mock[MlrActivitiesAppliedFor]
          val hasEab = mlrActivities.copy(eab = true)

          when(api5.businessActivities) thenReturn businessActivities
          when(businessActivities.mlrActivitiesAppliedFor) thenReturn Some(hasEab)
          when(api5.eabResdEstAgncy) thenReturn Some(fullEabResdEstAgncy)
          when(api6.eabResdEstAgncy) thenReturn Some(fullEabResdEstAgncy)

          "section has not changed" must {
            "not set eab change indicator" in {
              when(api5.eabAll) thenReturn Some(eabSection)
              when(api6.eabAll) thenReturn Some(eabSection)
              eabChangedIndicator(api5, api6) mustBe (false)
            }
          }

          "section has changed" must {
            val updatedEabSection = eabSection.copy(estateAgencyActProhibition = true)

            "set tcsp change indicator" in {
              when(api5.eabAll) thenReturn Some(eabSection)
              when(api6.eabAll) thenReturn Some(updatedEabSection)
              eabChangedIndicator(api5, api6) mustBe (true)
            }
          }

          "eab residential estate agency section has changed" must {
            val updatedEabResdEstAgncy = eabResdEstAgncy.copy(regWithRedressScheme = true)

            "set tcsp change indicator" in {
              when(api6.eabResdEstAgncy) thenReturn Some(updatedEabResdEstAgncy)
              eabChangedIndicator(api5, api6) mustBe (true)
            }
          }
        }
      }
    }
  }
}
