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

package models.des

import models.des.tradingpremises.{AgentDetails, AgentPremises, Amp, Asp, Bpsp, Eab, Hvd, Msb, Tcsp, Tditpsp, Address => DesAddress}
import models.fe.tradingpremises._
import org.joda.time.LocalDate
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}

class AgentDetailsSpec extends PlaySpec with OneAppPerSuite {

  "AgentDetails" must {
    "convert frontend Trading premises into backend model in Amendment flow" in {
      implicit val requestType = RequestType.Amendment

      val feTradingPremises = TradingPremises(Some(RegisteringAgentPremises(true)), YourTradingPremises("string",
        Address("string", "string", Some("string"), Some("string"), "AA1 1AA"), new LocalDate(2008, 1, 1), true),
        Some(BusinessStructure.SoleProprietor), Some(AgentName("entity name",None,Some("1970-01-01"))), None, None,
        WhatDoesYourBusinessDo(Set(BusinessActivity.EstateAgentBusinessService, BusinessActivity.BillPaymentServices))
      )
      AgentDetails.convert(feTradingPremises) must be(AgentDetails("Sole Proprietor",None,Some("1970-01-01"),Some("entity name"),
        AgentPremises("string",DesAddress("string","string",Some("string"),Some("string"),"GB",Some("AA1 1AA"),None),true,
          Msb(false,false,false,false,false),Hvd(false),Asp(false),
          Tcsp(false),Eab(true),Bpsp(true),Tditpsp(false),Amp(false),None,None),Some("2008-01-01"),None,None,None))
    }

    "convert frontend Trading premises into backend model in Subscription flow" in {
      implicit val requestType = RequestType.Subscription

      val feTradingPremises = TradingPremises(Some(RegisteringAgentPremises(true)), YourTradingPremises("string",
        Address("string", "string", Some("string"), Some("string"), "AA1 1AA"), new LocalDate(2008, 1, 1), true),
        Some(BusinessStructure.SoleProprietor), Some(AgentName("entity name",None,Some("1970-01-01"))), None, None,
        WhatDoesYourBusinessDo(Set(BusinessActivity.EstateAgentBusinessService, BusinessActivity.BillPaymentServices))
      )
      AgentDetails.convert(feTradingPremises) must be(AgentDetails("Sole Proprietor",None,Some("1970-01-01"),Some("entity name"),
        AgentPremises("string",DesAddress("string","string",Some("string"),Some("string"),"GB",Some("AA1 1AA"),None),true,
          Msb(false,false,false,false,false),Hvd(false),Asp(false),
          Tcsp(false),Eab(true),Bpsp(true),Tditpsp(false),Amp(false),Some("2008-01-01"),None),None,None,None,None))
    }
  }

}
