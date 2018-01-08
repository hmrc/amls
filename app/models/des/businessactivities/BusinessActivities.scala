/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.libs.json.Json

case class BusinessActivities(
                               mlrActivitiesAppliedFor: Option[MlrActivitiesAppliedFor] = None,
                               msbServicesCarriedOut: Option[MsbServicesCarriedOut] = None,
                               hvdGoodsSold: Option[HvdGoodsSold] = None,
                               hvdAlcoholTobacco: Option[HvdAlcoholTobacco] = None,
                               aspServicesOffered: Option[AspServicesOffered] = None,
                               tcspServicesOffered: Option[TcspServicesOffered] = None,
                               tcspServicesforRegOffBusinessAddrVirtualOff: Option[ServicesforRegOff] = None,
                               eabServicesCarriedOut: Option[EabServices] = None,
                               all: Option[BusinessActivitiesAll] = None)

object BusinessActivities {
  implicit val format = Json.format[BusinessActivities]

  implicit def conv(feModel: fe.SubscriptionRequest, amendVariation: Boolean = false): BusinessActivities = {

    BusinessActivities(
      feModel.businessMatchingSection,
      feModel.businessMatchingSection,
      HvdGoodsSold.conv(feModel.hvdSection),
      feModel.hvdSection,
      feModel.aspSection,
      feModel.tcspSection,
      feModel.tcspSection,
      feModel.eabSection,
      BusinessActivitiesAll.convtoActivitiesALL(feModel, amendVariation)
    )
  }
}
