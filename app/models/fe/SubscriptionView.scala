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

package models.fe

import models.fe.amp.Amp
import models.fe.asp.Asp
import models.fe.bankdetails.BankDetails
import models.fe.businessactivities.BusinessActivities
import models.fe.businessdetails.BusinessDetails
import models.fe.businessmatching.BusinessMatching
import models.fe.declaration.AddPerson
import models.fe.eab.Eab
import models.fe.hvd.Hvd
import models.fe.moneyservicebusiness.MoneyServiceBusiness
import models.fe.responsiblepeople.ResponsiblePeople
import models.fe.supervision.Supervision
import models.fe.tcsp.Tcsp
import models.fe.tradingpremises.TradingPremises
import play.api.libs.json.{Json, OFormat}

case class SubscriptionView(
  etmpFormBundleNumber: String,
  businessMatchingSection: BusinessMatching,
  eabSection: Option[Eab],
  tradingPremisesSection: Option[Seq[TradingPremises]],
  businessDetailsSection: BusinessDetails,
  bankDetailsSection: Seq[BankDetails],
  aboutYouSection: AddPerson,
  businessActivitiesSection: BusinessActivities,
  responsiblePeopleSection: Option[Seq[ResponsiblePeople]],
  tcspSection: Option[Tcsp],
  aspSection: Option[Asp],
  msbSection: Option[MoneyServiceBusiness],
  hvdSection: Option[Hvd],
  ampSection: Option[Amp],
  supervisionSection: Option[Supervision]
)

object SubscriptionView {

  implicit val format: OFormat[Outgoing] = Json.format[SubscriptionView]

  final type Outgoing = SubscriptionView
  final type Incoming = models.des.SubscriptionView

  implicit def convert(desView: Incoming): SubscriptionView =
    SubscriptionView(
      etmpFormBundleNumber = desView.etmpFormBundleNumber,
      businessMatchingSection = desView,
      eabSection = desView,
      tradingPremisesSection = desView.tradingPremises,
      businessDetailsSection = desView,
      bankDetailsSection = desView.bankAccountDetails,
      aboutYouSection = desView.extraFields.filingIndividual,
      businessActivitiesSection = BusinessActivities.convertBusinessActivities(
        desView.businessActivities.all,
        desView.businessActivities.mlrActivitiesAppliedFor
      ),
      responsiblePeopleSection = desView.responsiblePersons,
      tcspSection = desView,
      aspSection = desView,
      msbSection = desView,
      hvdSection = desView,
      ampSection = desView,
      supervisionSection =
        Supervision.convertFrom(desView.aspOrTcsp, desView.businessActivities.mlrActivitiesAppliedFor)
    )
}
