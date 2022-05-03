/*
 * Copyright 2022 HM Revenue & Customs
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

package models.fe.eab

import models.EabSection
import models.des.{DesConstants}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

class EabSpec extends PlaySpec with MockitoSugar {

  "conv" when {

    "passed a subscription view with EabAll" must {

      "create an Eab model" in {
        Eab.conv(DesConstants.viewModelNoRedress) mustBe EabSection.modelForViewNoRedress
      }

      "when passed a subscription view with residential" must {

        "create an eab model with a redress scheme The Property Ombudsman Limited" in {
          Eab.conv(DesConstants.viewModelRedress) mustBe EabSection.modelForView
        }

        "create an eab model with a redress scheme Ombudsman Services" in {
          Eab.conv(DesConstants.viewModelRedressOmbudsmanServices) mustBe EabSection.modelForViewOmbusmanServices
        }

        "create an eab model with a redress scheme Property Redress Scheme" in {
          Eab.conv(DesConstants.viewModelRedressPropertyRedressScheme) mustBe
            EabSection.modelForViewPropertyRedressScheme
        }

        "create an eab model with a redress scheme Other" in {
          Eab.conv(DesConstants.viewModelRedressOther) mustBe EabSection.modelForViewOther
        }

        "create an eab model with a redress scheme notRegistered where no EabResdEstAgncy" in {
          Eab.conv(DesConstants.viewModelRedressNoEabResdEstAgncy) mustBe EabSection.modelForViewNoEabResdEstAgncy
        }
      }

      "when passed a subscription view with lettings" must {

        "create an eab model with client money protection" in {
          Eab.conv(DesConstants.viewModelLettings) mustBe EabSection.modelForViewLA
        }

        "create an eab model with a client money protection false where no LettingAgent" in {
          Eab.conv(DesConstants.viewModelLettingsNoLettingAgents) mustBe EabSection.modelForViewLANoLettingAgentSection
        }
      }

      "when passed a subscription view with penalisedEstateAgentsAct false" must {

        "create an eab model with false and no detail" in {
          Eab.conv(DesConstants.viewModelPenalisedEstateAgentsFalse) mustBe EabSection.modelPenalisedEstateAgentsFalse
        }
      }

      "when passed a subscription view with eabAll missing (null) for penalisedEstateAgentsAct" must {

        "create an eab model with false and no detail" in {
          Eab.conv(DesConstants.viewModelPenalisedNoEabAll) mustBe
            EabSection.modelPenalisedpenalisedEstateAgentsActAndProfessionalBodyFalse
        }
      }

      "when passed a subscription view with penalisedEstateAgentsAct true" must {

        "create an eab model with true and some detail" in {
          Eab.conv(DesConstants.viewModelPenalisedEstateAgentsTrue) mustBe EabSection.modelPenalisedEstateAgentsTrue
        }
      }

      "when passed a subscription view with penalisedProfessionalBody false" must {

        "create an eab model with false and no detail" in {
          Eab.conv(
            DesConstants.viewModelPenalisedProfessionalBodyFalse
          ) mustBe EabSection.modelPenalisedProfessionalBodyFalse
        }
      }

      "when passed a subscription view with eabAll missing (null) for penalisedProfessionalBody" must {

        "create an eab model with false and no detail" in {
          Eab.conv(
            DesConstants.viewModelPenalisedNoEabAll
          ) mustBe EabSection.modelPenalisedpenalisedEstateAgentsActAndProfessionalBodyFalse
        }
      }

      "passed a subscription view with penalisedProfessionalBody true" must {

        "create an eab model with true and some detail" in {
          Eab.conv(
            DesConstants.viewModelPenalisedProfessionalBodyTrue
          ) mustBe EabSection.modelPenalisedProfessionalBodyTrue
        }
      }
    }

    "passed a subscription view with no Eab in MlrActivitiesAppliedFor" must {

      "return none" in {
        Eab.conv(DesConstants.SubscriptionViewModelNoEab) mustBe None
      }
    }
  }
}
