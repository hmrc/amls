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

import play.api.libs.json.{Json, OFormat}
import models.fe.responsiblepeople.ResponsiblePeople

case class NameDetails(
  personName: PersonName,
  othrNamesOrAliasesDetails: Option[OthrNamesOrAliasesDetails], // it is not optional
  previousNameDetails: Option[PreviousNameDetails]
) //it is not optional

object NameDetails {
  implicit val format: OFormat[NameDetails] = Json.format[NameDetails]

  def from(maybePerson: Option[ResponsiblePeople], amendVariation: Boolean): Option[NameDetails] =
    maybePerson match {
      case Some(person) =>
        Some(
          NameDetails(
            person.personName,
            OthrNamesOrAliasesDetails.from(person),
            PreviousNameDetails.from(person, amendVariation)
          )
        )
      case _            => None
    }
}
