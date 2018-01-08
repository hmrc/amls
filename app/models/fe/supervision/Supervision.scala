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

package models.fe.supervision

import models.des.supervision.AspOrTcsp

case class Supervision(anotherBody: Option[AnotherBody] = None,
                       professionalBodyMember: Option[ProfessionalBodyMember] = None,
                       businessTypes: Option[BusinessTypes] = None,
                       professionalBody: Option[ProfessionalBody] = None)

object Supervision {

  import play.api.libs.json._

  implicit val formats = Json.format[Supervision]

  implicit def conv(aspOrTcsp: Option[AspOrTcsp]) : Option[Supervision] = {
    aspOrTcsp match {
      case Some(supervision) => Some(Supervision(
        supervision.supervisionDetails,
        supervision.professionalBodyDetails,
        supervision.professionalBodyDetails,
        supervision.professionalBodyDetails
      ))
      case None => Some(Supervision(
        Some(AnotherBodyNo),
        Some(ProfessionalBodyMemberNo),
        None,
        Some(ProfessionalBodyNo)
      ))
    }
  }

}
