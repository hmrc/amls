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

import models.fe.responsiblepeople.ResponsiblePeople
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class PreviousNameDetails (nameEverChanged: Boolean,
                                previousName: Option[PersonName],
                                dateOfChange: Option[String],
                                dateChangeFlag: Boolean = false
                               )

object PreviousNameDetails {
  implicit val jsonReads = {
    (
      (__ \ "nameEverChanged").read[Boolean] and
        (__ \ "previousName").readNullable[PersonName] and
        (__ \ "dateOfChange").readNullable[String] and
        ((__ \ "dateChangeFlag").read[Boolean] or Reads.pure(false))
      ) (PreviousNameDetails.apply _)
  }

  implicit def jsonWrites = Json.writes[PreviousNameDetails]

  def from(person: ResponsiblePeople): Option[PreviousNameDetails] = {
    (person.legalName, person.legalNameChangeDate) match {
      case (Some(name), date) if name.hasPreviousName => Some(PreviousNameDetails(true, name, date.map(_.toString)))
      case _ => Some(PreviousNameDetails(false, None, None))
    }
  }
}
