/*
 * Copyright 2016 HM Revenue & Customs
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

package models.fe.moneyservicebusiness

import models.des.msb.{CurrencyWholesalerDetails, MSBBankDetails, MsbCeDetails}
import play.api.libs.json.Json


case class WhichCurrencies(currencies : Seq[String]
                           , bankMoneySource : Option[BankMoneySource]
                           , wholesalerMoneySource : Option[WholesalerMoneySource]
                           , customerMoneySource : Boolean)

object WhichCurrencies {

  implicit val format = Json.format[WhichCurrencies]

  implicit def convMsbCe(msbCe: Option[MsbCeDetails]): Option[WhichCurrencies] = {
    msbCe match {
      case Some(msbDtls) => Some(WhichCurrencies(
        msbDtls.currencySources.currSupplyToCust.fold[Seq[String]](Seq.empty)(x => x.currency),
        msbDtls.currencySources.bankDetails,
        msbDtls.currencySources.currencyWholesalerDetails,
        msbDtls.currencySources.reSellCurrTakenIn))
      case None => None
    }
  }

  implicit def convMSBBankDetails(bankDtls: Option[MSBBankDetails]) : Option[BankMoneySource] = {
    bankDtls match {
      case Some(dtls) => dtls.bankNames match {
        case Some(sourceSeq) => sourceSeq.headOption.fold[Option[BankMoneySource]](None)(x => Some(BankMoneySource(x)))
        case _ => None
      }
      case None => None
    }
  }

  implicit def convWholesalerDetails(details: Option[CurrencyWholesalerDetails]) : Option[WholesalerMoneySource] = {
    details match {
      case Some(dtls) => dtls.currencyWholesalersNames match {
        case Some(currencySeq) => currencySeq.headOption.fold[Option[WholesalerMoneySource]](None)(x => Some(WholesalerMoneySource(x)))
        case _ => None
      }
      case None => None
    }
  }
}
