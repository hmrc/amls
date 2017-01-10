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

package models.fe.businessmatching

import models.des.msb.MsbMtDetails
import models.fe.businesscustomer.ReviewDetails

case class BusinessMatching(
                             reviewDetails: ReviewDetails,
                             activities: BusinessActivities,
                             msbServices : Option[MsbServices] = None,
                             typeOfBusiness: Option[TypeOfBusiness] = None,
                             companyRegistrationNumber: Option[CompanyRegistrationNumber] = None,
                             businessAppliedForPSRNumber: Option[BusinessAppliedForPSRNumber] = None
                           )

object BusinessMatching {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val reads: Reads[BusinessMatching] = (
    __.read[ReviewDetails] and
      __.read[BusinessActivities] and
      __.read(Reads.optionNoError[MsbServices]) and
      __.read(Reads.optionNoError[TypeOfBusiness]) and
      __.read(Reads.optionNoError[CompanyRegistrationNumber]) and
      __.read(Reads.optionNoError[BusinessAppliedForPSRNumber])
    ) (BusinessMatching.apply _)

  implicit val writes: Writes[BusinessMatching] =
    Writes[BusinessMatching] {
      model =>
        Seq(
          Json.toJson(model.reviewDetails).asOpt[JsObject],
          Json.toJson(model.activities).asOpt[JsObject],
          Json.toJson(model.msbServices).asOpt[JsObject],
          Json.toJson(model.typeOfBusiness).asOpt[JsObject],
          Json.toJson(model.companyRegistrationNumber).asOpt[JsObject],
          Json.toJson(model.businessAppliedForPSRNumber).asOpt[JsObject]
        ).flatten.fold(Json.obj()) {
          _ ++ _
        }
    }


  implicit def conv(desView: models.des.SubscriptionView): BusinessMatching = {
    BusinessMatching(desView, desView.businessActivities.mlrActivitiesAppliedFor, desView.businessActivities.msbServicesCarriedOut,
      desView.businessDetails, desView.businessDetails, desView.msb.fold[Option[MsbMtDetails]](None)(x => x.msbMtDetails))
  }

}
