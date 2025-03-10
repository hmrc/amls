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

package models.des.tradingpremises

import models.des.RequestType
import models.fe.tradingpremises.MsbService
import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, _}

case class AgentPremises(
  tradingName: String,
  businessAddress: Address,
  residential: Boolean,
  msb: Msb,
  hvd: Hvd,
  asp: Asp,
  tcsp: Tcsp,
  eab: Eab,
  bpsp: Bpsp,
  tditpsp: Tditpsp,
  amp: Amp,
  startDate: Option[String],
  sectorChangeDate: Option[String] = None
)

object AgentPremises {
  implicit val jsonReads: Reads[AgentPremises] =
    (
      (__ \ "tradingName").read[String] and
        (__ \ "businessAddress").read[Address] and
        (__ \ "residential").read[Boolean] and
        (__ \ "msb").readNullable[Msb].map(_.getOrElse(Msb(false, false, false, false, false))) and
        (__ \ "hvd").readNullable[Hvd].map(_.getOrElse(Hvd(false))) and
        (__ \ "asp").readNullable[Asp].map(_.getOrElse(Asp(false))) and
        (__ \ "tcsp").readNullable[Tcsp].map(_.getOrElse(Tcsp(false))) and
        (__ \ "eab").readNullable[Eab].map(_.getOrElse(Eab(false))) and
        (__ \ "bpsp").readNullable[Bpsp].map(_.getOrElse(Bpsp(false))) and
        (__ \ "tditpsp").readNullable[Tditpsp].map(_.getOrElse(Tditpsp(false))) and
        (__ \ "amp").readNullable[Amp].map(_.getOrElse(Amp(false))) and
        (__ \ "startDate").readNullable[String] and
        (__ \ "agentSectorChgDate").readNullable[String]
    )(AgentPremises.apply _)

  implicit val jsonWrites: Writes[AgentPremises] =
    (
      (__ \ "tradingName").write[String] and
        (__ \ "businessAddress").write[Address] and
        (__ \ "residential").write[Boolean] and
        (__ \ "msb").write[Msb] and
        (__ \ "hvd").write[Hvd] and
        (__ \ "asp").write[Asp] and
        (__ \ "tcsp").write[Tcsp] and
        (__ \ "eab").write[Eab] and
        (__ \ "bpsp").write[Bpsp] and
        (__ \ "tditpsp").write[Tditpsp] and
        (__ \ "amp").write[Amp] and
        (__ \ "startDate").writeNullable[String] and
        (__ \ "agentSectorChgDate").writeNullable[String]
    )(unlift(AgentPremises.unapply _))

  implicit def convert(
    tradingPremises: models.fe.tradingpremises.TradingPremises
  )(implicit requestType: RequestType): AgentPremises = {
    val ytp = tradingPremises.yourTradingPremises

    val startDate = requestType match {
      case RequestType.Amendment => None
      case _                     => Some(ytp.startDate.toString)
    }
    val z         = tradingPremises.whatDoesYourBusinessDoAtThisAddress.activities
    AgentPremises(
      ytp.tradingName,
      ytp.tradingPremisesAddress,
      ytp.isResidential,
      tradingPremises.msbServices.fold[Set[MsbService]](Set.empty)(x => x.msbServices),
      z,
      z,
      z,
      z,
      z,
      z,
      z,
      startDate,
      tradingPremises.whatDoesYourBusinessDoAtThisAddress.dateOfChange
    )
  }
}
