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

package models.fe.tradingpremises

import models.des.{DesConstants, StringOrInt}
import models.des.tradingpremises.{Address => DesAddress, TradingPremises => DesTradingPremises, _}
import org.joda.time.LocalDate
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json
import utils.StatusConstants

class TradingPremisesSpec extends WordSpec with MustMatchers {

  val ytp = YourTradingPremises("foo", Address("1", "2", None, None, "asdfasdf"),
    new LocalDate(1990, 2, 24), true)


  val businessStructure = BusinessStructure.SoleProprietor

  val agentName = AgentName("test",None,None)

  val agentCompanyName = AgentCompanyName("test")

  val agentPartnership = AgentPartnership("test")

  val wdbd = WhatDoesYourBusinessDo(
    Set(
      BusinessActivity.BillPaymentServices,
      BusinessActivity.EstateAgentBusinessService,
      BusinessActivity.MoneyServiceBusiness), Some("2010-03-01"))

  val msbServices = MsbServices(Set(TransmittingMoney, CurrencyExchange))

  val completeModel = TradingPremises(Some(RegisteringAgentPremises(true)),
    ytp, Some(businessStructure), Some(agentName),Some(agentCompanyName),Some(agentPartnership),wdbd, Some(msbServices),
    Some(123456),
    Some("Added"),
    Some(ActivityEndDate(new LocalDate(1999,1,1))))

  val completeJson = Json.obj("registeringAgentPremises"-> Json.obj("agentPremises"->true),
    "yourTradingPremises"-> Json.obj("tradingName" -> "foo",
      "addressLine1" ->"1",
      "addressLine2" ->"2",
      "postcode" ->"asdfasdf",
      "startDate" ->"1990-02-24",
      "isResidential" ->true),
    "businessStructure" -> Json.obj("agentsBusinessStructure" ->"01"),
    "agentName" -> Json.obj("agentName" ->"test"),
    "agentCompanyName" -> Json.obj("agentCompanyName" ->"test"),
    "agentPartnership" -> Json.obj("agentPartnership" ->"test"),
    "whatDoesYourBusinessDoAtThisAddress" ->Json.obj("activities" -> Json.arr("02","03","05"), "dateOfChange" -> "2010-03-01"),
    "msbServices" -> Json.obj("msbServices"-> Json.arr("01","02")),
     "lineId" ->123456,
    "status" ->"Added",
    "endDate"-> Json.obj("endDate" ->"1999-01-01")
  )

  "TradingPremises" must {

    "Serialise as expected" in {
      Json.toJson(completeModel) must
        be(completeJson)
    }

    "Deserialise as expected" in {
      completeJson.as[TradingPremises] must
        be(completeModel)
    }

    "Convert des agent model to frontend Trading Premises model" in {
      import models.fe.tradingpremises.BusinessActivity._

      val agentTradingPremises1 = TradingPremises(Some(RegisteringAgentPremises(true)),
        YourTradingPremises("aaaaaaaaaaaa",Address("a","a",Some("a"),Some("a"),"aaaaaaaaaa"), new LocalDate(1967,8,13),true),
        Some(BusinessStructure.SoleProprietor),Some(AgentName("AgentLegalEntityName", None, Some("1970-01-01"))),None,
       None,
        WhatDoesYourBusinessDo(Set(HighValueDealing, AccountancyServices,
          EstateAgentBusinessService, BillPaymentServices,
          TelephonePaymentService, MoneyServiceBusiness,
          TrustAndCompanyServices)),
        Some(MsbServices(Set(TransmittingMoney, CurrencyExchange))),Some(111111),Some("Added"))

      val agentTradingPremises2 = TradingPremises(Some(RegisteringAgentPremises(true)),
        YourTradingPremises("aaaaaaaaaaaa",Address("a","a",Some("a"),Some("a"),"aaaaaaaaaa"),
        new LocalDate(1967,8,13),true),
        Some(BusinessStructure.SoleProprietor),Some(AgentName("aaaaaaaaaaa", None, Some("1975-01-01"))),None,
        None,
        WhatDoesYourBusinessDo(Set(HighValueDealing, AccountancyServices,
          EstateAgentBusinessService, BillPaymentServices, TelephonePaymentService,
          MoneyServiceBusiness, TrustAndCompanyServices)),
        Some(MsbServices(Set(TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))),None,Some("Added"))

      val agentTradingPremises3 = TradingPremises(Some(RegisteringAgentPremises(true)),
        YourTradingPremises("TradingName",
          Address("AgentAddressLine1","AgentAddressLine2",Some("AgentAddressLine3"),Some("AgentAddressLine4"),"XX1 1XX"), new LocalDate(2001,1,1),true),
        Some(BusinessStructure.SoleProprietor),Some(AgentName("AgentLegalEntityName2", None, Some("1975-01-01"))),None,
        None,
        WhatDoesYourBusinessDo(Set(HighValueDealing, AccountancyServices,
          EstateAgentBusinessService, BillPaymentServices, TelephonePaymentService,
          MoneyServiceBusiness, TrustAndCompanyServices)),
        Some(MsbServices(Set(TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))),None,Some("Added"))

      val ownPremises1 = TradingPremises(Some(RegisteringAgentPremises(false)),
        YourTradingPremises("OwnBusinessTradingName",Address("OwnBusinessAddressLine1","OwnBusinessAddressLine2",Some("OwnBusinessAddressLine3"),
          Some("OwnBusinessAddressLine4"),"YY1 1YY"), new LocalDate(2001,5,5),false),None,None,None,None,
        WhatDoesYourBusinessDo(Set(EstateAgentBusinessService, BillPaymentServices, MoneyServiceBusiness, TrustAndCompanyServices)),
        Some(MsbServices(Set(TransmittingMoney))),
        Some(444444),Some(StatusConstants.Unchanged))

      val ownPremises2 = TradingPremises(Some(RegisteringAgentPremises(false)),
        YourTradingPremises("OwnBusinessTradingName1",Address("OB11AddressLine1","OB1AddressLine2",Some("OB1AddressLine3"),Some("OB1AddressLine4"),"XX1 1XX"),
          new LocalDate(2001,1,1),false),
        None,None,None,None,
        WhatDoesYourBusinessDo(Set(HighValueDealing, AccountancyServices, EstateAgentBusinessService,
          BillPaymentServices, TelephonePaymentService, MoneyServiceBusiness,
          TrustAndCompanyServices)),Some(MsbServices(Set(ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))),Some(555555),Some(StatusConstants.Unchanged))

      val convertedModel = Some(List(agentTradingPremises1, agentTradingPremises2, agentTradingPremises3, ownPremises1, ownPremises2))

      val details = DesConstants.agentDetailsAPI51

      TradingPremises.conv(DesConstants.testTradingPremisesAPI5.copy(
        ownBusinessPremises = Some(OwnBusinessPremises(true, Some(Seq(
          DesConstants.ownBusinessPremisesTP.x.ownBusinessPremisesDetails.get.lift(0).get.copy(sectorDateChange = Some("2009-08-07"), msb = Msb(true, false, false, false, false)),
          DesConstants.ownBusinessPremisesTP.x.ownBusinessPremisesDetails.get.lift(1).get
        )))),
        agentBusinessPremises = Some(AgentBusinessPremises(true,
          Some(Seq(
            details.copy(agentPremises = details.agentPremises.copy(sectorChangeDate = Some("2013-01-01"))),
            DesConstants.agentDetailsAPI52,
            DesConstants.agentDetailsAPI53
          ))
      )))) must be(convertedModel)

    }

    "convert des to frontend trading premises when premises lis it empty" in {
      val desTp = DesTradingPremises(
        Some(OwnBusinessPremises(false, None)),
        Some(AgentBusinessPremises(
          false,
          None
        ))
      )

      TradingPremises.conv(desTp) must be(None)
    }
  }

