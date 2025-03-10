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

package models.des.responsiblepeople

import models.fe.responsiblepeople._
import play.api.libs.json.{Json, OFormat}

case class RegDetails(vatRegistered: Boolean, vrnNumber: Option[String], saRegistered: Boolean, saUtr: Option[String])

object RegDetails {
  implicit val format: OFormat[RegDetails] = Json.format[RegDetails]

  implicit def conv(rp: ResponsiblePeople): Option[RegDetails] = {
    val (vat, vatNum) = convVat(rp.vatRegistered)
    val (sa, saNum)   = convSa(rp.saRegistered)
    Some(RegDetails(vat, vatNum, sa, saNum))
  }

  def convVat(vat: Option[VATRegistered]): (Boolean, Option[String]) =
    vat match {
      case Some(data) =>
        data match {
          case VATRegisteredYes(num) => (true, Some(num))
          case VATRegisteredNo       => (false, None)
        }
      case _          => (false, None)
    }

  def convSa(vat: Option[SaRegistered]): (Boolean, Option[String]) =
    vat match {
      case Some(data) =>
        data match {
          case SaRegisteredYes(num) => (true, Some(num))
          case SaRegisteredNo       => (false, None)
        }
      case _          => (false, None)
    }
}
