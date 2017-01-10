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

package models.fe.moneyservicebusiness

import play.api.libs.json._

case class BankMoneySource(bankNames : String)

case object BankMoneySource {

  implicit val jsonReads: Reads[Option[BankMoneySource]] = {
    (__ \ "bankMoneySource").readNullable[String] flatMap {
      case Some("Yes") => (__ \ "bankNames").read[String].map(names => Some(BankMoneySource(names)))
      case _ => Reads(_ => JsSuccess(None))
    }
  }

  implicit val jsonWrites = Writes[Option[BankMoneySource]] {
      case Some(bankNames) =>  Json.obj("bankMoneySource" -> "Yes", "bankNames" -> bankNames.bankNames)
      case _ =>  Json.obj()
  }

}

case class WholesalerMoneySource(wholesalerNames : String)

object WholesalerMoneySource {

  implicit val jsonReads: Reads[Option[WholesalerMoneySource]] = {
    (__ \ "wholesalerMoneySource").readNullable[String] flatMap {
      case Some("Yes") => (__ \ "wholesalerNames").read[String].map(names => Some(WholesalerMoneySource(names)))
      case _ => Reads(_ => JsSuccess(None))
    }
  }

  implicit val jsonWrites = Writes[Option[WholesalerMoneySource]] {
    case Some(source) =>  Json.obj("wholesalerMoneySource" -> "Yes", "wholesalerNames" -> source.wholesalerNames)
    case _ =>  Json.obj()
  }
}

case object CustomerMoneySource
