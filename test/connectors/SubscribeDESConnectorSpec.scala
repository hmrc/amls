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
import generators.AmlsReferenceNumberGenerator
import metrics.{API4, Metrics}
import models.des
import models.des.SubscriptionRequest
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubscribeDESConnectorSpec extends PlaySpec
  with MockitoSugar
  with ScalaFutures
  with IntegrationPatience
  with OneAppPerSuite
  with AmlsReferenceNumberGenerator{

  trait Fixture {

    object testDESConnector extends SubscribeDESConnector {
      override private[connectors] val baseUrl: String = "baseUrl"
      override private[connectors] val token: String = "token"
      override private[connectors] val env: String = "ist0"
      override private[connectors] val httpGet: HttpGet = mock[HttpGet]
      override private[connectors] val httpPost: HttpPost = mock[HttpPost]
      override private[connectors] val metrics: Metrics = mock[Metrics]
      override private[connectors] val audit = MockAudit
      override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl/"
      override private[connectors] def auditConnector = mock[AuditConnector]

    }

    val safeId = "safeId"

    implicit val hc = HeaderCarrier()

    val successModel = des.SubscriptionResponse(
      etmpFormBundleNumber = "111111",
      amlsRefNo = amlsRegistrationNumber,
      Some(1301737.96),
      Some(231.42),
      870458,
      2172427.38,
      "string"
    )

    val url = s"${testDESConnector.fullUrl}/$safeId"

    val mockTimer = mock[Timer.Context]

    when {
      testDESConnector.metrics.timer(eqTo(API4))
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
        testDESConnector.httpPost.POST[des.SubscriptionRequest,
          HttpResponse](eqTo(url), any(), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.subscribe(safeId, testRequest)) {
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
        testDESConnector.httpPost.POST[des.SubscriptionRequest,
          HttpResponse](eqTo(url), any(), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.subscribe(safeId, testRequest).failed) {
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
        testDESConnector.httpPost.POST[des.SubscriptionRequest,
          HttpResponse](eqTo(url), any(), any())(any(), any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.subscribe(safeId, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual OK
          body mustEqual Some("message")
      }
    }

    "return a failed future (exception)" in new Fixture {

      when {
        testDESConnector.httpPost.POST[des.SubscriptionRequest,
          HttpResponse](eqTo(url), any(), any())(any(), any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady(testDESConnector.subscribe(safeId, testRequest).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }
  }

  def testRequest = Json.parse(

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
      "tditpsp": false
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
            "ce": false,
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
}""").as[SubscriptionRequest]
}
