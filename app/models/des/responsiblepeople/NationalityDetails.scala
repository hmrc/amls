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

package models.des.responsiblepeople

import config.AmlsConfig
import models.fe.responsiblepeople.{NonUKResidence, ResponsiblePeople, UKResidence}
import play.api.libs.json.Json

case class NationalityDetails (areYouUkResident: Boolean,
                               idDetails: Option[IdDetail],
                               countryOfBirth: Option[String],
                               nationality: Option[String])
object NationalityDetails {
  implicit val format = Json.format[NationalityDetails]

  implicit def convert(rp: ResponsiblePeople) : Option[NationalityDetails] = {
    rp.personResidenceType map { residenceType =>
      (residenceType.isUKResidence, AmlsConfig.phase2Changes) match {
        case (uk: UKResidence, false) =>
          NationalityDetails(true, UkResident.convert(uk), Some(residenceType.countryOfBirth), Some(residenceType.nationality))
        case (uk: UKResidence, true) =>
          NationalityDetails(true, UkResident.convert(uk, rp.dateOfBirth), Some(residenceType.countryOfBirth), Some(residenceType.nationality))
        case (NonUKResidence, _) =>
          NationalityDetails(false, NonUkResident.convert(rp), Some(residenceType.countryOfBirth), Some(residenceType.nationality))
      }
    }
  }
}
