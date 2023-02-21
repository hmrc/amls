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

import models.des.msb.MsbMtDetails
import play.api.libs.json.Json

case class SendTheLargestAmountsOfMoney(country_1: String, country_2: Option[String] = None, country_3: Option[String] = None)

object SendTheLargestAmountsOfMoney {

  implicit val format = Json.format[SendTheLargestAmountsOfMoney]

  implicit def convMsbMt(msbMt: Option[MsbMtDetails]): Option[SendTheLargestAmountsOfMoney] = {
    msbMt match {
      case Some(msbDtls) => {
        val listOfCountries = msbDtls.countriesLrgstMoneyAmtSentTo.fold[Seq[String]](Seq.empty)(x => x.listOfCountries)
        if (listOfCountries.nonEmpty) {
          Some(SendTheLargestAmountsOfMoney(listOfCountries.head, listOfCountries.lift(1), listOfCountries.lift(2)))
        }
        else None
      }
      case None => None
    }
  }
}
