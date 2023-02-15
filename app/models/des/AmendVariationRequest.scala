/*
 * Copyright 2023 HM Revenue & Customs
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

import models.des.aboutthebusiness._
import models.des.amp.Amp
import models.des.asp.Asp
import models.des.bankdetails.BankDetailsView
import models.des.businessactivities.BusinessActivities
import models.des.businessactivities.BusinessActivities.conv
import models.des.businessdetails.BusinessDetails
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy, LettingAgents}
import models.des.hvd.Hvd
import models.des.msb.MoneyServiceBusiness
import models.des.responsiblepeople.ResponsiblePersons
import models.des.supervision.AspOrTcsp
import models.des.tcsp.{TcspAll, TcspTrustCompFormationAgt}
import models.des.tradingpremises.TradingPremises
import models.fe
import utils.AckRefGenerator

case class AmendVariationRequest(
                                  acknowledgementReference: String,
                                  changeIndicators: ChangeIndicators,
                                  amlsMessageType: String,
                                  businessDetails: BusinessDetails,
                                  businessContactDetails: BusinessContactDetails,
                                  businessReferencesAll: Option[PreviouslyRegisteredMLRView],
                                  businessReferencesAllButSp: Option[VATRegistration],
                                  businessReferencesCbUbLlp: Option[CorporationTaxRegisteredCbUbLlp],
                                  businessActivities: BusinessActivities,
                                  tradingPremises: TradingPremises,
                                  bankAccountDetails: Option[BankDetailsView],
                                  msb: Option[MoneyServiceBusiness],
                                  hvd: Option[Hvd],
                                  asp: Option[Asp],
                                  aspOrTcsp: Option[AspOrTcsp],
                                  tcspAll: Option[TcspAll],
                                  tcspTrustCompFormationAgt: Option[TcspTrustCompFormationAgt],
                                  eabAll: Option[EabAll],
                                  eabResdEstAgncy: Option[EabResdEstAgncy],
                                  responsiblePersons: Option[Seq[ResponsiblePersons]],
                                  amp: Option[Amp],
                                  lettingAgents: Option[LettingAgents],
                                  extraFields: ExtraFields
                                ) {

  def setChangeIndicator(changeIndicators: ChangeIndicators): AmendVariationRequest = {
    this.copy(changeIndicators = changeIndicators)
  }

  def setExtraFields(extraFields: ExtraFields): AmendVariationRequest = {
    this.copy(extraFields = extraFields)
  }

  def setResponsiblePersons(responsiblePersons: Seq[ResponsiblePersons]): AmendVariationRequest = {
    this.copy(responsiblePersons = Some(responsiblePersons))
  }

  def setTradingPremises(tradingPremises: TradingPremises): AmendVariationRequest = {
    this.copy(tradingPremises = tradingPremises)
  }

}

object AmendVariationRequest {

  final type Outgoing = AmendVariationRequest
  final type Incoming = fe.SubscriptionRequest

  import play.api.libs.functional.syntax._
  import play.api.libs.json.Reads._
  import play.api.libs.json._

  /** ****************************************************************
    * As the object is now > 22 fields we need to address the tupple 22
    * problem. This is done by splitting the reads/writes into two
    * parts and merging them together.
    * **************************************************************** */

  val readsOne: Reads[Tuple12[String,
    ChangeIndicators,
    String,
    BusinessDetails,
    BusinessContactDetails,
    Option[PreviouslyRegisteredMLRView],
    Option[VATRegistration],
    Option[CorporationTaxRegisteredCbUbLlp],
    BusinessActivities,
    TradingPremises,
    Option[BankDetailsView],
    Option[MoneyServiceBusiness]]] =
    (
      (__ \ "acknowledgementReference").read[String] and
        (__ \ "changeIndicators").read[ChangeIndicators] and
        (__ \ "amlsMessageType").read[String] and
        (__ \ "businessDetails").read[BusinessDetails] and
        (__ \ "businessContactDetails").read[BusinessContactDetails] and
        (__ \ "businessReferencesAll").readNullable[PreviouslyRegisteredMLRView] and
        (__ \ "businessReferencesAllButSp").readNullable[VATRegistration] and
        (__ \ "businessReferencesCbUbLlp").readNullable[CorporationTaxRegisteredCbUbLlp] and
        (__ \ "businessActivities").read[BusinessActivities] and
        (__ \ "tradingPremises").read[TradingPremises] and
        (__ \ "bankAccountDetails").readNullable[BankDetailsView] and
        (__ \ "msb").readNullable[MoneyServiceBusiness]
      ).tupled

  val readsTwo: Reads[Tuple11[Option[Hvd],
    Option[Asp],
    Option[AspOrTcsp],
    Option[TcspAll],
    Option[TcspTrustCompFormationAgt],
    Option[EabAll],
    Option[EabResdEstAgncy],
    Option[Seq[ResponsiblePersons]],
    Option[Amp],
    Option[LettingAgents],
    ExtraFields]] =
    (
      (__ \ "hvd").readNullable[Hvd] and
        (__ \ "asp").readNullable[Asp] and
        (__ \ "aspOrTcsp").readNullable[AspOrTcsp] and
        (__ \ "tcspAll").readNullable[TcspAll] and
        (__ \ "tcspTrustCompFormationAgt").readNullable[TcspTrustCompFormationAgt] and
        (__ \ "eabAll").readNullable[EabAll] and
        (__ \ "eabResdEstAgncy").readNullable[EabResdEstAgncy] and
        (__ \ "responsiblePersons").readNullable[Seq[ResponsiblePersons]] and
        (__ \ "amp").readNullable[Amp] and
        (__ \ "lettingAgents").readNullable[LettingAgents] and
        __.read[ExtraFields]
      ).tupled

  //Combine reads
  implicit val reads: Reads[AmendVariationRequest] = (readsOne and readsTwo) {
    (first, second) =>
      AmendVariationRequest(
        first._1, first._2, first._3, first._4, first._5, first._6, first._7, first._8, first._9, first._10, first._11, first._12,
        second._1, second._2, second._3, second._4, second._5, second._6, second._7, second._8, second._9, second._10, second._11)
  }

  val writesOne: OWrites[Tuple12[String,
    ChangeIndicators,
    String,
    BusinessDetails,
    BusinessContactDetails,
    Option[PreviouslyRegisteredMLRView],
    Option[VATRegistration],
    Option[CorporationTaxRegisteredCbUbLlp],
    BusinessActivities,
    TradingPremises,
    Option[BankDetailsView],
    Option[MoneyServiceBusiness]]] =
    (
      (__ \ "acknowledgementReference").write[String] and
        (__ \ "changeIndicators").write[ChangeIndicators] and
        (__ \ "amlsMessageType").write[String] and
        (__ \ "businessDetails").write[BusinessDetails] and
        (__ \ "businessContactDetails").write[BusinessContactDetails] and
        (__ \ "businessReferencesAll").write[Option[PreviouslyRegisteredMLRView]] and
        (__ \ "businessReferencesAllButSp").writeNullable[VATRegistration] and
        (__ \ "businessReferencesCbUbLlp").writeNullable[CorporationTaxRegisteredCbUbLlp] and
        (__ \ "businessActivities").write[BusinessActivities] and
        (__ \ "tradingPremises").write[TradingPremises] and
        (__ \ "bankAccountDetails").write[Option[BankDetailsView]] and
        (__ \ "msb").writeNullable[MoneyServiceBusiness]
      ) (t => t)

  val writesTwo: OWrites[Tuple11[Option[Hvd],
    Option[Asp],
    Option[AspOrTcsp],
    Option[TcspAll],
    Option[TcspTrustCompFormationAgt],
    Option[EabAll],
    Option[EabResdEstAgncy],
    Option[Seq[ResponsiblePersons]],
    Option[Amp],
    Option[LettingAgents],
    ExtraFields]] =
    (
      (__ \ "hvd").writeNullable[Hvd] and
        (__ \ "asp").writeNullable[Asp] and
        (__ \ "aspOrTcsp").writeNullable[AspOrTcsp] and
        (__ \ "tcspAll").writeNullable[TcspAll] and
        (__ \ "tcspTrustCompFormationAgt").writeNullable[TcspTrustCompFormationAgt] and
        (__ \ "eabAll").writeNullable[EabAll] and
        (__ \ "eabResdEstAgncy").writeNullable[EabResdEstAgncy] and
        (__ \ "responsiblePersons").write[Option[Seq[ResponsiblePersons]]] and
        (__ \ "amp").writeNullable[Amp] and
        (__ \ "lettingAgents").writeNullable[LettingAgents] and
        __.write[ExtraFields]
      ) (t => t)

  //Combine the writes
  implicit val writes: Writes[AmendVariationRequest] = Writes { (amendVariationRequest: AmendVariationRequest) =>
    val fieldsOne = (
      amendVariationRequest.acknowledgementReference,
      amendVariationRequest.changeIndicators,
      amendVariationRequest.amlsMessageType,
      amendVariationRequest.businessDetails,
      amendVariationRequest.businessContactDetails,
      amendVariationRequest.businessReferencesAll,
      amendVariationRequest.businessReferencesAllButSp,
      amendVariationRequest.businessReferencesCbUbLlp,
      amendVariationRequest.businessActivities,
      amendVariationRequest.tradingPremises,
      amendVariationRequest.bankAccountDetails,
      amendVariationRequest.msb
    )

    val fieldsTwo = (
      amendVariationRequest.hvd,
      amendVariationRequest.asp,
      amendVariationRequest.aspOrTcsp,
      amendVariationRequest.tcspAll,
      amendVariationRequest.tcspTrustCompFormationAgt,
      amendVariationRequest.eabAll,
      amendVariationRequest.eabResdEstAgncy,
      amendVariationRequest.responsiblePersons,
      amendVariationRequest.amp,
      amendVariationRequest.lettingAgents,
      amendVariationRequest.extraFields
    )

    val w1: JsObject = Json.toJsObject(fieldsOne)(writesOne)
    val w2: JsObject = Json.toJsObject(fieldsTwo)(writesTwo)
    w1.deepMerge(w2)
  }

  // scalastyle:off
  implicit def convert(data: Incoming)(implicit
                                       gen: AckRefGenerator,
                                       conv2: fe.eab.Eab => EabAll,
                                       vatABConv: fe.businessdetails.BusinessDetails => Option[VATRegistration],
                                       contactABConv: fe.businessdetails.BusinessDetails => BusinessContactDetails,
                                       conv4: Seq[fe.bankdetails.BankDetails] => Option[BankDetailsView],
                                       aspConv: Option[fe.asp.Asp] => Option[Asp],
                                       tcspAllConv: fe.tcsp.Tcsp => TcspAll,
                                       tcspTrustCompConv: fe.tcsp.Tcsp => TcspTrustCompFormationAgt,
                                       responsiblePeopleConv: (Option[Seq[fe.responsiblepeople.ResponsiblePeople]], fe.businessmatching.BusinessMatching, Boolean) => Option[Seq[ResponsiblePersons]],
                                       msbConv: (Option[fe.moneyservicebusiness.MoneyServiceBusiness], fe.businessmatching.BusinessMatching, Boolean) => Option[MoneyServiceBusiness],
                                       hvdConv: Option[fe.hvd.Hvd] => Option[Hvd],
                                       ampConv: Option[fe.amp.Amp] => Option[Amp],
                                       lettingAgentConv: Option[fe.eab.Eab] => Option[LettingAgents],
                                       messageType: AmlsMessageType,
                                       requestType: RequestType
  ): Outgoing =
    AmendVariationRequest(
      acknowledgementReference = gen.ackRef,
      changeIndicators = ChangeIndicators(false),
      amlsMessageType = messageType,
      businessDetails = data.businessMatchingSection,
      businessContactDetails = data.businessDetailsSection,
      businessReferencesAll = data.businessDetailsSection,
      businessReferencesAllButSp = data.businessDetailsSection,
      businessReferencesCbUbLlp = data.businessDetailsSection,
      businessActivities = BusinessActivities.convWithFlag(data),
      tradingPremises = data.tradingPremisesSection,
      bankAccountDetails = data.bankDetailsSection,
      msb = msbConv(data.msbSection, data.businessMatchingSection, true),
      hvd = data.hvdSection,
      asp = data.aspSection,
      aspOrTcsp = AspOrTcsp.conv(data.supervisionSection),
      tcspAll = data.tcspSection.map(tcspAllConv),
      tcspTrustCompFormationAgt = if (data.tcspServicesOffered.isDefined && data.tcspServicesOffered.get.trustOrCompFormAgent) {
        data.tcspSection.map(tcspTrustCompConv)
      } else {
        None
      },
      eabAll = data.eabSection.map(conv2),
      eabResdEstAgncy = data.eabSection,
      responsiblePersons = responsiblePeopleConv(data.responsiblePeopleSection, data.businessMatchingSection, true),
      extraFields = data.aboutYouSection,
      amp = data.ampSection,
      lettingAgents = data.eabSection
    )
}
