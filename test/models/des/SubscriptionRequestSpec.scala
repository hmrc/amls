/*
 * Copyright 2020 HM Revenue & Customs
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
import models.des.aboutyou.{AboutYouRelease7, IndividualDetails, RoleForTheBusiness, RolesWithinBusiness}
import models.des.businessactivities._
import models.des.msb.{CurrSupplyToCust, _}
import models.fe.businessactivities.ExpectedBusinessTurnover
import models.fe.businessdetails.{RegisteredOfficeUK, UKCorrespondenceAddress, _}
import org.joda.time.LocalDate
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json._
import utils.AckRefGenerator

class SubscriptionRequestSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  "SubscriptionRequest serialisation" when {

    "given release 7 structure of filingIndividual" must {
      "read Json correctly" in {

        val release7FilingIndividualModel = AboutYouRelease7(
          Some(IndividualDetails("fname", None, "lname")),
          false,
          Some(RolesWithinBusiness(false, false, false, false, false, false, false, false, None)),
          Some(RoleForTheBusiness(true, false, None))
        )

        SubscriptionRequest.format.reads(release7Json).asOpt.get.filingIndividual must be(release7FilingIndividualModel)

      }
    }

    "given release 7 structure of msbcedetails" must {
      "read Json correctly" in {

        val release7MsbCeDetails = MsbCeDetailsR7(Some(true), Some(CurrencySourcesR7(Some(MSBBankDetails(false, Some(Seq("bank1", "bank2", "bank3")))),
          Some(CurrencyWholesalerDetails(true, Some(Seq("name1", "name2", "name3"))
          )), false)), "94955689863", Some(CurrSupplyToCust(Seq("GBP", "EUR", "USD"))))

        SubscriptionRequest.format.reads(release7Json).asOpt.get.msb.get.msbCeDetails.get must be(release7MsbCeDetails)

      }

      "write Json correctly" in {
        val json = SubscriptionRequest.format.writes(SubscriptionRequest.format.reads(release7Json).asOpt.get)
        (json \\ "msbCeDetails").head must be(JsObject(Seq(
          ("dealInPhysCurrencies", JsBoolean(true)),
          ("currencySources", JsObject(Seq(
            ("bankDetails", JsObject(Seq(
              ("banks", JsBoolean(false)), ("bankNames", JsArray(Seq(JsString("bank1"), JsString("bank2"), JsString("bank3"))))
            )))
            , ("currencyWholesalerDetails", JsObject(Seq(
              ("currencyWholesalers", JsBoolean(true)), ("currencyWholesalersNames", JsArray(Seq(JsString("name1"), JsString("name2"), JsString("name3"))))))),
            ("reSellCurrTakenIn", JsBoolean(false))))),
          ("antNoOfTransNxt12Mnths", JsString("94955689863")), ("currSupplyToCust", JsObject(Seq(("currency", JsArray(Seq(JsString("GBP"), JsString("EUR"), JsString("USD"))))))))))
      }
    }

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
            amp = DefaultDesValues.AmpSection,
            aspOrTcsp = DefaultDesValues.AspOrTcspSection,
            declaration = Declaration(true),
            lettingAgents = None
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
            amp = DefaultDesValues.AmpSection,
            aspOrTcsp = DefaultDesValues.AspOrTcspSection,
            declaration = Declaration(true),
            lettingAgents = None
          )
        ) \ "businessReferencesCbUbLlp") must be(a[JsUndefined])
      }
    }
  }

  val businessDetailsModel = BusinessDetails(PreviouslyRegisteredYes(Some("12345678")),
    Some(ActivityStartDate(new LocalDate(2001, 1, 1))),
    Some(VATRegisteredYes("123456789")),
    Some(CorporationTaxRegisteredYes("1234567890")),
    ContactingYou("019212323222323222323222323222", "abc@hotmail.co.uk"),
    RegisteredOfficeUK("line1", "line2",
      Some("some street"), Some("some city"), "EE1 1EE"),
    true,
    Some(UKCorrespondenceAddress("kap", "Trading", "Park", "lane",
      Some("Street"), Some("city"), "EE1 1EE"))
  )

  val msbSectionRelease7 = Some(
    MoneyServiceBusiness(
      Some(MsbAllDetails(Some("£15k-50k"), true, Some(CountriesList(List("GB"))), true)),
      Some(MsbMtDetails(true, Some("123456"),
        IpspServicesDetails(true, Some(Seq(IpspDetails("name", "123456789123456")))),
        true,
        Some("12345678963"), Some(CountriesList(List("GB"))), Some(CountriesList(List("LA", "LV"))))),
      Some(MsbCeDetailsR7(Some(true), Some(CurrencySourcesR7(Some(MSBBankDetails(true, Some(List("Bank names")))),
        Some(CurrencyWholesalerDetails(true, Some(List("wholesaler names")))), true)), "12345678963", Some(CurrSupplyToCust(List("USD", "MNO", "PQR"))))), None)
  )

  val desallActivitiesModel = BusinessActivitiesAll(None, Some("2001-01-01"), None, BusinessActivityDetails(true,
    Some(ExpectedAMLSTurnover(Some("£0-£15k")))), Some(FranchiseDetails(true, Some(Seq("Name")))), Some("10"), Some("5"),
    NonUkResidentCustDetails(true, Some(Seq("GB", "AB"))), AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("value")))),
    true, true, Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true)))), Some(MlrAdvisor(true,
      Some(MlrAdvisorDetails(Some(AdvisorNameAddress("Name", Some("TradingName"), Address("Line1", "Line2", Some("Line3"), Some("Line4"), "GB", Some("AA1 1AA")))), true, None)))))

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
      responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
      asp = DefaultDesValues.AspSection,
      amp = DefaultDesValues.AmpSection,
      aspOrTcsp = DefaultDesValues.AspOrTcspSection,
      declaration = Declaration(true),
      lettingAgents = None
    )

  val feSubscriptionReq = {
    import models.fe.SubscriptionRequest
    SubscriptionRequest(
      businessMatchingSection = BusinessMatchingSection.model,
      eabSection = EabSection.model,
      businessDetailsSection = businessDetailsModel,
      tradingPremisesSection = TradingPremisesSection.model,
      bankDetailsSection = BankDetailsSection.model,
      aboutYouSection = AboutYouSection.model,
      businessActivitiesSection = BusinessActivitiesSection.model,
      responsiblePeopleSection = ResponsiblePeopleSection.model,
      tcspSection = ASPTCSPSection.TcspSection,
      aspSection = ASPTCSPSection.AspSection,
      msbSection = MsbSection.completeModel,
      hvdSection = HvdSection.completeModel,
      ampSection = AmpSection.completeModel,
      supervisionSection = SupervisionSection.completeModel
    )
  }

  val release7Json = Json.parse(

    """{
  "acknowledgementReference": "$AckRef$",
  "businessDetails": {
    "typeOfLegalEntity": "Sole Proprietor"
  },
  "businessContactDetails": {
    "businessAddress": {
      "addressLine1": "address line1",
      "addressLine2": "address line2",
      "addressLine3": "address line3",
      "addressLine4": "address line4",
      "country": "AA",
      "postcode": "AA1 1AA"
    },
    "altCorrespondenceAddress": false,
    "businessTelNo": "1234567891",
    "businessEmail": "scenario1.2@test.com"
  },
  "businessReferencesAll": {
    "amlsRegistered": true,
    "mlrRegNumber8Long": "12345678",
    "prevRegForMlr": false
  },
  "businessActivities": {
    "mlrActivitiesAppliedFor": {
      "msb": false,
      "hvd": false,
      "asp": true,
      "tcsp": true,
      "eab": true,
      "bpsp": false,
      "tditpsp": false,
      "amp": false
    },
    "aspServicesOffered": {
      "accountant": true,
      "payrollServiceProvider": true,
      "bookKeeper": true,
      "auditor": false,
      "financialOrTaxAdvisor": false
    },
    "tcspServicesOffered": {
      "nomineeShareholders": true,
      "trusteeProvider": true,
      "regOffBusinessAddrVirtualOff": true,
      "compDirSecPartnerProvider": false,
      "trustOrCompFormAgent": true
    },
    "tcspServicesforRegOffBusinessAddrVirtualOff": {
      "callHandling": true,
      "emailHandling": true,
      "emailServer": true,
      "selfCollectMailboxes": true,
      "mailForwarding": true,
      "receptionist": false,
      "conferenceRooms": false,
      "other": false
    },
    "eabServicesCarriedOut": {
      "residentialEstateAgency": true,
      "commercialEstateAgency": true,
      "auctioneer": true,
      "relocationAgent": true,
      "businessTransferAgent": false,
      "assetManagementCompany": false,
      "landManagementAgent": false,
      "developmentCompany": false,
      "socialHousingProvider": false
    },
    "all": {
      "businessActivityDetails": {
        "actvtsBusRegForOnlyActvtsCarOut": false,
        "respActvtsBusRegForOnlyActvtsCarOut": {
          "otherBusActivitiesCarriedOut": {
            "otherBusinessActivities": "scenario1.2",
            "anticipatedTotBusinessTurnover": "99999",
            "mlrActivityTurnover": "999999"
          }
        }
      },
      "franchiseDetails": {
        "isBusinessAFranchise": true,
        "franchiserName": [
          "scenario1.2"
        ]
      },
      "noOfEmployees": "123",
      "noOfEmployeesForMlr": "321",
      "nonUkResidentCustDetails": {
        "nonUkResidentCustomers": true,
        "whichCountries": [
          "AA"
        ]
      },
      "auditableRecordsDetails": {
        "detailedRecordsKept": "Yes",
        "transactionRecordingMethod": {
          "manual": true,
          "spreadsheet": true,
          "commercialPackage": true,
          "commercialPackageName": "scenario1.2"
        }
      },
      "suspiciousActivityGuidance": true,
      "nationalCrimeAgencyRegistered": true,
      "formalRiskAssessmentDetails": {
        "formalRiskAssessment": true,
        "riskAssessmentFormat": {
          "electronicFormat": false,
          "manualFormat": true
        }
      },
      "mlrAdvisor": {
        "doYouHaveMlrAdvisor": true,
        "mlrAdvisorDetails": {
          "advisorNameAddress": {
            "name": "scenario1.2",
            "tradingName": "",
            "address": {
              "addressLine1": "add",
              "addressLine2": "add",
              "country": "AA",
              "postcode": "bb11bb"
            }
          },
          "agentDealsWithHmrc": true,
          "hmrcAgentRefNo": "12345678911"
        }
      }
    }
  },
  "tradingPremises": {
    "ownBusinessPremises": {
      "ownBusinessPremises": true,
      "ownBusinessPremisesDetails": [
        {
          "tradingName": "trading1",
          "businessAddress": {
            "addressLine1": "add",
            "addressLine2": "add",
            "country": "AA",
            "postcode": "cc11cc"
          },
          "residential": true,
          "msb": {
            "mt": false,
            "ce": true,
            "smdcc": false,
            "nonSmdcc": false,
            "fx": false
          },
          "hvd": {
            "hvd": false
          },
          "asp": {
            "asp": true
          },
          "tcsp": {
            "tcsp": false
          },
          "eab": {
            "eab": true
          },
          "bpsp": {
            "bpsp": false
          },
          "tditpsp": {
            "tditpsp": false
          },
          "startDate": "1980-11-11"
        }
      ]
    },
    "agentBusinessPremises": {
      "agentBusinessPremises": false
    }
  },
  "bankAccountDetails": {
    "noOfMlrBankAccounts": "0"

  },
    "msb": {
    "msbAllDetails": {
      "anticipatedTotThrputNxt12Mths": "£10m+",
      "otherCntryBranchesOrAgents": true,
      "countriesList": {
        "listOfCountries": [
          "FR",
          "IT"
        ]
      },
      "sysLinkedTransIdentification": false
    },
    "msbMtDetails": {
      "applyForFcapsrRegNo": false,
      "fcapsrRefNo": "123456",
      "ipspServicesDetails": {
        "ipspServicesUsed": false,
        "ipspDetails": [
          {
            "ipspName": "Ipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipspnameipsp",
            "ipspMlrRegNo": "12345"
          }
        ]
      },
      "informalFundsTransferSystem": true,
      "noOfMoneyTrnsfrTransNxt12Mnths": "88248543600",
      "countriesLrgstMoneyAmtSentTo": {
        "listOfCountries": [
          "LT",
          "JP"
        ]
      },
      "countriesLrgstTranscsSentTo": {
        "listOfCountries": [
          "LV",
          "LA"
        ]
      }
    },
    "msbCeDetails": {
      "dealInPhysCurrencies": true,
      "currencySources": {
        "bankDetails": {
          "banks": false,
          "bankNames": [
            "bank1",
            "bank2",
            "bank3"
          ]
        },
        "currencyWholesalerDetails": {
          "currencyWholesalers": true,
          "currencyWholesalersNames": [
            "name1",
            "name2",
            "name3"
          ]
        },
        "reSellCurrTakenIn": false
      },
      "antNoOfTransNxt12Mnths": "94955689863",
      "currSupplyToCust": {
        "currency": [
          "GBP",
          "EUR",
          "USD"
        ]
      }
    },
    "msbFxDetails": {
      "anticipatedNoOfTransactions": "01234567890"
    }
  },
  "asp": {
    "regHmrcAgtRegSchTax": true,
    "hmrcAgentRegNo": "12345678911"
  },
  "aspOrTcsp": {
    "supervisionDetails": {
      "prevSupervisedByMlsRegs": true,
      "supervisorDetails": {
        "nameOfLastSupervisor": "joe",
        "supervisionStartDate": "2010-11-11",
        "supervisionEndDate": "2010-11-11",
        "supervisionEndingReason": "being over experienced"
      }
    },
    "professionalBodyDetails": {
      "prevWarnedWRegToAspActivities": true,
      "detailsIfFinedWarned": "scenario1.2",
      "professionalBody": {
        "professionalBodyMember": true,
        "professionalBodyDetails": {
          "associationofAccountingTechnicians": true,
          "associationofCharteredCertifiedAccountants": true,
          "associationofInternationalAccountants": true,
          "associationofTaxationTechnicians": true,
          "charteredInstituteofManagementAccountants": true,
          "charteredInstituteofTaxation": true,
          "instituteofCertifiedBookkeepers": true,
          "instituteofCharteredAccountantsinIreland": true,
          "instituteofCharteredAccountantsinScotland": true,
          "instituteofCharteredAccountantsofEnglandandWales": true,
          "instituteofFinancialAccountants": true,
          "internationalAssociationofBookKeepers": true,
          "lawSociety": true,
          "other": true,
          "specifyOther": "scenario1.2"
        }
      }
    }
  },
  "tcspAll": {
    "anotherTcspServiceProvider": true,
    "tcspMlrRef": "12345678"
  },
  "tcspTrustCompFormationAgt": {
    "onlyOffTheShelfCompsSold": true,
    "complexCorpStructureCreation": true
  },
  "eabAll": {
    "estateAgencyActProhibition": true,
    "estAgncActProhibProvideDetails": "scenario1.2",
    "prevWarnedWRegToEstateAgencyActivities": true,
    "prevWarnWRegProvideDetails": "scenario1.2"
  },
  "eabResdEstAgncy": {
    "regWithRedressScheme": true,
    "whichRedressScheme": "The Property Ombudsman Limited"
  },
  "responsiblePersons": [
    {
      "nameDetails": {
        "personName": {
          "firstName": "somename",
          "lastName": "somelastname"
        },
        "othrNamesOrAliasesDetails": {
          "otherNamesOrAliases": true,
          "aliases": [
            "somealiase"
          ]
        },
        "previousNameDetails": {
          "nameEverChanged": true,
          "previousName": {
            "firstName": "firstname",
            "lastName": "lastname"
          },
          "dateOfChange": "2011-11-11"
        }
      },
      "nationalityDetails": {
        "areYouUkResident": true,
        "idDetails": {
          "ukResident": {
            "nino": "AA123456C"
          }
        },
        "countryOfBirth": "AA",
        "nationality": "BB"
      },
      "contactCommDetails": {
        "contactEmailAddress": "scenario1.2@test.com",
        "primaryTeleNo": "1234567891"
      },
      "currentAddressDetails": {
        "address": {
          "addressLine1": "add",
          "addressLine2": "add",
          "country": "AA",
          "postcode": "mm11mm"
        }
      },
      "timeAtCurrentAddress": "1-3 years",
      "addressUnderThreeYears": {
        "address": {
          "addressLine1": "add",
          "addressLine2": "add",
          "country": "AA",
          "postcode": "AA11AA"
        }
      },
      "timeAtAddressUnderThreeYears": "1-3 years",
      "addressUnderOneYear": {
        "address": {
          "addressLine1": "add",
          "addressLine2": "add",
          "country": "AA",
          "postcode": "BB11BB"
        }
      },
      "timeAtAddressUnderOneYear": "1-3 years",
      "positionInBusiness": {
        "soleProprietor": {
          "soleProprietor": false,
          "nominatedOfficer": true
        },
        "partnership": {
          "partner": false,
          "nominatedOfficer": false
        },
        "corpBodyOrUnInCorpBodyOrLlp": {
          "director": false,
          "beneficialOwner": false,
          "nominatedOfficer": false
        }
      },
      "regDetails": {
        "vatRegistered": false,
        "saRegistered": false
      },
      "previousExperience": true,
      "descOfPrevExperience": "scenario1.2",
      "amlAndCounterTerrFinTraining": true,
      "trainingDetails": "scenario1.2",
      "msbOrTcsp": {
        "passedFitAndProperTest": true
      }
    }
  ],
  "filingIndividual": {
    "individualDetails": {
      "firstName": "fname",
      "lastName": "lname"
    },
    "employedWithinBusiness": false,
    "roleWithinBusiness":{
      "beneficialShareholder": false,
      "director": false,
      "partner": false,
      "internalAccountant": false,
      "soleProprietor": false,
      "nominatedOfficer": false,
      "designatedMember": false,
      "other": false
    },
    "roleForTheBusiness":{
      "externalAccountant": true,
      "other": false
    }
  },
  "declaration": {
    "declarationFlag": true
  }
}""")


  "SubscriptionRequestSpec" must {
    "convert correctly" in {

      val businessDetailsModel = BusinessDetails(
        PreviouslyRegisteredYes(Some("12345678")),
        Some(ActivityStartDate(new LocalDate(2001, 1, 1))),
        Some(VATRegisteredYes("123456789")),
        Some(CorporationTaxRegisteredYes("1234567890")),
        ContactingYou("019212323222323222323222323222", "abc@hotmail.co.uk"),
        RegisteredOfficeUK("line1", "line2",
          Some("some street"), Some("some city"), "EE1 1EE"),
        true,
        Some(UKCorrespondenceAddress("kap", "Trading", "Park", "lane",
          Some("Street"), Some("city"), "EE1 1EE"))
      )

      val msbSectionRelease7 = Some(MoneyServiceBusiness(
        Some(MsbAllDetails(Some("£15k-50k"), true, Some(CountriesList(List("GB"))), true)),
        Some(MsbMtDetails(true, Some("123456"),
          IpspServicesDetails(true, Some(Seq(IpspDetails("name", "123456789123456")))),
          true,
          Some("12345678963"), Some(CountriesList(List("GB"))), Some(CountriesList(List("LA", "LV"))), None)),
        Some(MsbCeDetailsR7(
          Some(true), Some(CurrencySourcesR7
          (
            Some(MSBBankDetails(true, Some(List("Bank names")))),
            Some(CurrencyWholesalerDetails(true, Some(List("wholesaler names")))), true)), "12345678963", Some(CurrSupplyToCust(List("USD", "MNO", "PQR"))))), None)
      )

      val desallActivitiesModel = BusinessActivitiesAll(None,
        Some("2001-01-01"),
        None,
        BusinessActivityDetails(true,
          Some(ExpectedAMLSTurnover(Some("£0-£15k")))),
        Some(FranchiseDetails(true, Some(Seq("Name")))),
        Some("10"),
        Some("5"),
        NonUkResidentCustDetails(true, Some(Seq("GB", "AB"))),
        AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("value")))),
        true,
        true,
        Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true)))),
        Some(MlrAdvisor(true,
          Some(MlrAdvisorDetails(Some(AdvisorNameAddress("Name", Some("TradingName"), Address("Line1", "Line2", Some("Line3"), Some("Line4"), "GB", Some("AA1 1AA")))), true, None)))))

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
          responsiblePersons = DefaultDesValues.ResponsiblePersonsSectionForRelease7Phase2,
          asp = DefaultDesValues.AspSection,
          aspOrTcsp = DefaultDesValues.AspOrTcspSection,
          amp = DefaultDesValues.AmpSection,
          declaration = Declaration(true),
          lettingAgents = None
        )

      val feSubscriptionReq = {
        import models.fe.SubscriptionRequest
        SubscriptionRequest(
          businessMatchingSection = BusinessMatchingSection.model,
          eabSection = EabSection.model,
          businessDetailsSection = businessDetailsModel,
          tradingPremisesSection = TradingPremisesSection.model,
          bankDetailsSection = BankDetailsSection.model,
          aboutYouSection = AboutYouSection.model,
          businessActivitiesSection = BusinessActivitiesSection.model,
          responsiblePeopleSection = ResponsiblePeopleSection.modelPhase2,
          tcspSection = ASPTCSPSection.TcspSection,
          aspSection = ASPTCSPSection.AspSection,
          msbSection = MsbSection.completeModel,
          hvdSection = HvdSection.completeModel,
          ampSection = AmpSection.completeModel,
          supervisionSection = SupervisionSection.completeModel
        )
      }

      val feRelease7SubscriptionViewModel = feSubscriptionReq.copy(businessActivitiesSection = BusinessActivitiesSection.model.copy(
        expectedBusinessTurnover = Some(ExpectedBusinessTurnover.First)
      )
      )

      val desRelease7SubscriptionViewModel = desSubscriptionReq.copy(businessActivities = DefaultDesValues.BusinessActivitiesSection.copy(
        all = Some(desallActivitiesModel)
      )
      )

      implicit val requestType = RequestType.Subscription
      des.SubscriptionRequest.convert(feRelease7SubscriptionViewModel) must be(desRelease7SubscriptionViewModel)

    }
  }
}