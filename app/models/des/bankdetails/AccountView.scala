/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.{Json, Reads, Writes}

sealed trait AccountView

object AccountView {

  implicit val ukAccountReads: Reads[ukAccountView] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    ((__ \ "ukAccount" \ "sortCode").read[String] and
      (__ \ "ukAccount" \ "accountNumber").read[String]) (ukAccountView.apply _)
  }

  implicit val accountNumberReads: Reads[AccountNumberView] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    (__ \ "nonUkAccount" \ "accountNumber" \ "bankAccountNumber").read[String] fmap AccountNumberView.apply
  }

  implicit val ibanNumberReads: Reads[IBANNumberView] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    (__ \ "nonUkAccount" \ "accountNumber" \ "iban").read[String] fmap IBANNumberView.apply
  }

  implicit val nonUkAccountReads: Reads[nonUkAccountView] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    __.read[AccountNumberView].map(x => x: nonUkAccountView) |
      __.read[IBANNumberView].map(x => x: nonUkAccountView)
  }

  implicit val AccountViewReads: Reads[AccountView] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    __.read[ukAccountView].map(x => x: AccountView) |
      __.read[nonUkAccountView].map(x => x: AccountView)
  }


  implicit val jsonWrites = Writes[AccountView] {
    case m: ukAccountView =>
      Json.obj(
        "ukAccount" -> Json.obj(
          "sortCode" -> m.sortCode,
          "accountNumber" -> m.accountNumber
        ))
    case acc: AccountNumberView =>
      Json.obj(
        "nonUkAccount" -> Json.obj(
          "accountHasIban" -> false,
          "accountNumber" -> Json.obj("bankAccountNumber" -> acc.accountNumber)
        ))
    case iban: IBANNumberView =>
      Json.obj(
        "nonUkAccount" -> Json.obj(
          "accountHasIban" -> true,
          "accountNumber" -> Json.obj("iban" -> iban.iban)
        ))
  }
}

case class ukAccountView(sortCode: String,
                         accountNumber: String
                        ) extends AccountView

sealed trait nonUkAccountView extends AccountView

case class AccountNumberView(accountNumber: String) extends nonUkAccountView

case class IBANNumberView(iban: String) extends nonUkAccountView
