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

package models.des.aboutyou

import play.api.libs.json.{Json, OFormat}

case class IndividualDetails(firstName: String, middleName: Option[String], lastName: String)

object IndividualDetails {

  implicit val formats: OFormat[IndividualDetails] = Json.format[IndividualDetails]

  implicit def convert(person: models.fe.declaration.AddPerson): Option[IndividualDetails] =
    person.firstName match {
      case "" => None
      case _  => Some(IndividualDetails(person.firstName, person.middleName, person.lastName))
    }
}
