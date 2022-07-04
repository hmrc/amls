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

package models.des.businessActivities

import models.des.DesConstants
import models.des.aboutthebusiness.Address
import models.des.businessactivities._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class BusinessActivitiesSpec extends PlaySpec {

  "BusinessActivities" must {

    val eabServiceModel = Some(EabServices(true,false,
      false,false,false,false,false,false,false))
    val  mlrActivities = Some(MlrActivitiesAppliedFor(false, false, false, false, false,false, false, false))
    val tcspService = Some(TcspServicesOffered(true,false, true, true, true))
    val servicesforRegOff = Some(ServicesforRegOff(true, true, true, false, false, false, false, false))
    val ampServiceModel = Some(AmpServices(true, true, true, true, AmpServicesOther(true, Some("Another service"))))

    val model = BusinessActivities(mlrActivities, None, None, None, None, tcspService, servicesforRegOff, eabServiceModel, ampServiceModel)

    "serialise business activities model for eabServicesCarriedOut " in {
      BusinessActivities.format.writes(model) must be(Json.obj(
        "mlrActivitiesAppliedFor" -> Json.obj(
          "msb" -> false,
          "hvd" -> false,
          "asp" -> false,
          "tcsp" -> false,
          "eab" -> false,
          "bpsp" -> false,
          "tditpsp" ->false,
          "amp" -> false
        ),
	  "tcspServicesOffered" -> Json.obj("nomineeShareholders" -> true,
          "trusteeProvider" -> false,
          "regOffBusinessAddrVirtualOff" -> true,
          "compDirSecPartnerProvider" ->true,
          "trustOrCompFormAgent" ->true),
          "tcspServicesforRegOffBusinessAddrVirtualOff" -> Json.obj("callHandling"->true,
          "emailHandling" ->true,
          "emailServer" ->true,
          "selfCollectMailboxes" ->false,
          "mailForwarding" ->false,
          "receptionist" ->false,
          "conferenceRooms" ->false,
          "other" ->false),
        "eabServicesCarriedOut" -> Json.obj(
        "residentialEstateAgency" -> true,
          "commercialEstateAgency" ->false,
          "auctioneer" ->false,
          "relocationAgent" ->false,
          "businessTransferAgent" ->false,
          "assetManagementCompany" ->false,
          "landManagementAgent" ->false,
          "developmentCompany" ->false,
          "socialHousingProvider" ->false),
        "ampServicesCarriedOut" -> Json.obj(
          "artGallery" -> true,
          "auctionHouse" -> true,
          "privateDealer" -> true,
          "intermediary" -> true,
          "other" -> Json.obj(
            "otherAnswer" -> true,
            "specifyOther" -> "Another service")
        )
       )
      )
    }

    val activityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("100"))))
    val franchiseDetails = Some(FranchiseDetails(true, Some(Seq("Name1","Name2"))))
    val noOfEmployees = Some("10")
    val noOfEmployeesForMlr = Some("5")
    val nonUkResidentCustDetails = NonUkResidentCustDetails(false)
    val auditableRecordsDetails = AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true)))
    val suspiciousActivityGuidance = true
    val nationalCrimeAgencyRegistered = true
    val formalRiskAssessmentDetails = Some(FormalRiskAssessmentDetails(true, Some(RiskAssessmentFormat(true))))
    val advisorNameAddress = AdvisorNameAddress("Name", Some("TradingName"), Address("Line1", "Line2", Some("Line3"), Some("Line4"),"GB", None))
    val mlrAdvisor = Some(MlrAdvisor(true, Some(MlrAdvisorDetails(Some(advisorNameAddress), true, None))))
    val activitiesModel = BusinessActivitiesAll(None, None,false, activityDetails, franchiseDetails, noOfEmployees, noOfEmployeesForMlr,
      nonUkResidentCustDetails, auditableRecordsDetails, suspiciousActivityGuidance, nationalCrimeAgencyRegistered,
      formalRiskAssessmentDetails, mlrAdvisor)

    val tcspService1 = Some(TcspServicesOffered(true,false, false, true, true))
    val servicesforRegOff1 = Some(ServicesforRegOff(false, false, false, false, false, false, false, false))
    val bmMlrActivities = Some(MlrActivitiesAppliedFor(true, false, false, true, false, false, true, false))

    val aspModel = Some(AspServicesOffered(true, false,false,true,true))

    val hvdGoodsSold = Some(HvdGoodsSold(true, true,false,false,
      false,false,false,false,false,false,false,true, Some("Details"), Some(HowGoodsAreSold(true,true,true))))
    val hvdAlcoholTobacco = Some(HvdAlcoholTobacco(true))

    val allmodel = BusinessActivities(bmMlrActivities, None, hvdGoodsSold,
      hvdAlcoholTobacco, aspModel, tcspService1, servicesforRegOff1, eabServiceModel, ampServiceModel, Some(activitiesModel))

    "serialise business activities model for all" in {
      BusinessActivities.format.writes(allmodel) must be(Json.obj(
        "mlrActivitiesAppliedFor" -> Json.obj(
          "msb" -> true,
          "hvd" -> false,
          "asp" -> false,
          "tcsp" -> true,
          "eab" -> false,
          "bpsp" -> false,
          "tditpsp" ->true,
          "amp" -> false
        ),
        "hvdGoodsSold" -> Json.obj("alcohol" -> true,
          "tobacco" ->true,
          "antiques" ->false,
          "cars" ->false,
          "otherMotorVehicles" ->false,
          "caravans" ->false,
          "jewellery" ->false,
          "gold" ->false,
          "scrapMetals" ->false,
          "mobilePhones" ->false,
          "clothing" ->false,
          "other" ->true,
          "specifyOther" ->"Details",
          "howGoodsAreSold" -> Json.obj("retail" ->true,
          "wholesale" ->true,
          "auction" ->true)),
        "hvdAlcoholTobacco" ->Json.obj("dutySuspExAtGoods" -> true),
        "aspServicesOffered" -> Json.obj(
        "accountant" -> true,
          "payrollServiceProvider" -> false,
          "bookKeeper" -> false,
          "auditor" -> true,
          "financialOrTaxAdvisor" -> true
        ),
        "tcspServicesOffered" -> Json.obj("nomineeShareholders" -> true,
          "trusteeProvider" -> false,
          "regOffBusinessAddrVirtualOff" -> false,
          "compDirSecPartnerProvider" ->true,
          "trustOrCompFormAgent" ->true),
        "tcspServicesforRegOffBusinessAddrVirtualOff" -> Json.obj("callHandling"->false,
          "emailHandling" ->false,
          "emailServer" ->false,
          "selfCollectMailboxes" ->false,
          "mailForwarding" ->false,
          "receptionist" ->false,
          "conferenceRooms" ->false,
          "other" ->false),
        "eabServicesCarriedOut" -> Json.obj(
              "residentialEstateAgency" -> true,
              "commercialEstateAgency" -> false,
              "auctioneer" -> false,
              "relocationAgent" -> false,
              "businessTransferAgent" -> false,
              "assetManagementCompany" -> false,
              "landManagementAgent" -> false,
              "developmentCompany" -> false,
              "socialHousingProvider" -> false),
        "ampServicesCarriedOut" -> Json.obj(
          "artGallery" -> true,
          "auctionHouse" -> true,
          "privateDealer" -> true,
          "intermediary" -> true,
          "other" -> Json.obj(
            "otherAnswer" -> true,
            "specifyOther" -> "Another service")
        ),
        "all" -> Json.obj("businessActivityDetails" -> Json.obj("actvtsBusRegForOnlyActvtsCarOut" -> true,
          "respActvtsBusRegForOnlyActvtsCarOut" -> Json.obj("mlrActivityTurnover" -> "100")),
          "franchiseDetails"->
            Json.obj("isBusinessAFranchise"->true,
              "franchiserName"->Json.arr("Name1","Name2")),
          "noOfEmployees"->"10",
          "noOfEmployeesForMlr"->"5",
          "nonUkResidentCustDetails"->Json.obj("nonUkResidentCustomers"->false),
          "auditableRecordsDetails"->Json.obj("detailedRecordsKept"->"Yes",
            "transactionRecordingMethod"->Json.obj("manual"->true,"spreadsheet"->false,"commercialPackage"->false)),
          "suspiciousActivityGuidance"->true,
          "nationalCrimeAgencyRegistered"->true,
          "formalRiskAssessmentDetails"->Json.obj("formalRiskAssessment"->true,
            "riskAssessmentFormat"->Json.obj("electronicFormat"->true,"manualFormat"->false)),
          "mlrAdvisor"->Json.obj("doYouHaveMlrAdvisor"->true,
            "mlrAdvisorDetails"->Json.obj(
              "advisorNameAddress"->Json.obj("name"->"Name",
                "tradingName"->"TradingName",
                "address"->Json.obj("addressLine1"->"Line1","addressLine2"->"Line2",
                  "addressLine3"->"Line3","addressLine4"->"Line4","country"->"GB")),
              "agentDealsWithHmrc"->true))
      )))
    }

    "successfully compare api5 model with api6 model" in {
      allmodel.equals(DesConstants.testBusinessActivities) must be(false)
      allmodel.equals(allmodel) must be(true)
    }

    "successfully compare api5 model with api6 model1" in {
      val viewModel = BusinessActivities(bmMlrActivities, None, hvdGoodsSold,
        hvdAlcoholTobacco, aspModel, tcspService1, servicesforRegOff1, eabServiceModel, None)

      allmodel.equals(viewModel) must be(false)
    }


    "successfully compare api5 model with api6 model2" in {

      val viewTcspService = Some(TcspServicesOffered(true,false, false, false, true))
      val viewModel = BusinessActivities(bmMlrActivities, None, hvdGoodsSold,
        hvdAlcoholTobacco, aspModel, viewTcspService, servicesforRegOff1, eabServiceModel, ampServiceModel, Some(activitiesModel))

      allmodel.equals(viewModel) must be(false)
    }
  }
}
