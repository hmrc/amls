/*
 * Copyright 2017 HM Revenue & Customs
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
import models.fe.responsiblepeople.{PersonName => FEPersonName}

case class NameDetails (personName: PersonName,
                        othrNamesOrAliasesDetails: Option[OthrNamesOrAliasesDetails], //it is not optional
                        previousNameDetails: Option[PreviousNameDetails]) //it is not optional

object NameDetails {
  implicit val format = Json.format[NameDetails]

  implicit def convert(person: Option[FEPersonName]) : Option[NameDetails] = {
    person match {
      case Some(data) => Some(NameDetails(data, data.otherNames, data.previousName))
      case _ => None
    }
  }
}
