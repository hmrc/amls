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

import play.api.libs.json.{Json, OFormat}

case class LettingAgents(clientMoneyProtection: Option[Boolean])

object LettingAgents {

  implicit val format: OFormat[LettingAgents] = Json.format[LettingAgents]

  implicit def conv(eabOpt: Option[models.fe.eab.Eab]): Option[LettingAgents] =
    eabOpt match {
      case Some(eab) =>
        eab.data.clientMoneyProtectionScheme match {
          case Some(true)  => Some(LettingAgents(clientMoneyProtection = Some(true)))
          case Some(false) => Some(LettingAgents(clientMoneyProtection = Some(false)))
          case _           => None
        }
      case _         => None
    }
}
