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

import models.des.msb.MsbCeDetailsR7
import play.api.libs.json.{Json, OFormat}

case class CETransactionsInNext12Months(ceTransaction: String)

object CETransactionsInNext12Months {

  implicit val format: OFormat[CETransactionsInNext12Months] = Json.format[CETransactionsInNext12Months]

  implicit def convMsbCe(msbMt: Option[MsbCeDetailsR7]): Option[CETransactionsInNext12Months] = {
    msbMt match {
      case Some(msbDtls) => Some(CETransactionsInNext12Months(msbDtls.antNoOfTransNxt12Mnths))
      case None => None
    }
  }
}
