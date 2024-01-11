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

package models.des.businessactivities

import models.fe
import models.fe.businessdetails.ActivityStartDate
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

  implicit val format: OFormat[BusinessActivitiesAll] = Json.format[BusinessActivitiesAll]
  val jsonRead: Reads[BusinessActivitiesAll] = {
    (
      (__ \ "busActivitiesChangeDate").readNullable[String] and
        (__ \ "activitiesCommenceDate").readNullable[String] and
        ((__ \ "dateChangeFlag").read[Boolean] or Reads.pure(false)).map(x=>Some(x)) and
        (__ \ "businessActivityDetails").read[BusinessActivityDetails] and
        (__ \ "franchiseDetails").readNullable[FranchiseDetails] and
        (__ \ "noOfEmployees").readNullable[String] and
        (__ \ "noOfEmployeesForMlr").readNullable[String] and
        (__ \ "nonUkResidentCustDetails").read[NonUkResidentCustDetails] and
        (__ \ "auditableRecordsDetails").read[AuditableRecordsDetails] and
        (__ \ "suspiciousActivityGuidance").read[Boolean] and
        (__ \ "nationalCrimeAgencyRegistered").read[Boolean] and
        (__ \ "formalRiskAssessmentDetails").readNullable[FormalRiskAssessmentDetails] and
        (__ \ "mlrAdvisor").readNullable[MlrAdvisor]
      ) (BusinessActivitiesAll.apply _)
  }

  def getEarliestDate(aspSection: Option[models.fe.asp.Asp],
                      eabSection: Option[models.fe.eab.Eab],
                      hvdSection: Option[models.fe.hvd.Hvd],
                      businessMatchingSection: models.fe.businessmatching.BusinessMatching):Option[String] = {
    implicit def ord: Ordering[LocalDate] = Ordering.by(_.toEpochDay)

    val aspDate = aspSection.fold[Option[String]](None)(_.services.fold[Option[String]](None)(_.dateOfChange))
    val eabDate = eabSection.fold[Option[String]](None)(_.data.dateOfChange)
    val hvdDate = hvdSection.fold[Option[String]](None)(_.dateOfChange)
    val baDate =  businessMatchingSection.activities.dateOfChange
    val dateLst = Seq(aspDate, eabDate, hvdDate, baDate).flatten

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm:ss]")
    dateLst.map(x => LocalDate.parse(x, formatter)).sorted(ord).headOption.map(_.format(DateTimeFormatter.ISO_LOCAL_DATE))
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

  def convtoActivitiesALLWithFlag(feModel: fe.SubscriptionRequest): Option[BusinessActivitiesAll] = {
    convertWithFlag(
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

  def convertWithFlag(atb:models.fe.businessdetails.BusinessDetails,
              activities: models.fe.businessactivities.BusinessActivities,
              dateOfChange: Option[String]): Option[BusinessActivitiesAll] = {

    Some(BusinessActivitiesAll(
      dateOfChange,
      atb.activityStartDate,
      Some(false),
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
