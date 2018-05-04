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

package models.des

import config.AmlsConfig
import models.des.aboutthebusiness.{BusinessContactDetails, CorporationTaxRegisteredCbUbLlp, PreviouslyRegisteredMLR, VATRegistration}
import models.des.aboutyou.{AboutYouRelease7, Aboutyou}
import models.des.asp.Asp
import models.des.bankdetails.BankDetails
import models.des.businessactivities.BusinessActivities
import models.des.businessdetails.BusinessDetails
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy}
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
                                responsiblePersons: Option[Seq[ResponsiblePersons]],
                                filingIndividual: AboutYouRelease7,
                                declaration: Declaration
                              )

object SubscriptionRequest {

  final type Outgoing = SubscriptionRequest
  final type Incoming = fe.SubscriptionRequest

  implicit def format = if (AmlsConfig.release7) {
    Json.format[SubscriptionRequest]
  } else {
    val reads: Reads[SubscriptionRequest] = {
      import play.api.libs.functional.syntax._
      import play.api.libs.json.Reads._
      import play.api.libs.json._

      (
        (__ \ "acknowledgementReference").read[String] and
          (__ \ "businessDetails").read[BusinessDetails] and
          (__ \ "businessContactDetails").read[BusinessContactDetails] and
          (__ \ "businessReferencesAll").readNullable[PreviouslyRegisteredMLR] and
          (__ \ "businessReferencesAllButSp").readNullable[VATRegistration] and
          (__ \ "businessReferencesCbUbLlp").readNullable[CorporationTaxRegisteredCbUbLlp] and
          (__ \ "businessActivities").read[BusinessActivities] and
          (__ \ "tradingPremises").read[TradingPremises] and
          (__ \ "bankAccountDetails").read[BankDetails] and
          (__ \ "msb").readNullable[MoneyServiceBusiness] and
          (__ \ "hvd").readNullable[Hvd] and
          (__ \ "asp").readNullable[Asp] and
          (__ \ "aspOrTcsp").readNullable[AspOrTcsp] and
          (__ \ "tcspAll").readNullable[TcspAll] and
          (__ \ "tcspTrustCompFormationAgt").readNullable[TcspTrustCompFormationAgt] and
          (__ \ "eabAll").readNullable[EabAll] and
          (__ \ "eabResdEstAgncy").readNullable[EabResdEstAgncy] and
          (__ \ "responsiblePersons").readNullable[Seq[ResponsiblePersons]] and
          (__ \ "filingIndividual").read[Aboutyou].map{x:Aboutyou => AboutYouRelease7.convertToRelease7(x)} and
          (__ \ "declaration").read[Declaration]
        ) (SubscriptionRequest.apply _)
    }

    val aboutYouWrites = new Writes[AboutYouRelease7] {
      override def writes(o: AboutYouRelease7): JsValue = {
        Aboutyou.format.writes(Aboutyou.convertFromRelease7(o))
      }
    }

    val writes: Writes[SubscriptionRequest] = {
      import play.api.libs.functional.syntax._
      import play.api.libs.json._

      (
        (__ \ "acknowledgementReference").write[String] and
          (__ \ "businessDetails").write[BusinessDetails] and
          (__ \ "businessContactDetails").write[BusinessContactDetails] and
          (__ \ "businessReferencesAll").writeNullable[PreviouslyRegisteredMLR] and
          (__ \ "businessReferencesAllButSp").writeNullable[VATRegistration] and
          (__ \ "businessReferencesCbUbLlp").writeNullable[CorporationTaxRegisteredCbUbLlp] and
          (__ \ "businessActivities").write[BusinessActivities] and
          (__ \ "tradingPremises").write[TradingPremises] and
          (__ \ "bankAccountDetails").write[BankDetails] and
          (__ \ "msb").writeNullable[MoneyServiceBusiness] and
          (__ \ "hvd").writeNullable[Hvd] and
          (__ \ "asp").writeNullable[Asp] and
          (__ \ "aspOrTcsp").writeNullable[AspOrTcsp] and
          (__ \ "tcspAll").writeNullable[TcspAll] and
          (__ \ "tcspTrustCompFormationAgt").writeNullable[TcspTrustCompFormationAgt] and
          (__ \ "eabAll").writeNullable[EabAll] and
          (__ \ "eabResdEstAgncy").writeNullable[EabResdEstAgncy] and
          (__ \ "responsiblePersons").writeNullable[Seq[ResponsiblePersons]] and
          (__ \ "filingIndividual").write(aboutYouWrites) and
          (__ \ "declaration").write[Declaration]
        ) (unlift(SubscriptionRequest.unapply _))
    }

    Format(reads, writes)
  }

  // scalastyle:off
  implicit def convert(data: Incoming)(implicit
     gen: AckRefGenerator,
     conv: (Incoming, Boolean) => BusinessActivities,
     conv2 : fe.estateagentbusiness.EstateAgentBusiness => EabAll,
     prevRegMLR : fe.aboutthebusiness.AboutTheBusiness => Option[PreviouslyRegisteredMLR],
     vatABConv : fe.aboutthebusiness.AboutTheBusiness => Option[VATRegistration],
     contactABConv : fe.aboutthebusiness.AboutTheBusiness => BusinessContactDetails,
     conv4 : Seq[fe.bankdetails.BankDetails] => BankDetails,
     tpConv : Seq[fe.tradingpremises.TradingPremises] => TradingPremises,
     aboutyouConv: fe.declaration.AddPerson => AboutYouRelease7,
     aspConv : Option[fe.asp.Asp] => Option[Asp],
     tcspAllConv: fe.tcsp.Tcsp => TcspAll,
     tcspTrustCompConv: fe.tcsp.Tcsp => TcspTrustCompFormationAgt,
     responsiblePeopleConv: (Option[Seq[fe.responsiblepeople.ResponsiblePeople]], fe.businessmatching.BusinessMatching) => Option[Seq[ResponsiblePersons]],
     msbConv : (Option[fe.moneyservicebusiness.MoneyServiceBusiness], fe.businessmatching.BusinessMatching, Boolean) => Option[MoneyServiceBusiness],
     hvdConv : Option[fe.hvd.Hvd] => Option[Hvd],
     requestType: RequestType
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
      hvd = data.hvdSection,
      asp = data.aspSection,
      aspOrTcsp = AspOrTcsp.conv(data.supervisionSection),
      tcspAll = data.tcspSection.map(tcspAllConv),
      tcspTrustCompFormationAgt = data.tcspSection.map(tcspTrustCompConv),
      eabAll = data.eabSection.map(conv2),
      eabResdEstAgncy = data.eabSection,
      responsiblePersons = responsiblePeopleConv(data.responsiblePeopleSection, data.businessMatchingSection),
      filingIndividual = data.aboutYouSection,
      declaration = Declaration(true)
    )
}
