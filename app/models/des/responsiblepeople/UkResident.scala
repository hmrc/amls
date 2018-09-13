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

package models.des.responsiblepeople

import config.AmlsConfig
import models.fe.responsiblepeople.{DateOfBirth, UKResidence}
import play.api.libs.json.Json

case class UkResident(nino: String)

object UkResident {
  implicit val format = Json.format[UkResident]

  implicit def convert(dtls: UKResidence, dob: Option[DateOfBirth] = None) : Option[IdDetail] = {
      val dateOfBirth: Option[String] = if (AmlsConfig.phase2Changes) {
          dob map { _.dateOfBirth.toString }
      } else {
          None
      }
      Some(IdDetail(
          Some(UkResident(dtls.nino)),
          None,
          dateOfBirth = dateOfBirth
      ))
  }
}
