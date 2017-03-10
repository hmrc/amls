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

package models.des.estateagentbusiness

import models.fe.estateagentbusiness.EstateAgentBusiness
import models.fe.estateagentbusiness._

import play.api.libs.json.Json

case class EabResdEstAgncy(regWithRedressScheme:Boolean,
  whichRedressScheme:Option[String],
  specifyOther: Option[String]
)

object EabResdEstAgncy {
  implicit val format = Json.format[EabResdEstAgncy]

  implicit def convert(eabOpt: Option[EstateAgentBusiness]): Option[EabResdEstAgncy] = {
    eabOpt match {
      case Some(eab) => eab
      case _ => None
    }
  }

  implicit def convert(eab: EstateAgentBusiness): Option[EabResdEstAgncy] = {
    eab.redressScheme match {
      case Some(Other(x)) => Some(EabResdEstAgncy(true, Some("Other"), Some(x)))
      case Some(ThePropertyOmbudsman) => Some(EabResdEstAgncy(true, Some("The Property Ombudsman Limited"), None))
      case Some(OmbudsmanServices) => Some(EabResdEstAgncy(true, Some("Ombudsman Services"), None))
      case Some(PropertyRedressScheme) => Some(EabResdEstAgncy(true, Some("Property Redress Scheme"), None))
      case Some(RedressSchemedNo) => Some(EabResdEstAgncy(false, None, None))
      case _ => None
    }
  }
}
