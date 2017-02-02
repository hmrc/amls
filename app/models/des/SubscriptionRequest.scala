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

package models.des

import models.des.aboutthebusiness.{CorporationTaxRegisteredCbUbLlp, BusinessContactDetails, PreviouslyRegisteredMLR, VATRegistration}
import models.des.aboutyou.Aboutyou
import models.des.asp.Asp
import models.des.bankdetails.BankDetails
import models.des.businessactivities.BusinessActivities
import models.des.businessdetails.BusinessDetails
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy}
import models.des.hvd.Hvd
import models.des.msb.MoneyServiceBusiness
import models.des.responsiblepeople.ResponsiblePersons
import models.des.supervision.AspOrTcsp
import models.des.tcsp.{TcspTrustCompFormationAgt, TcspAll}
import models.fe
import models.des.tradingpremises.TradingPremises
import play.api.libs.json.Json
import utils.AckRefGenerator

case class SubscriptionRequest(
                                acknowledgementReference: String,
                                businessDetails: BusinessDetails,
                                businessContactDetails : BusinessContactDetails,
                                businessReferencesAll : Option[PreviouslyRegisteredMLR],
                                businessReferencesAllButSp: Option[VATRegistration],
                                businessReferencesCbUbLlp: Option[CorporationTaxRegisteredCbUbLlp],
                                businessActivities : BusinessActivities,
                                tradingPremises: TradingPremises,
                                bankAccountDetails : BankDetails,
                                msb: Option[MoneyServiceBusiness],
                                hvd: Option[Hvd],
                                asp: Option[Asp],
                                aspOrTcsp: Option[AspOrTcsp],
                                tcspAll: Option[TcspAll],
                                tcspTrustCompFormationAgt: Option[TcspTrustCompFormationAgt],
                                eabAll : Option[EabAll],
                                eabResdEstAgncy : Option[EabResdEstAgncy],
                                responsiblePersons: Option[Seq[ResponsiblePersons]],
                                filingIndividual: Aboutyou,
                                declaration: Declaration
                              )

object SubscriptionRequest {

  final type Outgoing = SubscriptionRequest
  final type Incoming = fe.SubscriptionRequest

  implicit val format = Json.format[SubscriptionRequest]

  // scalastyle:off
  implicit def convert(data: Incoming)(implicit
     gen: AckRefGenerator,
     conv: (Incoming, Boolean) => BusinessActivities,
     conv2 : fe.estateagentbusiness.EstateAgentBusiness => EabAll,
     conv3 : fe.estateagentbusiness.EstateAgentBusiness => EabResdEstAgncy,
     prevRegMLR : fe.aboutthebusiness.AboutTheBusiness => Option[PreviouslyRegisteredMLR],
     vatABConv : fe.aboutthebusiness.AboutTheBusiness => Option[VATRegistration],
     contactABConv : fe.aboutthebusiness.AboutTheBusiness => BusinessContactDetails,
     conv4 : Seq[fe.bankdetails.BankDetails] => BankDetails,
     tpConv : Seq[fe.tradingpremises.TradingPremises] => TradingPremises,
     aboutyouConv: fe.declaration.AddPerson => Aboutyou,
     aspConv : fe.asp.Asp => Asp,
     aspOrTcspConv : fe.supervision.Supervision => AspOrTcsp,
     tcspAllConv: fe.tcsp.Tcsp => TcspAll,
     tcspTrustCompConv: fe.tcsp.Tcsp => TcspTrustCompFormationAgt,
     responsiblePeopleConv: (Option[Seq[fe.responsiblepeople.ResponsiblePeople]], fe.businessmatching.BusinessMatching) => Option[Seq[ResponsiblePersons]],
     msbConv : (Option[fe.moneyservicebusiness.MoneyServiceBusiness], fe.businessmatching.BusinessMatching, Boolean) => Option[MoneyServiceBusiness],
     hvdConv : fe.hvd.Hvd => Hvd
  ): Outgoing =
    SubscriptionRequest(
      acknowledgementReference = gen.ackRef,
      businessDetails = data.businessMatchingSection,
      businessContactDetails = data.aboutTheBusinessSection,
      businessReferencesAll = data.aboutTheBusinessSection,
      businessReferencesAllButSp = data.aboutTheBusinessSection,
      businessReferencesCbUbLlp = data.aboutTheBusinessSection,
      businessActivities = conv(data, false),
      tradingPremises = data.tradingPremisesSection,
      bankAccountDetails = data.bankDetailsSection,
      msb = msbConv(data.msbSection, data.businessMatchingSection, false),
      hvd= data.hvdSection.map(hvdConv),
      asp = data.aspSection.map(aspConv),
      aspOrTcsp = data.supervisionSection.map(aspOrTcspConv),
      tcspAll = data.tcspSection.map(tcspAllConv),
      tcspTrustCompFormationAgt = data.tcspSection.map(tcspTrustCompConv),
      eabAll = data.eabSection.map(conv2),
      eabResdEstAgncy = data.eabSection.map(conv3),
      responsiblePersons = responsiblePeopleConv(data.responsiblePeopleSection, data.businessMatchingSection),
      filingIndividual = data.aboutYouSection,
      declaration = Declaration(true)
    )
}
