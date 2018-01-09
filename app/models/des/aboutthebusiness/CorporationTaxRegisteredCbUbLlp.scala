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

package models.des.aboutthebusiness

import models.fe.aboutthebusiness.{CorporationTaxRegisteredNo, CorporationTaxRegisteredYes, CorporationTaxRegistered}
import play.api.libs.json.Json

case class CorporationTaxRegisteredCbUbLlp (cotaxRegistered: Boolean, ctutr: Option[String])

object CorporationTaxRegisteredCbUbLlp {

  implicit val format =  Json.format[CorporationTaxRegisteredCbUbLlp]

  implicit def conv(atb: models.fe.aboutthebusiness.AboutTheBusiness): Option[CorporationTaxRegisteredCbUbLlp]  = {

    atb.corporationTaxRegistered match {
      case Some(data) => data
      case _ => None
    }
  }

  implicit def conv1(corp: CorporationTaxRegistered): Option[CorporationTaxRegisteredCbUbLlp] = {

    corp match {
      case CorporationTaxRegisteredYes(num) => Some(CorporationTaxRegisteredCbUbLlp(true, Some(num)))
      case CorporationTaxRegisteredNo => Some(CorporationTaxRegisteredCbUbLlp(false, None))
    }
  }
}
