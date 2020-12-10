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

package models.fe.tradingpremises

import models.des.tradingpremises.{AgentDetails, AgentPremises}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsSuccess

class AgentNameSpec extends PlaySpec with MockitoSugar{

  "AgentName" must {
    "Success read and write json" in {
      val expected = AgentName("somename", Some("2010-01-24"), Some("1970-01-01"))

      AgentName.formats.reads(AgentName.formats.writes(expected)) must be(JsSuccess(expected))
    }

    val agentPremises = mock[AgentPremises]

    "convert when agentLegalEntityName is empty" in {
      AgentName.conv(AgentDetails("thing",None,None,None,agentPremises)) must be(None)
    }
  }
}
