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
import play.api.libs.json.Json

case class MsbCeDetails (currencySources: CurrencySources, dealInPhysCurrencies: Option[String] = None)

object MsbCeDetails {

  implicit val format = Json.format[MsbCeDetails]

  implicit def conv(msb: models.fe.moneyservicebusiness.MoneyServiceBusiness): Option[MsbCeDetails] = {

    AmlsConfig.release7 match {
      case true =>
        Some(MsbCeDetails(msb, msb.whichCurrencies.fold(Some("false"))(w => w.usesForeignCurrencies match {
          case Some(true) => Some("true")
          case _ => Some("false")
        })))
      case _ => Some(MsbCeDetails(msb))
    }

  }

}
