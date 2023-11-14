/*
 * Copyright 2023 HM Revenue & Customs
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

package utils

import models.DefaultDesValues
import models.des.tradingpremises._
import models.des.amp.{Amp, TransactionsAccptOvrThrshld}
import models.des.aboutthebusiness.{Address, AlternativeAddress, BusinessContactDetails, PreviouslyRegisteredMLRView}
import models.des.aboutyou.{AboutYouRelease7, IndividualDetails, RoleForTheBusiness, RolesWithinBusiness}
import models.des.bankdetails.{Account, AccountNumber, AccountView, BankAccount, BankAccountView, BankDetailsView, ukAccountView}
import models.des.businessactivities.{AdvisorNameAddress, AmpServices, AmpServicesOther, AspServicesOffered, AuditableRecordsDetails, BusinessActivities, BusinessActivitiesAll, BusinessActivityDetails, EabServices, ExpectedAMLSTurnover, FormalRiskAssessmentDetails, FranchiseDetails, HowGoodsAreSold, HvdAlcoholTobacco, HvdGoodsSold, MlrActivitiesAppliedFor, MlrAdvisor, MlrAdvisorDetails, MsbServicesCarriedOut, NonUkResidentCustDetails, RiskAssessmentFormat, ServicesforRegOff, TcspServicesOffered, TransactionRecordingMethod}
import models.des.businessdetails.{BusinessDetails, BusinessType}
import models.des.hvd.{Hvd, HvdFromUnseenCustDetails, ReceiptMethods}
import models.des.msb.{CountriesList, CurrSupplyToCust, CurrencySourcesR7, CurrencyWholesalerDetails, IpspServicesDetails, MSBBankDetails, MoneyServiceBusiness, MsbAllDetails, MsbCeDetailsR7, MsbMtDetails}
import models.des.responsiblepeople.{AddressWithChangeDate, ContactCommDetails, CorpBodyOrUnInCorpBodyOrLlp, CurrentAddress, IdDetail, MsbOrTcsp, NameDetails, NationalityDetails, Partnership, PersonName, PositionInBusiness, RPExtra, RegDetails, ResponsiblePersons, SoleProprietor, UkResident}
import models.des.tradingpremises.{Msb, OwnBusinessPremises, OwnBusinessPremisesDetails, TradingPremises}
import models.des.{AmendVariationRequest, AmlsMessageType, ChangeIndicators, Declaration, DesConstants, EtmpFields, ExtraFields}
import models.fe.declaration.RoleWithinBusiness
import org.scalatest.EitherValues
import org.scalatestplus.play.PlaySpec

class AmendVariationValidatorSpec extends PlaySpec with EitherValues {

  val amendVariationValidator = new AmendVariationValidator

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  "AmendVariationValidator" must {
    "validate an invalid request" in {

      val jsResult = amendVariationValidator.validateResult(givenInvalidAmendVariationRequest())

      jsResult.isLeft mustBe true
    }

    "validate a valid request" in {

      val jsResult = amendVariationValidator.validateResult(givenValidAmendVariationRequest())

      jsResult.isRight mustBe true
    }
  }

  private def givenInvalidAmendVariationRequest() = {
    AmendVariationRequest(acknowledgementReference = ackref.ackRef,
      changeIndicators = ChangeIndicators(tradingPremises = true),
      amlsMessageType = "Amendment",
      businessDetails = BusinessDetails(BusinessType.SoleProprietor, None, None),
      businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
      businessReferencesAll = None,
      businessReferencesAllButSp = None,
      businessReferencesCbUbLlp = None,
      businessActivities = DefaultDesValues.BusinessActivitiesSection,
      tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(DesConstants.subscriptionRequestOwnBusinessPremisesDetails)))), None),
      bankAccountDetails = None,
      msb = DefaultDesValues.msbSection,
      hvd = DefaultDesValues.hvdSection,
      asp = DefaultDesValues.AspSection,
      aspOrTcsp = DefaultDesValues.AspOrTcspSection,
      tcspAll = DefaultDesValues.tcspAllSection,
      tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
      eabAll = DefaultDesValues.EabAllDetails,
      eabResdEstAgncy = DefaultDesValues.EabResd,
      responsiblePersons = DefaultDesValues.validResponsiblePersons,
      amp = Some(validAmpSection),
      lettingAgents = None,
      extraFields = ExtraFields(Declaration(true), AboutYouRelease7(None, true, None, None), None))
  }

  private def givenValidAmendVariationRequest() = {
    AmendVariationRequest(acknowledgementReference = "b998aae85b78423989ffb9161ee74a0d",
      changeIndicators = ChangeIndicators(hvd = true, filingIndividual = true),
      amlsMessageType = "Amendment",
      businessDetails = BusinessDetails(BusinessType.SoleProprietor, None, None),
      businessContactDetails = validBusinessContactDetails,
      businessReferencesAll = Some(PreviouslyRegisteredMLRView(true, Some("ABCD1234"), false, None)),
      businessReferencesAllButSp = None,
      businessReferencesCbUbLlp = None,
      businessActivities = givenValidBusinessActivities,
      tradingPremises = validTradingPremises,
      bankAccountDetails = Some(BankDetailsView(Some("1"), Some(List(BankAccountView("Account Name", "Personal", true, ukAccountView("123456", "12345678")))))),
      msb = Some(validMsb),
      hvd = validHvdSection,
      asp = DefaultDesValues.AspSection,
      aspOrTcsp = DefaultDesValues.AspOrTcspSection,
      tcspAll = DefaultDesValues.tcspAllSection,
      tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
      eabAll = DefaultDesValues.EabAllDetails,
      eabResdEstAgncy = DefaultDesValues.EabResd,
      responsiblePersons = Some(Seq(validResponsiblePersons)),
      amp = Some(validAmpSection),
      lettingAgents = None,
      extraFields = ExtraFields(Declaration(true),
        AboutYouRelease7(Some(IndividualDetails("First Name", None, "Last Name")),
          true,
          Some(RolesWithinBusiness(false, false, false, false, true, false, false, false, None)),
          Some(RoleForTheBusiness(true, false, None))),
        None))
  }

  private val givenValidBusinessActivities =
    BusinessActivities(
      Some(MlrActivitiesAppliedFor(true, true, true, false, false, false, false, false)),
      Some(MsbServicesCarriedOut(true, true, false, true, false)),
      Some(HvdGoodsSold(true, true, true, true, true, false, false, false, false, false, false, true, Some("Details"),
        Some(HowGoodsAreSold(true, false, true)))),
      Some(HvdAlcoholTobacco(true)),
      Some(AspServicesOffered(true, false, false, true, true)),
      Some(TcspServicesOffered(true, true, true, true, true)),
      Some(ServicesforRegOff(true, false, false, false, false, false, false, true, Some("other service"))),
      Some(EabServices(false, false, true, false, true, false, false, false, false, Some(false))),
      Some(AmpServices(true, false, false, false, AmpServicesOther(false, None))),
      Some(
        BusinessActivitiesAll(
          Some("2016-05-25"),
          Some("1990-02-24"),
          Some(true),
          BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£0-£15k")))),
          Some(FranchiseDetails(true, Some(Seq("Name")))),
          Some("10"),
          Some("5"),
          NonUkResidentCustDetails(true, Some(Seq("GB", "AD"))),
          AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("value")))),
          true,
          true,
          Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true)))),
          Some(MlrAdvisor(true, Some(MlrAdvisorDetails(Some(AdvisorNameAddress("Name", Some("TradingName"),
            Address("Line1", Some("Line2"), Some("Line3"), Some("Line4"), "GB", Some("AA1 1AA")))), true, Some("1234567")))))
        )
      )
    )

  private val validBusinessContactDetails = BusinessContactDetails(
    Address("line1", Some("line2"),
      Some("some street"), Some("some city"), "GB", Some("EE1 1EE")),
    true,
    Some(AlternativeAddress("kap", "Trading", Address("Park", Some("lane"),
      Some("Street"), Some("city"), "GB", Some("EE1 1EE")))),
    "02081231234",
    "abc@hotmail.co.uk")

  private val validHvdSection = Some(Hvd(true, Some("1978-02-15"), Some(true), true, Some(40),
    Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))))

  private val validResponsiblePersons = ResponsiblePersons(
    nameDetails = Some(NameDetails(PersonName(Some("First"), None, Some("Name")), None, None)),
    nationalityDetails = Some(NationalityDetails(true, Some(IdDetail(Some(UkResident("BB000000A")), None, dateOfBirth = Some("1990-02-24"))), Some("GB"), Some("GB"))),
    contactCommDetails = Some(ContactCommDetails("email@email.com", "02081231234", None)),
    currentAddressDetails = Some(CurrentAddress(AddressWithChangeDate("address 1", None, None, None, "GB", Some("N1 8WE")))),
    timeAtCurrentAddress = Some("3+ years"),
    addressUnderThreeYears = None,
    timeAtAddressUnderThreeYears = None,
    addressUnderOneYear = None,
    timeAtAddressUnderOneYear = None,
    positionInBusiness = Some(PositionInBusiness(Some(SoleProprietor(true, other = Some(false))), None, None)),
    regDetails = Some(RegDetails(false, None, true, Some("0123456789"))),
    previousExperience = false,
    descOfPrevExperience = None,
    amlAndCounterTerrFinTraining = false,
    trainingDetails = None,
    startDate = None,
    dateChangeFlag = Some(true),
    msbOrTcsp = None,
    passedFitAndProperTest = Some(true),
    passedApprovalCheck = Some(true),
    extra = RPExtra())

  private val validTradingPremises = TradingPremises(
    ownBusinessPremises = Some(OwnBusinessPremises(true, Some(Seq(OwnBusinessPremisesDetails(
      tradingName = Some("trading name"),
      businessAddress = models.des.tradingpremises.Address("address line 1", None, None, None, country = "GB", Some("N1 8WE")),
      residential = false,
      msb = Msb(true, true, true, true, true),
      hvd = models.des.tradingpremises.Hvd(true),
      asp = models.des.tradingpremises.Asp(false),
      tcsp = models.des.tradingpremises.Tcsp(false),
      eab = models.des.tradingpremises.Eab(false),
      bpsp = models.des.tradingpremises.Bpsp(false),
      tditpsp = models.des.tradingpremises.Tditpsp(false),
      amp = models.des.tradingpremises.Amp(false),
      startDate = "2016-02-15",
      endDate = None,
      lineId = Some("1234"),
      status = None,
      sectorDateChange = Some("2017-02-15"),
      dateChangeFlag = Some(true),
      tradingNameChangeDate = Some("2017-04-15")
    ))))),
    agentBusinessPremises = Some(AgentBusinessPremises(
      agentBusinessPremises = true,
      agentDetails = Some(Seq(AgentDetails(
        agentLegalEntity = "Sole Proprietor",
        companyRegNo = Some("11111111"),
        dateOfBirth = Some("1980-02-15"),
        agentLegalEntityName = Some("Name"),
        agentPremises = AgentPremises(
          tradingName = "Trading Name",
          businessAddress = models.des.tradingpremises.Address("address line 1", None, None, None, "GB", Some("N1 2WS"), None),
          residential = false,
          msb = models.des.tradingpremises.Msb(true, true, true, true, true),
          hvd = models.des.tradingpremises.Hvd(true),
          asp = models.des.tradingpremises.Asp(false),
          tcsp = models.des.tradingpremises.Tcsp(false),
          eab = models.des.tradingpremises.Eab(false),
          bpsp = models.des.tradingpremises.Bpsp(false),
          tditpsp = models.des.tradingpremises.Tditpsp(false),
          amp = models.des.tradingpremises.Amp(false),
          startDate = None,
          sectorChangeDate = None
        ),
        startDate = Some("2020-02-15"),
        dateChangeFlag = Some(true),
        endDate = None,
        status = None,
        lineId = Some("1234"),
        agentDetailsChangeDate = Some("2021-02-15"),
        removalReason = None,
        removalReasonOther = None
      )))
    )))

  private val validMsb = MoneyServiceBusiness(
    msbAllDetails = Some(MsbAllDetails(
      anticipatedTotThrputNxt12Mths = Some("£0-£15k"),
      otherCntryBranchesOrAgents = true,
      countriesList = Some(CountriesList(List("GB"))),
      sysLinkedTransIdentification = true
    )),
    msbMtDetails = Some(MsbMtDetails(
      applyForFcapsrRegNo = true,
      fcapsrRefNo = Some("123456"),
      ipspServicesDetails = IpspServicesDetails(false, None),
      informalFundsTransferSystem = false,
      noOfMoneyTrnsfrTransNxt12Mnths = None,
      countriesLrgstMoneyAmtSentTo = Some(CountriesList(Seq("GB"))),
      countriesLrgstTranscsSentTo = Some(CountriesList(Seq("GB"))),
      psrRefChangeFlag = Some(true)
    )),
    msbCeDetails = Some(MsbCeDetailsR7(
      dealInPhysCurrencies = Some(false),
      currencySources = Some(CurrencySourcesR7(Some(MSBBankDetails(true, Some(List("Bank names")))), Some(CurrencyWholesalerDetails(false, None)), false)),
      antNoOfTransNxt12Mnths = "100",
      currSupplyToCust = Some(CurrSupplyToCust(List("USD")))
    )),
    msbFxDetails = None)

  private val validAmpSection = Amp(TransactionsAccptOvrThrshld(true, Some("2021-02-15")), true, ampPercentageTurnover = 20)
}
