/*
 * Copyright 2021 HM Revenue & Customs
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

package models.des.businessactivities

import models.des.aboutthebusiness.Address
import models.fe.businessactivities.{NonUkAccountantsAddress, UkAccountantsAddress}
import play.api.libs.json.Json

case class AdvisorNameAddress(name: String,
                              tradingName: Option[String],
                              address: Address)

object AdvisorNameAddress{
  implicit val format = Json.format[AdvisorNameAddress]

  implicit def convert(accountant: models.fe.businessactivities.WhoIsYourAccountant): Option[AdvisorNameAddress] = {

    Some(AdvisorNameAddress(accountant.accountantsName, accountant.accountantsTradingName, accountant.address))
  }

  implicit def convert(address: models.fe.businessactivities.AccountantsAddress): Address ={
    address match{
      case UkAccountantsAddress(lin1, lin2, lin3, lin4, code) => Address(lin1,lin2,lin3,lin4,"GB", Some(code))
      case NonUkAccountantsAddress(lin1, lin2, lin3, lin4, country) => Address(lin1,lin2,lin3,lin4, country, None)
    }
  }
}
