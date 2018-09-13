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

package models.fe.responsiblepeople

import config.AmlsConfig
import models.des.responsiblepeople.{IdDetail, ResponsiblePersons}
import org.joda.time.LocalDate
import play.api.libs.json.Json

case class DateOfBirth(dateOfBirth: LocalDate)

object DateOfBirth {

  implicit val format = Json.format[DateOfBirth]

  implicit def conv(responsiblePeople: ResponsiblePersons): Option[DateOfBirth] = {
//    val result: Option[DateOfBirth] = for {
//      nd <- responsiblePeople.nationalityDetails
//      id <- nd.idDetails
////      non <- id.nonUkResident
//    } yield if (AmlsConfig.phase2Changes) {
//      DateOfBirth(LocalDate.parse(non.dateOfBirth))
//    } else {
//      id.nonUkResident.map(
//        nonUkResident => DateOfBirth(LocalDate.parse("1990-02-24"))
//      )
//    }
    val idDetail: IdDetail = (for {
      nd <- responsiblePeople.nationalityDetails
      id <- nd.idDetails
    } yield id).getOrElse(IdDetail())

    val result: Option[DateOfBirth] =
      (idDetail.nonUkResident.isDefined, idDetail.ukResident.isDefined, AmlsConfig.phase2Changes) match {
        case (true, false, _) => Some(DateOfBirth(LocalDate.parse(idDetail.nonUkResident.get.dateOfBirth)))
        case (false, true, true) => Some(DateOfBirth(LocalDate.parse(idDetail.dateOfBirth.get)))
        case(_, _, _) => None
      }
    result
  }

}
