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

package models.fe.tradingpremises

import models.des.tradingpremises.{Address => TradingPremisesAddress, _}
import org.joda.time.LocalDate
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json._

class YourTradingPremisesSpec extends WordSpec with MustMatchers with OneAppPerSuite {

  "YourTradingPremises" must {

    "convert des model to frontend model" in {

      val agentPremises = AgentPremises("TradingName",
        TradingPremisesAddress("AddressLine1",
          "AddressLine2",
          Some("AddressLine3"),
          Some("AddressLine4"),
          "AD",
          Some("AA1 1AA")
        ),
        true,
        Msb(true, false, true, true, true),
        Hvd(true),
        Asp(false),
        Tcsp(true),
        Eab(false),
        Bpsp(true),
        Tditpsp(false),
        Amp(false),
        None
      )
      val agentDetail = AgentDetails("", None,None,None,agentPremises, Some("2001-01-01"))

      val feModel = YourTradingPremises("TradingName",
        Address("AddressLine1", "AddressLine2", Some("AddressLine3"), Some("AddressLine4"), "AA1 1AA", None),
        new LocalDate(2001, 1, 1), true)

      YourTradingPremises.conv(agentDetail) must be(feModel)

    }
  }
}