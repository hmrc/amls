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

package models.fe.responsiblepeople

import models.des.responsiblepeople.PreviousNameDetails
import org.joda.time.LocalDate
import play.api.libs.json.Json

case class PreviousName(
                         firstName: Option[String],
                         middleName: Option[String],
                         lastName: Option[String],
                         date: LocalDate
                       )

object PreviousName {
  implicit val format = Json.format[PreviousName]

  implicit def conv(desPrevNames: Option[PreviousNameDetails]): Option[PreviousName] = {
    desPrevNames match {
      case Some(pName) => pName.nameEverChanged match {
        case true => {
          pName.previousName match {
            case Some(name) => Some(PreviousName(Some(name.firstName),
              name.middleName,
              Some(name.lastName),
              pName.dateOfChange.fold(LocalDate.now)(x => LocalDate.parse(x))
            ))
            case None => None
          }
        }
        case false => None
      }
      case None => None
    }
  }
}
