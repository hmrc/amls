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

package models.des.tradingpremises

import models.des.{DesConstants, StringOrInt}
import models.fe.tradingpremises.{TradingPremises => FETradingPremises, _}
import models.fe.{tradingpremises => FETradingPremisesPkg}
import org.joda.time.LocalDate
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class TradingPremisesSpec extends PlaySpec {

  "TradingPremises" must {

    val premises = OwnBusinessPremisesDetails(
      "string",
      Address("string",
        "string",
        Some("string"),
        Some("string"),
        "GB",
        Some("string")
      ),
      false,
      Msb(false, false, true, true, false),
      Hvd(true),
      Asp(false),
      Tcsp(true),
      Eab(false),
      Bpsp(false),
      Tditpsp(false),
      "2010-01-01",
      None,
      None
    )

    val ownBusinessPremises = Some(OwnBusinessPremises(true, Some(Seq(premises))))

    val agentPremises = AgentPremises("string", Address("string", "string", Some("string"), Some("string"), "GB", Some("string")), true,
      Msb(false, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(false),
      Eab(true),
      Bpsp(true),
      Tditpsp(false),
      "2008-01-01",
      Some("1999-01-01")
    )

    val agentPremises1 = AgentPremises("string", Address("string", "string", Some("string"), Some("string"), "GB", Some("string")), true,
      Msb(false, false, false, false, false),
      Hvd(true),
      Asp(true),
      Tcsp(false),
      Eab(false),
      Bpsp(false),
      Tditpsp(false),
      "2008-01-01")

    val agentPremises2 = AgentPremises("string", Address("string", "string", Some("string"), Some("string"), "GB", Some("string")), true,
      Msb(false, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(true),
      Eab(false),
      Bpsp(false),
      Tditpsp(true),
      "2008-01-01")

    "serialise Trading premises model" in {

      val agentDetail1 = AgentDetails("Limited Liability Partnership", Some("string"), agentPremises, Some("Deleted"),
        Some(StringOrInt("11223344")))

      val agentBusinessPremises = Some(AgentBusinessPremises(true, Some(Seq(agentDetail1))))

      val desTradingPremises = {
        TradingPremises(ownBusinessPremises, agentBusinessPremises)
      }

      val json = Json.obj("ownBusinessPremises" -> Json.obj("ownBusinessPremises" -> true,
        "ownBusinessPremisesDetails" -> Json.arr(Json.obj("tradingName" -> "string",
          "businessAddress" -> Json.obj("addressLine1" -> "string",
            "addressLine2" -> "string",
            "addressLine3" -> "string",
            "addressLine4" -> "string",
            "country" -> "GB",
            "postcode" -> "string"),
          "residential" -> false,
          "msb" -> Json.obj("mt" -> false, "ce" -> false, "smdcc" -> true, "nonSmdcc" -> true, "fx" -> false),
          "hvd" -> Json.obj("hvd" -> true),
          "asp" -> Json.obj("asp" -> false),
          "tcsp" -> Json.obj("tcsp" -> true),
          "eab" -> Json.obj("eab" -> false),
          "bpsp" -> Json.obj("bpsp" -> false),
          "tditpsp" -> Json.obj("tditpsp" -> false),
          "startDate" -> "2010-01-01"
          ))),
        "agentBusinessPremises" -> Json.obj("agentBusinessPremises" -> true,
          "agentDetails" -> Json.arr(Json.obj("agentLegalEntity" -> "Limited Liability Partnership",
            "agentLegalEntityName" -> "string",
            "agentPremises" -> Json.obj("tradingName" -> "string",
              "businessAddress" -> Json.obj("addressLine1" -> "string",
                "addressLine2" -> "string",
                "addressLine3" -> "string",
                "addressLine4" -> "string",
                "country" -> "GB", "postcode" -> "string"),
              "residential" -> true,
              "msb" -> Json.obj("mt" -> false, "ce" -> false, "smdcc" -> false, "nonSmdcc" -> false, "fx" -> false),
              "hvd" -> Json.obj("hvd" -> false),
              "asp" -> Json.obj("asp" -> false),
              "tcsp" -> Json.obj("tcsp" -> false),
              "eab" -> Json.obj("eab" -> true),
              "bpsp" -> Json.obj("bpsp" -> true),
              "tditpsp" -> Json.obj("tditpsp" -> false),
              "startDate" -> "2008-01-01",
              "endDate" -> "1999-01-01"
            ),
            "status" ->"Deleted",
            "lineId" -> "11223344"
            ))))

      TradingPremises.format.writes(desTradingPremises) must be(json)
      TradingPremises.format.reads(json) must be(JsSuccess(desTradingPremises))
      TradingPremises.format.reads(TradingPremises.format.writes(desTradingPremises)) must be(JsSuccess(desTradingPremises))
    }

    "convert TradingPremises" in {

      val agentDetail1 = AgentDetails("Limited Liability Partnership", Some("LLP Partnership"), agentPremises,Some("Deleted"),
        Some(StringOrInt("11223344")))
      val agentDetail2 = AgentDetails("Partnership", Some("Partnership"), agentPremises1)
      val agentDetail4 = AgentDetails("Unincorporated Body", Some(""), agentPremises2)

      val agentBusinessPremises = Some(AgentBusinessPremises(true, Some(Seq(agentDetail1, agentDetail2, agentDetail4))))

      val desTradingPremises = {
        TradingPremises(ownBusinessPremises, agentBusinessPremises)
      }

      val tradiongPremises = Some(Seq(FETradingPremises(Some(RegisteringAgentPremises(false)), YourTradingPremises("string",
        FETradingPremisesPkg.Address("string", "string", Some("string"), Some("string"), "string"), new LocalDate(2010, 1, 1), false),
        None, None, None, None,
        WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing, BusinessActivity.TrustAndCompanyServices)),
        Some(MsbServices(Set(ChequeCashingNotScrapMetal, ChequeCashingScrapMetal)))),
        FETradingPremises(Some(RegisteringAgentPremises(true)),YourTradingPremises("string",
        FETradingPremisesPkg.Address("string", "string", Some("string"), Some("string"), "string"), new LocalDate(2008, 1, 1), true),
          Some(BusinessStructure.LimitedLiabilityPartnership), None, Some(AgentCompanyName("LLP Partnership")), None,
        WhatDoesYourBusinessDo(Set(BusinessActivity.EstateAgentBusinessService, BusinessActivity.BillPaymentServices)),None,Some(11223344),Some("Deleted"),
          Some(ActivityEndDate(new LocalDate(1999, 1, 1)))),
        FETradingPremises(Some(RegisteringAgentPremises(true)), YourTradingPremises("string",
          FETradingPremisesPkg.Address("string", "string", Some("string"), Some("string"), "string"), new LocalDate(2008, 1, 1), true),
          Some(BusinessStructure.Partnership), None, None, Some(AgentPartnership("Partnership")),
          WhatDoesYourBusinessDo(Set(BusinessActivity.AccountancyServices, BusinessActivity.HighValueDealing))),
        FETradingPremises(Some(RegisteringAgentPremises(true)), YourTradingPremises("string",
          FETradingPremisesPkg.Address("string", "string", Some("string"), Some("string"), "string"), new LocalDate(2008, 1, 1), true),
          Some(BusinessStructure.UnincorporatedBody),None, None, None,
          WhatDoesYourBusinessDo(Set(BusinessActivity.TrustAndCompanyServices, BusinessActivity.TelephonePaymentService)))
      ))

      TradingPremises.convert(tradiongPremises) must be (desTradingPremises)
    }

    "successfully evaluate api5 trading premises data with api6 when data is different" in {

      val viewTradingPremises = DesConstants.testTradingPremisesAPI5

      val desTradingPremises = DesConstants.testTradingPremisesAPI6.copy(
        ownBusinessPremises = DesConstants.amendStatusOwnBusinessPremises
      )

      viewTradingPremises.equals(desTradingPremises) must be(false)

    }


    "successfully evaluate api5 trading premises data with api6 when data is same" in {

      val viewTradingPremises = DesConstants.testTradingPremisesAPI5

      val desTradingPremises = DesConstants.testTradingPremisesAPI5

      viewTradingPremises.equals(desTradingPremises) must be(true)

    }
  }
}
