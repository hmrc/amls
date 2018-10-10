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
    val idDetail: Option[IdDetail] = for {
      nd <- responsiblePeople.nationalityDetails
      id <- nd.idDetails
    } yield id

    /**
      * TODO: This code isn't correct as if the non-uk resident does not have a date of birth, and for some reason it previously have a uk one, then it will default back to the
      * new one, we will need to write a new test for this.
      */

    idDetail.flatMap(idDetail =>
        idDetail.nonUkResident map(_.dateOfBirth) match {
          case Some(str) => Some(DateOfBirth(LocalDate.parse(str)))
          case _ if AmlsConfig.phase2Changes => idDetail.dateOfBirth.map(ukDOB => DateOfBirth(LocalDate.parse(ukDOB)))
          case _ => None
      }
    )
  }

}
