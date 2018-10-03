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

import models._
import models.des.DesConstants
import models.des.businessactivities.{BusinessActivityDetails, ExpectedAMLSTurnover, MlrActivitiesAppliedFor}
import models.des.msb.{CountriesList, MsbAllDetails}
import models.des.tradingpremises.{AgentBusinessPremises, AgentDetails}
import models.fe.businessmatching.{BusinessActivities, MoneyServiceBusiness}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication

class SubscriptionViewSpec extends PlaySpec with OneAppPerSuite{

  implicit override lazy val app = FakeApplication(
    additionalConfiguration = Map(
      "microservice.services.feature-toggle.release7" -> false,
      "microservice.services.feature-toggle.phase-2-changes" -> false
    )
  )

  "SubscriptionView" must {
    "deserialise the subscription json if phase 2 toggle is off" when {
      "given valid json" in {

        val json = Json.toJson(GetSuccessModel)

        val subscriptionViewModel = GetSuccessModel

        json.as[SubscriptionView] must be(subscriptionViewModel)

        Json.toJson(GetSuccessModel) must be(json)
      }

      "convert des model to frontend model" in {

        SubscriptionView.convert(DesConstants.SubscriptionViewModelForRp) must be(SubscriptionViewModel.convertedViewModel)
      }

      "convert des model correctly to include fit and proper answer" in {

        SubscriptionView.convert(DesConstants.SubscriptionViewModelForRp.copy(responsiblePersons = Some(DesConstants.testResponsiblePersonsForRp.map {
          rp => rp.copy(msbOrTcsp = None)
        }))) must be(SubscriptionViewModel.convertedViewModel.copy(
          responsiblePeopleSection = SubscriptionViewModel.convertedViewModel.responsiblePeopleSection match {
            case None => None
            case Some(rpSeq) => Some(rpSeq.map {
              rp => rp.copy(approvalFlags = rp.approvalFlags.copy(hasAlreadyPassedFitAndProper = Some(false)))
            })
          }
        ))
      }

      "convert des model correctly to include fit and proper answer when only msb" in {

        SubscriptionView.convert(DesConstants.SubscriptionViewModelForRp.copy(responsiblePersons = Some(DesConstants.testResponsiblePersonsForRp.map {
          rp => rp.copy(msbOrTcsp = None)
        }), businessActivities = DesConstants.testBusinessActivities.copy(
          mlrActivitiesAppliedFor = Some(MlrActivitiesAppliedFor(true, false, false, false, false, false, false))))) must be(
          SubscriptionViewModel.convertedViewModel.copy(
          responsiblePeopleSection = SubscriptionViewModel.convertedViewModel.responsiblePeopleSection match {
            case None => None
            case Some(rpSeq) => Some(rpSeq.map {
              rp => rp.copy(approvalFlags = rp.approvalFlags.copy(hasAlreadyPassedFitAndProper = Some(false)))
            })
          }
        ,businessMatchingSection = SubscriptionViewModel.convertedViewModel.businessMatchingSection.copy(activities = BusinessActivities(Set(MoneyServiceBusiness)))))
      }
    }
  }

  val GetSuccessModel = SubscriptionView(
    etmpFormBundleNumber = "111111",
    businessMatchingSection = BusinessMatchingSection.model,
    eabSection = EabSection.model,
    aboutTheBusinessSection = AboutTheBusinessSection.model,
    tradingPremisesSection = TradingPremisesSection.model,
    bankDetailsSection = BankDetailsSection.model,
    aboutYouSection = AboutYouSection.model,
    businessActivitiesSection = BusinessActivitiesSection.model,
    responsiblePeopleSection = ResponsiblePeopleSection.model,
    tcspSection = ASPTCSPSection.TcspSection,
    aspSection = ASPTCSPSection.AspSection,
    msbSection = MsbSection.completeModel,
    hvdSection = HvdSection.completeModel,
    supervisionSection = SupervisionSection.completeModel
  )
}

class SubscriptionViewSpecPhase2 extends PlaySpec with OneAppPerSuite{

  implicit override lazy val app = FakeApplication(
    additionalConfiguration = Map(
      "microservice.services.feature-toggle.release7" -> false,
      "microservice.services.feature-toggle.phase-2-changes" -> true
    )
  )

