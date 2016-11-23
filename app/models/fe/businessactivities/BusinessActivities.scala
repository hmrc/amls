/*
 * Copyright 2016 HM Revenue & Customs
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

import models.des.businessactivities.BusinessActivitiesAll

case class BusinessActivities(
                               involvedInOther: Option[InvolvedInOther] = None,
                               expectedBusinessTurnover: Option[ExpectedBusinessTurnover] = None,
                               expectedAMLSTurnover: Option[ExpectedAMLSTurnover] = None,
                               businessFranchise: Option[BusinessFranchise] = None,
                               transactionRecord: Option[TransactionRecord] = None,
                               customersOutsideUK: Option[CustomersOutsideUK] = None,
                               ncaRegistered: Option[NCARegistered] = None,
                               accountantForAMLSRegulations: Option[AccountantForAMLSRegulations] = None,
                               identifySuspiciousActivity: Option[IdentifySuspiciousActivity] = None,
                               riskAssessmentPolicy: Option[RiskAssessmentPolicy] = None,
                               howManyEmployees: Option[HowManyEmployees] = None,
                               whoIsYourAccountant: Option[WhoIsYourAccountant] = None,
                               taxMatters: Option[TaxMatters] = None)

object BusinessActivities {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val reads: Reads[BusinessActivities] = (
    __.readNullable[InvolvedInOther] and
      __.readNullable[ExpectedBusinessTurnover] and
      __.readNullable[ExpectedAMLSTurnover] and
      __.readNullable[BusinessFranchise] and
      __.readNullable[TransactionRecord] and
      __.readNullable[CustomersOutsideUK] and
      __.readNullable[NCARegistered] and
      __.readNullable[AccountantForAMLSRegulations] and
      __.readNullable[IdentifySuspiciousActivity] and
      __.readNullable[RiskAssessmentPolicy] and
      __.readNullable[HowManyEmployees] and
      __.readNullable[WhoIsYourAccountant] and
      __.readNullable[TaxMatters]
    ) (BusinessActivities.apply _)

  implicit val writes: Writes[BusinessActivities] = Writes[BusinessActivities] {
    model =>
      Seq(
        Json.toJson(model.involvedInOther).asOpt[JsObject],
        Json.toJson(model.expectedBusinessTurnover).asOpt[JsObject],
        Json.toJson(model.expectedAMLSTurnover).asOpt[JsObject],
        Json.toJson(model.businessFranchise).asOpt[JsObject],
        Json.toJson(model.transactionRecord).asOpt[JsObject],
        Json.toJson(model.customersOutsideUK).asOpt[JsObject],
        Json.toJson(model.ncaRegistered).asOpt[JsObject],
        Json.toJson(model.accountantForAMLSRegulations).asOpt[JsObject],
        Json.toJson(model.identifySuspiciousActivity).asOpt[JsObject],
        Json.toJson(model.riskAssessmentPolicy).asOpt[JsObject],
        Json.toJson(model.howManyEmployees).asOpt[JsObject],
        Json.toJson(model.whoIsYourAccountant).asOpt[JsObject],
        Json.toJson(model.taxMatters).asOpt[JsObject]
      ).flatten.fold(Json.obj()) {
        _ ++ _
      }
  }

  implicit def conv(desBA: Option[BusinessActivitiesAll]): BusinessActivities = {
    BusinessActivities(involvedInOther = desBA.fold[Option[InvolvedInOther]](None)(x => x.businessActivityDetails),
      expectedBusinessTurnover = desBA.fold[Option[ExpectedBusinessTurnover]](None)(x =>x.businessActivityDetails),
      expectedAMLSTurnover = desBA.fold[Option[ExpectedAMLSTurnover]](None)(x=> x.businessActivityDetails),
      businessFranchise = desBA.fold[Option[BusinessFranchise]](None)(x => x.franchiseDetails),
      transactionRecord = desBA.fold[Option[TransactionRecord]](None)(x => x),
      customersOutsideUK = desBA.fold[Option[CustomersOutsideUK]](None)(x => x),
      ncaRegistered = desBA.fold[Option[NCARegistered]](None)(x => Some(NCARegistered(x.nationalCrimeAgencyRegistered))),
      accountantForAMLSRegulations = desBA.fold[Option[AccountantForAMLSRegulations]](None)(x =>
        Some(AccountantForAMLSRegulations(x.mlrAdvisor.doYouHaveMlrAdvisor))),
      identifySuspiciousActivity = desBA.fold[Option[IdentifySuspiciousActivity]](None)(x =>
        Some(IdentifySuspiciousActivity(x.suspiciousActivityGuidance))),
      riskAssessmentPolicy = desBA.fold[Option[RiskAssessmentPolicy]](None)(x => x.formalRiskAssessmentDetails),
      howManyEmployees = desBA.fold[Option[HowManyEmployees]](None)(x => x),
      whoIsYourAccountant = desBA.fold[Option[WhoIsYourAccountant]](None)(x => x.mlrAdvisor),
      taxMatters = desBA.fold[Option[TaxMatters]](None)(x => x.mlrAdvisor.mlrAdvisorDetails)
    )
  }

}
