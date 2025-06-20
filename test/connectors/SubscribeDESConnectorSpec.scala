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

package connectors

import audit.SubscriptionFailedEvent
import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import metrics.API4
import models.des
import models.des.SubscriptionRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.{ArgumentCaptor, ArgumentMatchers}
import org.scalatest.BeforeAndAfter
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.audit.HandlerResult
import uk.gov.hmrc.http.client.RequestBuilder
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import utils.{AmlsBaseSpec, ApiRetryHelper}

import scala.concurrent.Future

class SubscribeDESConnectorSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator with BeforeAndAfter {

  before {
    reset(mockAuditConnector)
  }

  val testConnector                      = new SubscribeDESConnector(mockAppConfig, mockAuditConnector, mockHttpClient, mockMetrics) {
    override private[connectors] val baseUrl: String = "http://localhost:1234"
    override private[connectors] val token: String   = "token"
    override private[connectors] val env: String     = "ist0"
    override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl/"
  }
  val mockApiRetryHelper                 = mock[ApiRetryHelper]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]
  val safeId                             = "safeId"

  val successModel = des.SubscriptionResponse(
    etmpFormBundleNumber = "111111",
    amlsRefNo = amlsRegistrationNumber,
    Some(1301737.96),
    Some(231.42),
    870458,
    2172427.38,
    "string"
  )

  val url = s"${testConnector.fullUrl}/$safeId"

  val mockTimer = mock[Timer.Context]
  when {
    testConnector.metrics.timer(ArgumentMatchers.eq(API4))
  } thenReturn mockTimer

  "DESConnector" must {

    "return a succesful future" in {

      val response = HttpResponse(
        status = OK,
        json = Json.toJson(successModel),
        headers = Map("CorrelationId" -> Seq("my-correlation-id"))
      )
      when {
        testConnector.httpClientV2.post(url"$url")
      } thenReturn mockRequestBuilder
      when(
        mockRequestBuilder.setHeader(
          ("Authorization", "token"),
          ("Environment", "ist0"),
          ("Accept", "application/json"),
          ("Content-Type", "application/json;charset=utf-8")
        )
      ).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(testRequest)))(any(), any(), any()))
        .thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(response))

      whenReady(testConnector.subscribe(safeId, testRequest)) {
        _ mustEqual successModel
      }
    }

    "return a failed future" in {

      val response = HttpResponse(
        status = BAD_REQUEST,
        body = "",
        headers = Map("CorrelationId" -> Seq("my-correlation-id"))
      )

      when {
        testConnector.httpClientV2.post(url"$url")
      } thenReturn mockRequestBuilder
      when(
        mockRequestBuilder.setHeader(
          ("Authorization", "token"),
          ("Environment", "ist0"),
          ("Accept", "application/json"),
          ("Content-Type", "application/json;charset=utf-8")
        )
      ).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(testRequest)))(any(), any(), any()))
        .thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(response))

      whenReady(testConnector.subscribe(safeId, testRequest).failed) { case HttpStatusException(status, body) =>
        status mustEqual BAD_REQUEST
        body.getOrElse("").isEmpty mustEqual true
      }
    }

    "return a failed future (json validation)" in {

      val response = HttpResponse(
        status = OK,
        json = Json.toJson("message"),
        headers = Map("CorrelationId" -> Seq("my-correlation-id"))
      )

      val auditResult = AuditResult.fromHandlerResult(HandlerResult.Success)

      val captor = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])

      when {
        testConnector.httpClientV2.post(url"$url")
      } thenReturn mockRequestBuilder
      when(
        mockRequestBuilder.setHeader(
          ("Authorization", "token"),
          ("Environment", "ist0"),
          ("Accept", "application/json"),
          ("Content-Type", "application/json;charset=utf-8")
        )
      ).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(testRequest)))(any(), any(), any()))
        .thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(response))

      when {
        testConnector.ac.sendExtendedEvent(captor.capture())(any(), any())
      } thenReturn Future.successful(auditResult)

      whenReady(testConnector.subscribe(safeId, testRequest).failed) { case HttpStatusException(status, body) =>
        status mustEqual OK
        body mustEqual Some("\"message\"")
        val subscriptionEvent                = SubscriptionFailedEvent(safeId, testRequest, HttpStatusException(status, body))
        verify(testConnector.ac, times(2)).sendExtendedEvent(any())(any(), any())
        val capturedEvent: ExtendedDataEvent = captor.getValue
        capturedEvent.auditSource mustEqual subscriptionEvent.auditSource
        capturedEvent.auditType mustEqual subscriptionEvent.auditType
        capturedEvent.detail mustEqual subscriptionEvent.detail
      }
    }

    "return a failed future (exception)" in {

      val auditResult = AuditResult.fromHandlerResult(HandlerResult.Success)

      val captor = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])

      when {
        testConnector.httpClientV2.post(url"$url")
      } thenReturn mockRequestBuilder
      when(
        mockRequestBuilder.setHeader(
          ("Authorization", "token"),
          ("Environment", "ist0"),
          ("Accept", "application/json"),
          ("Content-Type", "application/json;charset=utf-8")
        )
      ).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(testRequest)))(any(), any(), any()))
        .thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.failed(new Exception("message")))

      when {
        testConnector.ac.sendExtendedEvent(captor.capture())(any(), any())
      } thenReturn Future.successful(auditResult)

      whenReady(testConnector.subscribe(safeId, testRequest).failed) { case HttpStatusException(status, body) =>
        status mustEqual INTERNAL_SERVER_ERROR
        body mustEqual Some("message")
        val subscriptionEvent                = SubscriptionFailedEvent(safeId, testRequest, HttpStatusException(status, body))
        verify(testConnector.ac, times(maxRetries)).sendExtendedEvent(any())(any(), any())
        val capturedEvent: ExtendedDataEvent = captor.getValue
        capturedEvent.auditSource mustEqual subscriptionEvent.auditSource
        capturedEvent.auditType mustEqual subscriptionEvent.auditType
        capturedEvent.detail mustEqual subscriptionEvent.detail
      }
    }

    "return a failed future (BAD_REQUEST)" in {

      val auditResult = AuditResult.fromHandlerResult(HandlerResult.Success)

      val captor = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])

      when {
        testConnector.httpClientV2.post(url"$url")
      } thenReturn mockRequestBuilder
      when(
        mockRequestBuilder.setHeader(
          ("Authorization", "token"),
          ("Environment", "ist0"),
          ("Accept", "application/json"),
          ("Content-Type", "application/json;charset=utf-8")
        )
      ).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.withBody(ArgumentMatchers.eq(Json.toJson(testRequest)))(any(), any(), any()))
        .thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any()))
        .thenReturn(Future.failed(HttpStatusException(BAD_REQUEST, Some("error message"))))

      when {
        testConnector.ac.sendExtendedEvent(captor.capture())(any(), any())
      } thenReturn Future.successful(auditResult)

      whenReady(testConnector.subscribe(safeId, testRequest).failed) { case HttpStatusException(status, body) =>
        status mustEqual BAD_REQUEST
        body mustBe Some("error message")
        val subscriptionEvent                = SubscriptionFailedEvent(safeId, testRequest, HttpStatusException(status, body))
        verify(testConnector.ac, times(1)).sendExtendedEvent(any())(any(), any())
        val capturedEvent: ExtendedDataEvent = captor.getValue
        capturedEvent.auditSource mustEqual subscriptionEvent.auditSource
        capturedEvent.auditType mustEqual subscriptionEvent.auditType
        capturedEvent.detail mustEqual subscriptionEvent.detail
      }
    }
  }

  def testRequest = Json
    .parse("""{
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
      "dateChangeFlag":false,
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
          "dateChangeFlag":false,
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
        "dateChangeFlag":false,
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
      "dateChangeFlag":false,
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
          "dateChangeFlag":false,
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
    .as[SubscriptionRequest]

}
