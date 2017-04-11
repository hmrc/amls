/*
 * Copyright 2017 HM Revenue & Customs
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


import models.des.tradingpremises.{Address => TradingPremisesAddress}
import models.fe.tradingpremises.{TradingPremises => FETradingPremises}
import models.fe.{tradingpremises => FETradingPremisesPkg}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}


class TradingPremisesViewSpec extends PlaySpec {
/*

  "TradingPremisesView" must {
    "deserialise Trading premises view model" in {
      TradingPremisesView.format.reads(testJson) must be(JsSuccess(desTradingPremises))
    }

    "serialise Trading premises view model" in {
      TradingPremisesView.format.writes(desTradingPremises) must be(testJson)
    }
  }

  val desTradingPremises = TradingPremisesView(
    OwnBusinessPremises(true, Some(Seq(
      OwnBusinessPremisesDetails(
        "OwnBusinessTradingName",
        TradingPremisesAddress("OwnBusinessAddressLine1",
          "OwnBusinessAddressLine2",
          Some("OwnBusinessAddressLine3"),
          Some("OwnBusinessAddressLine4"),
          "AD",
          Some("AA1 1AA")),
        false,
        Msb(true, true, true, true, true),
        Hvd(true),
        Asp(true),
        Tcsp(true),
        Eab(true),
        Bpsp(true),
        Tditpsp(true),
        "2001-01-01"),
      OwnBusinessPremisesDetails(
        "OwnBusinessTradingName1",
        TradingPremisesAddress("OB11AddressLine1",
          "OB1AddressLine2",
          Some("OB1AddressLine3"),
          Some("OB1AddressLine4"),
          "AD",
          Some("AA1 1AA")),
        false,
        Msb(true, true, true, true, true),
        Hvd(true),
        Asp(true),
        Tcsp(true),
        Eab(true),
        Bpsp(true),
        Tditpsp(true),
        "2001-01-01")
    ))),
    AgentBusinessPremisesView(
      true,
      Some(Seq(
        AgentDetailsView(
          "Sole Proprietor",
          "1234567891",
          "CT",
          "AgentLegalEntityName",
          AgentPremises("TradingName",
            TradingPremisesAddress("AddressLine1",
              "AddressLine2",
              Some("AddressLine3"),
              Some("AddressLine4"),
              "AD",
              Some("AA1 1AA")),
            true,
            Msb(true, true, true, true, true),
            Hvd(true),
            Asp(true),
            Tcsp(true),
            Eab(true),
            Bpsp(true),
            Tditpsp(true),
            "2001-01-01"
          )
        ),
        AgentDetailsView(
          "Sole Proprietor",
          "0000000001",
          "CT",
          "aaaaaaaaaaa",
          AgentPremises("aaaaaaaaaaaa",
            TradingPremisesAddress("a",
              "a",
              Some("a"),
              Some("a"),
              "AD",
              Some("aaaaaaaaaa")),
            true,
            Msb(true, true, true, true, true),
            Hvd(true),
            Asp(true),
            Tcsp(true),
            Eab(true),
            Bpsp(true),
            Tditpsp(true),
            "1967-08-13"
          )
        ),
        AgentDetailsView(
          "Sole Proprietor",
          "9876543211",
          "CT",
          "AgentLegalEntityName2",
          AgentPremises("TradingName",
            TradingPremisesAddress("AgentAddressLine1",
              "AgentAddressLine2",
              Some("AgentAddressLine3"),
              Some("AgentAddressLine4"),
              "AD",
              Some("AA1 1AA")),
            true,
            Msb(true, true, true, true, true),
            Hvd(true),
            Asp(true),
            Tcsp(true),
            Eab(true),
            Bpsp(true),
            Tditpsp(true),
            "2001-01-01"
          )
        )
      ))
    )
  )

  val json = Json.obj("ownBusinessPremises" -> Json.obj("ownBusinessPremises" -> true,
    "ownBusinessPremisesDetails" -> Json.arr(Json.obj(
      "tradingName" -> "string",
      "agentUtr" -> "1111",
      "agentUtrType" -> "UtrType",
      "businessAddress" -> Json.obj("addressLine1" -> "string",
        "addressLine2" -> "string",
        "addressLine3" -> "string",
        "addressLine4" -> "string",
        "country" -> "GB",
        "postcode" -> "string"),
      "residential" -> false,
      "msb" -> Json.obj("mt" -> false, "ce" -> false, "smdcc" -> false, "nonSmdcc" -> false, "fx" -> false),
      "hvd" -> Json.obj("hvd" -> true),
      "asp" -> Json.obj("asp" -> false),
      "tcsp" -> Json.obj("tcsp" -> true),
      "eab" -> Json.obj("eab" -> false),
      "bpsp" -> Json.obj("bpsp" -> false),
      "tditpsp" -> Json.obj("tditpsp" -> false),
      "startDate" -> "2010-01-01"))),
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
          "startDate" -> "2008-01-01")))))

  val testJson = Json.obj("ownBusinessPremises" -> Json.obj("ownBusinessPremises" -> true,
    "ownBusinessPremisesDetails" -> Json.arr(Json.obj(
      "tradingName" -> "OwnBusinessTradingName",
      "businessAddress" -> Json.obj(
        "addressLine1" -> "OwnBusinessAddressLine1",
        "addressLine2" -> "OwnBusinessAddressLine2",
        "addressLine3" -> "OwnBusinessAddressLine3",
        "addressLine4" -> "OwnBusinessAddressLine4",
        "country" -> "AD",
        "postcode" -> "AA1 1AA"
      ),
      "residential" -> false,
      "msb" -> Json.obj(
        "mt" -> true,
        "ce" -> true,
        "smdcc" -> true,
        "nonSmdcc" -> true,
        "fx" -> true
      ),
      "hvd" -> Json.obj(
        "hvd" -> true
      ),
      "asp" -> Json.obj(
        "asp" -> true
      ),
      "tcsp" -> Json.obj(
        "tcsp" -> true
      ),
      "eab" -> Json.obj(
        "eab" -> true
      ),
      "bpsp" -> Json.obj(
        "bpsp" -> true
      ),
      "tditpsp" -> Json.obj(
        "tditpsp" -> true
      ),
      "startDate" -> "2001-01-01"
    ),
      Json.obj("tradingName" -> "OwnBusinessTradingName1",
        "businessAddress" -> Json.obj(
          "addressLine1" -> "OB11AddressLine1",
          "addressLine2" -> "OB1AddressLine2",
          "addressLine3" -> "OB1AddressLine3",
          "addressLine4" -> "OB1AddressLine4",
          "country" -> "AD",
          "postcode" -> "AA1 1AA"
        ),
        "residential" -> false,
        "msb" -> Json.obj(
          "mt" -> true,
          "ce" -> true,
          "smdcc" -> true,
          "nonSmdcc" -> true,
          "fx" -> true
        ),
        "hvd" -> Json.obj(
          "hvd" -> true
        ),
        "asp" -> Json.obj(
          "asp" -> true
        ),
        "tcsp" -> Json.obj(
          "tcsp" -> true
        ),
        "eab" -> Json.obj(
          "eab" -> true
        ),
        "bpsp" -> Json.obj(
          "bpsp" -> true
        ),
        "tditpsp" -> Json.obj(
          "tditpsp" -> true
        ),
        "startDate" -> "2001-01-01"
      )
    )
  ),
    "agentBusinessPremises" -> Json.obj(
      "agentBusinessPremises" -> true,
      "agentDetails" -> Json.arr(Json.obj("agentLegalEntity" -> "Sole Proprietor",
        "agentUtr" -> "1234567891",
        "agentUtrType" -> "CT",
        "agentLegalEntityName" -> "AgentLegalEntityName",
        "agentPremises" -> Json.obj(
          "tradingName" -> "TradingName",
          "businessAddress" -> Json.obj(
            "addressLine1" -> "AddressLine1",
            "addressLine2" -> "AddressLine2",
            "addressLine3" -> "AddressLine3",
            "addressLine4" -> "AddressLine4",
            "country" -> "AD",
            "postcode" -> "AA1 1AA"
          ),
          "residential" -> true,
          "msb" -> Json.obj(
            "mt" -> true,
            "ce" -> true,
            "smdcc" -> true,
            "nonSmdcc" -> true,
            "fx" -> true
          ),
          "hvd" -> Json.obj(
            "hvd" -> true
          ),
          "asp" -> Json.obj(
            "asp" -> true
          ),
          "tcsp" -> Json.obj(
            "tcsp" -> true
          ),
          "eab" -> Json.obj(
            "eab" -> true
          ),
          "bpsp" -> Json.obj(
            "bpsp" -> true
          ),
          "tditpsp" -> Json.obj(
            "tditpsp" -> true
          ),
          "startDate" -> "2001-01-01"
        )
      ),
        Json.obj("agentLegalEntity" -> "Sole Proprietor",
          "agentUtr" -> "0000000001",
          "agentUtrType" -> "CT",
          "agentLegalEntityName" -> "aaaaaaaaaaa",
          "agentPremises" -> Json.obj(
            "tradingName" -> "aaaaaaaaaaaa",
            "businessAddress" -> Json.obj(
              "addressLine1" -> "a",
              "addressLine2" -> "a",
              "addressLine3" -> "a",
              "addressLine4" -> "a",
              "country" -> "AD",
              "postcode" -> "aaaaaaaaaa"
            ),
            "residential" -> true,
            "msb" -> Json.obj(
              "mt" -> true,
              "ce" -> true,
              "smdcc" -> true,
              "nonSmdcc" -> true,
              "fx" -> true
            ),
            "hvd" -> Json.obj(
              "hvd" -> true
            ),
            "asp" -> Json.obj(
              "asp" -> true
            ),
            "tcsp" -> Json.obj(
              "tcsp" -> true
            ),
            "eab" -> Json.obj(
              "eab" -> true
            ),
            "bpsp" -> Json.obj(
              "bpsp" -> true
            ),
            "tditpsp" -> Json.obj(
              "tditpsp" -> true
            ),
            "startDate" -> "1967-08-13"
          )
        ),
        Json.obj(
          "agentLegalEntity" -> "Sole Proprietor",
          "agentUtr" -> "9876543211",
          "agentUtrType" -> "CT",
          "agentLegalEntityName" -> "AgentLegalEntityName2",
          "agentPremises" -> Json.obj(
            "tradingName" -> "TradingName",
            "businessAddress" -> Json.obj(
              "addressLine1" -> "AgentAddressLine1",
              "addressLine2" -> "AgentAddressLine2",
              "addressLine3" -> "AgentAddressLine3",
              "addressLine4" -> "AgentAddressLine4",
              "country" -> "AD",
              "postcode" -> "AA1 1AA"
            ),
            "residential" -> true,
            "msb" -> Json.obj(
              "mt" -> true,
              "ce" -> true,
              "smdcc" -> true,
              "nonSmdcc" -> true,
              "fx" -> true
            ),
            "hvd" -> Json.obj(
              "hvd" -> true
            ),
            "asp" -> Json.obj(
              "asp" -> true
            ),
            "tcsp" -> Json.obj(
              "tcsp" -> true
            ),
            "eab" -> Json.obj(
              "eab" -> true
            ),
            "bpsp" -> Json.obj(
              "bpsp" -> true
            ),
            "tditpsp" -> Json.obj(
              "tditpsp" -> true
            ),
            "startDate" -> "2001-01-01"
          )
        )
      )
    )
  )*/
}
