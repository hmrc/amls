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

package models.des.tradingpremises

import models.des.{DesConstants, RequestType, StringOrInt}
import models.fe.tradingpremises.{TradingPremises => FETradingPremises, _}
import models.fe.{tradingpremises => FETradingPremisesPkg}
import models.des.tradingpremises.{Address => DESAddress}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

import java.time.LocalDate

class TradingPremisesSpec extends PlaySpec with GuiceOneAppPerSuite {

  "TradingPremises" must {

    val premises = OwnBusinessPremisesDetails(
      tradingName = Some("string"),
      businessAddress =
        DESAddress("string", Some("string"), Some("string"), Some("string"), "GB", Some("AA1 1AA"), Some("1999-05-01")),
      residential = false,
      msb = Msb(false, false, true, true, false),
      hvd = Hvd(true),
      asp = Asp(false),
      tcsp = Tcsp(true),
      eab = Eab(false),
      bpsp = Bpsp(false),
      tditpsp = Tditpsp(false),
      amp = Amp(false),
      startDate = "2010-01-01",
      endDate = None,
      lineId = None,
      sectorDateChange = Some("2009-01-01"),
      dateChangeFlag = Some(false),
      tradingNameChangeDate = Some("1999-04-01")
    )

    val premises1 = premises.copy(dateChangeFlag = None)

    val ownBusinessPremises  = Some(OwnBusinessPremises(true, Some(Seq(premises))))
    val ownBusinessPremises1 = Some(OwnBusinessPremises(true, Some(Seq(premises1))))

    val agentPremises = AgentPremises(
      "string",
      DESAddress("string", Some("string"), Some("string"), Some("string"), "GB", Some("AA1 1AA"), Some("2002-03-11")),
      true,
      Msb(true, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(false),
      Eab(true),
      Bpsp(true),
      Tditpsp(false),
      Amp(false),
      Some("2008-01-01"),
      Some("2003-04-05")
    )

    val agentPremises1 = AgentPremises(
      "string",
      DESAddress("string", Some("string"), Some("string"), Some("string"), "GB", Some("AA1 1AA")),
      true,
      Msb(false, false, false, false, false),
      Hvd(true),
      Asp(true),
      Tcsp(false),
      Eab(false),
      Bpsp(false),
      Tditpsp(false),
      Amp(false),
      Some("2008-01-01")
    )

    val agentPremises2 = AgentPremises(
      "string",
      DESAddress("string", Some("string"), Some("string"), Some("string"), "GB", Some("AA1 1AA")),
      true,
      Msb(false, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(true),
      Eab(false),
      Bpsp(false),
      Tditpsp(true),
      Amp(false),
      Some("2008-01-01")
    )

    "serialise Trading premises model" in {

      val agentDetail = AgentDetails(
        agentLegalEntity = "Limited Liability Partnership",
        companyRegNo = None,
        dateOfBirth = Some("string"),
        agentLegalEntityName = Some("string"),
        agentPremises = agentPremises,
        startDate = None,
        dateChangeFlag = Some(false),
        endDate = Some("1999-01-01"),
        status = Some("Deleted"),
        lineId = Some(StringOrInt("11223344")),
        agentDetailsChangeDate = Some("2010-01-23"),
        removalReason = Some("Other"),
        removalReasonOther = Some("Some other reason")
      )

      val agentBusinessPremises = Some(AgentBusinessPremises(true, Some(Seq(agentDetail))))

      val desTradingPremises =
        TradingPremises(ownBusinessPremises, agentBusinessPremises)

      val json = Json.obj(
        "ownBusinessPremises"   -> Json.obj(
          "ownBusinessPremises"        -> true,
          "ownBusinessPremisesDetails" -> Json.arr(
            Json.obj(
              "tradingName"           -> "string",
              "businessAddress"       -> Json.obj(
                "addressLine1"      -> "string",
                "addressLine2"      -> "string",
                "addressLine3"      -> "string",
                "addressLine4"      -> "string",
                "country"           -> "GB",
                "postcode"          -> "AA1 1AA",
                "addressChangeDate" -> "1999-05-01"
              ),
              "residential"           -> false,
              "msb"                   -> Json.obj("mt" -> false, "ce" -> false, "smdcc" -> true, "nonSmdcc" -> true, "fx" -> false),
              "hvd"                   -> Json.obj("hvd" -> true),
              "asp"                   -> Json.obj("asp" -> false),
              "tcsp"                  -> Json.obj("tcsp" -> true),
              "eab"                   -> Json.obj("eab" -> false),
              "bpsp"                  -> Json.obj("bpsp" -> false),
              "tditpsp"               -> Json.obj("tditpsp" -> false),
              "amp"                   -> Json.obj("amp" -> false),
              "startDate"             -> "2010-01-01",
              "sectorDateChange"      -> "2009-01-01",
              "dateChangeFlag"        -> false,
              "tradingNameChangeDate" -> "1999-04-01"
            )
          )
        ),
        "agentBusinessPremises" -> Json.obj(
          "agentBusinessPremises" -> true,
          "agentDetails"          -> Json.arr(
            Json.obj(
              "agentLegalEntity"     -> "Limited Liability Partnership",
              "dateOfBirth"          -> "string",
              "agentLegalEntityName" -> "string",
              "agentPremises"        -> Json.obj(
                "tradingName"        -> "string",
                "businessAddress"    -> Json.obj(
                  "addressLine1"      -> "string",
                  "addressLine2"      -> "string",
                  "addressLine3"      -> "string",
                  "addressLine4"      -> "string",
                  "country"           -> "GB",
                  "postcode"          -> "AA1 1AA",
                  "addressChangeDate" -> "2002-03-11"
                ),
                "residential"        -> true,
                "msb"                -> Json.obj("mt" -> true, "ce" -> false, "smdcc" -> false, "nonSmdcc" -> false, "fx" -> false),
                "hvd"                -> Json.obj("hvd" -> false),
                "asp"                -> Json.obj("asp" -> false),
                "tcsp"               -> Json.obj("tcsp" -> false),
                "eab"                -> Json.obj("eab" -> true),
                "bpsp"               -> Json.obj("bpsp" -> true),
                "tditpsp"            -> Json.obj("tditpsp" -> false),
                "amp"                -> Json.obj("amp" -> false),
                "startDate"          -> "2008-01-01",
                "agentSectorChgDate" -> "2003-04-05"
              ),
              "dateChangeFlag"       -> false,
              "endDate"              -> "1999-01-01",
              "status"               -> "Deleted",
              "lineId"               -> "11223344",
              "agentDetailsChgDate"  -> "2010-01-23",
              "status"               -> "Deleted",
              "removalReason"        -> "Other",
              "removalReasonOther"   -> "Some other reason"
            )
          )
        )
      )

      TradingPremises.format.writes(desTradingPremises)                               must be(json)
      TradingPremises.format.reads(json)                                              must be(JsSuccess(desTradingPremises))
      TradingPremises.format.reads(TradingPremises.format.writes(desTradingPremises)) must be(
        JsSuccess(desTradingPremises)
      )
    }

    "convert TradingPremises" in {

      val agentBusinessPremises = Some(
        AgentBusinessPremises(
          agentBusinessPremises = true,
          Some(
            Seq(
              AgentDetails(
                agentLegalEntity = "Limited Liability Partnership",
                companyRegNo = None,
                dateOfBirth = None,
                agentLegalEntityName = Some("LLP Partnership"),
                agentPremises = agentPremises,
                startDate = None,
                dateChangeFlag = None,
                endDate = None,
                status = Some("Deleted"),
                lineId = Some(StringOrInt("11223344")),
                agentDetailsChangeDate = Some("2009-05-03"),
                removalReason = None,
                removalReasonOther = None
              ),
              AgentDetails("Partnership", None, None, Some("Partnership"), agentPremises1),
              AgentDetails("Unincorporated Body", None, None, None, agentPremises2)
            )
          )
        )
      )

      val desTradingPremises = TradingPremises(ownBusinessPremises1, agentBusinessPremises)

      val tradingPremises      = Some(
        Seq(
          FETradingPremises(
            Some(RegisteringAgentPremises(false)),
            YourTradingPremises(
              "string",
              FETradingPremisesPkg
                .Address("string", Some("string"), Some("string"), Some("string"), "AA1 1AA", Some("1999-05-01")),
              LocalDate.of(2010, 1, 1),
              false,
              Some("1999-04-01")
            ),
            None,
            None,
            None,
            None,
            WhatDoesYourBusinessDo(
              Set(BusinessActivity.HighValueDealing, BusinessActivity.TrustAndCompanyServices),
              Some("2009-01-01")
            ),
            Some(MsbServices(Set(ChequeCashingNotScrapMetal, ChequeCashingScrapMetal)))
          ),
          FETradingPremises(
            Some(RegisteringAgentPremises(true)),
            YourTradingPremises(
              "string",
              FETradingPremisesPkg
                .Address("string", Some("string"), Some("string"), Some("string"), "AA1 1AA", Some("2002-03-11")),
              LocalDate.of(2008, 1, 1),
              true
            ),
            Some(BusinessStructure.LimitedLiabilityPartnership),
            Some(AgentName("test name", Some("2009-05-03"), None)),
            Some(AgentCompanyDetails("LLP Partnership", None)),
            None,
            WhatDoesYourBusinessDo(
              Set(BusinessActivity.EstateAgentBusinessService, BusinessActivity.BillPaymentServices),
              Some("2003-04-05")
            ),
            Some(MsbServices(Set(TransmittingMoney))),
            Some(11223344),
            Some("Deleted"),
            Some(ActivityEndDate(LocalDate.of(1999, 1, 1)))
          ),
          FETradingPremises(
            Some(RegisteringAgentPremises(true)),
            YourTradingPremises(
              "string",
              FETradingPremisesPkg.Address("string", Some("string"), Some("string"), Some("string"), "AA1 1AA"),
              LocalDate.of(2008, 1, 1),
              true
            ),
            Some(BusinessStructure.Partnership),
            None,
            None,
            Some(AgentPartnership("Partnership")),
            WhatDoesYourBusinessDo(Set(BusinessActivity.AccountancyServices, BusinessActivity.HighValueDealing))
          ),
          FETradingPremises(
            Some(RegisteringAgentPremises(true)),
            YourTradingPremises(
              "string",
              FETradingPremisesPkg.Address("string", Some("string"), Some("string"), Some("string"), "AA1 1AA"),
              LocalDate.of(2008, 1, 1),
              true
            ),
            Some(BusinessStructure.UnincorporatedBody),
            None,
            None,
            None,
            WhatDoesYourBusinessDo(
              Set(BusinessActivity.TrustAndCompanyServices, BusinessActivity.TelephonePaymentService)
            )
          )
        )
      )
      implicit val requestType = RequestType.Subscription

      val converted: TradingPremises = TradingPremises.convert(tradingPremises)

      converted must be(desTradingPremises)

      converted.agentBusinessPremises match {
        case Some(x: AgentBusinessPremises) =>
          x.agentDetails match {
            case Some(details: Seq[AgentDetails]) =>
              details.head.agentDetailsChangeDate         must be(
                agentBusinessPremises.get.agentDetails.get.head.agentDetailsChangeDate
              )
              details.head.agentPremises.sectorChangeDate must be(Some("2003-04-05"))

            case _ => throw new RuntimeException("Invalid Agent details")
          }
        case _                              => throw new RuntimeException("Invalid Agent Business Premises")
      }

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

    "successfully convert TradingPremises front-end model to DES" when {

      val tradingPremises = Some(
        Seq(
          FETradingPremises(
            registeringAgentPremises = Some(RegisteringAgentPremises(true)),
            yourTradingPremises = YourTradingPremises(
              "Test",
              FETradingPremisesPkg.Address("Addr 1", Some("Addr 2"), None, None, "TEST"),
              LocalDate.of(2002, 1, 2),
              true
            ),
            whatDoesYourBusinessDoAtThisAddress = WhatDoesYourBusinessDo(Set.empty[BusinessActivity]),
            removalReason = Some("Other"),
            removalReasonOther = Some("Some other reason")
          )
        )
      )

      "given a removal reason" in {

        val result = TradingPremises.convert(tradingPremises)(RequestType.Amendment)

        result.agentBusinessPremises match {
          case Some(p) =>
            p.agentDetails match {
              case Some(agentDetails :: _) =>
                agentDetails.removalReason      must be(Some("Other"))
                agentDetails.removalReasonOther must be(Some("Some other reason"))

              case _ => throw new RuntimeException("Invalid Agent details")
            }

          case _ => throw new RuntimeException("Invalid Agent Business Premises")
        }

      }
    }
  }
}
