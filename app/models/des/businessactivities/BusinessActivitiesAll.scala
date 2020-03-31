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

package models.des.businessactivities

import models.fe
import models.fe.SubscriptionRequest
import models.fe.businessdetails.ActivityStartDate
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import play.api.libs.json.Json

case class BusinessActivitiesAll(
                                  busActivitiesChangeDate:Option[String],
                                  activitiesCommenceDate: Option[String],
                                  dateChangeFlag: Option[Boolean],
                                  businessActivityDetails: BusinessActivityDetails,
                                  franchiseDetails: Option[FranchiseDetails],
                                  noOfEmployees: Option[String],
                                  noOfEmployeesForMlr: Option[String],
                                  nonUkResidentCustDetails: NonUkResidentCustDetails,
                                  auditableRecordsDetails: AuditableRecordsDetails,
                                  suspiciousActivityGuidance: Boolean,
                                  nationalCrimeAgencyRegistered: Boolean,
                                  formalRiskAssessmentDetails: Option[FormalRiskAssessmentDetails],
                                  mlrAdvisor: Option[MlrAdvisor])

object BusinessActivitiesAll{

  implicit val format = Json.format[BusinessActivitiesAll]

  def getEarliestDate(aspSection: Option[models.fe.asp.Asp],
                      eabSection: Option[models.fe.eab.Eab],
                      hvdSection: Option[models.fe.hvd.Hvd],
                      businessMatchingSection: models.fe.businessmatching.BusinessMatching):Option[String] = {
    implicit def ord: Ordering[DateTime] = Ordering.by(_.getMillis)

    val aspDate = aspSection.fold[Option[String]](None)(_.services.fold[Option[String]](None)(_.dateOfChange))
    val eabDate = eabSection.fold[Option[String]](None)(_.data.dateOfChange)
    val hvdDate = hvdSection.fold[Option[String]](None)(_.dateOfChange)
    val baDate =  businessMatchingSection.activities.dateOfChange
    val dateLst = Seq(aspDate, eabDate, hvdDate, baDate).flatten

    dateLst.map(x => DateTime.parse(x)).sorted(ord).headOption.map(_.toString("yyyy-MM-dd"))
  }

  implicit def convtoActivitiesALL(feModel: fe.SubscriptionRequest): Option[BusinessActivitiesAll] = {
      convert(
        feModel.businessDetailsSection,
        feModel.businessActivitiesSection,
        getEarliestDate(
          feModel.aspSection,
          feModel.eabSection,
          feModel.hvdSection,
          feModel.businessMatchingSection
        )
      )
  }

  def convert(atb:models.fe.businessdetails.BusinessDetails,
              activities: models.fe.businessactivities.BusinessActivities,
              dateOfChange: Option[String]): Option[BusinessActivitiesAll] = {

    Some(BusinessActivitiesAll(
      dateOfChange,
      atb.activityStartDate,
      None,
      BusinessActivityDetails.convert(activities),
      activities.businessFranchise,
      employeeCount(activities.howManyEmployees),
      mlremployeeCount(activities.howManyEmployees),
      activities.customersOutsideUK,
      AuditableRecordsDetails.convert(activities),
      activities.identifySuspiciousActivity,
      activities.ncaRegistered,
      activities.riskAssessmentPolicy,
      activities))
  }

  implicit def convStartDate(startDate: Option[ActivityStartDate]): Option[String] = {
    startDate match {
      case Some(data) => Some(data.startDate.toString)
      case _ => None
    }
  }

  def employeeCount(empCount:Option[models.fe.businessactivities.HowManyEmployees]): Option[String] ={
    empCount match {
      case None => None
      case Some(x) => Some(x.employeeCount)
    }
  }
  def mlremployeeCount(empCount:Option[models.fe.businessactivities.HowManyEmployees]): Option[String] ={
    empCount match {
      case None => None
      case Some(x) => Some(x.employeeCountAMLSSupervision)
    }
  }

  implicit def suspicious(susAct:Option[models.fe.businessactivities.IdentifySuspiciousActivity]): Boolean ={
    susAct match{
      case Some(x) => x.hasWrittenGuidance
      case _ => false

    }
  }

  implicit def ncaRegistered(criminalReg:Option[models.fe.businessactivities.NCARegistered]): Boolean ={
    criminalReg match{
      case Some(x) => x.ncaRegistered
      case _ => false
    }
  }

  implicit def convtoActivitiesALLChangeFlags(businessDetailsSection: models.fe.businessdetails.BusinessDetails,
                                              businessActivitiesSection: models.fe.businessactivities.BusinessActivities,
                                              aspSection: Option[models.fe.asp.Asp],
                                              eabSection: Option[models.fe.eab.Eab],
                                              hvdSection: Option[models.fe.hvd.Hvd],
                                              businessMatchingSection: models.fe.businessmatching.BusinessMatching): Option[BusinessActivitiesAll] = {
    convert(
      businessDetailsSection,
      businessActivitiesSection,
      getEarliestDate(
        aspSection,
        eabSection,
        hvdSection,
        businessMatchingSection
      )
    )
  }

}
