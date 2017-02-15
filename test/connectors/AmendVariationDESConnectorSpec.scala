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

package connectors

import audit.MockAudit
import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import metrics.{API6, Metrics}
import models.des
import models.des.AmendVariationRequest
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.FakeApplication
import uk.gov.hmrc.play.http.{HttpGet, HttpPost, HttpPut, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendVariationDESConnectorSpec extends PlaySpec with MockitoSugar with ScalaFutures with IntegrationPatience with OneAppPerSuite {

  implicit override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> true))

  trait Fixture {

    object testDESConnector extends AmendVariationDESConnector {
      override private[connectors] val baseUrl: String = "baseUrl"
      override private[connectors] val token: String = "token"
      override private[connectors] val env: String = "ist0"
      override private[connectors] val httpGet: HttpGet = mock[HttpGet]
      override private[connectors] val httpPut: HttpPut = mock[HttpPut]
      override private[connectors] val httpPost: HttpPost = mock[HttpPost]
      override private[connectors] val metrics: Metrics = mock[Metrics]
      override private[connectors] val audit = MockAudit
      override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl/"
    }

    val amlsRegistrationNumber = "test"

    val successModel = des.AmendVariationResponse(
      processingDate = "2016-09-17T09:30:47Z",
      etmpFormBundleNumber = "111111",
      Some(1301737.96d),
      Some(231.42d),
      Some(870458d),
      Some(2172427.38),
      Some("string"),
      Some(3456.12)
    )

    val url = s"${testDESConnector.fullUrl}/$amlsRegistrationNumber"

    val mockTimer = mock[Timer.Context]

    when {
      testDESConnector.metrics.timer(eqTo(API6))
    } thenReturn mockTimer
  }

  "DESConnector" must {

    "return a succesful future" in new Fixture {

      val response = HttpResponse(
        responseStatus = OK,
        responseHeaders = Map(
          "CorrelationId" -> Seq("my-correlation-id")
        ),
        responseJson = Some(Json.toJson(successModel))
      )

      when {
        testDESConnector.httpPut.PUT[des.AmendVariationRequest,
          HttpResponse](eqTo(url), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.amend(amlsRegistrationNumber, testRequest)) {
        _ mustEqual successModel
      }
    }

    "return a failed future" in new Fixture {

      val response = HttpResponse(
        responseStatus = BAD_REQUEST,
        responseHeaders = Map(
          "CorrelationId" -> Seq("my-correlation-id")
        )
      )
      when {
        testDESConnector.httpPut.PUT[des.AmendVariationRequest,
          HttpResponse](eqTo(url), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.amend(amlsRegistrationNumber, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body mustEqual None
      }
    }

    "return a failed future (json validation)" in new Fixture {

      val response = HttpResponse(
        responseStatus = OK,
        responseHeaders = Map(
          "CorrelationId" -> Seq("my-correlation-id")
        ),
        responseString = Some("message")
      )

      when {
        testDESConnector.httpPut.PUT[des.AmendVariationRequest,
          HttpResponse](eqTo(url), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.amend(amlsRegistrationNumber, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual OK
          body mustEqual Some("message")
      }
    }

    "return a failed future (exception)" in new Fixture {

      when {
        testDESConnector.httpPut.PUT[des.AmendVariationRequest,
          HttpResponse](eqTo(url), any())(any(), any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady(testDESConnector.amend(amlsRegistrationNumber, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }
  }

  def testRequest = Json.parse(
    """{
         "acknowledgementReference": "$AckRef$",
         "changeIndicators":{
            "businessDetails":false,
            "businessAddress":false,
            "businessReferences":false,
            "tradingPremises":false,
            "businessActivities":false,
            "bankAccountDetails":false,
             "msb": {
              "msb": false
              },
              "hvd": {
              "hvd": false
              },
              "asp": {
              "asp": false
              },
              "aspOrTcsp": {
              "aspOrTcsp": false
              },
              "tcsp": {
              "tcsp": false
              },
              "eab": {
              "eab": false
              },
            "responsiblePersons":false,
            "filingIndividual":false
         },
         "amlsMessageType":"Amendment",
         "businessDetails":{
            "typeOfLegalEntity":"Sole Proprietor",
            "corpAndBodyLlps":{
               "companyName":"CompanyName",
               "companyRegNo":"12345678"
            },
            "unincorpBody":{
               "companyName":"CompanyName",
               "typeOfBusiness":"TypeOfBusiness"
            }
         },
         "businessContactDetails":{
            "businessAddress":{
               "addressLine1":"BusinessAddressLine1",
               "addressLine2":"BusinessAddressLine2",
               "addressLine3":"BusinessAddressLine3",
               "addressLine4":"BusinessAddressLine4",
               "country":"GB",
               "postcode":"Postcode"
            },
            "altCorrespondenceAddress":true,
            "alternativeAddress":{
               "name":"Name",
               "tradingName":"TradingName",
               "address":{
                  "addressLine1":"AlternativeAddressLine1",
                  "addressLine2":"AlternativeAddressLine2",
                  "addressLine3":"AlternativeAddressLine3",
                  "addressLine4":"AlternativeAddressLine4",
                  "country":"GB",
                  "postcode":"Postcode"
               }
            },
            "businessTelNo":"01921232322",
            "businessEmail":"BusinessEmail"
         },
         "businessReferencesAll":{
            "amlsRegistered":false,
            "prevRegForMlr":false
         },
         "businessReferencesAllButSp":{
            "vatRegistered":true,
            "vrnNumber":"123456789"
         },
         "businessReferencesCbUbLlp":{
            "cotaxRegistered":true,
            "ctutr":"1234567891"
         },
         "businessActivities":{
            "mlrActivitiesAppliedFor":{
               "msb":true,
               "hvd":true,
               "asp":true,
               "tcsp":true,
               "eab":true,
               "bpsp":true,
               "tditpsp":true
            },
            "msbServicesCarriedOut":{
               "mt":true,
               "ce":true,
               "smdcc":true,
               "nonSmdcc":true,
               "fx":false
            },
            "hvdGoodsSold":{
               "alcohol":true,
               "tobacco":true,
               "antiques":true,
               "cars":true,
               "otherMotorVehicles":true,
               "caravans":true,
               "jewellery":true,
               "gold":true,
               "scrapMetals":true,
               "mobilePhones":true,
               "clothing":true,
               "other":true,
               "specifyOther":"SpecifyOther",
               "howGoodsAreSold":{
                  "retail":true,
                  "wholesale":true,
                  "auction":true
               }
            },
            "hvdAlcoholTobacco":{
               "dutySuspExAtGoods":true
            },
            "aspServicesOffered":{
               "accountant":true,
               "payrollServiceProvider":true,
               "bookKeeper":true,
               "auditor":true,
               "financialOrTaxAdvisor":true
            },
            "tcspServicesOffered":{
               "nomineeShareholders":true,
               "trusteeProvider":true,
               "regOffBusinessAddrVirtualOff":true,
               "compDirSecPartnerProvider":true,
               "trustOrCompFormAgent":true
            },
            "tcspServicesforRegOffBusinessAddrVirtualOff":{
               "callHandling":true,
               "emailHandling":true,
               "emailServer":true,
               "selfCollectMailboxes":true,
               "mailForwarding":false,
               "receptionist":false,
               "conferenceRooms":true,
               "other":true,
               "specifyOther":"SpecifyOther"
            },
            "eabServicesCarriedOut":{
               "residentialEstateAgency":true,
               "commercialEstateAgency":true,
               "auctioneer":true,
               "relocationAgent":true,
               "businessTransferAgent":true,
               "assetManagementCompany":true,
               "landManagementAgent":true,
               "developmentCompany":true,
               "socialHousingProvider":true
            },
            "all":{
               "activitiesCommenceDate":"2001-01-01",
               "DateChangeFlag":false,
               "businessActivityDetails":{
                  "actvtsBusRegForOnlyActvtsCarOut":true,
                  "respActvtsBusRegForOnlyActvtsCarOut":{
                     "mlrActivityTurnover":"99999"
                  }
               },
               "franchiseDetails":{
                  "isBusinessAFranchise":true,
                  "franchiserName":[
                     "FranchiserName1"
                  ]
               },
               "noOfEmployees":"12345678901",
               "noOfEmployeesForMlr":"11223344556",
               "nonUkResidentCustDetails":{
                  "nonUkResidentCustomers":true,
                  "whichCountries":[
                     "AD",
                     "GB"
                  ]
               },
               "auditableRecordsDetails":{
                  "detailedRecordsKept":"Yes",
                  "transactionRecordingMethod":{
                     "manual":true,
                     "spreadsheet":true,
                     "commercialPackage":true,
                     "commercialPackageName":"CommercialPackageName"
                  }
               },
               "suspiciousActivityGuidance":true,
               "nationalCrimeAgencyRegistered":true,
               "formalRiskAssessmentDetails":{
                  "formalRiskAssessment":true,
                  "riskAssessmentFormat":{
                     "electronicFormat":true,
                     "manualFormat":true
                  }
               },
               "mlrAdvisor":{
                  "doYouHaveMlrAdvisor":true,
                  "mlrAdvisorDetails":{
                     "advisorNameAddress":{
                        "name":"Name",
                        "tradingName":"TradingName",
                        "address":{
                           "addressLine1":"AdvisorAddressLine1",
                           "addressLine2":"AdvisorAddressLine2",
                           "addressLine3":"AdvisorAddressLine3",
                           "addressLine4":"AdvisorAddressLine4",
                           "country":"GB",
                           "postcode":"Postcode"
                        }
                     },
                     "agentDealsWithHmrc":true
                  }
               }
            }
         },
         "tradingPremises":{
            "ownBusinessPremises":{
               "ownBusinessPremises":true,
               "ownBusinessPremisesDetails":[
                  {
                     "tradingName":"OwnBusinessTradingName",
                     "businessAddress":{
                        "addressLine1":"OwnBusinessAddressLine1",
                        "addressLine2":"OwnBusinessAddressLine2",
                        "addressLine3":"OwnBusinessAddressLine3",
                        "addressLine4":"OwnBusinessAddressLine4",
                        "country":"GB",
                        "postcode":"AA1 1AA"
                     },
                     "residential":false,
                     "msb":{
                        "mt":false,
                        "ce":false,
                        "smdcc":false,
                        "nonSmdcc":false,
                        "fx":false
                     },
                     "hvd":{
                        "hvd":false
                     },
                     "asp":{
                        "asp":false
                     },
                     "tcsp":{
                        "tcsp":true
                     },
                     "eab":{
                        "eab":true
                     },
                     "bpsp":{
                        "bpsp":true
                     },
                     "tditpsp":{
                        "tditpsp":false
                     },
                     "startDate":"2001-01-01",
                     "lineId":4,
                     "status":"Added"
                  },
                  {
                     "tradingName":"OwnBusinessTradingName1",
                     "businessAddress":{
                        "addressLine1":"OB11AddressLine1",
                        "addressLine2":"OB1AddressLine2",
                        "addressLine3":"OB1AddressLine3",
                        "addressLine4":"OB1AddressLine4",
                        "country":"GB",
                        "postcode":"BB1 1BB"
                     },
                     "residential":false,
                     "msb":{
                        "mt":false,
                        "ce":false,
                        "smdcc":true,
                        "nonSmdcc":true,
                        "fx":false
                     },
                     "hvd":{
                        "hvd":true
                     },
                     "asp":{
                        "asp":true
                     },
                     "tcsp":{
                        "tcsp":true
                     },
                     "eab":{
                        "eab":true
                     },
                     "bpsp":{
                        "bpsp":true
                     },
                     "tditpsp":{
                        "tditpsp":true
                     },
                     "startDate":"2001-01-01",
                     "lineId":5,
                     "status":"Added"
                  }
               ]
            },
            "agentBusinessPremises":{
               "agentBusinessPremises":true,
               "agentDetails":[
                  {
                     "agentLegalEntity":"Sole Proprietor",
                     "agentLegalEntityName":"AgentLegalEntityName",
                     "agentPremises":{
                        "tradingName":"aaaaaaaaaaaa",
                        "businessAddress":{
                           "addressLine1":"a",
                           "addressLine2":"a",
                           "addressLine3":"a",
                           "addressLine4":"a",
                           "country":"GB",
                           "postcode":"aaaaaaaaaa"
                        },
                        "residential":true,
                        "msb":{
                           "mt":true,
                           "ce":true,
                           "smdcc":false,
                           "nonSmdcc":false,
                           "fx":false
                        },
                        "hvd":{
                           "hvd":true
                        },
                        "asp":{
                           "asp":true
                        },
                        "tcsp":{
                           "tcsp":true
                        },
                        "eab":{
                           "eab":true
                        },
                        "bpsp":{
                           "bpsp":true
                        },
                        "tditpsp":{
                           "tditpsp":true
                        },
                        "startDate":"1967-08-13"
                     },
                     "status":"Added",
                     "lineId":1
                  },
                  {
                     "agentLegalEntity":"Sole Proprietor",
                     "agentLegalEntityName":"aaaaaaaaaaa",
                     "agentPremises":{
                        "tradingName":"aaaaaaaaaaaa",
                        "businessAddress":{
                           "addressLine1":"a",
                           "addressLine2":"a",
                           "addressLine3":"a",
                           "addressLine4":"a",
                           "country":"GB",
                           "postcode":"aaaaaaaaaa"
                        },
                        "residential":true,
                        "msb":{
                           "mt":true,
                           "ce":true,
                           "smdcc":true,
                           "nonSmdcc":true,
                           "fx":false
                        },
                        "hvd":{
                           "hvd":true
                        },
                        "asp":{
                           "asp":true
                        },
                        "tcsp":{
                           "tcsp":true
                        },
                        "eab":{
                           "eab":true
                        },
                        "bpsp":{
                           "bpsp":true
                        },
                        "tditpsp":{
                           "tditpsp":true
                        },
                        "startDate":"1967-08-13"
                     },
                     "status":"Added",
                     "lineId":2
                  },
                  {
                     "agentLegalEntity":"Sole Proprietor",
                     "agentLegalEntityName":"AgentLegalEntityName2",
                     "agentPremises":{
                        "tradingName":"TradingName",
                        "businessAddress":{
                           "addressLine1":"AgentAddressLine1",
                           "addressLine2":"AgentAddressLine2",
                           "addressLine3":"AgentAddressLine3",
                           "addressLine4":"AgentAddressLine4",
                           "country":"GB",
                           "postcode":"AA1 1AA"
                        },
                        "residential":true,
                        "msb":{
                           "mt":true,
                           "ce":true,
                           "smdcc":true,
                           "nonSmdcc":true,
                           "fx":false
                        },
                        "hvd":{
                           "hvd":true
                        },
                        "asp":{
                           "asp":true
                        },
                        "tcsp":{
                           "tcsp":true
                        },
                        "eab":{
                           "eab":true
                        },
                        "bpsp":{
                           "bpsp":true
                        },
                        "tditpsp":{
                           "tditpsp":true
                        },
                        "startDate":"2001-01-01"
                     },
                     "status":"Added",
                     "lineId":3
                  }
               ]
            }
         },
         "bankAccountDetails":{
            "noOfMlrBankAccounts":"3",
            "bankAccounts":[
               {
                  "accountName":"AccountName",
                  "accountType":"This business's",
                  "doYouHaveUkBankAccount":true,
                  "bankAccountDetails":{
                     "ukAccount":{
                        "sortCode":"123456",
                        "accountNumber":"12345678"
                     }
                  }
               },
               {
                  "accountName":"AccountName1",
                  "accountType":"Personal",
                  "doYouHaveUkBankAccount":false,
                  "bankAccountDetails":{
                     "nonUkAccount":{
                        "accountHasIban":true,
                        "accountNumber":{
                           "iban":"87654321"
                        }
                     }
                  }
               },
               {
                  "accountName":"AccountName2",
                  "accountType":"Another business's",
                  "doYouHaveUkBankAccount":false,
                  "bankAccountDetails":{
                     "nonUkAccount":{
                        "accountHasIban":false,
                        "accountNumber":{
                           "bankAccountNumber":"87654321"
                        }
                     }
                  }
               }
            ]
         },
         "msb":{
            "msbAllDetails":{
               "anticipatedTotThrputNxt12Mths":"999999",
               "otherCntryBranchesOrAgents":true,
               "countriesList":{
                  "listOfCountries":[
                     "AD",
                     "GB"
                  ]
               },
               "sysLinkedTransIdentification":true
            },
            "msbMtDetails":{
               "applyForFcapsrRegNo":true,
               "fcapsrRefNo":"123456",
               "ipspServicesDetails":{
                  "ipspServicesUsed":true,
                  "ipspDetails":[
                     {
                        "ipspName":"IPSPName1",
                        "ipspMlrRegNo":"IPSPMLRRegNo1"
                     }
                  ]
               },
               "informalFundsTransferSystem":true,
               "noOfMoneyTrnsfrTransNxt12Mnths":"11111111111",
               "countriesLrgstMoneyAmtSentTo":{
                  "listOfCountries":[
                     "GB",
                     "AD"
                  ]
               },
               "countriesLrgstTranscsSentTo":{
                  "listOfCountries":[
                     "AD",
                     "GB"
                  ]
               }
            },
            "msbCeDetails":{
               "currencySources":{
                  "bankDetails":{
                     "banks":true,
                     "bankNames":[
                        "BankNames1"
                     ]
                  },
                  "currencyWholesalerDetails":{
                     "currencyWholesalers":true,
                     "currencyWholesalersNames":[
                        "CurrencyWholesalerNames"
                     ]
                  },
                  "reSellCurrTakenIn":true,
                  "antNoOfTransNxt12Mnths":"11234567890",
                  "currSupplyToCust":{
                     "currency":[
                        "GBP",
                        "XYZ",
                        "ABC"
                     ]
                  }
               }
            }
         },
         "hvd":{
            "cashPaymentsAccptOvrThrshld":true,
            "dateOfTheFirst":"2001-01-01",
            "sysAutoIdOfLinkedCashPymts":true,
            "hvdFromUnseenCustDetails":{
               "hvdFromUnseenCustomers":true,
               "receiptMethods":{
                  "receiptMethodViaCourier":true,
                  "receiptMethodDirectBankAct":true,
                  "receiptMethodOther":true,
                  "specifyOther":"aaaaaaaaaaaaa"
               }
            }
         },
         "asp":{
            "regHmrcAgtRegSchTax":true,
            "hmrcAgentRegNo":"123456789"
         },
         "aspOrTcsp":{
            "supervisionDetails":{
               "prevSupervisedByMlsRegs":true,
               "supervisorDetails":{
                  "nameOfLastSupervisor":"NameOfLastSupervisor",
                  "supervisionStartDate":"2001-01-01",
                  "supervisionEndDate":"2001-01-01",
                  "supervisionEndingReason":"SupervisionEndingReason"
               }
            },
            "professionalBodyDetails":{
               "prevWarnedWRegToAspActivities":true,
               "detailsIfFinedWarned":"DetailsIfFinedWarned",
               "professionalBody":{
                  "professionalBodyMember":true,
                  "professionalBodyDetails":{
                     "associationofAccountingTechnicians":true,
                     "associationofCharteredCertifiedAccountants":true,
                     "associationofInternationalAccountants":true,
                     "associationofTaxationTechnicians":true,
                     "charteredInstituteofManagementAccountants":true,
                     "charteredInstituteofTaxation":true,
                     "instituteofCertifiedBookkeepers":true,
                     "instituteofCharteredAccountantsinIreland":true,
                     "instituteofCharteredAccountantsinScotland":true,
                     "instituteofCharteredAccountantsofEnglandandWales":true,
                     "instituteofFinancialAccountants":true,
                     "internationalAssociationofBookKeepers":true,
                     "lawSociety":true,
                     "other":true,
                     "specifyOther":"SpecifyOther"
                  }
               }
            }
         },
         "tcspAll":{
            "anotherTcspServiceProvider":true,
            "tcspMlrRef":"111111111111111"
         },
         "tcspTrustCompFormationAgt":{
            "onlyOffTheShelfCompsSold":true,
            "complexCorpStructureCreation":true
         },
         "eabAll":{
            "estateAgencyActProhibition":true,
            "estAgncActProhibProvideDetails":"EstAgncActProhibProvideDetails",
            "prevWarnedWRegToEstateAgencyActivities":true,
            "prevWarnWRegProvideDetails":"PrevWarnWRegProvideDetails"
         },
         "eabResdEstAgncy":{
            "regWithRedressScheme":true,
            "whichRedressScheme":"The Property Ombudsman Limited"
         },
         "responsiblePersons":[
            {
               "nameDetails":{
                  "personName":{
                     "firstName":"FirstName",
                     "middleName":"MiddleName",
                     "lastName":"LastName"
                  },
                  "othrNamesOrAliasesDetails":{
                     "otherNamesOrAliases":true,
                     "aliases":[
                        "Aliases1"
                     ]
                  },
                  "previousNameDetails":{
                     "nameEverChanged":true,
                     "previousName":{
                        "firstName":"FirstName",
                        "middleName":"MiddleName",
                        "lastName":"LastName"
                     },
                     "dateOfChange":"2001-01-01"
                  }
               },
               "nationalityDetails":{
                  "areYouUkResident":false,
                  "idDetails":{
                     "nonUkResident":{
                        "dateOfBirth":"2001-01-01",
                        "passportHeld":true,
                        "passportDetails":{
                           "ukPassport":true,
                           "passportNumber":{
                              "ukPassportNumber":"AA1111111"
                           }
                        }
                     }
                  },
                  "countryOfBirth":"AA",
                  "nationality":"AA"
               },
               "currentAddressDetails":{
                  "address":{
                     "addressLine1":"CurrentAddressLine1",
                     "addressLine2":"CurrentAddressLine2",
                     "addressLine3":"CurrentAddressLine3",
                     "addressLine4":"CurrentAddressLine4",
                     "country":"GB",
                     "postcode":"Postcode"
                  }
               },
               "timeAtCurrentAddress":"3+ years",
               "positionInBusiness":{
                  "soleProprietor":{
                     "soleProprietor":true,
                     "nominatedOfficer":true
                  },
                  "partnership":{
                     "partner":false,
                     "nominatedOfficer":false
                  },
                  "corpBodyOrUnInCorpBodyOrLlp":{
                     "director":false,
                     "beneficialOwner":false,
                     "nominatedOfficer":false
                  }
               },
               "regDetails":{
                  "vatRegistered":true,
                  "vrnNumber":"123456789",
                  "saRegistered":true,
                  "saUtr":"1234567890"
               },
               "previousExperience":false,
               "amlAndCounterTerrFinTraining":true,
               "trainingDetails":"TrainingDetails",
               "msbOrTcsp":{
                  "passedFitAndProperTest":false
               },
               "lineId":1,
               "endDate":"",
               "status":"added",
               "retestFlag":false,
               "testResult":"",
               "testDate":""
            },
            {
               "nameDetails":{
                  "personName":{
                     "firstName":"bbbbbbbbbbbb",
                     "middleName":"bbbbbbbbbbb",
                     "lastName":"bbbbbbbbbbb"
                  },
                  "othrNamesOrAliasesDetails":{
                     "otherNamesOrAliases":true,
                     "aliases":[
                        "bbbbbbbbbbb"
                     ]
                  },
                  "previousNameDetails":{
                     "nameEverChanged":true,
                     "previousName":{
                        "firstName":"bbbbbbbbbbbb",
                        "middleName":"bbbbbbbbbbbb",
                        "lastName":"bbbbbbbbbbbb"
                     },
                     "dateOfChange":"1967-08-13"
                  }
               },
               "nationalityDetails":{
                  "areYouUkResident":true,
                  "idDetails":{
                     "ukResident":{
                        "nino":"BB000000A"
                     }
                  },
                  "countryOfBirth":"GB",
                  "nationality":"GB"
               },
               "currentAddressDetails":{
                  "address":{
                     "addressLine1":"b",
                     "addressLine2":"b",
                     "addressLine3":"b",
                     "addressLine4":"b",
                     "country":"GB",
                     "postcode":"bbbbbb"
                  }
               },
               "timeAtCurrentAddress":"0-6 months",
               "addressUnderThreeYears":{
                  "address":{
                     "addressLine1":"b",
                     "addressLine2":"b",
                     "addressLine3":"b",
                     "addressLine4":"b",
                     "country":"GB",
                     "postcode":"bbbbb"
                  }
               },
               "timeAtAddressUnderThreeYears":"0-6 months",
               "addressUnderOneYear":{
                  "address":{
                     "addressLine1":"a",
                     "addressLine2":"a",
                     "addressLine3":"a",
                     "addressLine4":"a",
                     "country":"GB",
                     "postcode":"bbbbb1"
                  }
               },
               "timeAtAddressUnderOneYear":"7-12 months",
               "positionInBusiness":{
                  "soleProprietor":{
                     "soleProprietor":true,
                     "nominatedOfficer":true
                  },
                  "partnership":{
                     "partner":false,
                     "nominatedOfficer":false
                  },
                  "corpBodyOrUnInCorpBodyOrLlp":{
                     "director":false,
                     "beneficialOwner":false,
                     "nominatedOfficer":false
                  }
               },
               "regDetails":{
                  "vatRegistered":true,
                  "vrnNumber":"111111111",
                  "saRegistered":true,
                  "saUtr":"1111111111"
               },
               "previousExperience":true,
               "descOfPrevExperience":"bbbbbbbbbb",
               "amlAndCounterTerrFinTraining":false,
               "msbOrTcsp":{
                  "passedFitAndProperTest":true
               },
               "lineId":2,
               "endDate":"",
               "status":"added",
               "retestFlag":false,
               "testResult":"",
               "testDate":""
            }
         ],
         "declaration":{
            "declarationFlag":true
         },
         "filingIndividual": {
    "individualDetails": {
      "firstName": "fname",
      "lastName": "lname"
    },
    "employedWithinBusiness": false,
     "roleWithinBusiness": {
       "beneficialShareholder": true,
       "designatedMember": false,
       "director": true,
       "internalAccountant": false,
       "nominatedOfficer": true,
       "other": true,
       "partner": true,
       "soleProprietor": false,
       "specifyOtherRoleInBusiness": "manager"
     },
     "roleForTheBusiness": {
       "other": true,
       "externalAccountant": false,
       "specifyOtherRoleForBusiness": "manager"
     }
  }
      }""").as[AmendVariationRequest]
}
