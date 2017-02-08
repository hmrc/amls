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

package models.des

import models._
import models.des.aboutthebusiness.Address
import models.des.businessactivities._
import models.des.msb.{CurrSupplyToCust, _}
import models.fe.aboutthebusiness.{RegisteredOfficeUK, UKCorrespondenceAddress, _}
import org.joda.time.LocalDate
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsUndefined, Json}
import play.api.test.FakeApplication
import utils.AckRefGenerator

class SubscriptionRequestSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  "SubscriptionRequest serialisation" when {
    "businessReferencesAllButSp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          des.SubscriptionRequest(
            acknowledgementReference = ackref.ackRef,
            businessDetails = DefaultDesValues.BusinessCustomerDetails,
            businessActivities = DefaultDesValues.BusinessActivitiesSection,
            eabAll = DefaultDesValues.EabAllDetails,
            eabResdEstAgncy = DefaultDesValues.EabResd,
            businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
            businessReferencesAll = DefaultDesValues.PrevRegMLR,
            businessReferencesAllButSp = None,
            businessReferencesCbUbLlp = DefaultDesValues.CorpTaxRegime,
            tradingPremises = DefaultDesValues.TradingPremisesSection,
            bankAccountDetails = DefaultDesValues.bankDetailsSection,
            msb = DefaultDesValues.msbSection,
            hvd = DefaultDesValues.hvdSection,
            filingIndividual = DefaultDesValues.filingIndividual,
            tcspAll = DefaultDesValues.tcspAllSection,
            tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
            responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
            asp = DefaultDesValues.AspSection,
            aspOrTcsp = DefaultDesValues.AspOrTcspSection,
            declaration = Declaration(true)
          )
        ) \ "businessReferencesAllButSp") must be(a[JsUndefined])


      }
    }

    "businessReferencesCbUbLlp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          des.SubscriptionRequest(
            acknowledgementReference = ackref.ackRef,
            businessDetails = DefaultDesValues.BusinessCustomerDetails,
            businessActivities = DefaultDesValues.BusinessActivitiesSection,
            eabAll = DefaultDesValues.EabAllDetails,
            eabResdEstAgncy = DefaultDesValues.EabResd,
            businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
            businessReferencesAll = DefaultDesValues.PrevRegMLR,
            businessReferencesAllButSp = DefaultDesValues.VatALlBuySp,
            businessReferencesCbUbLlp = None,
            tradingPremises = DefaultDesValues.TradingPremisesSection,
            bankAccountDetails = DefaultDesValues.bankDetailsSection,
            msb = DefaultDesValues.msbSection,
            hvd = DefaultDesValues.hvdSection,
            filingIndividual = DefaultDesValues.filingIndividual,
            tcspAll = DefaultDesValues.tcspAllSection,
            tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
            responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
            asp = DefaultDesValues.AspSection,
            aspOrTcsp = DefaultDesValues.AspOrTcspSection,
            declaration = Declaration(true)
          )
        ) \ "businessReferencesCbUbLlp") must be(a[JsUndefined])
      }
    }
  }

  "SubscriptionRequestSpec" must {
    "convert correctly" in {

      val desSubscriptionReq =
        des.SubscriptionRequest(
          acknowledgementReference = ackref.ackRef,
          businessDetails = DefaultDesValues.BusinessCustomerDetails,
          businessActivities = DefaultDesValues.BusinessActivitiesSection,
          eabAll = DefaultDesValues.EabAllDetails,
          eabResdEstAgncy = DefaultDesValues.EabResd,
          businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
          businessReferencesAll = DefaultDesValues.PrevRegMLR,
          businessReferencesAllButSp = DefaultDesValues.VatALlBuySp,
          businessReferencesCbUbLlp = DefaultDesValues.CorpTaxRegime,
          tradingPremises = DefaultDesValues.TradingPremisesSection,
          bankAccountDetails = DefaultDesValues.bankDetailsSection,
          msb = DefaultDesValues.msbSectionR6,
          hvd = DefaultDesValues.hvdSection,
          filingIndividual = DefaultDesValues.filingIndividual,
          tcspAll = DefaultDesValues.tcspAllSection,
          tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
          responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
          asp = DefaultDesValues.AspSection,
          aspOrTcsp = DefaultDesValues.AspOrTcspSection,
          declaration = Declaration(true)
        )

      val feSubscriptionReq = {
        import models.fe.SubscriptionRequest
        SubscriptionRequest(
          businessMatchingSection = BusinessMatchingSection.model,
          eabSection = EabSection.model,
          aboutTheBusinessSection = AboutTheBusinessSection.model,
          tradingPremisesSection = TradingPremisesSection.model,
          bankDetailsSection = BankDetailsSection.model,
          aboutYouSection = AboutYouSection.model,
          businessActivitiesSection = BusinessActivitiesSection.model,
          responsiblePeopleSection = ResponsiblePeopleSection.model,
          tcspSection = ASPTCSPSection.TcspSection,
          aspSection = ASPTCSPSection.AspSection,
          msbSection = MsbSection.completeModel,
          hvdSection = HvdSection.completeModel,
          supervisionSection = SupervisionSection.completeModel
        )
      }

      des.SubscriptionRequest.convert(feSubscriptionReq) must be(desSubscriptionReq)
    }
  }
}

