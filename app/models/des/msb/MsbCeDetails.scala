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

package models.des.msb

import config.AmlsConfig
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class MsbCeDetails(currencySources: CurrencySources, dealInPhysCurrencies: Option[Boolean] = None)

object MsbCeDetails {

  val currencyReader = new Reads[Boolean] {
    override def reads(json: JsValue): JsResult[Boolean] =
      json match {
        case x: JsString => x.value match {
          case "true" => JsSuccess(true)
          case _ => JsSuccess(false)
        }
        case x: JsBoolean => JsSuccess(x.value)
      }
  }

  implicit val reads: Reads[MsbCeDetails] = {
    (
      (__ \ "currencySources").read[CurrencySources] and
        (__ \ "dealInPhysCurrencies").readNullable(currencyReader)
      ) (MsbCeDetails.apply _)
  }

  implicit val writes: Writes[MsbCeDetails] = {
    (
      (__ \ "currencySources").write[CurrencySources] and
        (__ \ "dealInPhysCurrencies").writeNullable[Boolean]
      ) (unlift(MsbCeDetails.unapply))
  }

  implicit def conv(msb: models.fe.moneyservicebusiness.MoneyServiceBusiness): Option[MsbCeDetails] = {

    AmlsConfig.release7 match {
      case true =>

        // Infer the value of dealInPhysCurrencies if it was not supplied
        val dealInPhysCurrencies = msb.whichCurrencies.fold[Option[Boolean]](Some(false))(wc => wc.usesForeignCurrencies match {
          case None => Some(wc.bankMoneySource.isDefined || wc.customerMoneySource || wc.wholesalerMoneySource.isDefined)
          case x => x
        })

        Some(MsbCeDetails(msb, dealInPhysCurrencies))
      case _ => Some(MsbCeDetails(msb))
    }

  }

  implicit def convertFromNewModel(msbceDetailsR7: MsbCeDetailsR7): MsbCeDetails = {

    MsbCeDetails(
      msbceDetailsR7.currencySources match {
        case Some(cs7) => CurrencySources(cs7.bankDetails, cs7.currencyWholesalerDetails, cs7.reSellCurrTakenIn,
          msbceDetailsR7.antNoOfTransNxt12Mnths, msbceDetailsR7.currSupplyToCust)
        case None => CurrencySources(None, None, false, msbceDetailsR7.antNoOfTransNxt12Mnths, msbceDetailsR7.currSupplyToCust)
      }, msbceDetailsR7.dealInPhysCurrencies
    )

  }

}
