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

package models.des

import models.des.aboutthebusiness.{BusinessContactDetails, CorporationTaxRegisteredCbUbLlp, PreviouslyRegisteredMLR, VATRegistration}
import models.des.aboutyou.AboutYouRelease7
import models.des.amp.Amp
import models.des.asp.Asp
import models.des.bankdetails.BankDetails
import models.des.businessactivities.BusinessActivities
import models.des.businessdetails.BusinessDetails
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy, LettingAgents}
import models.des.hvd.Hvd
import models.des.msb.MoneyServiceBusiness
import models.des.responsiblepeople.ResponsiblePersons
import models.des.supervision.AspOrTcsp
import models.des.tcsp.{TcspAll, TcspTrustCompFormationAgt}
import models.des.tradingpremises.TradingPremises
import models.fe
import play.api.libs.json._
import utils.AckRefGenerator

case class SubscriptionRequest(
                                acknowledgementReference: String,
                                businessDetails: BusinessDetails,
                                businessContactDetails: BusinessContactDetails,
                                businessReferencesAll: Option[PreviouslyRegisteredMLR],
                                businessReferencesAllButSp: Option[VATRegistration],
                                businessReferencesCbUbLlp: Option[CorporationTaxRegisteredCbUbLlp],
                                businessActivities: BusinessActivities,
                                tradingPremises: TradingPremises,
                                bankAccountDetails: BankDetails,
                                msb: Option[MoneyServiceBusiness],
                                hvd: Option[Hvd],
                                asp: Option[Asp],
                                aspOrTcsp: Option[AspOrTcsp],
                                tcspAll: Option[TcspAll],
                                tcspTrustCompFormationAgt: Option[TcspTrustCompFormationAgt],
                                eabAll: Option[EabAll],
                                eabResdEstAgncy: Option[EabResdEstAgncy],
                                amp: Option[Amp],
                                responsiblePersons: Option[Seq[ResponsiblePersons]],
                                filingIndividual: AboutYouRelease7,
                                declaration: Declaration,
                                lettingAgents: Option[LettingAgents]
                              )

object SubscriptionRequest {

  final type Outgoing = SubscriptionRequest
  final type Incoming = fe.SubscriptionRequest

  implicit def format =
    Json.format[SubscriptionRequest]

  // scalastyle:off
  implicit def convert(data: Incoming)(implicit
                                       gen: AckRefGenerator,
                                       conv: Incoming => BusinessActivities,
                                       conv2 : fe.eab.Eab => EabAll,
                                       prevRegMLR : fe.businessdetails.BusinessDetails => Option[PreviouslyRegisteredMLR],
                                       vatABConv : fe.businessdetails.BusinessDetails => Option[VATRegistration],
                                       contactABConv : fe.businessdetails.BusinessDetails => BusinessContactDetails,
                                       conv4 : Seq[fe.bankdetails.BankDetails] => BankDetails,
                                       aboutyouConv: fe.declaration.AddPerson => AboutYouRelease7,
                                       aspConv : Option[fe.asp.Asp] => Option[Asp],
                                       tcspAllConv: fe.tcsp.Tcsp => TcspAll,
                                       tcspTrustCompConv: fe.tcsp.Tcsp => TcspTrustCompFormationAgt,
                                       responsiblePeopleConv: (Option[Seq[fe.responsiblepeople.ResponsiblePeople]], fe.businessmatching.BusinessMatching, Boolean) => Option[Seq[ResponsiblePersons]],
                                       msbConv : (Option[fe.moneyservicebusiness.MoneyServiceBusiness], fe.businessmatching.BusinessMatching, Boolean) => Option[MoneyServiceBusiness],
                                       hvdConv : Option[fe.hvd.Hvd] => Option[Hvd],
                                       requestType: RequestType
  ): Outgoing =
    SubscriptionRequest(
      acknowledgementReference = gen.ackRef,
      businessDetails = data.businessMatchingSection,
      businessContactDetails = data.businessDetailsSection,
      businessReferencesAll = data.businessDetailsSection,
      businessReferencesAllButSp = data.businessDetailsSection,
      businessReferencesCbUbLlp = data.businessDetailsSection,
      businessActivities = conv(data),
      tradingPremises = data.tradingPremisesSection,
      bankAccountDetails = data.bankDetailsSection,
      msb = msbConv(data.msbSection, data.businessMatchingSection, false),
      hvd = data.hvdSection,
      asp = data.aspSection,
      aspOrTcsp = AspOrTcsp.conv1(data.supervisionSection),
      tcspAll = data.tcspSection.map(tcspAllConv),
      tcspTrustCompFormationAgt = data.tcspSection.map(tcspTrustCompConv),
      eabAll = data.eabSection.map(conv2),
      eabResdEstAgncy = data.eabSection,
      amp = data.ampSection,
      responsiblePersons = responsiblePeopleConv(data.responsiblePeopleSection, data.businessMatchingSection, false),
      filingIndividual = data.aboutYouSection,
      declaration = Declaration(true),
      lettingAgents = data.eabSection
    )
}
