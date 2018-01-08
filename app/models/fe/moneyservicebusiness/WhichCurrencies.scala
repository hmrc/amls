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

package models.fe.moneyservicebusiness

import config.AmlsConfig
import models.des.msb.{CurrencyWholesalerDetails, MSBBankDetails, MsbCeDetails, MsbCeDetailsR7}
import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class WhichCurrencies(currencies : Seq[String],
                           usesForeignCurrencies: Option[Boolean],
                           bankMoneySource : Option[BankMoneySource],
                           wholesalerMoneySource : Option[WholesalerMoneySource],
                           customerMoneySource : Boolean)

object WhichCurrencies {

  implicit val jsonReads: Reads[WhichCurrencies] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (
      (__ \ "currencies").read[Seq[String]] and
        (__ \ "usesForeignCurrencies").readNullable[Boolean] and
        __.read[Option[BankMoneySource]] and
        __.read[Option[WholesalerMoneySource]] and
        (__ \ "customerMoneySource").readNullable[String].flatMap {
          case Some("Yes") => Reads(_ => JsSuccess(true))
          case _ => Reads(_ => JsSuccess(false))
        }
      ) (WhichCurrencies.apply _)
  }

  implicit val jsonWrites1: Writes[WhichCurrencies] = Writes[WhichCurrencies]{w =>

   val customerMoneySource =  w.customerMoneySource match {
      case true => Some("Yes")
      case false => None
    }

     Json.obj("currencies" -> w.currencies) ++
     Json.obj("usesForeignCurrencies" -> w.usesForeignCurrencies) ++
     BankMoneySource.jsonWrites.writes(w.bankMoneySource).as[JsObject] ++
     WholesalerMoneySource.jsonWrites.writes(w.wholesalerMoneySource).as[JsObject] ++
     Json.obj("customerMoneySource" -> customerMoneySource)

  }

  implicit def convMsbCe(msbCe: Option[MsbCeDetailsR7]): Option[WhichCurrencies] = {
    msbCe match {
      case Some(msbDtls) =>

        val foreignCurrencyDefault: Option[Boolean] = AmlsConfig.release7 match {
          case true =>
            msbDtls.currencySources match {
              case Some(cs) => {
                cs.bankDetails.isDefined ||
                  cs.currencyWholesalerDetails.isDefined ||
                  cs.reSellCurrTakenIn match {
                  case true => Some(true)
                  case _ => Some(false)
              }
            }
              case None => None
            }
          case _ => None
        }

        msbDtls.currencySources match {
          case Some(cs) => Some(WhichCurrencies(
            msbDtls.currSupplyToCust.fold[Seq[String]](Seq.empty)(x => x.currency),
            msbDtls.dealInPhysCurrencies.fold(foreignCurrencyDefault)(Some(_)),
            cs.bankDetails,
            cs.currencyWholesalerDetails,
            cs.reSellCurrTakenIn))
          case None => Some(WhichCurrencies(msbDtls.currSupplyToCust.fold[Seq[String]](Seq.empty)(x => x.currency),
            msbDtls.dealInPhysCurrencies.fold(foreignCurrencyDefault)(Some(_)),
            None,None,false))
        }

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
