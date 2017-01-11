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

package models.des.businessActivities

import models.des.aboutthebusiness.Address
import models.des.businessactivities._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class BusinessActivitiesAllSpec extends PlaySpec {

  "All Business Activities" should {
    "be serialisable from business activities" in{

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
      val mlrAdvisor = MlrAdvisor(true, Some(MlrAdvisorDetails(Some(advisorNameAddress), true, None)))

      val model = BusinessActivitiesAll(Some("2016-05-25"), None, Some(true), activityDetails, franchiseDetails, noOfEmployees, noOfEmployeesForMlr,
        nonUkResidentCustDetails, auditableRecordsDetails, suspiciousActivityGuidance, nationalCrimeAgencyRegistered,
        formalRiskAssessmentDetails, mlrAdvisor)

      BusinessActivitiesAll.format.writes(model) must be(Json.obj(
        "busActivitiesChangeDate" ->"2016-05-25","DateChangeFlag" ->true,
        "businessActivityDetails" -> Json.obj("actvtsBusRegForOnlyActvtsCarOut" -> true,
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
                "agentDealsWithHmrc"->true))))
    }
  }
}
