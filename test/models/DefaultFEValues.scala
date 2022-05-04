/*
 * Copyright 2022 HM Revenue & Customs
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


import models.fe.SubscriptionView
import models.fe.amp.{Amp, AmpData}
import models.fe.declaration.{RoleWithinBusiness, Other => DeclarationOther}
import models.fe.eab.{Eab, EabData}
import models.fe.responsiblepeople.TimeAtAddress.ThreeYearsPlus
import org.joda.time.LocalDate
import utils.StatusConstants

object ASPTCSPSection {

  import models.fe.asp._
  import models.fe.tcsp.{Other, _}

  private val DefaultProvidedServices = ProvidedServices(Set(PhonecallHandling, Other("other service")))
  private val DefaultCompanyServiceProviders = TcspTypes(Set(NomineeShareholdersProvider,
    TrusteeProvider,
    CompanyDirectorEtc,
    RegisteredOfficeEtc,
    CompanyFormationAgent))
  private val DefaultDoServicesOfAnotherTCSP = true
  private val DefaultServicesOfAnotherTCSP = ServicesOfAnotherTCSPYes("12345678")

  val TcspSection = Some(Tcsp(
    Some(DefaultCompanyServiceProviders),
    Some(OnlyOffTheShelfCompsSoldYes),
    Some(ComplexCorpStructureCreationYes),
    Some(DefaultProvidedServices),
    Some(DefaultDoServicesOfAnotherTCSP),
    Some(DefaultServicesOfAnotherTCSP))
  )
  val aspOtherBusinessTax = OtherBusinessTaxMattersYes

  val aspServices = ServicesOfBusiness(Set(Accountancy, Auditing, FinancialOrTaxAdvice))

  val AspSection = Some(Asp(Some(aspServices), Some(aspOtherBusinessTax)))

  val TcspModelForView = Some(Tcsp(
    Some(TcspTypes(
      Set(CompanyDirectorEtc,
        NomineeShareholdersProvider,
        TrusteeProvider,
        RegisteredOfficeEtc,
        CompanyFormationAgent))),
    Some(OnlyOffTheShelfCompsSoldYes),
    Some(ComplexCorpStructureCreationYes),
    Some(ProvidedServices(Set(SelfCollectMailboxes, ConferenceRooms,
      PhonecallHandling, EmailHandling, Other("SpecifyOther"), EmailServer))), Some(true), Some(ServicesOfAnotherTCSPYes("111111111111111"))))

  val TcspModelForViewNoCompanyFormationAgent = Some(Tcsp(
    Some(TcspTypes(
      Set(CompanyDirectorEtc,
        NomineeShareholdersProvider,
        TrusteeProvider,
        RegisteredOfficeEtc))),
    None,
    None,
    Some(ProvidedServices(Set(SelfCollectMailboxes, ConferenceRooms,
      PhonecallHandling, EmailHandling, Other("SpecifyOther"), EmailServer))), Some(true), Some(ServicesOfAnotherTCSPYes("111111111111111"))))

  val AspModelForView = Some(Asp(Some(ServicesOfBusiness(Set(Auditing,
    FinancialOrTaxAdvice, BookKeeping, PayrollServices, Accountancy))), Some(OtherBusinessTaxMattersYes)))

}

object SupervisionSection {

  import models.fe.supervision._

  private val supervisor = "Company A"
  //scalastyle:off magic.number
  private val start = new LocalDate(1993, 8, 25)
  private val end = new LocalDate(1999, 8, 25)
  //scalastyle:off magic.number
  private val reason = "Ending reason"
  private val anotherBody = AnotherBodyYes(supervisor, start, end, reason)
  private val professionalBody = ProfessionalBodyYes("details")
  private val professionalBodyMember = ProfessionalBodyMemberYes
  private val businessTypes = BusinessTypes(Set(AccountingTechnicians, CharteredCertifiedAccountants, Other("test")))

  val completeModel = Some(Supervision(
    Some(anotherBody),
    Some(professionalBodyMember),
    Some(businessTypes),
    Some(professionalBody)
  ))

  val modelForView = Some(Supervision(
    Some(AnotherBodyYes("NameOfLastSupervisor", new LocalDate(2001, 1, 1), new LocalDate(2001, 1, 1), "SupervisionEndingReason")),
    Some(ProfessionalBodyMemberYes),
    Some(BusinessTypes(Set(
      AccountantsIreland,
      CharteredCertifiedAccountants,
      AssociationOfBookkeepers,
      AccountantsEnglandandWales,
      Bookkeepers,
      AccountingTechnicians,
      TaxationTechnicians,
      InternationalAccountants,
      Other("SpecifyOther"),
      LawSociety,
      InstituteOfTaxation,
      AccountantsScotland,
      FinancialAccountants,
      ManagementAccountants
    ))),
    Some(ProfessionalBodyYes("DetailsIfFinedWarned"))))
}

object AboutYouSection {

  import models.fe.declaration.{AddPerson, BeneficialShareholder}

  val model = AddPerson("fName", None, "lName", RoleWithinBusiness(Set(DeclarationOther("Agent"))))

  val modelforView = AddPerson("FirstName", Some("MiddleName"), "LastName", RoleWithinBusiness(Set(BeneficialShareholder)))
}

object BusinessActivitiesSection {

  import models.fe.businessactivities.ExpectedAMLSTurnover.Third
  import models.fe.businessactivities._

  val model = BusinessActivities(
    Some(InvolvedInOtherNo),
    None,
    Some(ExpectedAMLSTurnover.First),
    Some(BusinessFranchiseYes("Name")),
    Some(true),
    Some(CustomersOutsideUK(true, Some(Seq("GB", "AB")))),
    Some(NCARegistered(true)),
    Some(AccountantForAMLSRegulations(true)),
    Some(IdentifySuspiciousActivity(true)),
    Some(RiskAssessmentPolicyYes(Set(Digital))),
    Some(HowManyEmployees("10", "5")),
    Some(WhoIsYourAccountant("Name", Some("TradingName"),
      UkAccountantsAddress("Line1", "Line2", Some("Line3"), Some("Line4"), "AA1 1AA"))),
    Some(TaxMatters(true)),
    Some(TransactionTypes(Set(Paper, DigitalSpreadsheet, DigitalSoftware("value"))))
  )


  val modelForView = BusinessActivities(Some(InvolvedInOtherNo),
    None,
    Some(Third),
    Some(BusinessFranchiseYes("FranchiserName1")),
    Some(true),
    Some(CustomersOutsideUK(true, Some(List("AD", "GB")))), Some(NCARegistered(true)),
    Some(AccountantForAMLSRegulations(true)), Some(IdentifySuspiciousActivity(true)),
    Some(RiskAssessmentPolicyYes(Set(Digital, PaperBased))), Some(HowManyEmployees("12345678901", "11223344556")),
    Some(WhoIsYourAccountant("Name", Some("TradingName"),
      UkAccountantsAddress("AdvisorAddressLine1", "AdvisorAddressLine2", Some("AdvisorAddressLine3"), Some("AdvisorAddressLine4"), "AA1 1AA"))),
    Some(TaxMatters(true)),
    Some(TransactionTypes(Set(Paper, DigitalSpreadsheet, DigitalSoftware("CommercialPackageName"))))
  )
}

object EabSection {

  val model = Some(
    Eab(
      EabData(
        List("auctioneering", "businessTransfer"),
        None,
        Some("propertyRedressScheme"),
        None,
        true,
        Some("Details"),
        false,
        None
      )
    )
  )

  val modelPenalisedEstateAgentsFalse = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("propertyOmbudsman"),
        None,
        penalisedEstateAgentsAct = false,
        None,
        penalisedProfessionalBody = true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelPenalisedEstateAgentsTrue = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("propertyOmbudsman"),
        None,
        penalisedEstateAgentsAct = true,
        Some("EstAgncActProhibProvideDetails"),
        penalisedProfessionalBody = true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelPenalisedProfessionalBodyFalse = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("propertyOmbudsman"),
        None,
        penalisedEstateAgentsAct = true,
        Some("EstAgncActProhibProvideDetails"),
        penalisedProfessionalBody = false,
        None
      )
    )
  )

  val modelPenalisedpenalisedEstateAgentsActAndProfessionalBodyFalse = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("propertyOmbudsman"),
        None,
        penalisedEstateAgentsAct = false,
        None,
        penalisedProfessionalBody = false,
        None
      )
    )
  )

  val modelPenalisedProfessionalBodyTrue = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("propertyOmbudsman"),
        None,
        penalisedEstateAgentsAct = true,
        Some("EstAgncActProhibProvideDetails"),
        penalisedProfessionalBody = true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelLA = Some(
    Eab(
      EabData(
        List("auctioneering", "businessTransfer", "lettings"),
        None,
        Some("propertyRedressScheme"),
        Some(true),
        true,
        Some("Details"),
        false,
        None
      )
    )
  )

  val modelForView = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("propertyOmbudsman"),
        None,
        true,
        Some("EstAgncActProhibProvideDetails"),
        true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelForViewOmbusmanServices = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("ombudsmanServices"),
        None,
        true,
        Some("EstAgncActProhibProvideDetails"),
        true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelForViewPropertyRedressScheme = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("propertyRedressScheme"),
        None,
        true,
        Some("EstAgncActProhibProvideDetails"),
        true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelForViewOther = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("other"),
        None,
        true,
        Some("EstAgncActProhibProvideDetails"),
        true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelForViewNoEabResdEstAgncy = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision"
        ),
        None,
        Some("notRegistered"),
        None,
        true,
        Some("EstAgncActProhibProvideDetails"),
        true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelForViewNoRedress = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "socialHousingProvision"
        ),
        None,
        None,
        None,
        true,
        Some("EstAgncActProhibProvideDetails"),
        true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelForViewLA = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision",
          "lettings"
        ),
        None,
        Some("propertyOmbudsman"),
        Some(true),
        true,
        Some("EstAgncActProhibProvideDetails"),
        true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )

  val modelForViewLANoLettingAgentSection = Some(
    Eab(
      EabData(
        List(
          "assetManagement",
          "auctioneering",
          "businessTransfer",
          "commercial",
          "developmentCompany",
          "landManagement",
          "relocation",
          "residential",
          "socialHousingProvision",
          "lettings"
        ),
        None,
        Some("propertyOmbudsman"),
        Some(false),
        true,
        Some("EstAgncActProhibProvideDetails"),
        true,
        Some("PrevWarnWRegProvideDetails")
      )
    )
  )
}

object MsbSection {

  import models.fe.moneyservicebusiness.ExpectedThroughput.Third
  import models.fe.moneyservicebusiness._

  private val businessUseAnIPSP = BusinessUseAnIPSPYes("name", "123456789123456")
  private val sendTheLargestAmountsOfMoney = SendTheLargestAmountsOfMoney("GB")

  private val whichCurrencies = WhichCurrencies(Seq("USD", "MNO", "PQR"),
    usesForeignCurrencies = Some(true),
    Some(BankMoneySource("Bank names")),
    Some(WholesalerMoneySource("wholesaler names")), customerMoneySource = true)

  private val mostTransactions = MostTransactions(Seq("LA", "LV"))

  private val msb = MoneyServiceBusiness(
    Some(ExpectedThroughput.Second),
    Some(businessUseAnIPSP),
    Some(IdentifyLinkedTransactions(true)),
    Some(SendMoneyToOtherCountry(true)),
    Some(FundsTransfer(true)),
    Some(BranchesOrAgents(true, Some(Seq("GB")))),
    Some(TransactionsInNext12Months("12345678963")),
    Some(CETransactionsInNext12Months("12345678963")),
    Some(sendTheLargestAmountsOfMoney),
    Some(mostTransactions),
    Some(whichCurrencies),
    Some(FXTransactionsInNext12Months("234234234"))
  )

  val completeModel = Some(msb)

  val modelForView = Some(MoneyServiceBusiness(Some(Third),
    Some(BusinessUseAnIPSPYes("IPSPName1", "IPSPMLRRegNo1")),
    Some(IdentifyLinkedTransactions(true)),
    Some(SendMoneyToOtherCountry(true)),
    Some(FundsTransfer(true)),
    Some(BranchesOrAgents(true, Some(List("AD", "GB")))),
    Some(TransactionsInNext12Months("11111111111")),
    Some(CETransactionsInNext12Months("11234567890")),
    Some(SendTheLargestAmountsOfMoney("GB", Some("AD"), None)),
    Some(MostTransactions(List("AD", "GB"))),
    Some(WhichCurrencies(List("GBP", "XYZ", "ABC"), usesForeignCurrencies = Some(true), Some(BankMoneySource("BankNames1")), Some(WholesalerMoneySource("CurrencyWholesalerNames")), true)),
    Some(FXTransactionsInNext12Months("234234234"))
  ))
}

object TradingPremisesSection {

  import models.fe.tradingpremises._

  val model = Some(Seq(TradingPremises(Some(RegisteringAgentPremises(false)), YourTradingPremises("string",
    Address("string", "string", Some("string"), Some("string"), "AA1 1AA"), new LocalDate(2010, 1, 1), false),
    None, None, None, None,
    WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing, BusinessActivity.TrustAndCompanyServices, BusinessActivity.MoneyServiceBusiness,
      BusinessActivity.ArtMarketParticipant)),
    Some(MsbServices(Set(TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal)))
  ),
    TradingPremises(Some(RegisteringAgentPremises(true)), YourTradingPremises("string",
      Address("string", "string", Some("string"), Some("string"), "AA1 1AA"), new LocalDate(2008, 1, 1), true),
      Some(BusinessStructure.SoleProprietor), Some(AgentName("entity name", None, Some("1970-01-01"))), None, None,
      WhatDoesYourBusinessDo(Set(BusinessActivity.EstateAgentBusinessService, BusinessActivity.BillPaymentServices,
        BusinessActivity.ArtMarketParticipant))
    )
  ))

  val modelForView = Some(List(TradingPremises(Some(RegisteringAgentPremises(true)), YourTradingPremises("aaaaaaaaaaaa",
    Address("a", "a", Some("a"), Some("a"), "AA1 1AA"), new LocalDate(1967, 8, 13), true), Some(BusinessStructure.SoleProprietor),
    Some(AgentName("AgentLegalEntityName", None, Some("1970-01-01"))),
    None, None,
    WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
      BusinessActivity.AccountancyServices,
      BusinessActivity.EstateAgentBusinessService,
      BusinessActivity.BillPaymentServices,
      BusinessActivity.TelephonePaymentService,
      BusinessActivity.MoneyServiceBusiness,
      BusinessActivity.TrustAndCompanyServices,
      BusinessActivity.ArtMarketParticipant)),
    Some(MsbServices(Set(TransmittingMoney, CurrencyExchange))), Some(111111), Some("Added")),
    TradingPremises(Some(RegisteringAgentPremises(true)),
      YourTradingPremises("aaaaaaaaaaaa", Address("a", "a", Some("a"), Some("a"), "AA1 1AA"),
        new LocalDate(1967, 8, 13), true),
      Some(BusinessStructure.SoleProprietor),
      Some(AgentName("aaaaaaaaaaa", None, Some("1970-01-01"))),
      None,
      None,
      WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
        BusinessActivity.AccountancyServices,
        BusinessActivity.EstateAgentBusinessService,
        BusinessActivity.BillPaymentServices,
        BusinessActivity.TelephonePaymentService,
        BusinessActivity.MoneyServiceBusiness,
        BusinessActivity.TrustAndCompanyServices,
        BusinessActivity.ArtMarketParticipant)),
      Some(MsbServices(Set(ChequeCashingNotScrapMetal, TransmittingMoney, CurrencyExchange, ChequeCashingScrapMetal))), None, Some("Added")),
    TradingPremises(Some(RegisteringAgentPremises(true)),
      YourTradingPremises("TradingName",
        Address("AgentAddressLine1", "AgentAddressLine2", Some("AgentAddressLine3"), Some("AgentAddressLine4"), "XX1 1XX"),
        new LocalDate(2001, 1, 1), true),
      Some(BusinessStructure.SoleProprietor), Some(AgentName("AgentLegalEntityName2", None, Some("1970-01-01"))),
      None,
      None,
      WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
        BusinessActivity.AccountancyServices,
        BusinessActivity.EstateAgentBusinessService,
        BusinessActivity.BillPaymentServices,
        BusinessActivity.TelephonePaymentService,
        BusinessActivity.MoneyServiceBusiness,
        BusinessActivity.TrustAndCompanyServices,
        BusinessActivity.ArtMarketParticipant)),
      Some(MsbServices(Set(ChequeCashingNotScrapMetal, TransmittingMoney, CurrencyExchange, ChequeCashingScrapMetal))), None, Some("Added")),
    TradingPremises(Some(RegisteringAgentPremises(false)), YourTradingPremises("OwnBusinessTradingName",
      Address("OwnBusinessAddressLine1", "OwnBusinessAddressLine2", Some("OwnBusinessAddressLine3"), Some("OwnBusinessAddressLine4"), "YY1 1YY"),
      new LocalDate(2001, 5, 5), false),
      None, None, None, None,
      WhatDoesYourBusinessDo(Set(
        BusinessActivity.EstateAgentBusinessService,
        BusinessActivity.BillPaymentServices,
        BusinessActivity.ArtMarketParticipant,
        BusinessActivity.TrustAndCompanyServices)), None, Some(444444), Some(StatusConstants.Unchanged)),
    TradingPremises(Some(RegisteringAgentPremises(false)), YourTradingPremises("OwnBusinessTradingName1",
      Address("OB11AddressLine1", "OB1AddressLine2", Some("OB1AddressLine3"), Some("OB1AddressLine4"), "XX1 1XX"),
      new LocalDate(2001, 1, 1), false), None, None, None, None, WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
      BusinessActivity.AccountancyServices,
      BusinessActivity.EstateAgentBusinessService,
      BusinessActivity.BillPaymentServices,
      BusinessActivity.TelephonePaymentService,
      BusinessActivity.MoneyServiceBusiness,
      BusinessActivity.TrustAndCompanyServices,
      BusinessActivity.ArtMarketParticipant)),
      Some(MsbServices(Set(ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))), Some(555555), Some(StatusConstants.Unchanged))))


  val tradingPremisesOnlyAgentModel = Some(List(TradingPremises(Some(RegisteringAgentPremises(true)),
    YourTradingPremises("aaaaaaaaaaaa", Address("a", "a", Some("a"), Some("a"), "AA1 1AA"),
      new LocalDate(1967, 8, 13), true),
    Some(BusinessStructure.SoleProprietor),
    Some(AgentName("aaaaaaaaaaa", None, Some("1970-01-01"))),
    None,
    None,
    WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
      BusinessActivity.AccountancyServices,
      BusinessActivity.EstateAgentBusinessService,
      BusinessActivity.BillPaymentServices,
      BusinessActivity.TelephonePaymentService,
      BusinessActivity.MoneyServiceBusiness,
      BusinessActivity.TrustAndCompanyServices,
      BusinessActivity.ArtMarketParticipant)),
    Some(MsbServices(Set(TransmittingMoney, CurrencyExchange, ChequeCashingNotScrapMetal, ChequeCashingScrapMetal))), None, Some("Added"))))
}

object BankDetailsSection {

  import models.fe.bankdetails._

  val model = Seq(
    BankDetails(PersonalAccount,
      "Personal account", UKAccount("12345678", "112233")),
    BankDetails(BelongsToBusiness,
      "Business account", NonUKAccountNumber("12345678")),
    BankDetails(BelongsToOtherBusiness,
      "Another Business account", NonUKIBANNumber("12345678"))
  )

  val modelForView = List(BankDetails(BelongsToBusiness, "AccountName", UKAccount("12345678", "123456")),
    BankDetails(PersonalAccount, "AccountName1", NonUKIBANNumber("87654321")),
    BankDetails(BelongsToOtherBusiness, "AccountName2", NonUKAccountNumber("87654321")))

}

object BusinessMatchingSection {

  import models.fe.businesscustomer.{Address, ReviewDetails}
  import models.fe.businessmatching.{BusinessType, _}

  private val msbService = MsbServices(Set(TransmittingMoney, ChequeCashingNotScrapMetal, CurrencyExchange))
  private val psrPSRNumber = Some(BusinessAppliedForPSRNumberYes("123456"))
  private val bcAddress = Address("line1", "line2", Some("line3"), Some("line4"), Some("AA1 1AA"), "GB")
  private val reviewDetails = ReviewDetails("BusinessName", BusinessType.SoleProprietor, bcAddress, "XE0001234567890")
  val model = BusinessMatching(
    reviewDetails,
    models.fe.businessmatching.BusinessActivities(Set(MoneyServiceBusiness, HighValueDealing, AccountancyServices)),
    Some(msbService),
    None,
    None,
    psrPSRNumber)

  val emptyModel = BusinessMatching(
    activities = BusinessActivities(Set.empty),
    reviewDetails = ReviewDetails(
      "",
      BusinessType.SoleProprietor,
      models.fe.businesscustomer.Address(
        line_1 = "",
        line_2 = "",
        line_3 = None,
        line_4 = None,
        postcode = None,
        country = ""
      ),
      ""
    )
  )

  val msbServices = Some(MsbServices(Set(ChequeCashingNotScrapMetal,
    CurrencyExchange, TransmittingMoney,
    ChequeCashingScrapMetal, ForeignExchange)))
  val psrNumber = Some(BusinessAppliedForPSRNumberYes("123456"))

  val modelForView = BusinessMatching(
    ReviewDetails("CompanyName", BusinessType.SoleProprietor, Address("BusinessAddressLine1", "BusinessAddressLine2",
      Some("BusinessAddressLine3"), Some("BusinessAddressLine4"),
      Some("AA1 1AA"), "GB"), ""),
    BusinessActivities(Set(HighValueDealing, AccountancyServices, EstateAgentBusinessService,
      BillPaymentServices, TelephonePaymentService, MoneyServiceBusiness, TrustAndCompanyServices, ArtMarketParticipant)),
    msbServices,
    Some(TypeOfBusiness("TypeOfBusiness")), Some(CompanyRegistrationNumber("12345678")), psrNumber)
}

object AboutTheBusinessSection {

  import models.fe.businessdetails._

  private val regForCorpTax = CorporationTaxRegisteredYes("1234567890")
  // scalastyle:off magic.number
  val model = BusinessDetails(PreviouslyRegisteredYes(Some("12345678")),
    Some(ActivityStartDate(new LocalDate(1990, 2, 24))),
    Some(VATRegisteredYes("123456789")),
    Some(regForCorpTax),
    ContactingYou("019212323222323222323222323222", "abc@hotmail.co.uk"),
    RegisteredOfficeUK("line1", "line2",
      Some("some street"), Some("some city"), "EE1 1EE"),
    true,
    Some(UKCorrespondenceAddress("kap", "Trading", "Park", "lane",
      Some("Street"), Some("city"), "EE1 1EE"))
  )

  val modelForView = BusinessDetails(PreviouslyRegisteredNo, Some(ActivityStartDate(new LocalDate(2001, 1, 1))),
    Some(VATRegisteredYes("123456789")),
    Some(CorporationTaxRegisteredYes("1234567891")),
    ContactingYou("07000111222", "BusinessEmail"),
    RegisteredOfficeUK("BusinessAddressLine1", "BusinessAddressLine2", Some("BusinessAddressLine3"), Some("BusinessAddressLine4"), "AA1 1AA"),
    true,
    Some(UKCorrespondenceAddress("Name", "TradingName", "AlternativeAddressLine1", "AlternativeAddressLine2", Some("AlternativeAddressLine3"),
      Some("AlternativeAddressLine4"), "AA1 1AA")))
}

object ResponsiblePeopleSection {

  import models.fe.responsiblepeople.TimeAtAddress.{OneToThreeYears, SixToElevenMonths, ZeroToFiveMonths}
  import models.fe.responsiblepeople._

  private val residence = UKResidence("nino")
  private val residenceCountry = "GB"
  private val residenceNationality = "GB"
  private val currentPersonAddress = PersonAddressUK("ccLine 1", "ccLine 2", None, None, "AA1 1AA")
  private val currentAddress = ResponsiblePersonAddress(currentPersonAddress, ZeroToFiveMonths)
  private val additionalPersonAddress = PersonAddressUK("Line 1", "Line 2", None, None, "BB1 1BB")
  private val additionalAddress = ResponsiblePersonAddress(additionalPersonAddress, SixToElevenMonths)
  private val extraAdditionalAddress = PersonAddressUK("e Line 1", "e Line 2", Some("e Line 3"), Some("e Line 4"), "CC1 1CC")
  private val extraAdditionalPersonAddress = ResponsiblePersonAddress(extraAdditionalAddress, OneToThreeYears)

  private val personName = PersonName(
    firstName = "name",
    middleName = Some("some"),
    lastName = "surname"
  )

  private val previousName = PreviousName(
    hasPreviousName = true,
    firstName = Some("fname"),
    middleName = Some("mname"),
    lastName = Some("lname")
  )

  private val otherNames = Some(KnownBy(true, Some("Doc")))
  private val nameDateOfChange = new LocalDate(1990, 2, 24)
  private val personResidenceType = PersonResidenceType(residence, residenceCountry, residenceNationality)
  private val saRegistered = SaRegisteredYes("0123456789")
  private val contactDetails = ContactDetails("07000001122", "test@test.com")
  private val addressHistory = ResponsiblePersonAddressHistory(Some(currentAddress), Some(additionalAddress), Some(extraAdditionalPersonAddress))
  private val vatRegistered = VATRegisteredNo
  private val training = TrainingYes("test")
  private val experienceTraining = ExperienceTrainingYes("Some training")
  private val positions = Positions(Set(SoleProprietor, NominatedOfficer), Some(new LocalDate()))

  private val ukPassport = UKPassportYes("87654321")
  private val nonUKPassport = NonUKPassportYes("87654321")

  val model = Some(Seq(ResponsiblePeople(
    personName                    = Some(personName),
    legalName                     = Some(previousName),
    legalNameChangeDate           = Some(nameDateOfChange),
    knownBy                       = otherNames,
    personResidenceType           = Some(personResidenceType),
    ukPassport                    = Some(ukPassport),
    nonUKPassport                 = Some(nonUKPassport),
    dateOfBirth                   = None,
    contactDetails                = Some(contactDetails),
    addressHistory                = Some(addressHistory),
    positions                     = Some(positions),
    saRegistered                  = Some(saRegistered),
    vatRegistered                 = Some(vatRegistered),
    experienceTraining            = Some(experienceTraining),
    training                      = Some(training),
    approvalFlags = ApprovalFlags(hasAlreadyPassedFitAndProper  = Some(true), hasAlreadyPaidApprovalCheck = Some(true))
  )))

  val modelPhase2 = Some(Seq(ResponsiblePeople(
    personName                    = Some(personName),
    legalName                     = Some(previousName),
    legalNameChangeDate           = Some(nameDateOfChange),
    knownBy                       = otherNames,
    personResidenceType           = Some(personResidenceType),
    ukPassport                    = Some(ukPassport),
    nonUKPassport                 = Some(nonUKPassport),
    dateOfBirth                   = Some(DateOfBirth(new LocalDate(1970,1,1))),
    contactDetails                = Some(contactDetails),
    addressHistory                = Some(addressHistory),
    positions                     = Some(positions),
    saRegistered                  = Some(saRegistered),
    vatRegistered                 = Some(vatRegistered),
    experienceTraining            = Some(experienceTraining),
    training                      = Some(training),
    approvalFlags = ApprovalFlags(hasAlreadyPassedFitAndProper  = Some(false), hasAlreadyPaidApprovalCheck = Some(true))
  )))

  val modelForView = Some(List(
    ResponsiblePeople(
      Some(PersonName("FirstName", Some("MiddleName"), "LastName")),
      Some(PreviousName(true, Some("FirstName"), Some("MiddleName"), Some("LastName"))),
      Some(new LocalDate(2001, 1, 1)),
      Some(KnownBy(true, Some("Aliases1"))),
      Some(PersonResidenceType(NonUKResidence, "AA", "AA")),
      Some(UKPassportYes("AA1111111")),
      Some(NoPassport),
      Some(DateOfBirth(new LocalDate(2001, 1, 1))),
      None,
      Some(ResponsiblePersonAddressHistory(
        Some(ResponsiblePersonAddress(PersonAddressUK("CurrentAddressLine1",
          "CurrentAddressLine2", Some("CurrentAddressLine3"), Some("CurrentAddressLine4"), "AA1 1AA"),
          ThreeYearsPlus)),
        None,
        None
      )),
      Some(Positions(Set(NominatedOfficer, SoleProprietor), Some(new LocalDate()))),
      Some(SaRegisteredYes("1234567890")),
      Some(VATRegisteredYes("123456789")),
      Some(ExperienceTrainingNo),
      Some(TrainingYes("TrainingDetails")),
      ApprovalFlags(Some(false), hasAlreadyPaidApprovalCheck = None),
      Some(333333)
    ),

    ResponsiblePeople(
      Some(PersonName("bbbbbbbbbbbb", Some("bbbbbbbbbbb"), "bbbbbbbbbbb")),
      Some(PreviousName(true, Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
      Some(new LocalDate(1967, 8, 13)),
      Some(KnownBy(true, Some("bbbbbbbbbbb"))),
      Some(PersonResidenceType(UKResidence("BB000000A"), "GB", "GB")),
      ukPassport = None,
      nonUKPassport = None,
      dateOfBirth = None,
      contactDetails = None,
      addressHistory = Some(ResponsiblePersonAddressHistory(
        Some(ResponsiblePersonAddress(PersonAddressUK("b", "b", Some("b"), Some("b"), "AA1 1AA"), ZeroToFiveMonths)),
        Some(ResponsiblePersonAddress(PersonAddressUK("b", "b", Some("b"), Some("b"), "AA1 1AA"), ZeroToFiveMonths)),
        Some(ResponsiblePersonAddress(PersonAddressUK("a", "a", Some("a"), Some("a"), "AA1 1AA"), SixToElevenMonths)))),
      positions = Some(Positions(Set(NominatedOfficer, SoleProprietor), Some(new LocalDate()))),
      saRegistered = Some(SaRegisteredYes("1111111111")),
      vatRegistered = Some(VATRegisteredYes("111111111")),
      experienceTraining = Some(ExperienceTrainingYes("bbbbbbbbbb")),
      training = Some(TrainingNo),
      approvalFlags = ApprovalFlags(Some(true), hasAlreadyPaidApprovalCheck = None),
      lineId = Some(222222)
    )))

  val modelForViewPhase2 = Some(List(
    ResponsiblePeople(
      Some(PersonName("FirstName", Some("MiddleName"), "LastName")),
      Some(PreviousName(true, Some("FirstName"), Some("MiddleName"), Some("LastName"))),
      Some(new LocalDate(2001, 1, 1)),
      Some(KnownBy(true, Some("Aliases1"))),
      Some(PersonResidenceType(NonUKResidence, "AA", "AA")),
      Some(UKPassportYes("AA1111111")),
      Some(NoPassport),
      Some(DateOfBirth(new LocalDate(2001, 1, 1))),
      None,
      Some(ResponsiblePersonAddressHistory(
        Some(ResponsiblePersonAddress(PersonAddressUK("CurrentAddressLine1",
          "CurrentAddressLine2", Some("CurrentAddressLine3"), Some("CurrentAddressLine4"), "AA1 1AA"),
          ThreeYearsPlus)),
        None,
        None
      )),
      Some(Positions(Set(NominatedOfficer, SoleProprietor), Some(new LocalDate()))),
      Some(SaRegisteredYes("1234567890")),
      Some(VATRegisteredYes("123456789")),
      Some(ExperienceTrainingNo),
      Some(TrainingYes("TrainingDetails")),
      ApprovalFlags(hasAlreadyPassedFitAndProper = Some(false), hasAlreadyPaidApprovalCheck = Some(true)),
      Some(333333)
    ),

    ResponsiblePeople(
      Some(PersonName("bbbbbbbbbbbb", Some("bbbbbbbbbbb"), "bbbbbbbbbbb")),
      Some(PreviousName(true, Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"), Some("bbbbbbbbbbbb"))),
      Some(new LocalDate(1967, 8, 13)),
      Some(KnownBy(true, Some("bbbbbbbbbbb"))),
      Some(PersonResidenceType(UKResidence("BB000000A"), "GB", "GB")),
      None, None,
      Some(DateOfBirth(new LocalDate(2001, 1, 1))),
      None,
      Some(ResponsiblePersonAddressHistory(
        Some(ResponsiblePersonAddress(PersonAddressUK("b", "b", Some("b"), Some("b"), "AA1 1AA"), ZeroToFiveMonths)),
        Some(ResponsiblePersonAddress(PersonAddressUK("b", "b", Some("b"), Some("b"), "AA1 1AA"), ZeroToFiveMonths)),
        Some(ResponsiblePersonAddress(PersonAddressUK("a", "a", Some("a"), Some("a"), "AA1 1AA"), SixToElevenMonths)))),
      Some(Positions(Set(NominatedOfficer, SoleProprietor), Some(new LocalDate()))),
      Some(SaRegisteredYes("1111111111")),
      Some(VATRegisteredYes("111111111")),
      Some(ExperienceTrainingYes("bbbbbbbbbb")),
      Some(TrainingNo),
      ApprovalFlags(hasAlreadyPassedFitAndProper = Some(true), hasAlreadyPaidApprovalCheck = Some(false)),
      Some(222222)
    )))
}

object HvdSection {

  import models.fe.hvd.PercentageOfCashPaymentOver15000.Second
  import models.fe.hvd._

  private val DefaultCashPayment = CashPaymentYes(new LocalDate(1978, 2, 15))
  private val DefaultProducts = Products(Set(Antiques, Cars, OtherMotorVehicles, Other("Details"), Alcohol, Tobacco))
  private val DefaultExciseGoods = ExciseGoods(true)
  private val DefaultLinkedCashPayment = LinkedCashPayments(true)
  private val DefaultHowWillYouSellGoods = HowWillYouSellGoods(Seq(Retail, Auction))
  private val DefaultPercentageOfCashPaymentOver15000 = Second
  private val paymentMethods = PaymentMethods(courier = true, direct = true, other = true, details = Some("foo"))

  val completeModel = Some(Hvd(cashPayment = Some(DefaultCashPayment),
    products = Some(DefaultProducts),
    exciseGoods = Some(DefaultExciseGoods),
    linkedCashPayment = Some(DefaultLinkedCashPayment),
    howWillYouSellGoods = Some(DefaultHowWillYouSellGoods),
    receiveCashPayments = Some(true),
    cashPaymentMethods = Some(paymentMethods),
    percentageOfCashPaymentOver15000 = Some(DefaultPercentageOfCashPaymentOver15000)
  ))

  val modelForView = Some(Hvd(
    Some(CashPaymentYes(new LocalDate(2001, 1, 1))),
    Some(Products(Set(MobilePhones, Clothing, Jewellery, ScrapMetals, Alcohol, Caravans, Gold, Other("SpecifyOther"), Tobacco, Antiques, Cars, OtherMotorVehicles))),
    Some(ExciseGoods(true)), Some(HowWillYouSellGoods(List(Retail, Wholesale, Auction))),
    None,
    Some(true),
    Some(PaymentMethods(true, true, true, Some("aaaaaaaaaaaaa"))),
    Some(LinkedCashPayments(true))))
}

object AmpSection {

  private val ampData = AmpData(
    typeOfParticipant = List("artGalleryOwner", "artDealer", "artAgent" ,"artAuctioneer",  "somethingElse"),
    typeOfParticipantDetail = Some("Another service"),
    true,
    Some("2019-09-19 16:58:06.259Z"),
    true,
    Some("fortyOneToSixty")
  )

  val completeModel = Some(Amp(ampData))

}

object SubscriptionViewModel {

  val convertedViewModel = SubscriptionView(
    "111111",
    BusinessMatchingSection.modelForView,
    EabSection.modelForView,
    TradingPremisesSection.modelForView,
    AboutTheBusinessSection.modelForView,
    BankDetailsSection.modelForView,
    AboutYouSection.modelforView,
    BusinessActivitiesSection.modelForView,
    ResponsiblePeopleSection.modelForView,
    ASPTCSPSection.TcspModelForView,
    ASPTCSPSection.AspModelForView,
    MsbSection.modelForView,
    HvdSection.modelForView,
    AmpSection.completeModel,
    SupervisionSection.modelForView
  )


  val convertedViewModelPhase2 = SubscriptionView(
    "111111",
    BusinessMatchingSection.modelForView,
    EabSection.modelForView,
    TradingPremisesSection.modelForView,
    AboutTheBusinessSection.modelForView,
    BankDetailsSection.modelForView,
    AboutYouSection.modelforView,
    BusinessActivitiesSection.modelForView,
    ResponsiblePeopleSection.modelForViewPhase2,
    ASPTCSPSection.TcspModelForView,
    ASPTCSPSection.AspModelForView,
    MsbSection.modelForView,
    HvdSection.modelForView,
    AmpSection.completeModel,
    SupervisionSection.modelForView
  )
}