  "SubscriptionView" must {
    "deserialise the subscription json if phase 2 toggle is off" when {
      "given valid json" in {

        val json = Json.toJson(GetSuccessModel)

        val subscriptionViewModel = GetSuccessModel

        json.as[SubscriptionView] must be(subscriptionViewModel)

        Json.toJson(GetSuccessModel) must be(json)
      }

      "convert des model to frontend model" in {

        SubscriptionView.convert(DesConstants.SubscriptionViewModelForRpPhase2) must be(SubscriptionViewModel.convertedViewModelPhase2)
      }

      "convert des model correctly to include fit and proper answer" in {

        SubscriptionView.convert(DesConstants.SubscriptionViewModelForRpPhase2.copy(responsiblePersons = Some(DesConstants.testResponsiblePersonsForRp.map {
          rp => rp.copy(msbOrTcsp = None)
        }))) must be(SubscriptionViewModel.convertedViewModelPhase2.copy(
          responsiblePeopleSection = SubscriptionViewModel.convertedViewModelPhase2.responsiblePeopleSection match {
            case None => None
            case Some(rpSeq) => Some(rpSeq.map {
              rp => rp.copy(approvalFlags = rp.approvalFlags.copy(hasAlreadyPassedFitAndProper = Some(false)))
            })
          }
        ))
      }

      "convert des model correctly to include fit and proper answer when only msb" in {

        SubscriptionView.convert(DesConstants.SubscriptionViewModelForRpPhase2.copy(responsiblePersons = Some(DesConstants.testResponsiblePersonsForRp.map {
          rp => rp.copy(msbOrTcsp = None)
        }), businessActivities = DesConstants.testBusinessActivities.copy(
          mlrActivitiesAppliedFor = Some(MlrActivitiesAppliedFor(true, false, false, false, false, false, false))))) must be(
          SubscriptionViewModel.convertedViewModelPhase2.copy(
            responsiblePeopleSection = SubscriptionViewModel.convertedViewModelPhase2.responsiblePeopleSection match {
              case None => None
              case Some(rpSeq) => Some(rpSeq.map {
                rp => rp.copy(approvalFlags = rp.approvalFlags.copy(hasAlreadyPassedFitAndProper = Some(false)))
              })
            }
            ,businessMatchingSection = SubscriptionViewModel.convertedViewModelPhase2.businessMatchingSection.copy(activities = BusinessActivities(Set(MoneyServiceBusiness)))))
      }
    }
  }

  val GetSuccessModel = SubscriptionView(
    etmpFormBundleNumber = "111111",
    businessMatchingSection = BusinessMatchingSection.model,
    eabSection = EabSection.model,
    aboutTheBusinessSection = AboutTheBusinessSection.model,
    tradingPremisesSection = TradingPremisesSection.model,
    bankDetailsSection = BankDetailsSection.model,
    aboutYouSection = AboutYouSection.model,
    businessActivitiesSection = BusinessActivitiesSection.model,
    responsiblePeopleSection = ResponsiblePeopleSection.model,
    tcspSection = ASPTCSPSection.TcspSection,
    aspSection = ASPTCSPSection.AspSection,
    msbSection = MsbSection.completeModel,
    hvdSection = HvdSection.completeModel,
    supervisionSection = SupervisionSection.completeModel
  )
}

