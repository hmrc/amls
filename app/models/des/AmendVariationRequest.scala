/*
 * Copyright 2019 HM Revenue & Customs
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
import models.des.aboutyou.AboutYouRelease7
import models.des.amp.Amp
import models.des.asp.Asp
import models.des.bankdetails.BankDetailsView
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

case class AmendVariationRequest(
                                acknowledgementReference: String,
                                changeIndicators: ChangeIndicators,
                                amlsMessageType: String,
                                businessDetails: BusinessDetails,
                                businessContactDetails : BusinessContactDetails,
                                businessReferencesAll : Option[PreviouslyRegisteredMLRView],
                                businessReferencesAllButSp: Option[VATRegistration],
                                businessReferencesCbUbLlp: Option[CorporationTaxRegisteredCbUbLlp],
                                businessActivities : BusinessActivities,
                                tradingPremises: TradingPremises,
                                bankAccountDetails : Option[BankDetailsView],
                                msb: Option[MoneyServiceBusiness],
                                hvd: Option[Hvd],
                                asp: Option[Asp],
                                aspOrTcsp: Option[AspOrTcsp],
                                tcspAll: Option[TcspAll],
                                tcspTrustCompFormationAgt: Option[TcspTrustCompFormationAgt],
                                eabAll : Option[EabAll],
                                eabResdEstAgncy : Option[EabResdEstAgncy],
                                responsiblePersons: Option[Seq[ResponsiblePersons]],
                                amp: Option[Amp],
                                extraFields: ExtraFields
                              ) {
  def setChangeIndicator(changeIndicators: ChangeIndicators): AmendVariationRequest = {
    this.copy(changeIndicators = changeIndicators)
  }

  def setExtraFields(extraFields : ExtraFields): AmendVariationRequest = {
    this.copy(extraFields = extraFields)
  }

  def setResponsiblePersons(responsiblePersons: Seq[ResponsiblePersons]) : AmendVariationRequest = {
    this.copy(responsiblePersons = Some(responsiblePersons))
  }

  def setTradingPremises(tradingPremises: TradingPremises): AmendVariationRequest = {
    this.copy(tradingPremises = tradingPremises)
  }

}

object AmendVariationRequest {

  final type Outgoing = AmendVariationRequest
  final type Incoming = fe.SubscriptionRequest

  implicit def jsonReads: Reads[AmendVariationRequest] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
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
        (__ \ "msb").readNullable[MoneyServiceBusiness] and
        (__ \ "hvd").readNullable[Hvd] and
        (__ \ "asp").readNullable[Asp] and
        (__ \ "aspOrTcsp").readNullable[AspOrTcsp] and
        (__ \ "tcspAll").readNullable[TcspAll] and
        (__ \ "tcspTrustCompFormationAgt").readNullable[TcspTrustCompFormationAgt] and
        (__ \ "eabAll").readNullable[EabAll] and
        (__ \ "eabResdEstAgncy").readNullable[EabResdEstAgncy] and
        (__ \ "responsiblePersons").readNullable[Seq[ResponsiblePersons]] and
        (__ \ "amp").readNullable[Amp] and
        __.read[ExtraFields]
      ) (AmendVariationRequest.apply _)
  }

  implicit def jsonWrites: Writes[AmendVariationRequest] = {
    import play.api.libs.functional.syntax._
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
      (__ \ "msb").writeNullable[MoneyServiceBusiness] and
      (__ \ "hvd").writeNullable[Hvd] and
      (__ \ "asp").writeNullable[Asp] and
      (__ \ "aspOrTcsp").writeNullable[AspOrTcsp] and
      (__ \ "tcspAll").writeNullable[TcspAll] and
      (__ \ "tcspTrustCompFormationAgt").writeNullable[TcspTrustCompFormationAgt] and
      (__ \ "eabAll").writeNullable[EabAll] and
      (__ \ "eabResdEstAgncy").writeNullable[EabResdEstAgncy] and
      (__ \ "responsiblePersons").write[Option[Seq[ResponsiblePersons]]] and
      (__ \ "amp").writeNullable[Amp] and
      __.write[ExtraFields]
      ) (unlift(AmendVariationRequest.unapply _))
  }

  // scalastyle:off
  implicit def convert(data: Incoming)(implicit
                                       gen: AckRefGenerator,
                                       conv: Incoming => BusinessActivities,
                                       conv2 : fe.estateagentbusiness.EstateAgentBusiness => EabAll,
                                       prevRegMLR : fe.businessdetails.BusinessDetails => Option[PreviouslyRegisteredMLR],
                                       vatABConv : fe.businessdetails.BusinessDetails => Option[VATRegistration],
                                       contactABConv : fe.businessdetails.BusinessDetails => BusinessContactDetails,
                                       conv4 : Seq[fe.bankdetails.BankDetails] => Option[BankDetailsView],
                                       tpConv : Seq[fe.tradingpremises.TradingPremises] => TradingPremises,
                                       aboutyouConv: fe.declaration.AddPerson => AboutYouRelease7,
                                       aspConv : Option[fe.asp.Asp] => Option[Asp],
                                       tcspAllConv: fe.tcsp.Tcsp => TcspAll,
                                       tcspTrustCompConv: fe.tcsp.Tcsp => TcspTrustCompFormationAgt,
                                       responsiblePeopleConv: (Option[Seq[fe.responsiblepeople.ResponsiblePeople]], fe.businessmatching.BusinessMatching) => Option[Seq[ResponsiblePersons]],
                                       msbConv : (Option[fe.moneyservicebusiness.MoneyServiceBusiness], fe.businessmatching.BusinessMatching, Boolean) => Option[MoneyServiceBusiness],
                                       hvdConv : Option[fe.hvd.Hvd] => Option[Hvd],
                                       ampConv : Option[fe.amp.Amp] => Option[Amp],
                                       messageType : AmlsMessageType,
                                       requestType: RequestType
  ): Outgoing =
    AmendVariationRequest(
      acknowledgementReference = gen.ackRef,
      changeIndicators = ChangeIndicators(false),
      amlsMessageType =  messageType,
      businessDetails = data.businessMatchingSection,
      businessContactDetails = data.businessDetailsSection,
      businessReferencesAll = data.businessDetailsSection,
      businessReferencesAllButSp = data.businessDetailsSection,
      businessReferencesCbUbLlp = data.businessDetailsSection,
      businessActivities = conv(data),
      tradingPremises = data.tradingPremisesSection,
      bankAccountDetails = data.bankDetailsSection,
      msb = msbConv(data.msbSection, data.businessMatchingSection, true),
      hvd = data.hvdSection,
      asp = data.aspSection,
      aspOrTcsp = AspOrTcsp.conv(data.supervisionSection),
      tcspAll = data.tcspSection.map(tcspAllConv),
      tcspTrustCompFormationAgt = data.tcspSection.map(tcspTrustCompConv),
      eabAll = data.eabSection.map(conv2),
      eabResdEstAgncy = data.eabSection,
      responsiblePersons = responsiblePeopleConv(data.responsiblePeopleSection, data.businessMatchingSection),
      extraFields = data.aboutYouSection,
      amp = data.ampSection
    )
}
