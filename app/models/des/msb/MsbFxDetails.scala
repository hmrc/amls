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

package models.des.msb

import play.api.libs.json.{Json, OFormat}

case class MsbFxDetails(anticipatedNoOfTransactions: String)

object MsbFxDetails {

  implicit val format: OFormat[MsbFxDetails] = Json.format[MsbFxDetails]

  implicit def conv(msb: models.fe.moneyservicebusiness.MoneyServiceBusiness): Option[MsbFxDetails] =
    msb.fxTransactionsInNext12Months flatMap
      (feModel => Some(MsbFxDetails(feModel.fxTransaction)))

}
