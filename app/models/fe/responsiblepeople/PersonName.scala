/*
 * Copyright 2016 HM Revenue & Customs
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

package models.fe.responsiblepeople

import models.des.responsiblepeople.{OthrNamesOrAliasesDetails, NameDetails}
import play.api.libs.json.{Writes => _}

case class PersonName(
                       firstName: String,
                       middleName: Option[String],
                       lastName: String,
                       previousName: Option[PreviousName],
                       otherNames: Option[String]
                     )

object PersonName {

  import play.api.libs.json._

  implicit val format = Json.format[PersonName]

  implicit def conv(desNameDtls: Option[NameDetails]): Option[PersonName] = {
    desNameDtls match {
      case Some(data) => Some(PersonName(data.personName.firstName,
        data.personName.middleName,
        data.personName.lastName,
        data.previousNameDetails,
        data.othrNamesOrAliasesDetails
      ))
      case None => None
    }
  }

  implicit def convOtherNames(otherNames: Option[OthrNamesOrAliasesDetails]): Option[String] = {
    otherNames match {
      case Some(names) => names.aliases.fold[Option[String]](None)(x => x.headOption)
      case None => None
    }
  }
}
