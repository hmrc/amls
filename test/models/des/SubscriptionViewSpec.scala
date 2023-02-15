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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}
import utils.AmlsBaseSpec

class SubscriptionViewSpec extends PlaySpec with AmlsBaseSpec {

  "SubscriptionView" must {
    "deserialise the subscription json" when {
      "given valid json" in {
        val json = Json.toJson(GetSuccessModel)
        val subscriptionViewModel = GetSuccessModel

        json.as[SubscriptionView] must be(subscriptionViewModel)

        SubscriptionView.jsonReads.reads(
          SubscriptionView.jsonWrites.writes(GetSuccessModel)) must
          be(JsSuccess(GetSuccessModel))

        Json.toJson(GetSuccessModel) must be(json)
      }

      "given valid json with LA" in {
        val json = Json.toJson(getSuccessModelLA)
        val subscriptionViewModel = getSuccessModelLA

        json.as[SubscriptionView] must be(subscriptionViewModel)

        SubscriptionView.jsonReads.reads(
          SubscriptionView.jsonWrites.writes(getSuccessModelLA)) must
          be(JsSuccess(getSuccessModelLA))

        Json.toJson(getSuccessModelLA) must be(json)
      }
    }
  }

  "SubscriptionView" must {
    "deserialise real json" when {
      "given valid json" in {
        val json = Json.parse(
          """{
  "etmpFormBundleNumber": "082000001158",
  "businessDetails": {
    "typeOfLegalEntity": "Sole Proprietor"
  },
  "businessContactDetails": {
    "businessAddress": {
      "addressLine1": "Matheson House 1",
      "addressLine2": "Grange Central",
      "addressLine3": "Telford",
      "addressLine4": "Shropshire",
      "country": "GB",
      "postcode": "TF3 4ER"
    },
    "altCorrespondenceAddress": false,
    "businessTelNo": "0897686856765",
    "businessEmail": "test1@test.com"
  },
  "businessReferencesAll": {
    "amlsRegistered": true,
    "mlrRegNumber": "12345678",
    "prevRegForMlr": false
  },
  "businessActivities": {
    "mlrActivitiesAppliedFor": {
      "msb": false,
      "hvd": false,
      "asp": false,
      "tcsp": false,
      "eab": true,
      "bpsp": false,
      "tditpsp": false,
      "amp": false
    },
    "eabServicesCarriedOut": {
      "residentialEstateAgency": true,
      "commercialEstateAgency": false,
      "auctioneer": false,
      "relocationAgent": true,
      "businessTransferAgent": false,
      "assetManagementCompany": false,
      "landManagementAgent": false,
      "developmentCompany": false,
      "socialHousingProvider": false
    },
    "all": {
      "dateChangeFlag": false,
      "businessActivityDetails": {
        "actvtsBusRegForOnlyActvtsCarOut": false,
        "respActvtsBusRegForOnlyActvtsCarOut": {
          "otherBusActivitiesCarriedOut": {
            "otherBusinessActivities": "test1",
            "anticipatedTotBusinessTurnover": "249999",
            "mlrActivityTurnover": "249999"
          }
        }
      },
      "franchiseDetails": {
        "isBusinessAFranchise": true,
        "franchiserName": [
          "test franch"
        ]
      },
      "noOfEmployees": "123",
      "noOfEmployeesForMlr": "12",
      "nonUkResidentCustDetails": {
        "nonUkResidentCustomers": true,
        "whichCountries": [
          "AR"
        ]
      },
      "auditableRecordsDetails": {
        "detailedRecordsKept": "Yes",
        "transactionRecordingMethod": {
          "manual": true,
          "spreadsheet": true,
          "commercialPackage": true,
          "commercialPackageName": "test soft"
        }
      },
      "suspiciousActivityGuidance": true,
      "nationalCrimeAgencyRegistered": true,
      "formalRiskAssessmentDetails": {
        "formalRiskAssessment": true,
        "riskAssessmentFormat": {
          "electronicFormat": true,
          "manualFormat": true
        }
      },
      "mlrAdvisor": {
        "doYouHaveMlrAdvisor": true,
        "mlrAdvisorDetails": {
          "advisorNameAddress": {
            "name": "thing",
            "address": {
              "addressLine1": "add1",
              "addressLine2": "add1",
              "country": "GB",
              "postcode": "post1"
            }
          },
          "agentDealsWithHmrc": true
        }
      }
    }
  },
  "tradingPremises": {
    "ownBusinessPremises": {
      "ownBusinessPremises": true,
      "ownBusinessPremisesDetails": [
        {
        "dateChangeFlag":false,
          "lineId": 1,
          "tradingName": "trade1",
          "businessAddress": {
            "addressLine1": "add1",
            "addressLine2": "add1",
            "country": "GB",
            "postcode": "post1"
          },
          "residential": false,
          "eab": {
            "eab": true
          },
          "startDate": "1980-02-01",
          "endDate": "9999-12-31"
        },
        {
          "dateChangeFlag":false,
          "lineId": 2,
          "tradingName": "trade2",
          "businessAddress": {
            "addressLine1": "add2",
            "addressLine2": "add2",
            "country": "GB",
            "postcode": "post2"
          },
          "residential": false,
          "eab": {
            "eab": true
          },
          "startDate": "1981-02-01",
          "endDate": "9999-12-31"
        },
        {
          "dateChangeFlag":false,
          "lineId": 3,
          "tradingName": "trade3",
          "businessAddress": {
            "addressLine1": "add3",
            "addressLine2": "add3",
            "country": "GB",
            "postcode": "post3"
          },
          "residential": false,
          "eab": {
            "eab": true
          },
          "startDate": "1983-02-01",
          "endDate": "9999-12-31"
        }
      ]
    }
  },
  "bankAccountDetails": {
    "noOfMlrBankAccounts": "",
    "bankAccounts": [
      {
        "accountName": "bank1",
        "accountType": "This business's",
        "doYouHaveUkBankAccount": true,
        "bankAccountDetails": {
          "ukAccount": {
            "sortCode": "204266",
            "accountNumber": "12345678"
          }
        }
      }
    ]
  },
  "eabAll": {
    "estateAgencyActProhibition": true,
    "estAgncActProhibProvideDetails": "test1",
    "prevWarnedWRegToEstateAgencyActivities": true,
    "prevWarnWRegProvideDetails": "test1"
  },
  "eabResdEstAgncy": {
    "regWithRedressScheme": true,
    "whichRedressScheme": "The Property Ombudsman Limited"
  },
  "responsiblePersons": [
    {
      "dateChangeFlag":false,
      "lineId": 1,
      "nameDetails": {
        "personName": {
          "firstName": "fname1",
          "lastName": "lname1"
        },
        "othrNamesOrAliasesDetails": {
          "otherNamesOrAliases": true,
          "aliases": [
            "test"
          ]
        },
        "previousNameDetails": {
          "dateChangeFlag":false,
          "nameEverChanged": true,
          "previousName": {
            "firstName": "fn",
            "lastName": "ln"
          },
          "dateOfChange": "2000-04-03"
        }
      },
      "nationalityDetails": {
        "areYouUkResident": true,
        "idDetails": {
          "ukResident": {
            "nino": "AB123456C"
          }
        },
        "countryOfBirth": "AG",
        "nationality": "IE"
      },
      "contactCommDetails": {
        "contactEmailAddress": "test1@test.com",
        "primaryTeleNo": "092427342735"
      },
      "currentAddressDetails": {
        "address": {
          "addressLine1": "add1",
          "addressLine2": "add1",
          "country": "GB",
          "postcode": "post1"
        }
      },
      "timeAtCurrentAddress": "7-12 months",
      "addressUnderThreeYears": {
        "address": {
          "addressLine1": "add2",
          "addressLine2": "add2",
          "country": "GB",
          "postcode": "post2"
        }
      },
      "timeAtAddressUnderThreeYears": "7-12 months",
      "addressUnderOneYear": {
        "address": {
          "addressLine1": "add3",
          "addressLine2": "add3",
          "country": "GB",
          "postcode": "post3"
        }
      },
      "timeAtAddressUnderOneYear": "7-12 months",
      "positionInBusiness": {
        "soleProprietor": {
          "soleProprietor": false,
          "nominatedOfficer": true
        }
      },
      "previousExperience": true,
      "descOfPrevExperience": "test1",
      "amlAndCounterTerrFinTraining": true,
      "trainingDetails": "test1",
      "startDate": "2000-02-01",
      "endDate": "9999-12-31"
    },
    {
      "lineId": 2,
      "dateChangeFlag":false,
      "nameDetails": {
        "personName": {
          "firstName": "fn2",
          "lastName": "ln2"
        }
      },
      "nationalityDetails": {
        "areYouUkResident": true,
        "idDetails": {
          "ukResident": {
            "nino": "AB123456C"
          }
        },
        "countryOfBirth": "AI",
        "nationality": "GB"
      },
      "contactCommDetails": {
        "contactEmailAddress": "test1@test.com",
        "primaryTeleNo": "0829478247"
      },
      "currentAddressDetails": {
        "address": {
          "addressLine1": "add2",
          "addressLine2": "add2",
          "country": "GB",
          "postcode": "post2"
        }
      },
      "timeAtCurrentAddress": "1-3 years",
      "positionInBusiness": {
        "soleProprietor": {
          "soleProprietor": true,
          "nominatedOfficer": false
        }
      },
      "regDetails": {
        "vatRegistered": true,
        "vrnNumber": "123456789",
        "saRegistered": true,
        "saUtr": "1234567890"
      },
      "previousExperience": true,
      "descOfPrevExperience": "test1",
      "amlAndCounterTerrFinTraining": true,
      "trainingDetails": "test1",
      "startDate": "2000-02-01",
      "endDate": "9999-12-31"
    },
    {
      "lineId": 3,
      "dateChangeFlag":false,
      "nameDetails": {
        "personName": {
          "firstName": "fn3",
          "lastName": "ln3"
        }
      },
      "nationalityDetails": {
        "areYouUkResident": true,
        "idDetails": {
          "ukResident": {
            "nino": "AB123456C"
          }
        },
        "countryOfBirth": "BD",
        "nationality": "AO"
      },
      "contactCommDetails": {
        "contactEmailAddress": "test1@test.com",
        "primaryTeleNo": "4575685688"
      },
      "currentAddressDetails": {
        "address": {
          "addressLine1": "add1",
          "addressLine2": "add1",
          "country": "GB",
          "postcode": "post1"
        }
      },
      "timeAtCurrentAddress": "0-6 months",
      "addressUnderThreeYears": {
        "address": {
          "addressLine1": "add2",
          "addressLine2": "add2",
          "country": "GB",
          "postcode": "post2"
        }
      },
      "timeAtAddressUnderThreeYears": "7-12 months",
      "addressUnderOneYear": {
        "address": {
          "addressLine1": "add3",
          "addressLine2": "add3",
          "country": "GB",
          "postcode": "post3"
        }
      },
      "timeAtAddressUnderOneYear": "7-12 months",
      "positionInBusiness": {
        "soleProprietor": {
          "soleProprietor": true,
          "nominatedOfficer": true
        }
      },
      "regDetails": {
        "vatRegistered": true,
        "vrnNumber": "123456789",
        "saRegistered": true,
        "saUtr": "1234567890"
      },
      "previousExperience": true,
      "descOfPrevExperience": "test1",
      "amlAndCounterTerrFinTraining": true,
      "trainingDetails": "test1",
      "startDate": "2000-02-01",
      "endDate": "9999-12-31"
    }
  ],
  "declaration": {
    "declarationFlag": true
  },
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
  "etmpFields": {
    "dateOfSubmission": "2016-10-24"
  }
}""")
        json.as[SubscriptionView] mustBe a[SubscriptionView]

      }

      "given valid json with LA" in {
        val jsonLA = Json.parse(
          """{
  "etmpFormBundleNumber": "082000001158",
  "businessDetails": {
    "typeOfLegalEntity": "Sole Proprietor"
  },
  "businessContactDetails": {
    "businessAddress": {
      "addressLine1": "Matheson House 1",
      "addressLine2": "Grange Central",
      "addressLine3": "Telford",
      "addressLine4": "Shropshire",
      "country": "GB",
      "postcode": "TF3 4ER"
    },
    "altCorrespondenceAddress": false,
    "businessTelNo": "0897686856765",
    "businessEmail": "test1@test.com"
  },
  "businessReferencesAll": {
    "amlsRegistered": true,
    "mlrRegNumber": "12345678",
    "prevRegForMlr": false
  },
  "businessActivities": {
    "mlrActivitiesAppliedFor": {
      "msb": false,
      "hvd": false,
      "asp": false,
      "tcsp": false,
      "eab": true,
      "bpsp": false,
      "tditpsp": false,
      "amp": false
    },
    "eabServicesCarriedOut": {
      "residentialEstateAgency": true,
      "commercialEstateAgency": false,
      "auctioneer": false,
      "relocationAgent": true,
      "businessTransferAgent": false,
      "assetManagementCompany": false,
      "landManagementAgent": false,
      "developmentCompany": false,
      "socialHousingProvider": false,
      "lettingAgents": true
    },
    "all": {
      "dateChangeFlag" : false,
      "businessActivityDetails": {
        "actvtsBusRegForOnlyActvtsCarOut": false,
        "respActvtsBusRegForOnlyActvtsCarOut": {
          "otherBusActivitiesCarriedOut": {
            "otherBusinessActivities": "test1",
            "anticipatedTotBusinessTurnover": "249999",
            "mlrActivityTurnover": "249999"
          }
        }
      },
      "franchiseDetails": {
        "isBusinessAFranchise": true,
        "franchiserName": [
          "test franch"
        ]
      },
      "noOfEmployees": "123",
      "noOfEmployeesForMlr": "12",
      "nonUkResidentCustDetails": {
        "nonUkResidentCustomers": true,
        "whichCountries": [
          "AR"
        ]
      },
      "auditableRecordsDetails": {
        "detailedRecordsKept": "Yes",
        "transactionRecordingMethod": {
          "manual": true,
          "spreadsheet": true,
          "commercialPackage": true,
          "commercialPackageName": "test soft"
        }
      },
      "suspiciousActivityGuidance": true,
      "nationalCrimeAgencyRegistered": true,
      "formalRiskAssessmentDetails": {
        "formalRiskAssessment": true,
        "riskAssessmentFormat": {
          "electronicFormat": true,
          "manualFormat": true
        }
      },
      "mlrAdvisor": {
        "doYouHaveMlrAdvisor": true,
        "mlrAdvisorDetails": {
          "advisorNameAddress": {
            "name": "thing",
            "address": {
              "addressLine1": "add1",
              "addressLine2": "add1",
              "country": "GB",
              "postcode": "post1"
            }
          },
          "agentDealsWithHmrc": true
        }
      }
    }
  },
  "tradingPremises": {
    "ownBusinessPremises": {
      "ownBusinessPremises": true,
      "ownBusinessPremisesDetails": [
        {
          "lineId": 1,
          "dateChangeFlag":false,
          "tradingName": "trade1",
          "businessAddress": {
            "addressLine1": "add1",
            "addressLine2": "add1",
            "country": "GB",
            "postcode": "post1"
          },
          "residential": false,
          "eab": {
            "eab": true
          },
          "startDate": "1980-02-01",
          "endDate": "9999-12-31"
        },
        {
          "lineId": 2,
          "dateChangeFlag":false,
          "tradingName": "trade2",
          "businessAddress": {
            "addressLine1": "add2",
            "addressLine2": "add2",
            "country": "GB",
            "postcode": "post2"
          },
          "residential": false,
          "eab": {
            "eab": true
          },
          "startDate": "1981-02-01",
          "endDate": "9999-12-31"
        },
        {
          "lineId": 3,
          "dateChangeFlag":false,
          "tradingName": "trade3",
          "businessAddress": {
            "addressLine1": "add3",
            "addressLine2": "add3",
            "country": "GB",
            "postcode": "post3"
          },
          "residential": false,
          "eab": {
            "eab": true
          },
          "startDate": "1983-02-01",
          "endDate": "9999-12-31"
        }
      ]
    }
  },
  "bankAccountDetails": {
    "noOfMlrBankAccounts": "",
    "bankAccounts": [
      {
        "accountName": "bank1",
        "accountType": "This business's",
        "doYouHaveUkBankAccount": true,
        "bankAccountDetails": {
          "ukAccount": {
            "sortCode": "204266",
            "accountNumber": "12345678"
          }
        }
      }
    ]
  },
  "eabAll": {
    "estateAgencyActProhibition": true,
    "estAgncActProhibProvideDetails": "test1",
    "prevWarnedWRegToEstateAgencyActivities": true,
    "prevWarnWRegProvideDetails": "test1"
  },
  "eabResdEstAgncy": {
    "regWithRedressScheme": true,
    "whichRedressScheme": "The Property Ombudsman Limited"
  },
  "lettingAgents": {
    "clientMoneyProtection": true
  },
  "responsiblePersons": [
    {
    "dateChangeFlag":false,
      "lineId": 1,
      "nameDetails": {
        "personName": {
          "firstName": "fname1",
          "lastName": "lname1"
        },
        "othrNamesOrAliasesDetails": {
          "otherNamesOrAliases": true,
          "aliases": [
            "test"
          ]
        },
        "previousNameDetails": {
          "dateChangeFlag":false,
          "nameEverChanged": true,
          "previousName": {
            "firstName": "fn",
            "lastName": "ln"
          },
          "dateOfChange": "2000-04-03"
        }
      },
      "nationalityDetails": {
        "areYouUkResident": true,
        "idDetails": {
          "ukResident": {
            "nino": "AB123456C"
          }
        },
        "countryOfBirth": "AG",
        "nationality": "IE"
      },
      "contactCommDetails": {
        "contactEmailAddress": "test1@test.com",
        "primaryTeleNo": "092427342735"
      },
      "currentAddressDetails": {
        "address": {
          "addressLine1": "add1",
          "addressLine2": "add1",
          "country": "GB",
          "postcode": "post1"
        }
      },
      "timeAtCurrentAddress": "7-12 months",
      "addressUnderThreeYears": {
        "address": {
          "addressLine1": "add2",
          "addressLine2": "add2",
          "country": "GB",
          "postcode": "post2"
        }
      },
      "timeAtAddressUnderThreeYears": "7-12 months",
      "addressUnderOneYear": {
        "address": {
          "addressLine1": "add3",
          "addressLine2": "add3",
          "country": "GB",
          "postcode": "post3"
        }
      },
      "timeAtAddressUnderOneYear": "7-12 months",
      "positionInBusiness": {
        "soleProprietor": {
          "soleProprietor": false,
          "nominatedOfficer": true
        }
      },
      "previousExperience": true,
      "descOfPrevExperience": "test1",
      "amlAndCounterTerrFinTraining": true,
      "trainingDetails": "test1",
      "startDate": "2000-02-01",
      "endDate": "9999-12-31"
    },
    {
      "lineId": 2,
      "dateChangeFlag":false,
      "nameDetails": {
        "personName": {
          "firstName": "fn2",
          "lastName": "ln2"
        }
      },
      "nationalityDetails": {
        "areYouUkResident": true,
        "idDetails": {
          "ukResident": {
            "nino": "AB123456C"
          }
        },
        "countryOfBirth": "AI",
        "nationality": "GB"
      },
      "contactCommDetails": {
        "contactEmailAddress": "test1@test.com",
        "primaryTeleNo": "0829478247"
      },
      "currentAddressDetails": {
        "address": {
          "addressLine1": "add2",
          "addressLine2": "add2",
          "country": "GB",
          "postcode": "post2"
        }
      },
      "timeAtCurrentAddress": "1-3 years",
      "positionInBusiness": {
        "soleProprietor": {
          "soleProprietor": true,
          "nominatedOfficer": false
        }
      },
      "regDetails": {
        "vatRegistered": true,
        "vrnNumber": "123456789",
        "saRegistered": true,
        "saUtr": "1234567890"
      },
      "previousExperience": true,
      "descOfPrevExperience": "test1",
      "amlAndCounterTerrFinTraining": true,
      "trainingDetails": "test1",
      "startDate": "2000-02-01",
      "endDate": "9999-12-31"
    },
    {
      "lineId": 3,
      "dateChangeFlag":false,
      "nameDetails": {
        "personName": {
          "firstName": "fn3",
          "lastName": "ln3"
        }
      },
      "nationalityDetails": {
        "areYouUkResident": true,
        "idDetails": {
          "ukResident": {
            "nino": "AB123456C"
          }
        },
        "countryOfBirth": "BD",
        "nationality": "AO"
      },
      "contactCommDetails": {
        "contactEmailAddress": "test1@test.com",
        "primaryTeleNo": "4575685688"
      },
      "currentAddressDetails": {
        "address": {
          "addressLine1": "add1",
          "addressLine2": "add1",
          "country": "GB",
          "postcode": "post1"
        }
      },
      "timeAtCurrentAddress": "0-6 months",
      "addressUnderThreeYears": {
        "address": {
          "addressLine1": "add2",
          "addressLine2": "add2",
          "country": "GB",
          "postcode": "post2"
        }
      },
      "timeAtAddressUnderThreeYears": "7-12 months",
      "addressUnderOneYear": {
        "address": {
          "addressLine1": "add3",
          "addressLine2": "add3",
          "country": "GB",
          "postcode": "post3"
        }
      },
      "timeAtAddressUnderOneYear": "7-12 months",
      "positionInBusiness": {
        "soleProprietor": {
          "soleProprietor": true,
          "nominatedOfficer": true
        }
      },
      "regDetails": {
        "vatRegistered": true,
        "vrnNumber": "123456789",
        "saRegistered": true,
        "saUtr": "1234567890"
      },
      "previousExperience": true,
      "descOfPrevExperience": "test1",
      "amlAndCounterTerrFinTraining": true,
      "trainingDetails": "test1",
      "startDate": "2000-02-01",
      "endDate": "9999-12-31"
    }
  ],
  "declaration": {
    "declarationFlag": true
  },
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
  "etmpFields": {
    "dateOfSubmission": "2016-10-24"
  }
}""")
        jsonLA.as[SubscriptionView] mustBe a[SubscriptionView]

      }
    }
  }

  val GetSuccessModel = SubscriptionView(
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
    Some(DesConstants.testAmp),
    None,
    DesConstants.extraFields
  )

  val getSuccessModelLA = GetSuccessModel.copy(lettingAgents = Some(
    DesConstants.testLettingAgents),
    businessActivities = DesConstants.testBusinessActivitiesLA
  )

}
