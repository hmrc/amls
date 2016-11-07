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

package models.fe.aboutthebusiness

import models.des.SubscriptionView

case class AboutTheBusiness(
                             previouslyRegistered: PreviouslyRegistered,
                             activityStartDate: Option[ActivityStartDate] = None,
                             vatRegistered: Option[VATRegistered] = None,
                             corporationTaxRegistered: Option[CorporationTaxRegistered] = None,
                             contactingYou: ContactingYou,
                             registeredOffice: RegisteredOffice,
                             correspondenceAddress: Option[CorrespondenceAddress] = None
                           )

object AboutTheBusiness {

  import play.api.libs.json._

  implicit val format =  Json.format[AboutTheBusiness]

  implicit def conv(view: SubscriptionView): AboutTheBusiness = {
    val bcDetails = view.businessContactDetails
    val previouslyRegistered = view.businessReferencesAll
    AboutTheBusiness(previouslyRegistered,
      view.businessActivities.all,
      view.businessReferencesAllButSp,
      view.businessReferencesCbUbLlp,
      ContactingYou(bcDetails.businessTelNo, bcDetails.businessEmail),
      bcDetails.businessAddress, bcDetails.alternativeAddress)

  }

}
