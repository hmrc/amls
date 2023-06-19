/*
 * Copyright 2023 HM Revenue & Customs
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

import models.des.responsiblepeople.{IdDetail, ResponsiblePersons}

import java.time.LocalDate
import play.api.libs.json.Json

case class DateOfBirth(dateOfBirth: LocalDate)

object DateOfBirth {

  implicit val format = Json.format[DateOfBirth]

  implicit def conv(responsiblePeople: ResponsiblePersons): Option[DateOfBirth] = {
    val idDetail: Option[IdDetail] = for {
      nd <- responsiblePeople.nationalityDetails
      id <- nd.idDetails
    } yield id

    val nonUkDob = idDetail.flatMap(idDetail =>
      idDetail.nonUkResident.flatMap(nonUkRes =>
        nonUkRes.dateOfBirth
      )
    )

    val ukDob = idDetail.flatMap(idDetail =>
      idDetail.dateOfBirth
    )

    if (!ukDob.isEmpty) {
      Some(DateOfBirth(LocalDate.parse(ukDob.getOrElse(""))))
    } else if (!nonUkDob.isEmpty) {
      Some(DateOfBirth(LocalDate.parse(nonUkDob.getOrElse(""))))
    } else {
      None
    }
  }
}
