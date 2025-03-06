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

package models.des.estateagentbusiness

import models.fe.eab.Eab
import play.api.libs.json.{Json, OFormat}

case class EabResdEstAgncy(regWithRedressScheme: Boolean, whichRedressScheme: Option[String])

object EabResdEstAgncy {
  implicit val format: OFormat[EabResdEstAgncy] = Json.format[EabResdEstAgncy]

  implicit def conv(eabOpt: Option[Eab]): Option[EabResdEstAgncy] =
    eabOpt match {
      case Some(eab) => eab
      case _         => None
    }

  implicit def convert(eab: Eab): Option[EabResdEstAgncy] =
    eab.data.redressScheme match {
      case Some("propertyOmbudsman")     => Some(EabResdEstAgncy(true, Some("The Property Ombudsman Limited")))
      case Some("propertyRedressScheme") => Some(EabResdEstAgncy(true, Some("Property Redress Scheme")))
      case Some("notRegistered")         => Some(EabResdEstAgncy(false, None))
      case _                             => None
    }
}
