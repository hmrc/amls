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

package models.des

import models.des.aboutthebusiness._
import models.des.asp.Asp
import models.des.bankdetails.BankDetailsView
import models.des.businessactivities.BusinessActivities
import models.des.businessdetails.BusinessDetails
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy, LettingAgents}
import models.des.hvd.Hvd
import models.des.amp.Amp
import models.des.msb.MoneyServiceBusiness
import models.des.responsiblepeople.ResponsiblePersons
import models.des.supervision.AspOrTcsp
import models.des.tcsp.{TcspAll, TcspTrustCompFormationAgt}
import models.des.tradingpremises.TradingPremises
import play.api.libs.json._

case class SubscriptionView(
  etmpFormBundleNumber: String,
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
)

object SubscriptionView {

  implicit val jsonReads: Reads[SubscriptionView] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (
      (__ \ "etmpFormBundleNumber").read[String] and
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
        (__ \ "lettingAgents").readNullable[LettingAgents] and
        __.read[ExtraFields]
    )(SubscriptionView.apply _)
  }

  implicit val jsonWrites: Writes[SubscriptionView] = {
    import play.api.libs.functional.syntax._
    ((__ \ "etmpFormBundleNumber").write[String] and
      (__ \ "businessDetails").write[BusinessDetails] and
      (__ \ "businessContactDetails").write[BusinessContactDetails] and
      (__ \ "businessReferencesAll").write[Option[PreviouslyRegisteredMLRView]] and
      (__ \ "businessReferencesAllButSp").write[Option[VATRegistration]] and
      (__ \ "businessReferencesCbUbLlp").write[Option[CorporationTaxRegisteredCbUbLlp]] and
      (__ \ "businessActivities").write[BusinessActivities] and
      (__ \ "tradingPremises").write[TradingPremises] and
      (__ \ "bankAccountDetails").write[Option[BankDetailsView]] and
      (__ \ "msb").write[Option[MoneyServiceBusiness]] and
      (__ \ "hvd").write[Option[Hvd]] and
      (__ \ "asp").write[Option[Asp]] and
      (__ \ "aspOrTcsp").write[Option[AspOrTcsp]] and
      (__ \ "tcspAll").write[Option[TcspAll]] and
      (__ \ "tcspTrustCompFormationAgt").write[Option[TcspTrustCompFormationAgt]] and
      (__ \ "eabAll").write[Option[EabAll]] and
      (__ \ "eabResdEstAgncy").write[Option[EabResdEstAgncy]] and
      (__ \ "responsiblePersons").write[Option[Seq[ResponsiblePersons]]] and
      (__ \ "amp").write[Option[Amp]] and
      (__ \ "lettingAgents").write[Option[LettingAgents]] and
      __.write[ExtraFields])(unlift(SubscriptionView.unapply _))
  }
}
