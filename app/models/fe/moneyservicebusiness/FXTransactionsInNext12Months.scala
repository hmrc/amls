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

import models.des.msb.MsbFxDetails
import play.api.libs.json.Json

case class FXTransactionsInNext12Months (fxTransaction: String)

object FXTransactionsInNext12Months {

    implicit val format = Json.format[FXTransactionsInNext12Months]

    implicit def convMsbFx(msbFx: Option[MsbFxDetails]): Option[FXTransactionsInNext12Months] = {
        msbFx flatMap (msbDtls => Some(FXTransactionsInNext12Months(msbDtls.anticipatedNoOfTransactions)))
    }
}
