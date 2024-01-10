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

package models.des.msb

import models.fe.moneyservicebusiness.BankMoneySource
import play.api.libs.json.{Json, OFormat}

case class MSBBankDetails(banks: Boolean, bankNames: Option[Seq[String]])

object MSBBankDetails {
  implicit val format: OFormat[MSBBankDetails] = Json.format[MSBBankDetails]

  implicit def conv(bankDtls: Option[BankMoneySource]): Option[MSBBankDetails] = {
    bankDtls match {
      case Some(data) => Some(MSBBankDetails(true, Some(Seq(data.bankNames))))
      case _ => Some(MSBBankDetails(false, None))
    }
  }
}
