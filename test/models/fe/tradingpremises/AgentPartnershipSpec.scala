/*
 * Copyright 2024 HM Revenue & Customs
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

package models.fe.tradingpremises

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsSuccess

class AgentPartnershipSpec extends PlaySpec {

  "AgentPartnership" must {

    "Success read and write json" in {
      AgentPartnership.formats.reads(AgentPartnership.formats.writes(AgentPartnership("somename"))) must
        be(JsSuccess(AgentPartnership("somename")))
    }

    "convert when agentLegalEntityName is empty" in {
      AgentPartnership.conv(None) must be(None)
    }
  }
}
