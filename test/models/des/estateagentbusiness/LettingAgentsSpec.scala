/*
 * Copyright 2020 HM Revenue & Customs
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

package models.des.estateagentbusiness

import models.fe.estateagentbusiness._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class LettingAgentsSpec extends PlaySpec {
  "LettingAgents" must {

    val services = Services(Set(Residential, Commercial, Auction))
    val redressSchemeOther = Other("test")

    val lettingAgentModel = LettingAgents(Some(true))
    val lettingAgentModel2 = LettingAgents(Some(false))

    val eab = EstateAgentBusiness(Some(services),Some(redressSchemeOther), None, None, Some(ClientMoneyProtectionSchemeYes))
    val eab1 = EstateAgentBusiness(Some(services),Some(RedressSchemedNo), None, None, Some(ClientMoneyProtectionSchemeNo))
    val eab2 = EstateAgentBusiness(Some(services),None, None, None)

    "serialise LettingAgents model true" in {
      LettingAgents.format.writes(lettingAgentModel) must be(Json.obj("clientMoneyProtection"->true))
    }

    "serialise LettingAgents model false" in {
      LettingAgents.format.writes(lettingAgentModel2) must be(Json.obj("clientMoneyProtection"->false))
    }

    "successfully convert frontend eab to des LettingAgents model" in {
      LettingAgents.conv(Some(eab)) must be(Some(LettingAgents(Some(true))))
      LettingAgents.conv(Some(eab1)) must be(Some(LettingAgents(Some(false))))
      LettingAgents.conv(Some(eab2)) must be(None)
      LettingAgents.conv(None) must be(None)

    }
  }

}
