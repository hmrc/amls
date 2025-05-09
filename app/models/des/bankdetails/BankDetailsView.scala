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

package models.des.bankdetails

import play.api.libs.json.{Json, OFormat}

case class BankDetailsView(noOfMlrBankAccounts: Option[String], bankAccounts: Option[Seq[BankAccountView]])

object BankDetailsView {

  implicit val format: OFormat[BankDetailsView] = Json.format[BankDetailsView]

  def emptyToOption(seq: Seq[BankAccountView]): Option[Seq[BankAccountView]] =
    seq match {
      case x if x.isEmpty => None
      case x              => Some(x)
    }

  implicit def convert(bankdetails: Seq[models.fe.bankdetails.BankDetails]): Option[BankDetailsView] = {

    val count = bankdetails.length match {
      case x if x > 99 => Some("99")
      case _           => Some(bankdetails.length.toString)
    }

    Some(BankDetailsView(count, emptyToOption(bankdetails)))
  }
}
