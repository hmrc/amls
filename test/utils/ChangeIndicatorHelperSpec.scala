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

package utils

import models.des.aboutthebusiness.{Address, BusinessContactDetails}
import models.des.aboutyou.AboutYouRelease7
import models.des.amp.{Amp, TransactionsAccptOvrThrshld}
import models.des.asp.Asp
import models.des.businessactivities.{BusinessActivities, MlrActivitiesAppliedFor}
import models.des.businessdetails.{BusinessDetails, BusinessType}
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy, LettingAgents}
import models.des.hvd.Hvd
import models.des.msb.{MoneyServiceBusiness, MsbAllDetails}
import models.des.tcsp.{TcspAll, TcspTrustCompFormationAgt}
import models.des.tradingpremises.{Tcsp, TradingPremises}
import models.des.{AmendVariationRequest, ChangeIndicators, Declaration, ExtraFields, SubscriptionView}

class ChangeIndicatorHelperSpec extends AmlsBaseSpec with ChangeIndicatorHelper {

  "ChangeIndicatorHelper" must {
    "set change indicators correctly" must {
      val businessActivities = BusinessActivities(None, None, None, None, None, None, None, None, None, None)

      val api5 = SubscriptionView(
        "formbundlenumber",
        BusinessDetails(BusinessType.LimitedCompany, None, None),
        BusinessContactDetails(Address("line1", Some("line2"), None, None, "uk", None, None), false, None, "telNo", "email"),
        None,
        None,
        None,
        businessActivities,
        TradingPremises(None, None),
        None, Some(MoneyServiceBusiness(None, None, None, None)), None, None, None, None, None, None, None, None, None, None,
        ExtraFields(Declaration(false), AboutYouRelease7(employedWithinBusiness = false), None)
      )

      val api6 = AmendVariationRequest(
        "ref",
        ChangeIndicators(),
        "messageType",
        BusinessDetails(BusinessType.LimitedCompany, None, None),
        BusinessContactDetails(Address("line1", Some("line2"), None, None, "uk", None, None), false, None, "telNo", "email"),
        None,
        None,
        None,
        businessActivities,
        TradingPremises(None, None),
        None, Some(MoneyServiceBusiness(None, None, None, None)), None, None, None, None, None, None, None, None, None, None,
        ExtraFields(Declaration(false), AboutYouRelease7(employedWithinBusiness = false), None)
      )

      // MSB
      "msb" when {

        "user does not have msb" when {
          val noMsb = MlrActivitiesAppliedFor(false, false, false, false, false, false, false, true)
          val api5WithoutMsb = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(noMsb)))

          "msb section has not changed" must {
            "not set msb change indicator" in {
              msbChangedIndicator(api5WithoutMsb, api6) mustBe false
            }
          }
          "msb section has changed" must {
            val api6WithMsbAllDetails = api6.copy(msb = Some(api6.msb.get.copy(msbAllDetails = Some(MsbAllDetails(None, true, None, true)))))

            "set msb change indicator" in {
              msbChangedIndicator(api5WithoutMsb, api6WithMsbAllDetails) mustBe true
            }
          }
        }

        "user has msb" when {
          val msbApplies = MlrActivitiesAppliedFor(false, true, false, false, false, false, false, false)
          val api5WithMsb = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(msbApplies)))

