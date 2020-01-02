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

package models.payapi

import enumeratum.{Enum, EnumEntry}
import play.api.libs.json._
import utils.EnumFormat

sealed abstract class TaxType extends EnumEntry

object TaxTypes extends Enum[TaxType] {
  case object `self-assessment` extends TaxType
  case object `vat` extends TaxType
  case object `epaye` extends TaxType
  case object `p11d` extends TaxType
  case object `other` extends TaxType
  case object `stamp-duty` extends TaxType
  case object `corporation-tax` extends TaxType

  override def values = findValues
}

case class Payment( id: String,
                    taxType: TaxType,
                    reference: String,
                    amountInPence: Int,
                    status: PaymentStatus)

object Payment {
  implicit val taxTypeTypeFormat = EnumFormat(TaxTypes)
  implicit val format = Json.format[Payment]
}
