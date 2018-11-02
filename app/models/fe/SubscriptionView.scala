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

package models.fe

import models.fe.aboutthebusiness.AboutTheBusiness
import models.fe.asp.Asp
import models.fe.bankdetails.BankDetails
import models.fe.businessactivities.BusinessActivities
import models.fe.businessmatching.{BusinessActivity, BusinessMatching, TrustAndCompanyServices, MoneyServiceBusiness => MSBActivity}
import models.fe.declaration.AddPerson
import models.fe.estateagentbusiness.EstateAgentBusiness
import models.fe.hvd.Hvd
import models.fe.responsiblepeople.ResponsiblePeople
import models.fe.supervision.Supervision
import models.fe.tcsp.Tcsp
import models.fe.tradingpremises.TradingPremises
import models.des.{SubscriptionView => DesSubscriptionView}
import models.fe.moneyservicebusiness.MoneyServiceBusiness
import play.api.libs.json.Json

case class SubscriptionView(
                             etmpFormBundleNumber: String,
                             businessMatchingSection: BusinessMatching,
                             eabSection: Option[EstateAgentBusiness],
                             tradingPremisesSection: Option[Seq[TradingPremises]],
                             aboutTheBusinessSection: AboutTheBusiness,
                             bankDetailsSection: Seq[BankDetails],
                             aboutYouSection: AddPerson,
                             businessActivitiesSection: BusinessActivities,
                             responsiblePeopleSection: Option[Seq[ResponsiblePeople]],
                             tcspSection: Option[Tcsp],
                             aspSection: Option[Asp],
                             msbSection: Option[MoneyServiceBusiness],
                             hvdSection: Option[Hvd],
                             supervisionSection: Option[Supervision]
                           )


object SubscriptionView {

  implicit val format = Json.format[SubscriptionView]

  final type Outgoing = SubscriptionView
  final type Incoming = models.des.SubscriptionView

  implicit def convert(desView: Incoming): SubscriptionView = {
    SubscriptionView(
      etmpFormBundleNumber = desView.etmpFormBundleNumber,
      businessMatchingSection = desView,
      eabSection = desView,
      tradingPremisesSection = desView.tradingPremises,
      aboutTheBusinessSection = desView,
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
      supervisionSection = Supervision.convertFrom(desView.aspOrTcsp,
        desView.businessActivities.mlrActivitiesAppliedFor)
    )
  }
}