          "msb section has not changed" must {
            "not set msb change indicator" in {
              msbChangedIndicator(api5WithMsb, api6) mustBe false
            }
          }
          "msb section has changed" must {
            val api6WithMsbAllDetails = api6.copy(msb = Some(api6.msb.get.copy(msbAllDetails = Some(MsbAllDetails(None, true, None, true)))))

            "set msb change indicator" in {
              msbChangedIndicator(api5WithMsb, api6WithMsbAllDetails) mustBe true
            }
          }
        }
      }

      // HVD
      "hvd" when {

        "user does not have hvd" when {
          val noHvd = MlrActivitiesAppliedFor(false, false, false, false, false, false, false, true)
          val api5WithoutHvd = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(noHvd)))

          "section has not changed" must {
            "not set hvd change indicator" in {
              hvdChangedIndicator(api5WithoutHvd, api6) mustBe false
            }
          }

          "section has changed" must {
            val api6WithHvd = api6.copy(hvd = Some(Hvd(true, None, None, true, None, None)))

            "set hvd change indicator" in {
              hvdChangedIndicator(api5WithoutHvd, api6WithHvd) mustBe true
            }
          }
        }

        "user has hvd" when {
          val hvdApply = MlrActivitiesAppliedFor(false, true, false, false, false, false, false, false)
          val api5WithHvd = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(hvdApply)))

          "section has not changed" must {
            "not set hvd change indicator" in {
              hvdChangedIndicator(api5WithHvd, api6) mustBe false
            }
          }

          "section has changed" must {
            val api6WithHvd = api6.copy(hvd = Some(Hvd(true, None, None, true, None, None)))

            "set hvd change indicator" in {
              hvdChangedIndicator(api5WithHvd, api6WithHvd) mustBe true
            }
          }
        }
      }

      // AMP
      "amp" when {

        "user does not have amp" when {
          val noAmp = MlrActivitiesAppliedFor(true, false, false, false, false, false, false, false)
          val api5WithoutAmp = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(noAmp)))

          "section has not changed" must {
            "not set amp change indicator" in {
              ampChangeIndicator(api5WithoutAmp, api6) mustBe false
            }
          }

          "section has changed" must {
            val api6WithAmpDetails = api6.copy(amp = Some(Amp(TransactionsAccptOvrThrshld(true, None), true, 10: Int)))

            "set amp change indicator" in {
              ampChangeIndicator(api5, api6WithAmpDetails) mustBe true
            }
          }
        }

        "user has amp" when {
          val ampApply = MlrActivitiesAppliedFor(false, false, false, false, false, false, false, true)
          val api5WithAmp = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(ampApply)))

          "section has not changed" must {
            "not set amp change indicator" in {
              ampChangeIndicator(api5WithAmp, api6) mustBe false
            }
          }

          "section has changed" must {
            val api6WithAmpDetails = api6.copy(amp = Some(Amp(TransactionsAccptOvrThrshld(true, None), true, 10: Int)))

            "set hvd change indicator" in {
              ampChangeIndicator(api5WithAmp, api6WithAmpDetails) mustBe (true)
            }
          }
        }
      }

      // ASP
      "asp" when {

        "user does not have asp" when {
          val noAsp = MlrActivitiesAppliedFor(true, false, false, false, false, false, false, false)
          val api5WithoutAsp = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(noAsp)))

          "section has not changed" must {
            "not set asp change indicator" in {
              aspChangedIndicator(api5WithoutAsp, api6) mustBe false
            }
          }

          "section has changed" must {
            val api6WithoutRegHmrcAgtRegSchTax = api6.copy(asp = Some(Asp(false, None)))

            "set asp change indicator" in {
              aspChangedIndicator(api5WithoutAsp, api6WithoutRegHmrcAgtRegSchTax) mustBe true
            }
          }
        }

        "user has asp" when {
          val aspApply = MlrActivitiesAppliedFor(false, false, true, false, false, false, false, false)
          val api5WithAsp = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(aspApply)))

          "section has not changed" must {
            "not set asp change indicator" in {
              aspChangedIndicator(api5WithAsp, api6) mustBe false
            }
          }

          "section has changed" must {
            val api6WithRegHmrcAgtRegSchTax = api6.copy(asp = Some(Asp(false, None)))

            "set asp change indicator" in {
              aspChangedIndicator(api5WithAsp, api6WithRegHmrcAgtRegSchTax) mustBe (true)
            }
          }
        }
      }

      // TCSP
      "tcsp" when {

        "user does not have tcsp" when {
          val noTcsp = MlrActivitiesAppliedFor(true, false, false, false, false, false, false, false)
          val api5WithoutTcsp = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(noTcsp)))

          "tcsp section has not changed" must {
            "not set tcsp change indicator" in {
              tcspChangedIndicator(api5WithoutTcsp, api6) mustBe false
            }
          }

          "tcsp section has changed" must {
            val api6WithAnotherTcspServiceProvider = api6.copy(tcspAll = Some(TcspAll(true, None)))

            "set tcsp change indicator" in {
              tcspChangedIndicator(api5WithoutTcsp, api6WithAnotherTcspServiceProvider) mustBe true
            }
          }
        }

        "user has tcsp" when {
          val tcspApply = MlrActivitiesAppliedFor(false, false, false, true, false, false, false, false)
          val api5WithTcsp = api5.copy(businessActivities = api5.businessActivities.copy(mlrActivitiesAppliedFor = Some(tcspApply)))
          val api6WithAnotherTcspServiceProvider = api6.copy(tcspAll = Some(TcspAll(true, None)))
          val api6WithoutAnotherTcspServiceProvider = api6.copy(tcspAll = Some(TcspAll(false, None)))

          "section has not changed" must {
            "not set tcsp change indicator" in {
              tcspChangedIndicator(api5WithTcsp, api6WithoutAnotherTcspServiceProvider) mustBe false
            }
          }

          "section has changed" must {
            "set tcsp change indicator" in {
              tcspChangedIndicator(api5WithTcsp, api6WithAnotherTcspServiceProvider) mustBe true
            }
          }

          "tcsp formation agent section has changed" must {
            val api6WithOnlyOffTheShelfCompsSold = api6.copy(tcspTrustCompFormationAgt = Some(TcspTrustCompFormationAgt(onlyOffTheShelfCompsSold = true)))

            "set tcsp change indicator" in {
              tcspChangedIndicator(api5WithTcsp, api6WithOnlyOffTheShelfCompsSold) mustBe true
            }
          }
        }
      }

        // EAB
        "eab" when {

          "user does not have eab" when {
            val noEabApplies = MlrActivitiesAppliedFor(true, false, false, false, false, false, false, false)
            val api5WithNoEabAndEstAgncyRedressApplies = api5.copy(businessActivities = api5.businessActivities.copy(
              mlrActivitiesAppliedFor = Some(noEabApplies)), eabResdEstAgncy = Some(EabResdEstAgncy(true, None)))
            val api6WithResdEstAgncy = api6.copy(eabResdEstAgncy = Some(EabResdEstAgncy(true, None)))

            "eab section has not changed" must {
              "not set eab change indicator" in {
                eabChangedIndicator(api5WithNoEabAndEstAgncyRedressApplies, api6WithResdEstAgncy) mustBe false
              }
            }

            "eab section has changed" must {
              val api6WithEstAgncActProhibProvideDetails = api6WithResdEstAgncy.copy(eabAll = Some(EabAll(true, None, false, None)))

              "set eab change indicator" in {
                eabChangedIndicator(api5WithNoEabAndEstAgncyRedressApplies, api6WithEstAgncActProhibProvideDetails) mustBe true
              }
            }

            "eab residential estate agency section has changed" must {
              val api6WithNoEabAndEstAgncyRedressApplies = api6WithResdEstAgncy.copy(eabResdEstAgncy = Some(EabResdEstAgncy(true, Some("scheme"))))

              "set eab change indicator" in {
                eabChangedIndicator(api5WithNoEabAndEstAgncyRedressApplies, api6WithNoEabAndEstAgncyRedressApplies) mustBe true
              }
            }

            "eab letting agents section has changed" must {
              val api6WithClientMoneyProtection = api6.copy(lettingAgents = Some(LettingAgents(clientMoneyProtection = Some(true))))

              "set eab change indicator" in {
                eabChangedIndicator(api5WithNoEabAndEstAgncyRedressApplies, api6WithClientMoneyProtection) mustBe true
              }
            }
          }

          "user has eab" when {
            val eabApplies = MlrActivitiesAppliedFor(false, false, false, false, true, false, false, false)
            val api5WithEabDetails = api5.copy(businessActivities = api5.businessActivities.copy(
              mlrActivitiesAppliedFor =
                Some(eabApplies)), eabResdEstAgncy = Some(EabResdEstAgncy(true, Some("scheme"))))
            val api6WithEabDetails = api6.copy(eabAll = Some(EabAll(false, None, false, None)))

            "section has not changed" must {
              "not set eab change indicator" in {
                eabChangedIndicator(api5WithEabDetails, api6WithEabDetails) mustBe false
              }
            }

            "section has changed" must {
              val api6WithEstAgncyActProhibProvideDetails = api6WithEabDetails.copy(eabAll = Some(EabAll(true, None, false, None)))

              "set eab change indicator" in {
                eabChangedIndicator(api5, api6WithEstAgncyActProhibProvideDetails) mustBe true
              }
            }

            "eab residential estate agency section has changed" must {
              val api6WithNoEabAndEstAgncyRedressApplies = api6WithEabDetails.copy(eabResdEstAgncy = Some(EabResdEstAgncy(true, Some("address"))))

              "set eab change indicator" in {
                eabChangedIndicator(api5WithEabDetails, api6WithNoEabAndEstAgncyRedressApplies) mustBe true
              }
            }

            "eab letting agents section has changed" must {
              val api6WithClientMoneyProtection = api6.copy(lettingAgents = Some(LettingAgents(clientMoneyProtection = Some(true))))

              "set eab change indicator" in {
                eabChangedIndicator(api5WithEabDetails, api6WithClientMoneyProtection) mustBe true
              }
            }
          }
        }
      }
    }
  }
