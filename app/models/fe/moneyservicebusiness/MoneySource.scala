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

package models.fe.moneyservicebusiness

import play.api.libs.json.{Json, Reads, Writes}
import play.api.libs.json._


case class BankMoneySource(bankNames: String)

case object BankMoneySource {

  implicit val jsonReads: Reads[Option[BankMoneySource]] = {
    import play.api.libs.functional.syntax._

    ((__ \ "moneySources" \ "bankMoneySource").readNullable[String].orElse(Reads.pure(None)) and
      (__ \ "moneySources" \ "bankNames").readNullable[String].orElse(Reads.pure(None))) ((bankMoney: Option[String], names: Option[String]) => {
      (bankMoney, names) match {
        case (Some(_), Some(n)) => Some(BankMoneySource(n))
        case _ => None
      }
    })
  }

  implicit val jsonWrites = Writes[Option[BankMoneySource]] {
    case Some(bankNames) => Json.obj("bankMoneySource" -> "Yes", "bankNames" -> bankNames.bankNames)
    case _ => Json.obj()
  }

}

case class WholesalerMoneySource(wholesalerNames: String)

object WholesalerMoneySource {

  import play.api.libs.functional.syntax._

  implicit val jsonReads: Reads[Option[WholesalerMoneySource]] = {
    ((__ \ "moneySources" \ "wholesalerMoneySource").readNullable[String].orElse(Reads.pure(None)) and
      (__ \ "moneySources" \ "wholesalerNames").readNullable[String].orElse(Reads.pure(None))) ((wholesalerMoney: Option[String], names: Option[String]) => {
      (wholesalerMoney, names) match {
        case (Some(_), Some(n)) => Some(WholesalerMoneySource(n))
        case _ => None
      }
    })
  }

  implicit val jsonWrites = Writes[Option[WholesalerMoneySource]] {
    case Some(source) => Json.obj("wholesalerMoneySource" -> "Yes", "wholesalerNames" -> source.wholesalerNames)
    case _ => Json.obj()
  }
}

case object CustomerMoneySource
