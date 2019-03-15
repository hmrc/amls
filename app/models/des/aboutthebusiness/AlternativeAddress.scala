/*
 * Copyright 2019 HM Revenue & Customs
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

package models.des.aboutthebusiness

import models.fe.businessdetails.{NonUKCorrespondenceAddress, UKCorrespondenceAddress, CorrespondenceAddress}
import play.api.libs.json.Json

case class AlternativeAddress(name : String,
                              tradingName : String,
                              address: Address
                             )

object AlternativeAddress {
  implicit val format = Json.format[AlternativeAddress]

  implicit def convert(alternativeAddress: Option[CorrespondenceAddress]) : Option[AlternativeAddress] = {
    alternativeAddress match {
      case Some(UKCorrespondenceAddress(yourName, businessName, _, _, _, _, _)) =>
        Some(AlternativeAddress(yourName, businessName, alternativeAddress))
      case Some(NonUKCorrespondenceAddress(yourName, businessName, _, _, _, _, _)) =>
        Some(AlternativeAddress(yourName, businessName, alternativeAddress))
      case _ => None
    }
  }
}
