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
import models.des.aboutthebusiness.{Address, AlternativeAddress, BusinessContactDetails}
import models.des.amp.{Amp, TransactionsAccptOvrThrshld}
import models.des.businessactivities._
import models.des.businessdetails.{BusinessDetails, BusinessType, CorpAndBodyLlps}
import models.des.hvd.{Hvd, HvdFromUnseenCustDetails, ReceiptMethods}
import models.des.msb._
import models.des.supervision._
import models.des.tradingpremises.{AgentBusinessPremises, OwnBusinessPremises, OwnBusinessPremisesDetails, TradingPremises}
import models.des.{Declaration, DesConstants, SubscriptionRequest, tradingpremises}
import org.scalatest.EitherValues
import org.scalatestplus.play.PlaySpec

class SubscriptionRequestValidatorSpec extends PlaySpec with EitherValues {

  val validator = new SubscriptionRequestValidator

  implicit val ackref: AckRefGenerator = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  "Subscription request validator" must {
        "validate invalid request" in {
          val jsResult = validator.validateRequest(givenInvalidSubscriptionRequest())
          jsResult.isLeft mustBe true
        }

    "validate valid request" in {
      val jsResult = validator.validateRequest(givenValidSubscriptionRequest())
      jsResult.isRight mustBe true
    }
  }

