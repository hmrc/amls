/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json.Json

case class CurrencySources(
                            bankDetails: Option[MSBBankDetails] =  None,
                            currencyWholesalerDetails: Option[CurrencyWholesalerDetails] =  None,
                            reSellCurrTakenIn: Boolean ,
                            antNoOfTransNxt12Mnths: String,
                            currSupplyToCust: Option[CurrSupplyToCust]
                          )

object CurrencySources {
  implicit val format = Json.format[CurrencySources]

  implicit def conv(msb: models.fe.moneyservicebusiness.MoneyServiceBusiness): CurrencySources = {
    msb.whichCurrencies match {
      case Some(data) => CurrencySources(
                            data.bankMoneySource,
                            data.wholesalerMoneySource,
                            data.customerMoneySource,
                            msb.ceTransactionsInNext12Months.fold("")(x => x.ceTransaction),
                            data.currencies
                          )
      case None => CurrencySources(None, None, false, "", None)
    }
  }
}