class SubscriptionRequestSpecRelease7 extends PlaySpec with MockitoSugar with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> true))

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  "SubscriptionRequest serialisation" when {
    "businessReferencesAllButSp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          des.SubscriptionRequest(
            acknowledgementReference = ackref.ackRef,
            businessDetails = DefaultDesValues.BusinessCustomerDetails,
            businessActivities = DefaultDesValues.BusinessActivitiesSection,
            eabAll = DefaultDesValues.EabAllDetails,
            eabResdEstAgncy = DefaultDesValues.EabResd,
            businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
            businessReferencesAll = DefaultDesValues.PrevRegMLR,
            businessReferencesAllButSp = None,
            businessReferencesCbUbLlp = DefaultDesValues.CorpTaxRegime,
            tradingPremises = DefaultDesValues.TradingPremisesSection,
            bankAccountDetails = DefaultDesValues.bankDetailsSection,
            msb = DefaultDesValues.msbSection,
            hvd = DefaultDesValues.hvdSection,
            filingIndividual = DefaultDesValues.filingIndividual,
            tcspAll = DefaultDesValues.tcspAllSection,
            tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
            responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
            asp = DefaultDesValues.AspSection,
            aspOrTcsp = DefaultDesValues.AspOrTcspSection,
            declaration = Declaration(true)
          )
        ) \ "businessReferencesAllButSp") must be(a[JsUndefined])


      }
    }

    "businessReferencesCbUbLlp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          des.SubscriptionRequest(
            acknowledgementReference = ackref.ackRef,
            businessDetails = DefaultDesValues.BusinessCustomerDetails,
            businessActivities = DefaultDesValues.BusinessActivitiesSection,
            eabAll = DefaultDesValues.EabAllDetails,
            eabResdEstAgncy = DefaultDesValues.EabResd,
            businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
            businessReferencesAll = DefaultDesValues.PrevRegMLR,
            businessReferencesAllButSp = DefaultDesValues.VatALlBuySp,
            businessReferencesCbUbLlp = None,
            tradingPremises = DefaultDesValues.TradingPremisesSection,
            bankAccountDetails = DefaultDesValues.bankDetailsSection,
            msb = DefaultDesValues.msbSection,
            hvd = DefaultDesValues.hvdSection,
            filingIndividual = DefaultDesValues.filingIndividual,
            tcspAll = DefaultDesValues.tcspAllSection,
            tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
            responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
            asp = DefaultDesValues.AspSection,
            aspOrTcsp = DefaultDesValues.AspOrTcspSection,
            declaration = Declaration(true)
          )
        ) \ "businessReferencesCbUbLlp") must be(a[JsUndefined])
      }
    }
  }

  "SubscriptionRequestSpec" must {
    "convert correctly" in {

      val aboutTheBusinessModel = AboutTheBusiness(
        PreviouslyRegisteredYes("12345678"),
        Some(ActivityStartDate(new LocalDate(2001, 1, 1))),
        Some(VATRegisteredYes("123456789")),
        Some(CorporationTaxRegisteredYes("1234567890")),
        ContactingYou("019212323222323222323222323222", "abc@hotmail.co.uk"),
        RegisteredOfficeUK("line1", "line2",
          Some("some street"), Some("some city"), "EE1 1EE"),
        Some(UKCorrespondenceAddress("kap", "Trading", "Park", "lane",
          Some("Street"), Some("city"), "EE1 1EE"))
      )

      val msbSectionRelease7 = Some(MoneyServiceBusiness(
        Some(MsbAllDetails(Some("£15k-£50k"), true, Some(CountriesList(List("GB"))), true)),
        Some(MsbMtDetails(true, Some("123456"),
          IpspServicesDetails(true, Some(Seq(IpspDetails("name", "123456789123456")))),
          true,
          Some("12345678963"), Some(CountriesList(List("GB"))), Some(CountriesList(List("LA", "LV"))), None)),
        Some(MsbCeDetails(CurrencySources(Some(MSBBankDetails(true, Some(List("Bank names")))),
          Some(CurrencyWholesalerDetails(true, Some(List("wholesaler names")))), true, "12345678963", Some(CurrSupplyToCust(List("USD", "MNO", "PQR")))), dealInPhysCurrencies = Some(true))), None)
      )

      val desallActivitiesModel = BusinessActivitiesAll(None, Some("2001-01-01"), None, BusinessActivityDetails(true,
        Some(ExpectedAMLSTurnover(Some("£0-£15k")))), Some(FranchiseDetails(true, Some(Seq("Name")))), Some("10"), Some("5"),
        NonUkResidentCustDetails(true, Some(Seq("GB", "AB"))), AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("value")))),
        true, true, Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true)))), MlrAdvisor(true,
          Some(MlrAdvisorDetails(Some(AdvisorNameAddress("Name", Some("TradingName"), Address("Line1", "Line2", Some("Line3"), Some("Line4"), "GB", Some("postcode")))), true, None))))

      val desSubscriptionReq =
        des.SubscriptionRequest(
          acknowledgementReference = ackref.ackRef,
          businessDetails = DefaultDesValues.BusinessCustomerDetails,
          businessActivities = DefaultDesValues.BusinessActivitiesSection,
          eabAll = DefaultDesValues.EabAllDetails,
          eabResdEstAgncy = DefaultDesValues.EabResd,
          businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
          businessReferencesAll = DefaultDesValues.PrevRegMLR,
          businessReferencesAllButSp = DefaultDesValues.VatALlBuySp,
          businessReferencesCbUbLlp = DefaultDesValues.CorpTaxRegime,
          tradingPremises = DefaultDesValues.TradingPremisesSection,
          bankAccountDetails = DefaultDesValues.bankDetailsSection,
          msb = msbSectionRelease7,
          hvd = DefaultDesValues.hvdSection,
          filingIndividual = DefaultDesValues.filingIndividual,
          tcspAll = DefaultDesValues.tcspAllSection,
          tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
          responsiblePersons = DefaultDesValues.ResponsiblePersonsSectionForRelease7,
          asp = DefaultDesValues.AspSection,
          aspOrTcsp = DefaultDesValues.AspOrTcspSection,
          declaration = Declaration(true)
        )

      val feSubscriptionReq = {
        import models.fe.SubscriptionRequest
        SubscriptionRequest(
          businessMatchingSection = BusinessMatchingSection.model,
          eabSection = EabSection.model,
          aboutTheBusinessSection = aboutTheBusinessModel,
          tradingPremisesSection = TradingPremisesSection.model,
          bankDetailsSection = BankDetailsSection.model,
          aboutYouSection = AboutYouSection.model,
          businessActivitiesSection = BusinessActivitiesSection.model,
          responsiblePeopleSection = ResponsiblePeopleSection.model,
          tcspSection = ASPTCSPSection.TcspSection,
          aspSection = ASPTCSPSection.AspSection,
          msbSection = MsbSection.completeModel,
          hvdSection = HvdSection.completeModel,
          supervisionSection = SupervisionSection.completeModel
        )
      }

      val feRelease7SubscriptionViewModel = feSubscriptionReq.copy(businessActivitiesSection = BusinessActivitiesSection.model.copy(
        expectedBusinessTurnover = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£0-£15k"))))
      )
      )

      val desRelease7SubscriptionViewModel = desSubscriptionReq.copy(businessActivities = DefaultDesValues.BusinessActivitiesSection.copy(
        all = Some(desallActivitiesModel)
      )
      )

      des.SubscriptionRequest.convert(feRelease7SubscriptionViewModel) must be(desRelease7SubscriptionViewModel)

    }
  }
}