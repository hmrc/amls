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

package models.fe.businessactivities

import models.des.businessactivities.BusinessActivitiesAll
import play.api.libs.json.{Json, OFormat}

case class CustomersOutsideUK(isOutside: Boolean, countries: Option[Seq[String]])

object CustomersOutsideUK {

  implicit val format: OFormat[CustomersOutsideUK] = Json.format[CustomersOutsideUK]

  def conv(des: BusinessActivitiesAll): Option[CustomersOutsideUK] =
    des.nonUkResidentCustDetails.whichCountries match {
      case Some(countries) => Some(CustomersOutsideUK(true, Some(countries)))
      case _               => Some(CustomersOutsideUK(false, None))
    }
}
