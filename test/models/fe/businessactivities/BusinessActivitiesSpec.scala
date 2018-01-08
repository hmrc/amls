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

package models.fe.businessactivities

import models.des.aboutthebusiness.Address
import models.des.businessactivities.{ExpectedAMLSTurnover => DesExpectedAMLSTurnover, _}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeApplication

class BusinessActivitiesSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))

  val DefaultFranchiseName = "DEFAULT FRANCHISE NAME"
  val DefaultSoftwareName = "DEFAULT SOFTWARE"
  val DefaultBusinessTurnover = ExpectedBusinessTurnover.First
  val DefaultAMLSTurnover = ExpectedAMLSTurnover.First
  val DefaultInvolvedInOtherDetails = "DEFAULT INVOLVED"
  val DefaultInvolvedInOther = InvolvedInOtherYes(DefaultInvolvedInOtherDetails)
  val DefaultBusinessFranchise = BusinessFranchiseYes(DefaultFranchiseName)
  val DefaultCustomersOutsideUK = CustomersOutsideUK(true, Some(Seq("GB")))
  val DefaultNCARegistered = NCARegistered(true)
  val DefaultAccountantForAMLSRegulations = AccountantForAMLSRegulations(true)
  val DefaultRiskAssessments = RiskAssessmentPolicyYes(Set(PaperBased))
  val DefaultWhoIsYourAccountant = WhoIsYourAccountant("Accountant's name", Some("Accountant's trading name"),
    UkAccountantsAddress("address1", "address2", Some("address3"), Some("address4"), "POSTCODE"))
  val DefaultTaxMatters = TaxMatters(true)
  val DefaultIdentifySuspiciousActivity = IdentifySuspiciousActivity(true)
  val DefaultTransactionRecordTypes = TransactionTypes(Set(Paper, DigitalSoftware(DefaultSoftwareName)))

  val NewFranchiseName = "NEW FRANCHISE NAME"
  val NewBusinessFranchise = BusinessFranchiseYes(NewFranchiseName)
  val NewInvolvedInOtherDetails = "NEW INVOLVED"
  val NewInvolvedInOther = InvolvedInOtherYes(NewInvolvedInOtherDetails)
  val NewBusinessTurnover = ExpectedBusinessTurnover.Second
  val NewAMLSTurnover = ExpectedAMLSTurnover.Second
  val NewCustomersOutsideUK = CustomersOutsideUK(false, None)
  val NewNCARegistered = NCARegistered(false)
  val NewAccountantForAMLSRegulations = AccountantForAMLSRegulations(false)
  val NewRiskAssessment = RiskAssessmentPolicyNo
  val NewIdentifySuspiciousActivity = IdentifySuspiciousActivity(true)
  val NewWhoIsYourAccountant = WhoIsYourAccountant("newName", Some("newTradingName"),
    UkAccountantsAddress("98E", "Building1", Some("street1"), Some("road1"), "NE27 0QQ"))
  val NewTaxMatters = TaxMatters(false)
  val NewTransactionRecordTypes = TransactionTypes(Set(DigitalSpreadsheet))

  "BusinessActivities" must {

    val completeJson = Json.obj(
      "involvedInOther" -> true,
      "details" -> DefaultInvolvedInOtherDetails,
      "expectedBusinessTurnover" -> "01",
      "expectedAMLSTurnover" -> "01",
      "businessFranchise" -> true,
      "franchiseName" -> DefaultFranchiseName,
      "isRecorded" -> true,
      "transactionTypes" -> Json.obj(
        "types" -> Seq("01", "03"),
        "software" -> DefaultSoftwareName
      ),
      "isOutside" -> true,
      "countries" -> Json.arr("GB"),
      "ncaRegistered" -> true,
      "accountantForAMLSRegulations" -> true,
      "hasPolicy" -> true,
      "riskassessments" -> Seq("01"),
      "accountantsName" -> "Accountant's name",
      "accountantsTradingName" -> "Accountant's trading name",
      "accountantsAddressLine1" -> "address1",
      "accountantsAddressLine2" -> "address2",
      "accountantsAddressLine3" -> "address3",
      "accountantsAddressLine4" -> "address4",
      "accountantsAddressPostCode" -> "POSTCODE",
      "manageYourTaxAffairs" -> true
    )

     val completeModel = BusinessActivities(
      involvedInOther = Some(DefaultInvolvedInOther),
      expectedBusinessTurnover = Some(DefaultBusinessTurnover),
      expectedAMLSTurnover = Some(DefaultAMLSTurnover),
      businessFranchise = Some(DefaultBusinessFranchise),
      transactionRecord = Some(true),
      customersOutsideUK = Some(DefaultCustomersOutsideUK),
      ncaRegistered = Some(DefaultNCARegistered),
      accountantForAMLSRegulations = Some(DefaultAccountantForAMLSRegulations),
      riskAssessmentPolicy = Some(DefaultRiskAssessments),
      whoIsYourAccountant = Some(DefaultWhoIsYourAccountant),
      taxMatters = Some(DefaultTaxMatters),
      transactionRecordTypes = Some(DefaultTransactionRecordTypes)
    )

    "Serialise as expected" in {
      Json.toJson(completeModel) must be(completeJson)
    }

    "Deserialise as expected" in {
      completeJson.as[BusinessActivities] must be(completeModel)
    }

    "convert des model to frontend successfully" in {

      val desModel = Some(BusinessActivitiesAll(
        None,
        Some("2001-01-01"),
        None,
        BusinessActivityDetails(true, Some(DesExpectedAMLSTurnover(Some("11122233344")))),
        Some(FranchiseDetails(true, Some(Seq("FranchiserName1", "FranchiserName2")))),
        Some("14"),
        Some("11"),
        NonUkResidentCustDetails(true, Some(Seq("AD", "GB"))),
        AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("CommercialPackageName")))),
        true,
        true,
        Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true, true)))),
        Some(MlrAdvisor(
          true,
          Some(MlrAdvisorDetails(
            Some(AdvisorNameAddress(
              "Name",
              Some("TradingName"),
              Address(
                "AdvisorAddressLine1",
                "AdvisorAddressLine2",
                Some("AdvisorAddressLine3"),
                Some("AdvisorAddressLine4"),
                "AD",
                Some("AA1 1AA")))),
            true,
            Some("01234567890")
        ))))
      ))

      val feModel = BusinessActivities(
        Some(InvolvedInOtherNo),
        None,
        None,
        Some(BusinessFranchiseYes("FranchiserName1")),
        Some(true),
        Some(CustomersOutsideUK(true, Some(List("AD", "GB")))),
        Some(NCARegistered(true)),Some(AccountantForAMLSRegulations(true)),Some(IdentifySuspiciousActivity(true)),
        Some(RiskAssessmentPolicyYes(Set(Digital, PaperBased))),Some(HowManyEmployees("14","11")),
        Some(WhoIsYourAccountant("Name",Some("TradingName"),
          UkAccountantsAddress("AdvisorAddressLine1","AdvisorAddressLine2",Some("AdvisorAddressLine3"),Some("AdvisorAddressLine4"),"AA1 1AA"))),
        Some(TaxMatters(true)),
        Some(TransactionTypes(Set(Paper, DigitalSpreadsheet, DigitalSoftware("CommercialPackageName"))))
      )

      BusinessActivities.conv(desModel) must be(feModel)
    }

    "convert des model to frontend successfully when business all don't have data" in {

      val desModel = None

      val feModel = BusinessActivities(None,None,None,None,None,None,None,None,None,None,None,None)

      BusinessActivities.conv(desModel) must be(feModel)
    }
  }

}
