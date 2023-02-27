/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json.Json
import models.fe.responsiblepeople.{PersonName => FEPersonName, PreviousName}

case class PersonName (firstName: Option[String],
                       middleName: Option[String],
                       lastName: Option[String])

case object PersonName {
  implicit val format = Json.format[PersonName]

  implicit def convert(maybePerson: Option[FEPersonName]) : PersonName = maybePerson match {
    case Some(person) => PersonName(Some(person.firstName), person.middleName, Some(person.lastName))
    case _ => PersonName(None, None, None)
  }

  implicit def convertPreviousName(person: PreviousName) : Option[PersonName] = {
    Some(PersonName(person.firstName, person.middleName, person.lastName))
  }
}
