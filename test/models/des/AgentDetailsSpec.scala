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

package models.des

import models.des.tradingpremises.{Address => DesAddress, AgentDetails, AgentPremises, Amp, Asp, Bpsp, Eab, Hvd, Msb, Tcsp, Tditpsp}
import models.fe.tradingpremises._

import java.time.LocalDate
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec

class AgentDetailsSpec extends PlaySpec with GuiceOneAppPerSuite {

  "AgentDetails" must {
    "convert frontend Trading premises into backend model in Amendment flow" in {
      implicit val requestType = RequestType.Amendment

      val feTradingPremises = TradingPremises(
        Some(RegisteringAgentPremises(true)),
        YourTradingPremises(
          "string",
          Address("string", Some("string"), Some("string"), Some("string"), "AA1 1AA"),
          LocalDate.of(2008, 1, 1),
          true
        ),
        Some(BusinessStructure.SoleProprietor),
        Some(AgentName("entity name", None, Some("1970-01-01"))),
        None,
        None,
        WhatDoesYourBusinessDo(Set(BusinessActivity.EstateAgentBusinessService, BusinessActivity.BillPaymentServices))
      )
      AgentDetails.convert(feTradingPremises) must be(
        AgentDetails(
          "Sole Proprietor",
          None,
          Some("1970-01-01"),
          Some("entity name"),
          AgentPremises(
            "string",
            DesAddress("string", Some("string"), Some("string"), Some("string"), "GB", Some("AA1 1AA"), None),
            true,
            Msb(false, false, false, false, false),
            Hvd(false),
            Asp(false),
            Tcsp(false),
            Eab(true),
            Bpsp(true),
            Tditpsp(false),
            Amp(false),
            None,
            None
          ),
          Some("2008-01-01"),
          Some(false),
          None,
          None
        )
      )
    }

    "convert frontend Trading premises into backend model in Subscription flow" in {
      implicit val requestType = RequestType.Subscription

      val feTradingPremises = TradingPremises(
        Some(RegisteringAgentPremises(true)),
        YourTradingPremises(
          "string",
          Address("string", Some("string"), Some("string"), Some("string"), "AA1 1AA"),
          LocalDate.of(2008, 1, 1),
          true
        ),
        Some(BusinessStructure.SoleProprietor),
        Some(AgentName("entity name", None, Some("1970-01-01"))),
        None,
        None,
        WhatDoesYourBusinessDo(Set(BusinessActivity.EstateAgentBusinessService, BusinessActivity.BillPaymentServices))
      )
      AgentDetails.convert(feTradingPremises) must be(
        AgentDetails(
          "Sole Proprietor",
          None,
          Some("1970-01-01"),
          Some("entity name"),
          AgentPremises(
            "string",
            DesAddress("string", Some("string"), Some("string"), Some("string"), "GB", Some("AA1 1AA"), None),
            true,
            Msb(false, false, false, false, false),
            Hvd(false),
            Asp(false),
            Tcsp(false),
            Eab(true),
            Bpsp(true),
            Tditpsp(false),
            Amp(false),
            Some("2008-01-01"),
            None
          ),
          None,
          Some(false),
          None,
          None
        )
      )
    }
  }

}
