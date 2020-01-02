/*
 * Copyright 2020 HM Revenue & Customs
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

package models.des.msb

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class MsbCeDetailsR7(dealInPhysCurrencies: Option[Boolean] = None,
                          currencySources: Option[CurrencySourcesR7],
                          antNoOfTransNxt12Mnths: String,
                          currSupplyToCust: Option[CurrSupplyToCust]) {

}

object MsbCeDetailsR7 {

  val currencyReader = new Reads[Boolean] {
    override def reads(json: JsValue): JsResult[Boolean] =
      json match {
        case x: JsString => x.value match {
          case "true" => JsSuccess(true)
          case _ => JsSuccess(false)
        }
        case x: JsBoolean => JsSuccess(x.value)
        case _ => throw new MatchError(this)
      }
  }

  implicit val reads: Reads[MsbCeDetailsR7] = {
    (
      (__ \ "dealInPhysCurrencies").readNullable(currencyReader) and
        (__ \ "currencySources").readNullable[CurrencySourcesR7] and
        (__ \ "antNoOfTransNxt12Mnths").read[String] and
        (__ \ "currSupplyToCust").readNullable[CurrSupplyToCust]
      ) (MsbCeDetailsR7.apply _)
  }

  implicit val writes: Writes[MsbCeDetailsR7] = {
    (
      (__ \ "dealInPhysCurrencies").writeNullable[Boolean] and
        (__ \ "currencySources").writeNullable[CurrencySourcesR7] and
        (__ \ "antNoOfTransNxt12Mnths").write[String] and
        (__ \ "currSupplyToCust").writeNullable[CurrSupplyToCust]
      ) (unlift(MsbCeDetailsR7.unapply))
  }

  implicit def conv(msb: models.fe.moneyservicebusiness.MoneyServiceBusiness): Option[MsbCeDetailsR7] = {

    val dealInPhysCurrencies = msb.whichCurrencies.fold[Option[Boolean]](Some(false))(wc => wc.usesForeignCurrencies match {
            case None => Some(wc.bankMoneySource.isDefined || wc.customerMoneySource || wc.wholesalerMoneySource.isDefined)
            case x => x
          })


    Some(MsbCeDetailsR7(dealInPhysCurrencies, if(dealInPhysCurrencies.isEmpty || dealInPhysCurrencies.get) {
      msb
    } else None, msb.ceTransactionsInNext12Months.fold("")(x => x.ceTransaction),
      msb.whichCurrencies match {
        case Some(wc) => wc.currencies
        case None => None
      }))
  }

  implicit def convertFromOldModel(msbceOpt: Option[MsbCeDetails]): Option[MsbCeDetailsR7] = {
    msbceOpt map {
      msbce => MsbCeDetailsR7(msbce.dealInPhysCurrencies,
        Some(CurrencySourcesR7(msbce.currencySources.bankDetails, msbce.currencySources.currencyWholesalerDetails,
        msbce.currencySources.reSellCurrTakenIn)),
        msbce.currencySources.antNoOfTransNxt12Mnths,
        msbce.currencySources.currSupplyToCust)
    }
  }


}
