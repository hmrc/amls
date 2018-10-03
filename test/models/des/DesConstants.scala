/*
 * Copyright 2018 HM Revenue & Customs
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

import models.des.aboutthebusiness.{Address => AboutTheBusinessAddress, _}
import models.des.aboutyou.{Aboutyou, IndividualDetails}
import models.des.asp.{Asp => AspModel}
import models.des.bankdetails._
import models.des.businessactivities._
import models.des.businessdetails.{BusinessDetails, BusinessType, CorpAndBodyLlps, UnincorpBody}
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy}
import models.des.hvd.{HvdFromUnseenCustDetails, ReceiptMethods, Hvd => HvdModel}
import models.des.msb._
import models.des.responsiblepeople.{Address, PersonName, _}
import models.des.supervision._
import models.des.tcsp.{TcspAll, TcspTrustCompFormationAgt}
import models.des.tradingpremises.{Address => TradingPremisesAddress, _}
import org.joda.time.LocalDate
import utils.{StatusConstants, AckRefGenerator}

object DesConstants {
  val testChangeIndicators = ChangeIndicators(false)
  val testBusinessDetails = BusinessDetails(BusinessType.SoleProprietor,
    Some(CorpAndBodyLlps("CompanyName", "12345678")),
    Some(UnincorpBody("CompanyName", "TypeOfBusiness")))

  val today = new LocalDate().toString("yyyy-MM-dd")

  val testViewBusinessContactDetails = BusinessContactDetails(
    AboutTheBusinessAddress(
      "BusinessAddressLine1",
      "BusinessAddressLine2",
      Some("BusinessAddressLine3"),
      Some("BusinessAddressLine4"),
      "GB",
      Some("AA1 1AA")),
    true,
    Some(AlternativeAddress(
      "Name", "TradingName",
      AboutTheBusinessAddress("AlternativeAddressLine1",
        "AlternativeAddressLine2",
        Some("AlternativeAddressLine3"),
        Some("AlternativeAddressLine4"),
        "GB",
        Some("AA1 1AA")))),
    "07000111222",
    "BusinessEmail"
  )
  val testAmendViewBusinessContactDetails1 = BusinessContactDetails(
    AboutTheBusinessAddress(
      "new address",
      "BusinessAddressLine2",
      Some("BusinessAddressLine3"),
      Some("BusinessAddressLine4"),
      "GB",
      Some("AA1 1AA")),
    false,
    None,
    "07000111222",
    "BusinessEmail"
  )

  val testBusinessReferencesAll = None

  val testAmendBusinessReferencesAll = Some(PreviouslyRegisteredMLRView(true,
    Some("12345678"),
    false,
    None))

  val testbusinessReferencesAllButSp = VATRegistration(true, Some("123456789"))
  val testAmendBusinessReferencesAllButSp = VATRegistration(false, None)

  val testBusinessReferencesCbUbLlp = CorporationTaxRegisteredCbUbLlp(true, Some("1234567891"))
  val testAmendBusinessReferencesCbUbLlp = CorporationTaxRegisteredCbUbLlp(false, None)

  val testHvdGoodsSold = HvdGoodsSold(true, true, true, true, true, true, true, true, true, true, true, true,
    Some("SpecifyOther"), Some(HowGoodsAreSold(true, true, true)))

  val testBusinessActivitiesAll = BusinessActivitiesAll(
    None,
    Some("2001-01-01"),
    None,
    BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("99999")))),
    Some(FranchiseDetails(true, Some(Seq("FranchiserName1")))),
    Some("12345678901"),
    Some("11223344556"),
    NonUkResidentCustDetails(true, Some(Seq("AD", "GB"))),
    AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("CommercialPackageName")))),
    true,
    true,
    Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true, true)))),
    Some(MlrAdvisor(true, Some(MlrAdvisorDetails(
      Some(AdvisorNameAddress("Name", Some("TradingName"), AboutTheBusinessAddress(
        "AdvisorAddressLine1",
        "AdvisorAddressLine2",
        Some("AdvisorAddressLine3"),
        Some("AdvisorAddressLine4"),
        "GB",
        Some("AA1 1AA")))),
      true,
      None
    ))))
  )

  val testBusinessActivitiesAllWithDateChangeFlag = BusinessActivitiesAll(
    None,
    Some("2001-01-01"),
    Some(false),
    BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("99999")))),
    Some(FranchiseDetails(true, Some(Seq("FranchiserName1")))),
    Some("12345678901"),
    Some("11223344556"),
    NonUkResidentCustDetails(true, Some(Seq("AD", "GB"))),
    AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("CommercialPackageName")))),
    true,
    true,
    Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true, true)))),
    Some(MlrAdvisor(true, Some(MlrAdvisorDetails(
      Some(AdvisorNameAddress("Name", Some("TradingName"), AboutTheBusinessAddress(
        "AdvisorAddressLine1",
        "AdvisorAddressLine2",
        Some("AdvisorAddressLine3"),
        Some("AdvisorAddressLine4"),
        "GB",
        Some("AA1 1AA")))),
      true,
      None
    ))))
  )

  val testBusinessActivities = BusinessActivities(
    Some(MlrActivitiesAppliedFor(true, true, true, true, true, true, true)),
    Some(MsbServicesCarriedOut(true, true, true, true, true)),
    Some(testHvdGoodsSold),
    Some(HvdAlcoholTobacco(true)),
    Some(AspServicesOffered(true, true, true, true, true)),
    Some(TcspServicesOffered(true, true, true, true, true)),
    Some(ServicesforRegOff(true, true, true, true, false, false, true, true, Some("SpecifyOther"))),
    Some(EabServices(true, true, true, true, true, true, true, true, true)),
    Some(testBusinessActivitiesAll)
  )

  val testBusinessActivitiesWithDateChangeFlag = BusinessActivities(
    Some(MlrActivitiesAppliedFor(true, true, true, true, true, true, true)),
    Some(MsbServicesCarriedOut(true, true, true, true, false)),
    Some(testHvdGoodsSold),
    Some(HvdAlcoholTobacco(true)),
    Some(AspServicesOffered(true, true, true, true, true)),
    Some(TcspServicesOffered(true, true, true, true, true)),
    Some(ServicesforRegOff(true, true, true, true, false, false, true, true, Some("SpecifyOther"))),
    Some(EabServices(true, true, true, true, true, true, true, true, true)),
    Some(testBusinessActivitiesAllWithDateChangeFlag)
  )

  val testAmendBusinessActivities = BusinessActivities(
    Some(MlrActivitiesAppliedFor(true, true, true, false, false, false, true)),
    Some(MsbServicesCarriedOut(true, true, true, true, false)),
    Some(testHvdGoodsSold),
    Some(HvdAlcoholTobacco(true)),
    Some(AspServicesOffered(true, true, true, true, true)),
    Some(TcspServicesOffered(true, true, true, true, true)),
    Some(ServicesforRegOff(true, true, true, true, false, false, true, true, Some("SpecifyOther"))),
    Some(EabServices(true, true, true, true, true, true, true, true, true)),
    None)

  val AgentPremisesModel1 = AgentPremises("TradingName",
    TradingPremisesAddress("AddressLine1",
      "AddressLine2",
      Some("AddressLine3"),
      Some("AddressLine4"),
      "AD",
      Some("AA1 1AA")),
    true,
    Msb(true, false, true, true, true),
    Hvd(true),
    Asp(false),
    Tcsp(true),
    Eab(false),
    Bpsp(true),
    Tditpsp(false),
    Some("2001-01-01")
  )

  val AgentPremisesModel2 = AgentPremises("TradingName",
    TradingPremisesAddress("AddressLine1",
      "AddressLine2",
      Some("AddressLine3"),
      Some("AddressLine4"),
      "AD",
      Some("AA1 1AA")),
    true,
    Msb(false, false, false, false, false),
    Hvd(true),
    Asp(false),
    Tcsp(true),
    Eab(false),
    Bpsp(true),
    Tditpsp(false),
    Some("2001-01-01")
  )

  val AgentPremisesModel3 = AgentPremises("TradingName",
    TradingPremisesAddress("AddressLine1",
      "AddressLine2",
      Some("AddressLine3"),
      Some("AddressLine4"),
      "AD",
      Some("AA1 1AA")),
    true,
    Msb(false, false, false, false, false),
    Hvd(false),
    Asp(false),
    Tcsp(false),
    Eab(false),
    Bpsp(false),
    Tditpsp(false),
    Some("2001-01-01")
  )

  val agentPremisesapi51 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val agentDetailsAPI51 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("1970-01-01"),
    Some("AgentLegalEntityName"),
    agentPremisesapi51,
    None,
    None,
    None,
    Some("Added"),
    Some(StringOrInt(111111))
  )

  val agentPremisesapi52 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, true, true, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val agentDetailsAPI52 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("1970-01-01"),
    Some("aaaaaaaaaaa"),
    agentPremisesapi52,
    None,
    None,
    None,
    Some("Added"),
    None
  )

  val agentPremisesapi53 = AgentPremises("TradingName",
    TradingPremisesAddress("AgentAddressLine1",
      "AgentAddressLine2",
      Some("AgentAddressLine3"),
      Some("AgentAddressLine4"),
      "GB",
      Some("XX1 1XX")),
    true,
    Msb(true, true, true, true, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("2001-01-01"),
    None
  )

  val agentDetailsAPI53 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("1970-01-01"),
    Some("AgentLegalEntityName2"),
    agentPremisesapi53,
    None,
    None,
    None,
    Some("Added"),
    None
  )


  val agentPremisesapi61 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )
  val agentDetailsAPI61 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("1970-01-01"),
    Some("AgentLegalEntityName"),
    agentPremisesapi61,
    None,
    None,
    None,
    Some(StatusConstants.Unchanged),
    Some(StringOrInt("1"))
  )

  val agentPremisesapi61Release7 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, true, true, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    None,
    None
  )

  val agentPremisesapi6Release7 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("1970-01-01"),
    Some("aaaaaaaaaaa"),
    agentPremisesapi61Release7,
    None,
    None,
    None,
    Some(StatusConstants.Unchanged),
    Some(StringOrInt("2"))
  )
  val agentAmendDetailsAPI61 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("1970-01-01"),
    Some("AgentLegalEntityName for amend"),
    agentPremisesapi61,
    None,
    None,
    None,
    Some("Added"),
    Some(StringOrInt("133333"))
  )

  val agentPremisesapi62 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, true, true, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )



  val agentDetailsAPI62 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("1970-01-01"),
    Some("aaaaaaaaaaa"),
    agentPremisesapi62,
    None,
    None,
    None,
    Some(StatusConstants.Unchanged),
    Some(StringOrInt("2"))
  )

  val agentPremisesapi63 = AgentPremises("TradingName",
    TradingPremisesAddress("AgentAddressLine1",
      "AgentAddressLine2",
      Some("AgentAddressLine3"),
      Some("AgentAddressLine4"),
      "GB",
      Some("XX1 1XX")),
    true,
    Msb(true, true, true, true, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("2001-01-01"),
    None
  )

  val agentDetailsAPI63 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("1970-01-01"),
    Some("AgentLegalEntityName2"),
    agentPremisesapi63,
    None,
    None,
    None,
    Some(StatusConstants.Unchanged),
    Some(StringOrInt("3"))
  )

  val viewStatusOwnBusinessPremises = Some(OwnBusinessPremises(true, Some(Seq(
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName"),
      TradingPremisesAddress("OwnBusinessAddressLine1",
        "OwnBusinessAddressLine2",
        Some("OwnBusinessAddressLine3"),
        Some("OwnBusinessAddressLine4"),
        "GB",
        Some("YY1 1YY")),
      false,
      Msb(false, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(false),
      "2001-01-01",
      None,
      Some(StringOrInt(444444)),
      None
    ),
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName1"),
      TradingPremisesAddress("OB11AddressLine1",
        "OB1AddressLine2",
        Some("OB1AddressLine3"),
        Some("OB1AddressLine4"),
        "GB",
        Some("XX1 1XX")),
      false,
      Msb(false, false, true, true, false),
      Hvd(true),
      Asp(true),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(true),
      "2001-01-01",
      None,
      Some(StringOrInt(555555)),
      Some(StatusConstants.Deleted)
    )
  ))))

  val amendStatusOwnBusinessPremises = Some(OwnBusinessPremises(true, Some(Seq(
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName"),
      TradingPremisesAddress("OwnBusinessAddressLine1",
        "OwnBusinessAddressLine2",
        Some("OwnBusinessAddressLine3"),
        Some("OwnBusinessAddressLine4"),
        "GB",
        Some("YY1 1YY")),
      false,
      Msb(false, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(false),
      "2001-01-01",
      None,
      Some(StringOrInt(444444)),
      Some(StatusConstants.Unchanged)
    ),
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName1"),
      TradingPremisesAddress("OB11AddressLine1",
        "OB1AddressLine2",
        Some("OB1AddressLine3"),
        Some("OB1AddressLine4"),
        "GB",
        Some("XX1 1XX")),
      false,
      Msb(false, false, true, true, false),
      Hvd(true),
      Asp(true),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(true),
      "2001-01-01",
      None,
      Some(StringOrInt(555555)),
      Some(StatusConstants.Deleted)
    )
  ))))

  val amendStatusOwnBusinessPremisesR7 = Some(OwnBusinessPremises(true, Some(Seq(
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName"),
      TradingPremisesAddress("OwnBusinessAddressLine1",
        "OwnBusinessAddressLine2",
        Some("OwnBusinessAddressLine3"),
        Some("OwnBusinessAddressLine4"),
        "GB",
        Some("YY1 1YY")),
      false,
      Msb(false, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(false),
      "2001-01-01",
      None,
      Some(StringOrInt(444444)),
      Some(StatusConstants.Unchanged),
      dateChangeFlag = Some(false)
    ),
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName1"),
      TradingPremisesAddress("OB11AddressLine1",
        "OB1AddressLine2",
        Some("OB1AddressLine3"),
        Some("OB1AddressLine4"),
        "GB",
        Some("XX1 1XX")),
      false,
      Msb(false, false, true, true, false),
      Hvd(true),
      Asp(true),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(true),
      "2001-01-01",
      None,
      Some(StringOrInt(555555)),
      Some(StatusConstants.Deleted),
      dateChangeFlag = Some(false)
    )
  ))))

  val ownBusinessPremisesTP = Some(OwnBusinessPremises(true, Some(Seq(
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName"),
      TradingPremisesAddress("OwnBusinessAddressLine1",
        "OwnBusinessAddressLine2",
        Some("OwnBusinessAddressLine3"),
        Some("OwnBusinessAddressLine4"),
        "GB",
        Some("YY1 1YY")),
      false,
      Msb(false, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(false),
      "2001-05-05",
      None,
      Some(StringOrInt(444444)),
      Some(StatusConstants.Unchanged)
    ),
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName1"),
      TradingPremisesAddress("OB11AddressLine1",
        "OB1AddressLine2",
        Some("OB1AddressLine3"),
        Some("OB1AddressLine4"),
        "GB",
        Some("XX1 1XX")),
      false,
      Msb(false, false, true, true, false),
      Hvd(true),
      Asp(true),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(true),
      "2001-01-01",
      None,
      Some(StringOrInt(555555)),
      Some(StatusConstants.Unchanged)
    )
  ))))

  val ownBusinessPremisesTPR7 = Some(OwnBusinessPremises(true, Some(Seq(
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName"),
      TradingPremisesAddress("OwnBusinessAddressLine1",
        "OwnBusinessAddressLine2",
        Some("OwnBusinessAddressLine3"),
        Some("OwnBusinessAddressLine4"),
        "GB",
        Some("YY1 1YY")),
      false,
      Msb(false, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(false),
      "2001-05-05",
      None,
      Some(StringOrInt(444444)),
      Some(StatusConstants.Unchanged),
      dateChangeFlag = Some(false)
    ),
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName1"),
      TradingPremisesAddress("OB11AddressLine1",
        "OB1AddressLine2",
        Some("OB1AddressLine3"),
        Some("OB1AddressLine4"),
        "GB",
        Some("XX1 1XX")),
      false,
      Msb(false, false, true, true, false),
      Hvd(true),
      Asp(true),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(true),
      "2001-01-01",
      None,
      Some(StringOrInt(555555)),
      Some(StatusConstants.Unchanged),
      dateChangeFlag = Some(false)
    )
  ))))

  val ownBusinessPremisesTPAPI6 = Some(OwnBusinessPremises(true, Some(Seq(
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName"),
      TradingPremisesAddress("OwnBusinessAddressLine1",
        "OwnBusinessAddressLine2",
        Some("OwnBusinessAddressLine3"),
        Some("OwnBusinessAddressLine4"),
        "GB",
        Some("YY1 1YY")),
      false,
      Msb(false, false, false, false, false),
      Hvd(false),
      Asp(false),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(false),
      "2001-05-05",
      None,
      Some(StringOrInt("444444")),
      Some(StatusConstants.Unchanged)
    ),
    OwnBusinessPremisesDetails(
      Some("OwnBusinessTradingName1"),
      TradingPremisesAddress("OB11AddressLine1",
        "OB1AddressLine2",
        Some("OB1AddressLine3"),
        Some("OB1AddressLine4"),
        "GB",
        Some("XX1 1XX")),
      false,
      Msb(false, false, true, true, false),
      Hvd(true),
      Asp(true),
      Tcsp(true),
      Eab(true),
      Bpsp(true),
      Tditpsp(true),
      "2001-01-01",
      None,
      Some(StringOrInt("555555")),
      Some(StatusConstants.Unchanged)
    )
  ))))


  val viewStatusAgentPremises1 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val viewStatusAgentDetails1 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("AgentLegalEntityName"),
    Some("1970-01-01"),
    viewStatusAgentPremises1,
    None,
    None
  )

  val viewStatusAgentPremises2 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val viewStatusAgentDetails2 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("AgentLegalEntityName"),
    Some("1970-01-01"),
    viewStatusAgentPremises2,
    None,
    None,
    None,
    Some(StatusConstants.Deleted),
    Some(StringOrInt("222222"))
  )

  val viewStatusAgentPremises3 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val viewStatusAgentDetails3 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("AgentLegalEntityName"),
    Some("1970-01-01"),
    viewStatusAgentPremises3,
    None,
    None,
    None,
    Some(StatusConstants.Added),
    Some(StringOrInt("333333"))
  )

  val viewStatusAgentPremises4 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val viewStatusAgentDetails4 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("AgentLegalEntityName"),
    Some("1970-01-01"),
    viewStatusAgentPremises4,
    None,
    None,
    None,
    None,
    Some(StringOrInt("444444"))
  )

  val amenStatusAgentPremises1 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val amendStatusAgentDetails1 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("AgentLegalEntityName"),
    Some("1970-01-01"),
    amenStatusAgentPremises1,
    None,None,
    Some(StatusConstants.Added),
    None
  )

  val amendStatusAgentPremises2 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val amendStatusAgentDetails2 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("AgentLegalEntityName"),
    Some("1970-01-01"),
    amendStatusAgentPremises2,
    None,
    None,
    None,
    Some(StatusConstants.Deleted),
    Some(StringOrInt("222222"))
  )

  val amendStatusAgentPremises3 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val amendStatusAgentDetails3 = AgentDetails(
    "Limited Liability Partnership",
    None,
    Some("AgentLegalEntityName"),
    Some("1970-01-01"),
    amendStatusAgentPremises3,
    None,
    None,
    None,
    Some(StatusConstants.Updated),
    Some(StringOrInt("333333"))
  )

  val amendStatusAgentPremises4 = AgentPremises("aaaaaaaaaaaa",
    TradingPremisesAddress("a",
      "a",
      Some("a"),
      Some("a"),
      "GB",
      Some("AA1 1AA")),
    true,
    Msb(true, true, false, false, false),
    Hvd(true),
    Asp(true),
    Tcsp(true),
    Eab(true),
    Bpsp(true),
    Tditpsp(true),
    Some("1967-08-13"),
    None
  )

  val amendStatusAgentDetails4 = AgentDetails(
    "Sole Proprietor",
    None,
    Some("AgentLegalEntityName"),
    Some("1970-01-01"),
    amendStatusAgentPremises4,
    None,
    None,
    None,
    Some(StatusConstants.Unchanged),
    Some(StringOrInt("444444"))
  )

  val testTradingPremisesAPI5 = TradingPremises(
    ownBusinessPremisesTP,
    Some(AgentBusinessPremises(
      true,
      Some(Seq(agentDetailsAPI51,
        agentDetailsAPI52,
        agentDetailsAPI53
      ))
    )
  ))

  val viewStatusTradingPremises = TradingPremises(
    viewStatusOwnBusinessPremises,
    Some(AgentBusinessPremises(
      true,
      Some(Seq(viewStatusAgentDetails2,
        viewStatusAgentDetails3,
        viewStatusAgentDetails4
      ))
    )
  ))

  val amendStatusRequestTradingPremises = TradingPremises(
    viewStatusOwnBusinessPremises,
    Some(AgentBusinessPremises(
      true,
      Some(Seq(viewStatusAgentDetails1,
        viewStatusAgentDetails2,
        viewStatusAgentDetails3.copy(agentLegalEntity = "Limited Liability Partnership"),
        viewStatusAgentDetails4
      ))
    )
  ))
  val testTradingPremisesAPI6 = TradingPremises(
    ownBusinessPremisesTPAPI6,
    Some(AgentBusinessPremises(
      true,
      Some(Seq(agentDetailsAPI61,
        agentDetailsAPI62,
        agentDetailsAPI63
      ))
    )
  ))

  val tradingPremisesAPI6Release7 = TradingPremises(
    Some(OwnBusinessPremises(false,None)),
    Some(AgentBusinessPremises(
      true,
      Some(Seq(agentPremisesapi6Release7))
    )
    ))

  val amendStatusTradingPremisesAPI6 = TradingPremises(
    amendStatusOwnBusinessPremises,
    Some(AgentBusinessPremises(
      true,
      Some(Seq(
        amendStatusAgentDetails2,
        amendStatusAgentDetails3,
        amendStatusAgentDetails4,
        amendStatusAgentDetails1
      ))
    )
  ))

  val testAmendTradingPremisesAPI6 = TradingPremises(
    ownBusinessPremisesTP,
    Some(AgentBusinessPremises(
      true,
      Some(Seq(agentAmendDetailsAPI61,
        agentDetailsAPI62,
        agentDetailsAPI63
      ))
    )
  ))

  val testBankDetails = Some(BankDetailsView(
    Some("3"),
    Some(List(
      BankAccountView(
        "AccountName",
        "This business's",
        true,
        ukAccountView("123456", "12345678")
      ),
      BankAccountView(
        "AccountName1",
        "Personal",
        false,
        IBANNumberView("87654321")
      ),
      BankAccountView(
        "AccountName2",
        "Another business's",
        false,
        AccountNumberView("87654321")
      )
    ))
  ))

  val testAmendBankDetails = Some(BankDetailsView(
    Some("1"),
    Some(List(
      BankAccountView(
        "AccountName",
        "This business's",
        true,
        ukAccountView("123456", "12345678")
      )
    ))
  ))

  val desBankDetails = BankDetails("3",
    Some(Seq(BankAccount("Personal account", "Personal", true, ukAccount("112233", "12345678")),
      BankAccount("Business account", "This business's", false, AccountNumber("12345678")),
      BankAccount("Another Business account", "Another business's", false, IBANNumber("12345678")))))

  val testMsb = MoneyServiceBusiness(
    Some(MsbAllDetails(
      Some("999999"),
      true,
      Some(CountriesList(List("AD", "GB"))),
      true)
    ),
    Some(MsbMtDetails(
      true,
      Some("123456"),
      IpspServicesDetails(
        true,
        Some(List(IpspDetails("IPSPName1", "IPSPMLRRegNo1")))
      ),
      true,
      Some("11111111111"),
      Some(CountriesList(List("GB", "AD"))),
      Some(CountriesList(List("AD", "GB"))),
      Some(false)
    )),
    Some(MsbCeDetailsR7( Some(true),
      Some(CurrencySourcesR7(
        Some(MSBBankDetails(
          true,
          Some(List("BankNames1"))
        )),
        Some(CurrencyWholesalerDetails(
          true,
          Some(List("CurrencyWholesalerNames"))
        )),
        true
      )),
      "11234567890",
      Some(CurrSupplyToCust(List("GBP", "XYZ", "ABC")))
    )),
    Some(MsbFxDetails("234234234"))
  )

  val testMsbR6 = MoneyServiceBusiness(
    Some(MsbAllDetails(
      Some("999999"),
      true,
      Some(CountriesList(List("AD", "GB"))),
      true)
    ),
    Some(MsbMtDetails(
      true,
      Some("123456"),
      IpspServicesDetails(
        true,
        Some(List(IpspDetails("IPSPName1", "IPSPMLRRegNo1")))
      ),
      true,
      Some("11111111111"),
      Some(CountriesList(List("GB", "AD"))),
      Some(CountriesList(List("AD", "GB"))),
      None
    )),
    Some(MsbCeDetailsR7( None,
      Some(CurrencySourcesR7(
        Some(MSBBankDetails(
          true,
          Some(List("BankNames1"))
        )),
        Some(CurrencyWholesalerDetails(
          true,
          Some(List("CurrencyWholesalerNames"))
        )),
        true
      )),
      "11234567890",
      Some(CurrSupplyToCust(List("GBP", "XYZ", "ABC")))
    )),
    Some(MsbFxDetails("234234234"))
  )

  val testAmendMsb = MoneyServiceBusiness(
    Some(MsbAllDetails(
      Some("999999"),
      true,
      Some(CountriesList(List("AD", "GB"))),
      true)
    ),
    Some(MsbMtDetails(
      true,
      Some("123456"),
      IpspServicesDetails(
        false,
        None
      ),
      true,
      Some("11111111111"),
      Some(CountriesList(List("GB", "AD"))),
      Some(CountriesList(List("AD", "GB")))
    )),
    Some(MsbCeDetailsR7( Some(true),
      Some(CurrencySourcesR7(
        Some(MSBBankDetails(
          true,
          Some(List("BankNames1"))
        )),
        Some(CurrencyWholesalerDetails(
          true,
          Some(List("CurrencyWholesalerNames"))
        )),
        true
      )),
      "11234567890",
      Some(CurrSupplyToCust(List("GBP", "XYZ", "ABC")))
    )),
    None
  )

  val testHvd = HvdModel(true,
    Some("2001-01-01"),
    None,
    true,
    Some(0),
    Some(HvdFromUnseenCustDetails(
      true,
      Some(ReceiptMethods(true, true, true, Some("aaaaaaaaaaaaa")))
    ))
  )

  val testAsp = AspModel(true, None)
  val testAmendAsp = AspModel(false, None)

  val testSupervisorDetails = SupervisorDetails(
    "NameOfLastSupervisor",
    "2001-01-01",
    "2001-01-01",
    None,
    "SupervisionEndingReason")
  val testSupervisionDetails = SupervisionDetails(true, Some(testSupervisorDetails))
  val testAspOrTcsp = AspOrTcsp(
    Some(testSupervisionDetails),
    Some(ProfessionalBodyDetails(
      true,
      Some("DetailsIfFinedWarned"),
      Some(ProfessionalBodyDesMember(
        true,
        Some(MemberOfProfessionalBody(
          true, true, true, true, true, true, true, true, true, true, true, true, true, true, Some("SpecifyOther")
        ))
      ))
    ))
  )

  val testAmendAspOrTcsp = AspOrTcsp(
    Some(SupervisionDetails(
      false,
      None
    )),
    Some(ProfessionalBodyDetails(
      true,
      Some("DetailsIfFinedWarned"),
      Some(ProfessionalBodyDesMember(
        true,
        Some(MemberOfProfessionalBody(
          true, true, true, true, true, true, true, true, true, true, true, true, true, true, Some("SpecifyOther")
        ))
      ))
    ))
  )

  val testTcspAll = TcspAll(true, Some("111111111111111"))
  val testAmendTcspAll = TcspAll(true, Some("2222222222222"))

  val testTcspTrustCompFormationAgt = TcspTrustCompFormationAgt(true, true)
  val testAmendTcspTrustCompFormationAgt = TcspTrustCompFormationAgt(false, true)

  val testEabAll = EabAll(true, Some("EstAgncActProhibProvideDetails"), true, Some("PrevWarnWRegProvideDetails"))
  val testAmendEabAll = EabAll(true, Some("EstAgncActProhibProvideDetails"), false, None)

  val testEabResdEstAgncy = EabResdEstAgncy(true, Some("The Property Ombudsman Limited"), None)
  val testAmendEabResdEstAgncy = EabResdEstAgncy(false, None, None)
  val responsiblePersons2 = ResponsiblePersons(
    Some(NameDetails(
      PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbb"), Some("bbbbbbbbbbb")),
      Some(OthrNamesOrAliasesDetails(
        true,
        Some(List("bbbbbbbbbbb"))
      )),
      Some(PreviousNameDetails(
        true,
        Some(PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
        Some("1967-08-13"),
        Some(false)
      ))
    )),
    Some(NationalityDetails(
      true,
      Some(IdDetail(
        Some(UkResident("BB000000A")),
        None
      )),
      Some("GB"),
      Some("GB")
    )),
    None,
    Some(CurrentAddress(
      AddressWithChangeDate("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA"))
    )),
    Some("0-6 months"),
    Some(AddressUnderThreeYears(Address("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA")))),
    Some("0-6 months"),
    Some(AddressUnderThreeYears(Address("a", "a", Some("a"), Some("a"), "AD", Some("AA1 1AA")))),
    Some("0-6 months"),
    Some(PositionInBusiness(
      Some(SoleProprietor(true, true)),
      None,
      None
    )),
    Some(RegDetails(true, Some("111111111"), true, Some("1111111111"))
    ),
    true,
    Some("bbbbbbbbbb"),
    true,
    Some("bbbbbbbbbbb"),
    None,
    Some(false),
    Some(MsbOrTcsp(true)),
    extra = RPExtra(None, None, Some(StatusConstants.Added))
  )

  val responsiblePersons3 = ResponsiblePersons(
    Some(NameDetails(
      PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbb"), Some("bbbbbbbbbbb")),
      Some(OthrNamesOrAliasesDetails(
        true,
        Some(List("bbbbbbbbbbb"))
      )),
      Some(PreviousNameDetails(
        true,
        Some(PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
        Some("1967-08-13"),
        Some(false)
      ))
    )),
    Some(NationalityDetails(
      true,
      Some(IdDetail(
        Some(UkResident("BB000000A")),
        None
      )),
      Some("GB"),
      Some("GB")
    )),
    None,
    Some(CurrentAddress(
      AddressWithChangeDate("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA"))
    )),
    Some("0-6 months"),
    Some(AddressUnderThreeYears(Address("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA")))),
    Some("0-6 months"),
    Some(AddressUnderThreeYears(Address("a", "a", Some("a"), Some("a"), "AD", Some("AA1 1AA")))),
    Some("0-6 months"),
    Some(PositionInBusiness(
      Some(SoleProprietor(true, true)),
      None,
      None
    )),
    Some(RegDetails(true, Some("111111111"), true, Some("1111111111"))
    ),
    true,
    Some("bbbbbbbbbb"),
    true,
    Some("bbbbbbbbbbb"),
    None,
    Some(false),
    Some(MsbOrTcsp(true)),
    extra = RPExtra(Some(StringOrInt("123456")), None, Some(StatusConstants.Deleted))
  )

  val viewResponsiblePersons2 = ResponsiblePersons(
    Some(NameDetails(
      PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbb"), Some("bbbbbbbbbbb")),
      Some(OthrNamesOrAliasesDetails(
        true,
        Some(List("bbbbbbbbbbb"))
      )),
      Some(PreviousNameDetails(
        true,
        Some(PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
        Some("1967-08-13")
      ))
    )),
    Some(NationalityDetails(
      true,
      Some(IdDetail(
        Some(UkResident("BB000000A")),
        None
      )),
      Some("GB"),
      Some("GB")
    )),
    None,
    Some(CurrentAddress(
      AddressWithChangeDate("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA"))
    )),
    Some("0-6 months"),
    Some(AddressUnderThreeYears(Address("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA")))),
    Some("0-6 months"),
    Some(AddressUnderThreeYears(Address("a", "a", Some("a"), Some("a"), "AD", Some("AA1 1AA")))),
    Some("0-6 months"),
    Some(PositionInBusiness(
      Some(SoleProprietor(true, true)),
      None,
      None
    )),
    Some(RegDetails(true, Some("111111111"), true, Some("1111111111"))
    ),
    true,
    Some("bbbbbbbbbb"),
    true,
    Some("bbbbbbbbbbb"),
    None,
    None,
    Some(MsbOrTcsp(true)),
    extra = RPExtra(None, None, None)
  )

  val viewResponsiblePersons3 = ResponsiblePersons(
    Some(NameDetails(
      PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbb"), Some("bbbbbbbbbbb")),
      Some(OthrNamesOrAliasesDetails(
        true,
        Some(List("bbbbbbbbbbb"))
      )),
      Some(PreviousNameDetails(
        true,
        Some(PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
        Some("1967-08-13")
      ))
    )),
    Some(NationalityDetails(
      true,
      Some(IdDetail(
        Some(UkResident("BB000000A")),
        None
      )),
      Some("GB"),
      Some("GB")
    )),
    None,
    Some(CurrentAddress(
      AddressWithChangeDate("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA"))
    )),
    Some("0-6 months"),
    Some(AddressUnderThreeYears(Address("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA")))),
    Some("0-6 months"),
    Some(AddressUnderThreeYears(Address("a", "a", Some("a"), Some("a"), "AD", Some("AA1 1AA")))),
    Some("0-6 months"),
    Some(PositionInBusiness(
      Some(SoleProprietor(true, true)),
      None,
      None
    )),
    Some(RegDetails(true, Some("111111111"), true, Some("1111111111"))
    ),
    true,
    Some("bbbbbbbbbb"),
    true,
    Some("bbbbbbbbbbb"),
    None,
    None,
    Some(MsbOrTcsp(true)),
    extra = RPExtra(Some(StringOrInt("123456")), None, Some(StatusConstants.Deleted))
  )

  val testResponsiblePersons = Seq(
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("Aliases1"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
          Some("2001-01-01")
        ))
      )),
      Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            Some(PassportDetail(
              true,
              PassportNum(Some("AA1111111"), None)
            ))
          ))
        )),
        Some("AA"),
        Some("AA")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate(
          "CurrentAddressLine1",
          "CurrentAddressLine2",
          Some("CurrentAddressLine3"),
          Some("CurrentAddressLine4"),
          "GB",
          Some("AA1 1AA")
        )
      )),
      Some("3+ years"),
      None,
      None,
      None,
      None,
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true)),
        None,
        None
      )),
      Some(RegDetails(
        true,
        Some("123456789"),
        true,
        Some("1234567890")
      )),
      true,
      Some("DescOfPrevExperience"),
      true,
      Some("TrainingDetails"),
      None,
      None,
      Some(MsbOrTcsp(true)),
      extra = RPExtra(Some(StringOrInt("333333")), None, Some("added"), None, None, None)
    ),
    responsiblePersons2
  )

  val testAmendResponsiblePersons = Seq(
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("Aliases1"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
          Some("2001-01-01"),
          Some(false)
        ))
      )),
      Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            Some(PassportDetail(
              true,
              PassportNum(Some("AA1111111"), None)
            ))
          ))
        )),
        Some("AA"),
        Some("AA")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate(
          "CurrentAddressLine1",
          "CurrentAddressLine2",
          Some("CurrentAddressLine3"),
          Some("CurrentAddressLine4"),
          "GB",
          Some("AA1 1AA")
        )
      )),
      Some("3+ years"),
      None,
      None,
      None,
      None,
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true)),
        None,
        None
      )),
      Some(RegDetails(
        true,
        Some("123456789"),
        true,
        Some("1234567890")
      )),
      true,
      Some("DescOfPrevExperience"),
      true,
      Some("TrainingDetails"),
      None,
      Some(false),
      Some(MsbOrTcsp(true)),
      extra = RPExtra(Some(StringOrInt("333333")), None, Some(StatusConstants.Updated), Some(false), Some("some test result"), Some("2012-12-12"))
    ),
    responsiblePersons2
  )

  val updatedRPForAmendment = ResponsiblePersons(
    Some(NameDetails(
      PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
      Some(OthrNamesOrAliasesDetails(
        true,
        Some(List("Aliases1"))
      )),
      Some(PreviousNameDetails(
        true,
        Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
        Some("2001-01-01")
      ))
    )),
    Some(NationalityDetails(
      false,
      Some(IdDetail(
        None,
        Some(NonUkResident(
          "2001-01-01",
          true,
          Some(PassportDetail(
            true,
            PassportNum(Some("AA1111111"), None)
          ))
        ))
      )),
      Some("AA"),
      Some("AA")
    )),
    None,
    Some(CurrentAddress(
      AddressWithChangeDate(
        "CurrentAddressLine1",
        "CurrentAddressLine2",
        Some("CurrentAddressLine3"),
        Some("CurrentAddressLine4"),
        "GB",
        Some("AA1 1AA")
      )
    )),
    Some("3+ years"),
    None,
    None,
    None,
    None,
    Some(PositionInBusiness(
      Some(SoleProprietor(true, true)),
      None,
      None
    )),
    Some(RegDetails(
      true,
      Some("123456789"),
      true,
      Some("1234567890")
    )),
    true,
    Some("DescOfPrevExperience"),
    true,
    Some("TrainingDetails"),
    None,
    Some(false),
    Some(MsbOrTcsp(true)),
    extra = RPExtra(Some(StringOrInt("333333")), None, None, None, Some("10"), Some("some test result"), Some("2012-12-12"))
  )


  val viewResponsiblePersonsAPI5 = Seq(
    updatedRPForAmendment,
    updatedRPForAmendment,
    viewResponsiblePersons3
  )

  val amendStatusResponsiblePersonsAPI5 = Seq(
    updatedRPForAmendment.copy(nameDetails = None,
      extra = RPExtra(Some(StringOrInt("77777")), None, None, Some(false), None, Some("some test result"), Some("2012-12-12"))),
    updatedRPForAmendment,
    viewResponsiblePersons2,
    viewResponsiblePersons3
  )

  val testResponsiblePersonsForRp = Seq(
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("Aliases1"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
          Some("2001-01-01")
        ))
      )),
      Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            Some(PassportDetail(
              true,
              PassportNum(Some("AA1111111"), None)
            ))
          ))
        )),
        Some("AA"),
        Some("AA")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate(
          "CurrentAddressLine1",
          "CurrentAddressLine2",
          Some("CurrentAddressLine3"),
          Some("CurrentAddressLine4"),
          "GB",
          Some("AA1 1AA")
        )
      )),
      Some("3+ years"),
      None,
      None,
      None,
      None,
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true)),
        None,
        None
      )),
      Some(RegDetails(
        true,
        Some("123456789"),
        true,
        Some("1234567890")
      )),
      false,
      None,
      true,
      Some("TrainingDetails"),
      Some(today),
      None,
      Some(MsbOrTcsp(false)),
      passedFitAndProperTest = None,
      passedApprovalCheck = None,
      extra = RPExtra(Some(StringOrInt(333333)), None, None, None, None, None)
    ),
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbb"), Some("bbbbbbbbbbb")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("bbbbbbbbbbb"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
          Some("1967-08-13")
        ))
      )),
      Some(NationalityDetails(
        true,
        Some(IdDetail(
          Some(UkResident("BB000000A")),
          None,
          dateOfBirth = Some("2001-01-01")
        )),
        Some("GB"),
        Some("GB")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA"))
      )),
      Some("0-6 months"),
      Some(AddressUnderThreeYears(Address("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA")))),
      Some("0-6 months"),
      Some(AddressUnderThreeYears(Address("a", "a", Some("a"), Some("a"), "GB", Some("AA1 1AA")))),
      Some("7-12 months"),
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true)),
        None,
        None
      )),
      Some(RegDetails(true, Some("111111111"), true, Some("1111111111"))
      ),
      true,
      Some("bbbbbbbbbb"),
      false,
      None,
      Some(today),
      None,
      Some(MsbOrTcsp(true)),
      passedFitAndProperTest = None,
      passedApprovalCheck = None,
      extra = RPExtra(Some(StringOrInt(222222)), None, None, None, None, None)
    )
  )

  val testResponsiblePersonsForRpPhase2 = Seq(
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("Aliases1"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
          Some("2001-01-01")
        ))
      )),
      Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            Some(PassportDetail(
              true,
              PassportNum(Some("AA1111111"), None)
            ))
          ))
        )),
        Some("AA"),
        Some("AA")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate(
          "CurrentAddressLine1",
          "CurrentAddressLine2",
          Some("CurrentAddressLine3"),
          Some("CurrentAddressLine4"),
          "GB",
          Some("AA1 1AA")
        )
      )),
      Some("3+ years"),
      None,
      None,
      None,
      None,
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true)),
        None,
        None
      )),
      Some(RegDetails(
        true,
        Some("123456789"),
        true,
        Some("1234567890")
      )),
      false,
      None,
      true,
      Some("TrainingDetails"),
      Some(today),
      None,
      Some(MsbOrTcsp(false)),
      passedFitAndProperTest = Some(false),
      passedApprovalCheck = None,
      extra = RPExtra(Some(StringOrInt(333333)), None, None, None, None, None)
    ),
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbb"), Some("bbbbbbbbbbb")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("bbbbbbbbbbb"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
          Some("1967-08-13")
        ))
      )),
      Some(NationalityDetails(
        true,
        Some(IdDetail(
          Some(UkResident("BB000000A")),
          None,
          dateOfBirth = Some("2001-01-01")
        )),
        Some("GB"),
        Some("GB")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA"))
      )),
      Some("0-6 months"),
      Some(AddressUnderThreeYears(Address("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA")))),
      Some("0-6 months"),
      Some(AddressUnderThreeYears(Address("a", "a", Some("a"), Some("a"), "GB", Some("AA1 1AA")))),
      Some("7-12 months"),
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true)),
        None,
        None
      )),
      Some(RegDetails(true, Some("111111111"), true, Some("1111111111"))
      ),
      true,
      Some("bbbbbbbbbb"),
      false,
      None,
      Some(today),
      None,
      Some(MsbOrTcsp(true)),
      passedFitAndProperTest = Some(true),
      passedApprovalCheck = None,
      extra = RPExtra(Some(StringOrInt(222222)), None, None, None, None, None)
    )
  )


  val testResponsiblePersonsForRpAPI6 = Seq(
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("Aliases1"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
          Some("2001-01-01")
        ))
      )),
      Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            Some(PassportDetail(
              true,
              PassportNum(Some("AA1111111"), None)
            ))
          ))
        )),
        Some("AA"),
        Some("AA")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate(
          "CurrentAddressLine1",
          "CurrentAddressLine2",
          Some("CurrentAddressLine3"),
          Some("CurrentAddressLine4"),
          "GB",
          Some("AA1 1AA")
        )
      )),
      Some("3+ years"),
      None,
      None,
      None,
      None,
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true)),
        None,
        None
      )),
      Some(RegDetails(
        true,
        Some("123456789"),
        true,
        Some("1234567890")
      )),
      false,
      None,
      true,
      Some("TrainingDetails"),
      Some(today),
      None,
      Some(MsbOrTcsp(false)),
      extra = RPExtra(Some(StringOrInt("333333")), None, Some(StatusConstants.Unchanged), None, None, None)
    ),
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbb"), Some("bbbbbbbbbbb")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("bbbbbbbbbbb"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
          Some("1967-08-13")
        ))
      )),
      Some(NationalityDetails(
        true,
        Some(IdDetail(
          Some(UkResident("BB000000A")),
          None
        )),
        Some("GB"),
        Some("GB")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA"))
      )),
      Some("0-6 months"),
      Some(AddressUnderThreeYears(Address("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA")))),
      Some("0-6 months"),
      Some(AddressUnderThreeYears(Address("a", "a", Some("a"), Some("a"), "GB", Some("AA1 1AA")))),
      Some("7-12 months"),
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true)),
        None,
        None
      )),
      Some(RegDetails(true, Some("111111111"), true, Some("1111111111"))
      ),
      true,
      Some("bbbbbbbbbb"),
      false,
      None,
      Some(today),
      None,
      Some(MsbOrTcsp(true)),
      extra = RPExtra(Some(StringOrInt("222222")), None, Some(StatusConstants.Unchanged), None, None, None)
    )
  )

  val testResponsiblePersonsForRelease7RpAPI6 = Seq(
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("Aliases1"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
          Some("2001-01-01")
        ))
      )),
      Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            Some(PassportDetail(
              true,
              PassportNum(Some("AA1111111"), None)
            ))
          ))
        )),
        Some("AA"),
        Some("AA")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate(
          "CurrentAddressLine1",
          "CurrentAddressLine2",
          Some("CurrentAddressLine3"),
          Some("CurrentAddressLine4"),
          "GB",
          Some("AA1 1AA")
        )
      )),
      Some("3+ years"),
      None,
      None,
      None,
      None,
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true, Some(false))),
        None,
        None
      )),
      Some(RegDetails(
        true,
        Some("123456789"),
        true,
        Some("1234567890")
      )),
      false,
      None,
      true,
      Some("TrainingDetails"),
      Some(today),
      None,
      Some(MsbOrTcsp(false)),
      extra = RPExtra(Some(StringOrInt("333333")), None, Some(StatusConstants.Unchanged), None, None, None)
    ),
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbb"), Some("bbbbbbbbbbb")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("bbbbbbbbbbb"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
          Some("1967-08-13")
        ))
      )),
      Some(NationalityDetails(
        true,
        Some(IdDetail(
          Some(UkResident("BB000000A")),
          None
        )),
        Some("GB"),
        Some("GB")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA"))
      )),
      Some("0-6 months"),
      Some(AddressUnderThreeYears(Address("b", "b", Some("b"), Some("b"), "GB", Some("AA1 1AA")))),
      Some("0-6 months"),
      Some(AddressUnderThreeYears(Address("a", "a", Some("a"), Some("a"), "GB", Some("AA1 1AA")))),
      Some("7-12 months"),
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true, Some(false))),
        None,
        None
      )),
      Some(RegDetails(true, Some("111111111"), true, Some("1111111111"))
      ),
      true,
      Some("bbbbbbbbbb"),
      false,
      None,
      Some(today),
      None,
      Some(MsbOrTcsp(true)),
      extra = RPExtra(Some(StringOrInt("222222")), None, Some(StatusConstants.Unchanged), None, None, None)
    )
  )

  val testFilingIndividual = Aboutyou(
    Some(IndividualDetails(
      "FirstName",
      Some("MiddleName"),
      "LastName")),
    true,
    Some("Beneficial Shareholder"),
    None,
    Some("Other"),
    None
  )

  val testAmendFilingIndividual = Aboutyou(
    Some(IndividualDetails(
      "name",
      None,
      "LastName")),
    true,
    Some("Beneficial Shareholder"),
    None,
    None,
    None
  )

  val testDeclaration = Declaration(true)

  val extraFields = ExtraFields(testDeclaration, testFilingIndividual, None)
  val extraAmendFields = ExtraFields(testDeclaration, testAmendFilingIndividual, None)
  val nameDetails = NameDetails(
    PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
    Some(OthrNamesOrAliasesDetails(
      true,
      Some(List("Aliases1"))
    )),
    Some(PreviousNameDetails(
      true,
      Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
      Some("2001-01-01")
    ))
  )
  val nationalityDetails = NationalityDetails(
    false,
    Some(IdDetail(
      None,
      Some(NonUkResident(
        "2001-01-01",
        true,
        Some(PassportDetail(
          true,
          PassportNum(Some("AA1111111"), None)
        ))
      ))
    )),
    Some("AA"),
    Some("AA")
  )

  val regDetails = RegDetails(
    true,
    Some("123456789"),
    true,
    Some("1234567890")
  )

  val SubscriptionViewModel = SubscriptionView(
    etmpFormBundleNumber = "111111",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI5,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersons),
    DesConstants.extraFields
  )


  val SubscriptionViewModelForRp = SubscriptionView(
    etmpFormBundleNumber = "111111",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI5,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRp),
    DesConstants.extraFields
  )


  val SubscriptionViewModelForRpPhase2 = SubscriptionView(
    etmpFormBundleNumber = "111111",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI5,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRpPhase2),
    DesConstants.extraFields
  )

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  val AmendVariationRequestModel = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    DesConstants.testChangeIndicators,
    "Amendment",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersonsForRpAPI6),
    DesConstants.extraFields
  )

  val newEtmpField = Some(EtmpFields(Some("2016-09-17T09:30:47Z"), Some("2016-10-17T09:30:47Z"), Some("2016-11-17T09:30:47Z"), Some("2016-12-17T09:30:47Z")))
  val newChangeIndicator = ChangeIndicators(true, true, true, false, true)
  val newExtraFields = ExtraFields(DesConstants.testDeclaration, DesConstants.testFilingIndividual, newEtmpField)
  val newAmendExtraFields = ExtraFields(DesConstants.testDeclaration, DesConstants.testAmendFilingIndividual, newEtmpField)

  val newResponsiblePersons = Seq(ResponsiblePersons(
    Some(NameDetails(
      PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
      Some(OthrNamesOrAliasesDetails(
        true,
        Some(List("Aliases1"))
      )),
      Some(PreviousNameDetails(
        true,
        Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
        Some("2001-01-01")
      ))
    )),
    Some(NationalityDetails(
      false,
      Some(IdDetail(
        None,
        Some(NonUkResident(
          "2001-01-01",
          true,
          Some(PassportDetail(
            true,
            PassportNum(Some("AA1111111"), None)
          ))
        ))
      )),
      Some("AA"),
      Some("AA")
    )),
    None,
    Some(CurrentAddress(
      AddressWithChangeDate(
        "CurrentAddressLine1",
        "CurrentAddressLine2",
        Some("CurrentAddressLine3"),
        Some("CurrentAddressLine4"),
        "GB",
        Some("AA1 1AA")
      )
    )),
    Some("3+ years"),
    None,
    None,
    None,
    None,
    Some(PositionInBusiness(
      Some(SoleProprietor(true, true)),
      None,
      None
    )),
    Some(RegDetails(
      true,
      Some("123456789"),
      true,
      Some("1234567890")
    )),
    false,
    None,
    true,
    Some("TrainingDetails"),
    None,
    None,
    Some(MsbOrTcsp(false)),
    extra = RPExtra(Some(StringOrInt("333333")), Some("2016-09-17T09:30:47Z"), Some("added"), Some(false), Some("some test result"), Some("2012-12-12"))
  ))

  val testAmendBusinessDetails = BusinessDetails(BusinessType.SoleProprietor,
    Some(CorpAndBodyLlps("CompanyName", "12345678")),
    None)

  val testAmendHvd = HvdModel(true,
    Some("2001-01-01"),
    Some(false),
    true,
    None,
    Some(HvdFromUnseenCustDetails(
      true,
      Some(ReceiptMethods(true, true, false, None))
    ))
  )

  val updateAmendVariationRequestRP = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    DesConstants.testChangeIndicators,
    "Amendment",
    DesConstants.testAmendBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testAmendHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.amendStatusResponsiblePersonsAPI5),
    DesConstants.extraFields
  )


  val amendStatusDesVariationRequestTP = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    DesConstants.testChangeIndicators,
    "Amendment",
    DesConstants.testAmendBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.amendStatusRequestTradingPremises,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testAmendHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.amendStatusResponsiblePersonsAPI5),
    DesConstants.extraFields
  )


  val amendVariationRequest1 = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    DesConstants.testChangeIndicators,
    "Amendment",
    DesConstants.testAmendBusinessDetails,
    DesConstants.testAmendViewBusinessContactDetails1,
    DesConstants.testAmendBusinessReferencesAll,
    Some(DesConstants.testAmendBusinessReferencesAllButSp),
    Some(DesConstants.testAmendBusinessReferencesCbUbLlp),
    DesConstants.testAmendBusinessActivities,
    DesConstants.testAmendTradingPremisesAPI6,
    DesConstants.testAmendBankDetails,
    Some(DesConstants.testAmendMsb),
    Some(DesConstants.testAmendHvd),
    Some(DesConstants.testAmendAsp),
    Some(DesConstants.testAmendAspOrTcsp),
    Some(DesConstants.testAmendTcspAll),
    Some(DesConstants.testAmendTcspTrustCompFormationAgt),
    Some(DesConstants.testAmendEabAll),
    Some(DesConstants.testAmendEabResdEstAgncy),
    Some(DesConstants.testAmendResponsiblePersons),
    DesConstants.extraAmendFields
  )

  val amendExtraFields = RPExtra(Some(StringOrInt("333333")), None, Some("added"), Some(false), Some("some test result"), Some("2012-12-12"))

  val updateAmendVariationCompleteRequest1 = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    ChangeIndicators(true, true, true, true, true, true, true, true, true, true, true, true, true, true),
    "Amendment",
    DesConstants.testAmendBusinessDetails,
    DesConstants.testAmendViewBusinessContactDetails1,
    DesConstants.testAmendBusinessReferencesAll,
    Some(DesConstants.testAmendBusinessReferencesAllButSp),
    Some(DesConstants.testAmendBusinessReferencesCbUbLlp),
    DesConstants.testAmendBusinessActivities,
    DesConstants.testAmendTradingPremisesAPI6,
    DesConstants.testAmendBankDetails,
    Some(DesConstants.testAmendMsb),
    Some(DesConstants.testAmendHvd),
    Some(DesConstants.testAmendAsp),
    Some(DesConstants.testAmendAspOrTcsp),
    Some(DesConstants.testAmendTcspAll),
    Some(DesConstants.testAmendTcspTrustCompFormationAgt),
    Some(DesConstants.testAmendEabAll),
    Some(DesConstants.testAmendEabResdEstAgncy),
    Some(DesConstants.testAmendResponsiblePersons),
    DesConstants.newAmendExtraFields
  )

  val SubscriptionViewModelAPI5 = SubscriptionView(
    etmpFormBundleNumber = "111111",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI5,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.newResponsiblePersons),
    DesConstants.newExtraFields
  )

  val SubscriptionViewStatusRP = SubscriptionView(
    etmpFormBundleNumber = "111111",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI5,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.viewResponsiblePersonsAPI5),
    DesConstants.newExtraFields
  )

  val SubscriptionViewStatusTP = SubscriptionView(
    etmpFormBundleNumber = "111111",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.viewStatusTradingPremises,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.viewResponsiblePersonsAPI5),
    DesConstants.newExtraFields
  )

  val testAmendResponsiblePersonsTest1 = Seq(
    updatedRPForAmendment.copy(nameDetails = None,
      extra = RPExtra(lineId = Some(StringOrInt(Right("77777"))), status = Some(StatusConstants.Updated), retestFlag = Some(false))),
    ResponsiblePersons(
      Some(NameDetails(
        PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName")),
        Some(OthrNamesOrAliasesDetails(
          true,
          Some(List("Aliases1"))
        )),
        Some(PreviousNameDetails(
          true,
          Some(PersonName(Some("FirstName"), Some("MiddleName"), Some("LastName"))),
          Some("2001-01-01"),
          Some(false)
        ))
      )),
      Some(NationalityDetails(
        false,
        Some(IdDetail(
          None,
          Some(NonUkResident(
            "2001-01-01",
            true,
            Some(PassportDetail(
              true,
              PassportNum(Some("AA1111111"), None)
            ))
          ))
        )),
        Some("AA"),
        Some("AA")
      )),
      None,
      Some(CurrentAddress(
        AddressWithChangeDate(
          "CurrentAddressLine1",
          "CurrentAddressLine2",
          Some("CurrentAddressLine3"),
          Some("CurrentAddressLine4"),
          "GB",
          Some("AA1 1AA")
        )
      )),
      Some("3+ years"),
      None,
      None,
      None,
      None,
      Some(PositionInBusiness(
        Some(SoleProprietor(true, true)),
        None,
        None
      )),
      Some(RegDetails(
        true,
        Some("123456789"),
        true,
        Some("1234567890")
      )),
      true,
      Some("DescOfPrevExperience"),
      true,
      Some("TrainingDetails"),
      None,
      Some(false),
      Some(MsbOrTcsp(true)),
      extra = RPExtra(Some(StringOrInt("333333")), None, Some(StatusConstants.Unchanged), None, Some("10"), Some("some test result"), Some("2012-12-12"))
    ),
    responsiblePersons3,
    responsiblePersons2
  )

  val amendStatusAmendVariationRP = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    ChangeIndicators(true, false, false, false, false, false, false, true, false, false, false, false, true, false),
    "Amendment",
    DesConstants.testAmendBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivitiesWithDateChangeFlag,
    DesConstants.testTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testAmendHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testAmendResponsiblePersonsTest1),
    DesConstants.newExtraFields
  )


  val amendStatusAmendVariationTP = AmendVariationRequest(
    acknowledgementReference = ackref.ackRef,
    ChangeIndicators(true, false, false, true, false, false, false, true, false, false, false, false, true, false),
    "Amendment",
    DesConstants.testAmendBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivitiesWithDateChangeFlag,
    DesConstants.amendStatusTradingPremisesAPI6,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testAmendHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testAmendResponsiblePersonsTest1),
    DesConstants.newExtraFields
  )

}
