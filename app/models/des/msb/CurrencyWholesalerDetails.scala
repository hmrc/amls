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

package models.des.msb

import models.fe.moneyservicebusiness.WholesalerMoneySource
import play.api.libs.json.Json

case class CurrencyWholesalerDetails (
                                       currencyWholesalers: Boolean,
                                       currencyWholesalersNames: Option[Seq[String]]
                                     )

object CurrencyWholesalerDetails {

  implicit val format = Json.format[CurrencyWholesalerDetails]

  implicit def conv(moneySource: Option[WholesalerMoneySource]) : Option[CurrencyWholesalerDetails] = {

    moneySource match {
      case Some(data) => Some(CurrencyWholesalerDetails(true, Some(Seq(data.wholesalerNames))))
      case _ => None
    }
  }
}
