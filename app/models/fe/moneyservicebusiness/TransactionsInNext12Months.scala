/*
 * Copyright 2024 HM Revenue & Customs
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
import play.api.libs.json.{Json, OFormat}

case class TransactionsInNext12Months(txnAmount: String)

object TransactionsInNext12Months {

  implicit val format: OFormat[TransactionsInNext12Months] = Json.format[TransactionsInNext12Months]

  implicit def convMsbMt(msbMt: Option[MsbMtDetails]): Option[TransactionsInNext12Months] =
    msbMt match {
      case Some(msbDtls) => Some(TransactionsInNext12Months(msbDtls.noOfMoneyTrnsfrTransNxt12Mnths.getOrElse("")))
      case None          => None
    }
}
