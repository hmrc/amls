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

package models

import models.des.aboutthebusiness.{Address => ATBAddress, _}
import models.des.aboutyou.{Aboutyou, IndividualDetails}
import models.des.amp.{Amp, TransactionsAccptOvrThrshld}
import models.des.asp._
import models.des.businessactivities.{BusinessActivities => DesBusinessActivities, _}
import models.des.businessdetails.BusinessDetails
import models.des.businessdetails.BusinessType.SoleProprietor
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy}
import models.des.hvd.{HvdFromUnseenCustDetails, ReceiptMethods, Hvd => DesHvd}
import models.des.msb._
import models.des.responsiblepeople.{Address => RPAddress, SoleProprietor => DesSoleProprietor, _}
import models.des.supervision._
import models.des.tcsp.{TcspAll, TcspTrustCompFormationAgt}
import models.des.tradingpremises.{Amp => AmpTradingPremises, Asp => TPAsp, TradingPremises => DesTradingPremises, _}
import java.time.LocalDate

object DefaultDesValues {

  val BusinessCustomerDetails = BusinessDetails(SoleProprietor,
    None,
    None
  )
  private val deseabServiceModel = Some(des.businessactivities.EabServices(
    false, false, true, false, true, false, false, false, false, Some(false)
  ))

  private val deseabServiceModelLA = Some(des.businessactivities.EabServices(false, false,
    true, false, true, false, false, false, false, Some(true)))