class SubscriptionViewSpecRelease7 extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(
    additionalConfiguration = Map(
      "microservice.services.feature-toggle.release7" -> true,
      "microservice.services.feature-toggle.phase-2-changes" -> false
    )
  )

  val agentDetails = DesConstants.testTradingPremisesAPI5.agentBusinessPremises.fold[Option[Seq[AgentDetails]]](None){
    x => x.agentDetails match {
      case Some(data) => Some(data.map(y => y.copy(agentPremises = y.agentPremises.copy(startDate = None),
        startDate = y.agentPremises.startDate)))
      case _ => None
    }
  }

  val release7SubscriptionViewModel = DesConstants.SubscriptionViewModelForRp.copy(businessActivities = DesConstants.testBusinessActivities.copy(
    all = Some(DesConstants.testBusinessActivitiesAll.copy(
      businessActivityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£50k-£100k"))))
    ))
  ), tradingPremises = DesConstants.testTradingPremisesAPI5.copy(agentBusinessPremises = Some(AgentBusinessPremises(true, agentDetails))),
     msb = Some(DesConstants.testMsb.copy(
    msbAllDetails = Some(MsbAllDetails(
      Some("£50k-£100k"),
      true,
      Some(CountriesList(List("AD", "GB"))),
      true)
    ))))

  "SubscriptionView" must {
    "deserialise the subscription json when phase 2 toggle is off" when {
      "given valid json" in {

        val json = Json.toJson(GetSuccessModel)

        val subscriptionViewModel = GetSuccessModel

        json.as[SubscriptionView] must be(subscriptionViewModel)

        Json.toJson(GetSuccessModel) must be(json)
      }

      "convert des model to frontend model" in {

        SubscriptionView.convert(release7SubscriptionViewModel) must be(SubscriptionViewModel.convertedViewModel)
      }

      "convert des model correctly to include fit and proper answer" in {

        SubscriptionView.convert(release7SubscriptionViewModel.copy(responsiblePersons = Some(DesConstants.testResponsiblePersonsForRp.map {
          rp => rp.copy(msbOrTcsp = None)
        }))) must be(SubscriptionViewModel.convertedViewModel.copy(
          responsiblePeopleSection = SubscriptionViewModel.convertedViewModel.responsiblePeopleSection match {
            case None => None
            case Some(rpSeq) => Some(rpSeq.map {
              rp => rp.copy(approvalFlags = rp.approvalFlags.copy(hasAlreadyPassedFitAndProper = Some(false)))
            })
          }
        ))
      }

      "convert des model correctly to include fit and proper answer when only msb" in {

        SubscriptionView.convert(release7SubscriptionViewModel.copy(responsiblePersons = Some(DesConstants.testResponsiblePersonsForRp.map {
          rp => rp.copy(msbOrTcsp = None)
        }), businessActivities = DesConstants.testBusinessActivities.copy(
          mlrActivitiesAppliedFor = Some(MlrActivitiesAppliedFor(true, false, false, false, false, false, false)),
          all = Some(DesConstants.testBusinessActivitiesAll.copy(
            businessActivityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£50k-£100k"))))
          )) ))) must be(
          SubscriptionViewModel.convertedViewModel.copy(
            responsiblePeopleSection = SubscriptionViewModel.convertedViewModel.responsiblePeopleSection match {
              case None => None
              case Some(rpSeq) => Some(rpSeq.map {
                rp => rp.copy(approvalFlags = rp.approvalFlags.copy(hasAlreadyPassedFitAndProper = Some(false)))
              })
            }
            ,businessMatchingSection = SubscriptionViewModel.convertedViewModel.businessMatchingSection.copy(
              activities = BusinessActivities(Set(MoneyServiceBusiness)))))
      }
    }
  }

  val GetSuccessModel = SubscriptionView(
    etmpFormBundleNumber = "111111",
    businessMatchingSection = BusinessMatchingSection.model,
    eabSection = EabSection.model,
    aboutTheBusinessSection = AboutTheBusinessSection.model,
    tradingPremisesSection = TradingPremisesSection.model,
    bankDetailsSection = BankDetailsSection.model,
    aboutYouSection = AboutYouSection.model,
    businessActivitiesSection = BusinessActivitiesSection.model,
    responsiblePeopleSection = ResponsiblePeopleSection.model,
    tcspSection = ASPTCSPSection.TcspSection,
    aspSection = ASPTCSPSection.AspSection,
    msbSection = MsbSection.completeModel,
    hvdSection = HvdSection.completeModel,
    supervisionSection = SupervisionSection.completeModel
  )

}

