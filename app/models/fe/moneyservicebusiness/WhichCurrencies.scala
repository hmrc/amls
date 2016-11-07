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

import models.des.msb.{CurrencyWholesalerDetails, MSBBankDetails, MsbCeDetails, MoneyServiceBusiness => DesMoneyServiceBusiness}
import play.api.data.mapping._
import play.api.data.mapping.GenericRules._
import play.api.libs.functional.Monoid
import play.api.libs.json.{Reads, Writes}
import utils.OptionValidators._
import utils.TraversableValidators


case class WhichCurrencies(currencies : Seq[String]
                           , bankMoneySource : Option[BankMoneySource]
                           , wholesalerMoneySource : Option[WholesalerMoneySource]
                           , customerMoneySource : Boolean)

private sealed trait WhichCurrencies0 {

  private val length = 140
  private val nameType = maxLength(length)
  private val currencyType = TraversableValidators.minLengthR[Seq[String]](1)

  private implicit def rule[A]
    (implicit
      a : Path => RuleLike[A, Seq[String]],
      b: Path => RuleLike[A, Option[String]],
      c: Path => RuleLike[A, Boolean]
    ) : Rule[A, WhichCurrencies] = From[A] {__ =>

        val currencies = (__ \ "currencies").read[Seq[String], Seq[String]](currencyType)

        val bankMoneySource =
          (
            (__ \ "bankMoneySource").read[Option[String]] ~
            (__ \ "bankNames").read[Option[String], Option[String]](ifPresent(nameType))
          ).apply {(a,b) => (a,b) match {
              case (Some("Yes"), Some(names)) => Some(BankMoneySource(names))
              case (Some("Yes"), None) => Some(BankMoneySource(""))
              case _ => None
            }}

        val wholesalerMoneySource =
          (
            (__ \ "wholesalerMoneySource").read[Option[String]] ~
            (__ \ "wholesalerNames").read[Option[String], Option[String]](ifPresent(nameType))
          ).apply {(a,b) => (a,b) match {
            case (Some("Yes"), Some(names)) => Some(WholesalerMoneySource(names))
            case (Some("Yes"), None) => Some(WholesalerMoneySource(""))
            case _ => None
          }}

          val customerMoneySource = (__ \ "customerMoneySource").read[Option[String]] fmap {
            case Some("Yes") => true
            case _ => false
          }

        (currencies ~
          bankMoneySource ~
          wholesalerMoneySource ~
          customerMoneySource)(WhichCurrencies.apply(_,_,_,_))
    }

    private implicit def write[A]
    (implicit
    m: Monoid[A],
    a: Path => WriteLike[Seq[String], A],
    b: Path => WriteLike[String, A],
    c: Path => WriteLike[Option[String], A]
    ) : Write[WhichCurrencies, A] = To[A] { __ =>
      (
        (__ \ "currencies").write[Seq[String]] ~
        (__ \ "bankMoneySource").write[Option[String]] ~
        (__ \ "bankNames").write[Option[String]] ~
        (__ \ "wholesalerMoneySource").write[Option[String]] ~
        (__ \ "wholesalerNames").write[Option[String]] ~
        (__ \ "customerMoneySource").write[Option[String]]
      ).apply(wc => (wc.currencies,
                      wc.bankMoneySource.map(_ => "Yes"),
                      wc.bankMoneySource.map(bms => bms.bankNames),
                      wc.wholesalerMoneySource.map(_ => "Yes"),
                      wc.wholesalerMoneySource.map(bms => bms.wholesalerNames),
                      if (wc.customerMoneySource) Some("Yes") else None
                      ))
    }

  val jsonR: Reads[WhichCurrencies] = {
    import play.api.data.mapping.json.Rules.{JsValue => _, pickInJson => _, _}
    import utils.JsonMapping._
    implicitly[Reads[WhichCurrencies]]
  }


  val jsonW: Writes[WhichCurrencies] = {
    import play.api.data.mapping.json.Writes._
    import utils.JsonMapping._
    implicitly[Writes[WhichCurrencies]]
  }
}

object WhichCurrencies {
  private object Cache extends WhichCurrencies0

  implicit val jsonR: Reads[WhichCurrencies] = Cache.jsonR
  implicit val jsonW: Writes[WhichCurrencies] = Cache.jsonW

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
