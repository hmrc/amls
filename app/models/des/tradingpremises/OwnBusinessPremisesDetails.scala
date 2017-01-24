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

package models.des.tradingpremises

import models.des.{StatusProvider, StringOrInt}
import models.fe.tradingpremises.MsbService
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class OwnBusinessPremisesDetails(tradingName:String,
                                      businessAddress: Address,
                                      residential:Boolean,
                                      msb: Msb,
                                      hvd:Hvd,
                                      asp:Asp,
                                      tcsp:Tcsp,
                                      eab:Eab,
                                      bpsp:Bpsp,
                                      tditpsp:Tditpsp,
                                      startDate : String,
                                      endDate: Option[String] = None,
                                      lineId: Option[StringOrInt] = None,
                                      status: Option[String] = None,
                                      sectorDateChange: Option[String] = None,
                                      dateChangeFlag: Option[Boolean] = None,
                                      tradingNameChangeDate: Option[String] = None
                                     )


object OwnBusinessPremisesDetails {

  implicit val jsonReads: Reads[OwnBusinessPremisesDetails] = {
    (
        (__ \ "tradingName").read[String] and
        (__ \ "businessAddress").read[Address] and
        (__ \ "residential").read[Boolean] and
        (__ \ "msb").readNullable[Msb].map{_.getOrElse(Msb(false,false,false,false,false))} and
        (__ \ "hvd").readNullable[Hvd].map{_.getOrElse(Hvd(false))} and
        (__ \ "asp").readNullable[Asp].map{_.getOrElse(Asp(false))} and
        (__ \ "tcsp").readNullable[Tcsp].map{_.getOrElse(Tcsp(false))} and
        (__ \ "eab").readNullable[Eab].map{_.getOrElse(Eab(false))} and
        (__ \ "bpsp").readNullable[Bpsp].map{_.getOrElse(Bpsp(false))} and
        (__ \ "tditpsp").readNullable[Tditpsp].map{_.getOrElse(Tditpsp(false))} and
        (__ \ "startDate").read[String] and
        (__ \ "endDate").readNullable[String] and
        __.read(Reads.optionNoError[StringOrInt]) and
        (__ \ "status").readNullable[String] and
        (__ \ "sectorDateChange").readNullable[String] and
        (__ \ "dateChangeFlag").readNullable[Boolean] and
        (__ \ "tradingNameChangeDate").readNullable[String]
      ) (OwnBusinessPremisesDetails.apply _)
  }

  implicit val jsonWrites: Writes[OwnBusinessPremisesDetails] = {
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
        (__ \ "startDate").write[String] and
        (__ \ "endDate").writeNullable[String] and
        __.writeNullable[StringOrInt] and
        (__ \ "status").writeNullable[String] and
        (__ \ "sectorDateChange").writeNullable[String] and
        (__ \ "dateChangeFlag").writeNullable[Boolean] and
        (__ \ "tradingNameChangeDate").writeNullable[String]
      ) (unlift(OwnBusinessPremisesDetails.unapply))
  }

  implicit def convert(tradingPremises: Seq[models.fe.tradingpremises.TradingPremises]): Seq[OwnBusinessPremisesDetails] = {

    tradingPremises map {
      x => {
        val y = x.yourTradingPremises
        val z = x.whatDoesYourBusinessDoAtThisAddress.activities
        OwnBusinessPremisesDetails(y.tradingName, y.tradingPremisesAddress,
          y.isResidential,
          x.msbServices.fold[Set[MsbService]](Set.empty)(x => x.msbServices),
          z,
          z,
          z,
          z,
          z,
          z,
          y.startDate.toString,
          x.endDate.fold[Option[String]](None)(x=>Some(x.endDate.toString)),
          x.lineId,
          x.status,
          x.whatDoesYourBusinessDoAtThisAddress.dateOfChange,
          None,
          x.yourTradingPremises.tradingNameChangeDate
        )
      }
    }
  }

  implicit object OwnBusinessPremisesDetailsHasStatus extends StatusProvider[OwnBusinessPremisesDetails]{
    override def getStatus(sp:OwnBusinessPremisesDetails): Option[String] = sp.status
  }
}