  private val activityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£0-£15k"))))
  private val franchiseDetails = Some(FranchiseDetails(true, Some(Seq("Name"))))
  private val noOfEmployees = Some("10")
  private val noOfEmployeesForMlr = Some("5")
  private val nonUkResidentCustDetails = NonUkResidentCustDetails(true, Some(Seq("GB", "AB")))
  private val auditableRecordsDetails = AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("value"))))
  private val suspiciousActivityGuidance = true
  private val nationalCrimeAgencyRegistered = true
  private val formalRiskAssessmentDetails = Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true))))
  private val advisorNameAddress = AdvisorNameAddress("Name", Some("TradingName"), ATBAddress("Line1", Some("Line2"), Some("Line3"), Some("Line4"), "GB", Some("AA1 1AA")))
  private val mlrAdvisor = Some(MlrAdvisor(true, Some(MlrAdvisorDetails(Some(advisorNameAddress), true, None))))
  private val desallActivitiesModel = Some(BusinessActivitiesAll(None, Some("1990-02-24"), Some(false), activityDetails, franchiseDetails, noOfEmployees, noOfEmployeesForMlr,
    nonUkResidentCustDetails, auditableRecordsDetails, suspiciousActivityGuidance, nationalCrimeAgencyRegistered,
    formalRiskAssessmentDetails, mlrAdvisor))
  private val tcspServicesOffered = Some(TcspServicesOffered(true, true, true, true, true))
  private val servicesforRegOff = Some(ServicesforRegOff(true, false, false, false, false, false, false, true, Some("other service")))
  private val aspServicesOffered = Some(AspServicesOffered(true, false, false, true, true))
  private val ampServices = Some(AmpServices(true, true, true, true, AmpServicesOther(true, Some("Another service"))))
  private val mlrActivitiesAppliedFor = Some(MlrActivitiesAppliedFor(true, true, true, false, false, false, false, false))
  private val msbServicesCarriedOut = Some(MsbServicesCarriedOut(true, true, false, true, false))
  private val hvdGoodsSold = Some(HvdGoodsSold(true, true, true, true, true, false, false, false, false, false, false, true, Some("Details"),
    Some(HowGoodsAreSold(true, false, true))))
  private val hvdAlcoholTobacco = Some(HvdAlcoholTobacco(true))

  val BusinessActivitiesSection = DesBusinessActivities(mlrActivitiesAppliedFor, msbServicesCarriedOut, hvdGoodsSold, hvdAlcoholTobacco, aspServicesOffered,
    tcspServicesOffered, servicesforRegOff, deseabServiceModel, ampServices, desallActivitiesModel)

  val BusinessActivitiesSectionLA = DesBusinessActivities(mlrActivitiesAppliedFor, msbServicesCarriedOut, hvdGoodsSold, hvdAlcoholTobacco, aspServicesOffered,
    tcspServicesOffered, servicesforRegOff, deseabServiceModelLA, ampServices, desallActivitiesModel)

  val EabAllDetails = Some(EabAll(true, Some("Details"), false, None))

  val EabResd = Some(EabResdEstAgncy(true, Some("Property Redress Scheme")))

  val AboutTheBusinessSection = BusinessContactDetails(
    ATBAddress("line1", Some("line2"),
      Some("some street"), Some("some city"), "GB", Some("EE1 1EE")),
    true,
    Some(AlternativeAddress("kap", "Trading", ATBAddress("Park", Some("lane"),
      Some("Street"), Some("city"), "GB", Some("EE1 1EE")))),
    "019212323222323222323222323222",
    "abc@hotmail.co.uk")

  val PrevRegMLR = Some(PreviouslyRegisteredMLR(true, Some("12345678"), false, None))
  val VatALlBuySp = Some(VATRegistration(true, Some("123456789")))

  private val premises = OwnBusinessPremisesDetails(
    Some("string"),
    des.tradingpremises.Address("string",
      Some("string"),
      Some("string"),
      Some("string"),
      "GB",
      Some("AA1 1AA")
    ),
    false,
    Msb(true, true, true, true, false),
    models.des.tradingpremises.Hvd(true),
    TPAsp(false),
    Tcsp(true),
    Eab(false),
    Bpsp(false),
    Tditpsp(false),
    AmpTradingPremises(true),
    "2010-01-01")

  private val ownBusinessPremises = OwnBusinessPremises(true, Some(Seq(premises)))

  private val agentPremises = AgentPremises("string",
    des.tradingpremises.Address("string", Some("string"), Some("string"), Some("string"), "GB", Some("AA1 1AA")), true,
    Msb(false, false, false, false, false),
    models.des.tradingpremises.Hvd(false),
    TPAsp(false),
    Tcsp(false),
    Eab(true),
    Bpsp(true),
    Tditpsp(false),
    AmpTradingPremises(true),
    Some("2008-01-01"))

  private def agentDetails = AgentDetails("Sole Proprietor", None, None, Some("entity name"), agentPremises)

  private def agentDetails1 = AgentDetails(agentLegalEntity = "Sole Proprietor", companyRegNo = None, dateOfBirth = Some("1970-01-01"), agentLegalEntityName = Some("entity name"), agentPremises = agentPremises)

  private val agentBusinessPremises = AgentBusinessPremises(true, Some(Seq(agentDetails)))
  private val agentBusinessPremises1 = AgentBusinessPremises(true, Some(Seq(agentDetails1)))

  val TradingPremisesSection = {
    DesTradingPremises(Some(ownBusinessPremises), Some(agentBusinessPremises))
  }

  val TradingPremisesSection1 = {
    DesTradingPremises(Some(ownBusinessPremises), Some(agentBusinessPremises1))
  }

  val bankDetailsSection = {
    import des.bankdetails._
    BankDetails("3",
      Some(Seq(BankAccount("Personal account", "Personal", true, ukAccount("112233", "12345678")),
        BankAccount("Business account", "This business's", false, AccountNumber("12345678")),
        BankAccount("Another Business account", "Another business's", false, IBANNumber("12345678")))))
  }

  val filingIndividual = Aboutyou(Some(IndividualDetails("fName", None, "lName")), true, Some("Other"), Some("Agent"), Some("Other"), Some("Agent"))

  private val nameDtls = Some(NameDetails(PersonName(Some("name"), Some("some"), Some("surname")), Some(OthrNamesOrAliasesDetails(true, Some(Seq("Doc")))),
    Some(PreviousNameDetails(true, Some(PersonName(Some("fname"), Some("mname"), Some("lname"))), Some("1990-02-24"), Some(false)))))
  val nameDtls1 = Some(NameDetails(PersonName(Some("name"), Some("some"), Some("surname")), Some(OthrNamesOrAliasesDetails(true, Some(Seq("Doc")))),
    Some(PreviousNameDetails(true, Some(PersonName(Some("fname"), Some("mname"), Some("lname"))), Some("1990-02-24"), None))))
  private val nationalDtls = Some(NationalityDetails(true, Some(IdDetail(Some(UkResident("nino")), None)), Some("GB"), Some("GB")))
  private val nationalDtlsPhase2 = Some(NationalityDetails(true, Some(IdDetail(Some(UkResident("nino")), None, Some("1970-01-01"))), Some("GB"), Some("GB")))
  private val contactDtls = Some(ContactCommDetails("test@test.com", "07000001122", None))
  private val currentDesAddress = Some(CurrentAddress(AddressWithChangeDate("ccLine 1", Some("ccLine 2"), None, None, "GB", Some("AA1 1AA"))))
  private val additionalDesAddress = Some(AddressUnderThreeYears(RPAddress("Line 1", Some("Line 2"), None, None, "GB", Some("BB1 1BB"))))
  private val extraAdditional = Some(AddressUnderThreeYears(RPAddress("e Line 1", Some("e Line 2"), Some("e Line 3"), Some("e Line 4"), "GB", Some("CC1 1CC"))))
  private val regDtls = Some(RegDetails(false, None, true, Some("0123456789")))
  private val positionInBusinessForRelease7 = Some(PositionInBusiness(Some(DesSoleProprietor(true, true, Some(false))), None, None))
  private val positionInBusiness = Some(PositionInBusiness(Some(DesSoleProprietor(true, true)),
    None, None))

  val tcspAllSection = Some(TcspAll(true, Some("12345678")))
  val tcspTrustCompFormationAgtSection = Some(TcspTrustCompFormationAgt(true, true))

  private val responsiblePersons: ResponsiblePersons = ResponsiblePersons(
    nameDetails = nameDtls,
    nationalityDetails = nationalDtls,
    contactCommDetails = contactDtls,
    currentAddressDetails = currentDesAddress,
    timeAtCurrentAddress = Some("0-6 months"),
    addressUnderThreeYears = additionalDesAddress,
    timeAtAddressUnderThreeYears = Some("7-12 months"),
    addressUnderOneYear = extraAdditional,
    timeAtAddressUnderOneYear = Some("1-3 years"),
    positionInBusiness = positionInBusiness,
    regDetails = regDtls,
    previousExperience = true,
    descOfPrevExperience = Some("Some training"),
    amlAndCounterTerrFinTraining = true,
    trainingDetails = Some("test"),
    startDate = Some(LocalDate.now.toString),
    dateChangeFlag = Some(false),
    msbOrTcsp = Some(MsbOrTcsp(true)),
    extra = RPExtra()
  )

  val ResponsiblePersonsSection = Some(Seq(responsiblePersons))

  val validResponsiblePersons = Some(Seq(responsiblePersons.copy(
    nationalityDetails = Some(NationalityDetails(true, None, None, None)),
    passedFitAndProperTest = Some(true),
    passedApprovalCheck = Some(true),
    dateChangeFlag = None,
    positionInBusiness = Some(PositionInBusiness(Some(DesSoleProprietor(true, true, Some(false), None)), None, None)),
    nameDetails = Some(NameDetails(PersonName(Some("Jack"), None, Some("Humphrey")),
      Some(OthrNamesOrAliasesDetails(false, None)), Some(PreviousNameDetails(false, None, None, None)))),
    msbOrTcsp = None
  )))

  val ResponsiblePersonsSection1 = Some(Seq(ResponsiblePersons(
    nameDetails = nameDtls,
    nationalityDetails = nationalDtls,
    contactCommDetails = contactDtls,
    currentAddressDetails = currentDesAddress,
    timeAtCurrentAddress = Some("0-6 months"),
    addressUnderThreeYears = additionalDesAddress,
    timeAtAddressUnderThreeYears = Some("7-12 months"),
    addressUnderOneYear = extraAdditional,
    timeAtAddressUnderOneYear = Some("1-3 years"),
    positionInBusiness = positionInBusiness,
    regDetails = regDtls,
    previousExperience = true,
    descOfPrevExperience = Some("Some training"),
    amlAndCounterTerrFinTraining = true,
    trainingDetails = Some("test"),
    startDate = Some(LocalDate.now.toString),
    dateChangeFlag = None,
    msbOrTcsp = Some(MsbOrTcsp(true)),
    extra = RPExtra()
  )
  ))


  val ResponsiblePersonsSectionPhase2 = Some(Seq(ResponsiblePersons(
    nameDtls,
    nationalDtlsPhase2,
    contactDtls,
    currentDesAddress,
    Some("0-6 months"),
    additionalDesAddress,
    Some("7-12 months"),
    extraAdditional,
    Some("1-3 years"),
    positionInBusiness,
    regDtls,
    true,
    Some("Some training"),
    true,
    Some("test"),
    Some(LocalDate.now.toString),
    Some(false),
    None,
    Some(false),
    Some(true),
    extra = RPExtra()
  )
  ))
  val ResponsiblePersonsSectionForRelease7 = Some(Seq(ResponsiblePersons(
    nameDetails = nameDtls,
    nationalityDetails = nationalDtls,
    contactCommDetails = contactDtls,
    currentAddressDetails = currentDesAddress,
    timeAtCurrentAddress = Some("0-6 months"),
    addressUnderThreeYears = additionalDesAddress,
    timeAtAddressUnderThreeYears = Some("7-12 months"),
    addressUnderOneYear = extraAdditional,
    timeAtAddressUnderOneYear = Some("1-3 years"),
    positionInBusiness = positionInBusinessForRelease7,
    regDetails = regDtls,
    previousExperience = true,
    descOfPrevExperience = Some("Some training"),
    amlAndCounterTerrFinTraining = true,
    trainingDetails = Some("test"),
    startDate = Some(LocalDate.now.toString),
    dateChangeFlag = Some(false),
    msbOrTcsp = Some(MsbOrTcsp(true)),
    extra = RPExtra()
  )
  ))
  val ResponsiblePersonsSectionForRelease7Phase2: Option[Seq[ResponsiblePersons]] = Some(Seq(ResponsiblePersons(
    nameDetails = nameDtls,
    nationalityDetails = nationalDtlsPhase2,
    contactCommDetails = contactDtls,
    currentAddressDetails = currentDesAddress,
    timeAtCurrentAddress = Some("0-6 months"),
    addressUnderThreeYears = additionalDesAddress,
    timeAtAddressUnderThreeYears = Some("7-12 months"),
    addressUnderOneYear = extraAdditional,
    timeAtAddressUnderOneYear = Some("1-3 years"),
    positionInBusiness = positionInBusinessForRelease7,
    regDetails = regDtls,
    previousExperience = true,
    descOfPrevExperience = Some("Some training"),
    amlAndCounterTerrFinTraining = true,
    trainingDetails = Some("test"),
    startDate = Some(LocalDate.now.toString),
    dateChangeFlag = None,
    msbOrTcsp = None,
    passedFitAndProperTest = Some(false),
    passedApprovalCheck = Some(true),
    extra = RPExtra()
  )
  ))

  val ResponsiblePersonsSectionForRelease7Phase21 = ResponsiblePersonsSectionForRelease7Phase2.map(x => Seq(x.head.copy(nameDetails = nameDtls1)))

  val AmpSection = Some(Amp(TransactionsAccptOvrThrshld(true, Some("2019-09-19 16:58:06.259Z")), true, 60))

  val AspSection = Some(Asp(true, None))

  private val supervisionDetails = SupervisionDetails(true, Some(SupervisorDetails("Company A", "1993-08-25", "1999-08-25", Some(false), "Ending reason")))
  private val supervisionDetails1 = SupervisionDetails(true, Some(SupervisorDetails("Company A", "1993-08-25", "1999-08-25", None, "Ending reason")))
  private val professionalBodyDetails = ProfessionalBodyDetails(true, Some("details"),
    Some(ProfessionalBodyDesMember(true,
      Some(MemberOfProfessionalBody(true, true, false, false, false, false, false, false, false, false, false, false, false, true, Some("test"))))))

  val AspOrTcspSection = Some(AspOrTcsp(Some(supervisionDetails),
    Some(professionalBodyDetails)))
  val AspOrTcspSection1 = Some(AspOrTcsp(Some(supervisionDetails1),
    Some(professionalBodyDetails)))

  val CorpTaxRegime = Some(CorporationTaxRegisteredCbUbLlp(true, Some("1234567890")))

  val msbSection = Some(
    MoneyServiceBusiness(
      Some(MsbAllDetails(Some("499999"), true, Some(CountriesList(List("GB"))), true)),
      Some(MsbMtDetails(true, Some("123456"),
        IpspServicesDetails(true, Some(Seq(IpspDetails("name", "123456789123456")))),
        true,
        Some("12345678963"), Some(CountriesList(List("GB"))), Some(CountriesList(List("LA", "LV"))))),
      Some(MsbCeDetailsR7(Some(true), Some(CurrencySourcesR7(Some(MSBBankDetails(true, Some(List("Bank names")))),
        Some(CurrencyWholesalerDetails(true, Some(List("wholesaler names")))), true)), "12345678963", Some(CurrSupplyToCust(List("USD", "MNO", "PQR"))))), None)
  )

  val msbSectionR6 = Some(
    MoneyServiceBusiness(
      Some(MsbAllDetails(Some("499999"), true, Some(CountriesList(List("GB"))), true)),
      Some(MsbMtDetails(true, Some("123456"),
        IpspServicesDetails(true, Some(Seq(IpspDetails("name", "123456789123456")))),
        true,
        Some("12345678963"), Some(CountriesList(List("GB"))), Some(CountriesList(List("LA", "LV"))), None)),
      Some(MsbCeDetailsR7(None, Some(CurrencySourcesR7(Some(MSBBankDetails(true, Some(List("Bank names")))),
        Some(CurrencyWholesalerDetails(true, Some(List("wholesaler names")))), true)), "12345678963", Some(CurrSupplyToCust(List("USD", "MNO", "PQR"))))), None)
  )
  // scalastyle:off magic.number
  val hvdSection = Some(DesHvd(true, Some("1978-02-15"), Some(false), true, Some(40), Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))))
  val hvdSection1 = Some(DesHvd(true, Some("1978-02-15"), None, true, Some(40), Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))))

}