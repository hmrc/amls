/*
 * Copyright 2016 HM Revenue & Customs
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
import play.api.libs.json.{JsPath, JsSuccess}

class AgentCompanyNameSpec extends PlaySpec {

  "AgentCompanyName" must {

    "Success read and write json" in {
      AgentCompanyName.formats.reads(AgentCompanyName.formats.writes(AgentCompanyName("somename"))) must
        be(JsSuccess(AgentCompanyName("somename"), JsPath \ "agentCompanyName"))
    }

    "convert when agentLegalEntityName is empty" in {
      AgentCompanyName.conv(None) must be(None)
    }
  }
}