  "Conversion of agent's Premises from the Des model" when {
    val desModel = AgentDetails (
      "agentLegalEntity",
      Some("agentLegalEntityName"),
      None,
      AgentPremises(
        "tradingName",
        DesAddress(
          "Address Line 1",
          "Address Line 2",
          None,
          None,
          "Country",
          None
        ),
        false,
        Msb(true, false, false, false, false ),
        Hvd(false),
        Asp(false),
        Tcsp(false),
        Eab(false),
        Bpsp(false),
        Tditpsp(false),
        "1975",
        None,
        Some("2002-03-01")
      ),
      None,
      None
    )

    "Business structure is Sole Proprietor" must {
      "populate the agent Name field from the Legal entity name in DES" in  {
        val feModel = TradingPremises.convAgentPremises(desModel.copy(agentLegalEntity = "Sole Proprietor",dateOfBirth = Some("19750-01-01")))
        feModel.agentName must be (Some(AgentName("agentLegalEntityName", None, Some("1975-01-01"))))
        feModel.agentCompanyName must be (None)
        feModel.agentPartnership must be (None)
      }
    }

    "Business structure is Limited Liability Partnership" must {
      "populate the agent company Name field from the Legal entity name in DES" in  {
        val feModel = TradingPremises.convAgentPremises(desModel.copy(agentLegalEntity = "Limited Liability Partnership"))
        feModel.agentName must be (None)
        feModel.agentCompanyName must be (Some(AgentCompanyName("agentLegalEntityName")))
        feModel.agentPartnership must be (None)
      }
    }

    "Business structure is Partnership" must {
      "populate the agent partnership field from the Legal entity name in DES" in  {
        val feModel = TradingPremises.convAgentPremises(desModel.copy(agentLegalEntity = "Partnership"))
        feModel.agentName must be (None)
        feModel.agentCompanyName must be (None)
        feModel.agentPartnership must be (Some(AgentPartnership("agentLegalEntityName")))
      }
    }

    "Business structure is Corporate Body" must {
      "populate the agent partnership field from the Legal entity name in DES" in  {
        val feModel = TradingPremises.convAgentPremises(desModel.copy(agentLegalEntity = "Corporate Body"))
        feModel.agentName must be (None)
        feModel.agentCompanyName must be (Some(AgentCompanyName("agentLegalEntityName")))
        feModel.agentPartnership must be (None)
      }
    }

    "Business structure is Unincorporated Body" must {
      "populate none of teh name fields from the Legal entity name in DES" in  {
        val feModel = TradingPremises.convAgentPremises(desModel.copy(agentLegalEntity = "Unincorporated Body"))
        feModel.agentName must be (None)
        feModel.agentCompanyName must be (None)
        feModel.agentPartnership must be (None)
      }
    }
  }
}
