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

package models.des.supervision

import models.fe.supervision.Supervision
import play.api.libs.json.{Json, OFormat}

case class AspOrTcsp(supervisionDetails: Option[SupervisionDetails], professionalBodyDetails: Option[ProfessionalBodyDetails])

object AspOrTcsp {

  implicit val format: OFormat[AspOrTcsp] = Json.format[AspOrTcsp]

  def conv(supervision: Option[Supervision]): Option[AspOrTcsp] = {
    supervision match {
      case Some(x) if x != Supervision() => Some(AspOrTcsp(SupervisionDetails.conv(x.anotherBody), ProfessionalBodyDetails.conv(x)))
      case _ => None
    }
  }

  def conv1(supervision: Option[Supervision]): Option[AspOrTcsp] = {
    supervision match {
      case Some(x) if x != Supervision() => Some(AspOrTcsp(SupervisionDetails.conv1(x.anotherBody), ProfessionalBodyDetails.conv(x)))
      case _ => None
    }
  }

}