  private def givenInvalidSubscriptionRequest(): SubscriptionRequest = {
    SubscriptionRequest(
      acknowledgementReference = ackref.ackRef,
      businessDetails = BusinessDetails(BusinessType.LimitedCompany, Some(CorpAndBodyLlps("ABCDEFGHIJK ABCDE & LETTINGS LTD", "12345678")), None),
      businessActivities = DefaultDesValues.BusinessActivitiesSection,
      eabAll = DefaultDesValues.EabAllDetails,
      eabResdEstAgncy = DefaultDesValues.EabResd,
      businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
      businessReferencesAll = DefaultDesValues.PrevRegMLR,
      businessReferencesAllButSp = None,
      businessReferencesCbUbLlp = DefaultDesValues.CorpTaxRegime,
      tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(DesConstants.subscriptionRequestOwnBusinessPremisesDetails)))), None),
      bankAccountDetails = DefaultDesValues.bankDetailsSection,
      msb = DefaultDesValues.msbSection,
      hvd = DefaultDesValues.hvdSection,
      filingIndividual = DefaultDesValues.filingIndividual,
      tcspAll = DefaultDesValues.tcspAllSection,
      tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
      responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
      asp = DefaultDesValues.AspSection,
      amp = DefaultDesValues.AmpSection,
      aspOrTcsp = DefaultDesValues.AspOrTcspSection,
      declaration = Declaration(true),
      lettingAgents = None)
  }

  private def givenValidSubscriptionRequest(): SubscriptionRequest = {
    SubscriptionRequest(
      acknowledgementReference = "b998aae85b78423989ffb9161ee74a0d",
      businessDetails = BusinessDetails(BusinessType.LimitedCompany, Some(CorpAndBodyLlps("ABCDEFGHIJK ABCDE & LETTINGS LTD", "12345678")), None),
      businessActivities = givenValidBusinessActivities,
      eabAll = DefaultDesValues.EabAllDetails,
      eabResdEstAgncy = DefaultDesValues.EabResd,
      businessContactDetails = givenValidBusinessContactDetails,
      businessReferencesAll = DefaultDesValues.PrevRegMLR,
      businessReferencesAllButSp = None,
      businessReferencesCbUbLlp = DefaultDesValues.CorpTaxRegime,
      tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(validOwnBusinessPremisesDetails)))),
        Some(AgentBusinessPremises(false, None))),
      bankAccountDetails = DefaultDesValues.bankDetailsSection,
      msb = validMsbSection,
      hvd = validHvdSection,
      filingIndividual = DefaultDesValues.filingIndividual,
      tcspAll = DefaultDesValues.tcspAllSection,
      tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
      responsiblePersons = DefaultDesValues.validResponsiblePersons,
      asp = DefaultDesValues.AspSection,
      amp = validAmpSection,
      aspOrTcsp = validAspOrTcspSection,
      declaration = Declaration(true),
      lettingAgents = None)
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
      Some(AmpServices(true, true, true, true, AmpServicesOther(true, Some("Another service")))),
      Some(
        BusinessActivitiesAll(
          None,
          Some("1990-02-24"),
          None,
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
            Address("Line1", Some("Line2"), Some("Line3"), Some("Line4"), "GB", Some("AA1 1AA")))), true, None))))
        )
      )
    )

  private val givenValidBusinessContactDetails =
    BusinessContactDetails(
      Address("line1", Some("line2"), Some("some street"), Some("some city"), "GB", Some("EE1 1EE")),
      true,
      Some(AlternativeAddress("kap", "Trading", Address("Park", Some("lane"), Some("Street"), Some("city"), "GB", Some("EE1 1EE")))),
      "078 6353 4828",
      "abc@hotmail.co.uk"
    )

  private val validMsbSection =
    Some(
      MoneyServiceBusiness(
        Some(MsbAllDetails(Some("£1m-10m"), true, Some(CountriesList(List("GB"))), true)),
        Some(MsbMtDetails(true, Some("123456"),
          IpspServicesDetails(true, Some(Seq(IpspDetails("name", "123456789123456")))),
          true,
          Some("12345678963"), Some(CountriesList(List("GB"))), Some(CountriesList(List("LA", "LV"))))),
        Some(MsbCeDetailsR7(Some(true), Some(CurrencySourcesR7(Some(MSBBankDetails(true, Some(List("Bank names")))),
          Some(CurrencyWholesalerDetails(true, Some(List("wholesaler names")))), true)), "12345678963", Some(CurrSupplyToCust(List("USD", "MNO", "PQR"))))), None)
    )

  private val validAmpSection = Some(Amp(TransactionsAccptOvrThrshld(true, Some("2019-09-19")), true, 60))

  val validAspOrTcspSection =
    Some(
      AspOrTcsp(
        Some(SupervisionDetails(true, Some(SupervisorDetails("Company A", "1993-08-25", "1999-08-25", None, "Ending reason")))),
        Some(ProfessionalBodyDetails(true, Some("details"),
          Some(ProfessionalBodyDesMember(true,
            Some(MemberOfProfessionalBody(true, true, false, false, false, false, false, false, false, false, false, false, false, true, Some("test"))))))
        ))
    )

  val validHvdSection = Some(Hvd(true, Some("1978-02-15"), None, true, Some(40),
    Some(HvdFromUnseenCustDetails(true, Some(ReceiptMethods(true, true, true, Some("foo")))))))

  val validOwnBusinessPremisesDetails =
    OwnBusinessPremisesDetails(
      tradingName = Some("ABCDEFGHIJK ABCDE & LETTINGS LTD"),
      businessAddress = tradingpremises.Address("ABC 1234, ABCDEFGHIJ, CLYDE",
        Some("OwnBusinessAddressLine2"),
        Some("OwnBusinessAddressLine3"),
        Some("OwnBusinessAddressLine4"),
        "GB",
        Some("YY1 1YY")),
      residential = false,
      msb = models.des.tradingpremises.Msb(false, false, false, false, false),
      hvd = models.des.tradingpremises.Hvd(false),
      asp = models.des.tradingpremises.Asp(false),
      tcsp = models.des.tradingpremises.Tcsp(true),
      eab = models.des.tradingpremises.Eab(true),
      bpsp = models.des.tradingpremises.Bpsp(true),
      tditpsp = models.des.tradingpremises.Tditpsp(false),
      amp = models.des.tradingpremises.Amp(true),
      startDate = "2001-01-01",
      endDate = None,
      lineId = None,
      status = None,
      sectorDateChange = None,
      dateChangeFlag = None,
      tradingNameChangeDate = None
    )
}
