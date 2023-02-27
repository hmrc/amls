/*
 * Copyright 2022 HM Revenue & Customs
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

package models.des.msb

import models.fe.moneyservicebusiness.{BusinessUseAnIPSPNo, BusinessUseAnIPSPYes, BusinessUseAnIPSP}
import play.api.libs.json.Json

case class IpspServicesDetails (
                                 ipspServicesUsed: Boolean,
                                 ipspDetails: Option[Seq[IpspDetails]]
                               )

object IpspServicesDetails {

  implicit val format = Json.format[IpspServicesDetails]

  implicit def convIpsp(ipsp: Option[BusinessUseAnIPSP]) : IpspServicesDetails = {

    ipsp match {
      case Some(data) => data match {
        case BusinessUseAnIPSPYes(name, refNumber) => IpspServicesDetails(true, Some(Seq(IpspDetails(name, refNumber))))
        case BusinessUseAnIPSPNo => IpspServicesDetails(false, None)
      }
      case None => IpspServicesDetails(false, None)
    }
  }
}