class SubscriptionViewSpecRelease7Phase2 extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(
    additionalConfiguration = Map(
      "microservice.services.feature-toggle.release7" -> true,
      "microservice.services.feature-toggle.phase-2-changes" -> true
    )
  )

  val agentDetails = DesConstants.testTradingPremisesAPI5.agentBusinessPremises.fold[Option[Seq[AgentDetails]]](None){
    x => x.agentDetails match {
      case Some(data) => Some(data.map(y => y.copy(agentPremises = y.agentPremises.copy(startDate = None),
        startDate = y.agentPremises.startDate)))
      case _ => None
    }
  }

  val release7SubscriptionViewModel = DesConstants.SubscriptionViewModelForRpPhase2.copy(businessActivities = DesConstants.testBusinessActivities.copy(
    all = Some(DesConstants.testBusinessActivitiesAll.copy(
      businessActivityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£50k-£100k"))))
    ))
  ), tradingPremises = DesConstants.testTradingPremisesAPI5.copy(agentBusinessPremises = Some(AgentBusinessPremises(true, agentDetails))),
    msb = Some(DesConstants.testMsb.copy(
      msbAllDetails = Some(MsbAllDetails(
        Some("£50k-£100k"),
        true,
        Some(CountriesList(List("AD", "GB"))),
        true)
      ))))

  "SubscriptionView" must {
    "deserialise the subscription json when phase 2 toggle is off" when {
      "given valid json" in {

        val json = Json.toJson(GetSuccessModel)

        val subscriptionViewModel = GetSuccessModel

        json.as[SubscriptionView] must be(subscriptionViewModel)

        Json.toJson(GetSuccessModel) must be(json)
      }

      "convert des model to frontend model" in {

        SubscriptionView.convert(release7SubscriptionViewModel) must be(SubscriptionViewModel.convertedViewModelPhase2)
      }

      "convert des model correctly to include fit and proper answer" in {

        SubscriptionView.convert(release7SubscriptionViewModel.copy(responsiblePersons = Some(DesConstants.testResponsiblePersonsForRp.map {
          rp => rp.copy(msbOrTcsp = None)
        }))) must be(SubscriptionViewModel.convertedViewModelPhase2.copy(
          responsiblePeopleSection = SubscriptionViewModel.convertedViewModelPhase2.responsiblePeopleSection match {
            case None => None
            case Some(rpSeq) => Some(rpSeq.map {
              rp => rp.copy(approvalFlags = rp.approvalFlags.copy(hasAlreadyPassedFitAndProper = Some(false)))
            })
          }
        ))
      }

      "convert des model correctly to include fit and proper answer when only msb" in {

        SubscriptionView.convert(release7SubscriptionViewModel.copy(responsiblePersons = Some(DesConstants.testResponsiblePersonsForRp.map {
          rp => rp.copy(msbOrTcsp = None)
        }), businessActivities = DesConstants.testBusinessActivities.copy(
          mlrActivitiesAppliedFor = Some(MlrActivitiesAppliedFor(true, false, false, false, false, false, false)),
          all = Some(DesConstants.testBusinessActivitiesAll.copy(
            businessActivityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£50k-£100k"))))
          )) ))) must be(
          SubscriptionViewModel.convertedViewModelPhase2.copy(
            responsiblePeopleSection = SubscriptionViewModel.convertedViewModelPhase2.responsiblePeopleSection match {
              case None => None
              case Some(rpSeq) => Some(rpSeq.map {
                rp => rp.copy(approvalFlags = rp.approvalFlags.copy(hasAlreadyPassedFitAndProper = Some(false)))
              })
            }
            ,businessMatchingSection = SubscriptionViewModel.convertedViewModelPhase2.businessMatchingSection.copy(
              activities = BusinessActivities(Set(MoneyServiceBusiness)))))
      }
    }
  }

  val GetSuccessModel = SubscriptionView(
    etmpFormBundleNumber = "111111",
    businessMatchingSection = BusinessMatchingSection.model,
    eabSection = EabSection.model,
    aboutTheBusinessSection = AboutTheBusinessSection.model,
    tradingPremisesSection = TradingPremisesSection.model,
    bankDetailsSection = BankDetailsSection.model,
    aboutYouSection = AboutYouSection.model,
    businessActivitiesSection = BusinessActivitiesSection.model,
    responsiblePeopleSection = ResponsiblePeopleSection.model,
    tcspSection = ASPTCSPSection.TcspSection,
    aspSection = ASPTCSPSection.AspSection,
    msbSection = MsbSection.completeModel,
    hvdSection = HvdSection.completeModel,
    supervisionSection = SupervisionSection.completeModel
  )

}

