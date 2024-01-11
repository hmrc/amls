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

import models.fe.responsiblepeople.ResponsiblePeople
import play.api.libs.json.{Json, OFormat}

case class PreviousNameDetails(nameEverChanged: Boolean, previousName: Option[PersonName], dateOfChange: Option[String], dateChangeFlag: Option[Boolean])

object PreviousNameDetails {
  implicit val format: OFormat[PreviousNameDetails] = Json.format[PreviousNameDetails]


  def from(person: ResponsiblePeople, amendVariation: Boolean): Option[PreviousNameDetails] = {
    val dateChangeFlag = amendVariation match {
      case true => Some(false)
      case false => None
    }
    (person.legalName, person.legalNameChangeDate) match {
      case (Some(name), date) if name.hasPreviousName => Some(PreviousNameDetails(true, name, date.map(_.toString), dateChangeFlag))
      case _ => Some(PreviousNameDetails(false, None, None, dateChangeFlag))
    }
  }
}
