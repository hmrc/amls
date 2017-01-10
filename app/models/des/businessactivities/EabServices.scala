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

package models.des.businessactivities

import models.fe.estateagentbusiness._
import play.api.libs.json.Json

case class EabServices(
                        residentialEstateAgency : Boolean,
                        commercialEstateAgency : Boolean,
                        auctioneer : Boolean,
                        relocationAgent : Boolean,
                        businessTransferAgent : Boolean,
                        assetManagementCompany : Boolean,
                        landManagementAgent : Boolean,
                        developmentCompany : Boolean,
                        socialHousingProvider : Boolean
                      )

object EabServices {
  implicit val format = Json.format[EabServices]

  val none = EabServices(false, false, false, false, false, false, false, false, false)

  implicit def convert(eab : Option[models.fe.estateagentbusiness.EstateAgentBusiness]) : Option[EabServices] = {

    eab match {
      case Some(eab) => {
        eab.services match {
          case Some(services) => services
          case None => None
        }
      }
      case _ => None
    }

  }

  implicit def convServices(services: Services) : Option[EabServices] = {
    val eabServices = services.services.foldLeft[EabServices](none)((eabServices: EabServices, service) => service match {
      case Residential => eabServices.copy(residentialEstateAgency = true)
      case Commercial => eabServices.copy(commercialEstateAgency = true)
      case Auction => eabServices.copy(auctioneer = true)
      case Relocation => eabServices.copy(relocationAgent = true)
      case BusinessTransfer => eabServices.copy(businessTransferAgent = true)
      case AssetManagement => eabServices.copy(assetManagementCompany = true)
      case LandManagement => eabServices.copy(landManagementAgent = true)
      case Development => eabServices.copy(developmentCompany = true)
      case SocialHousing => eabServices.copy(socialHousingProvider = true)
    })
    Some(eabServices)

  }
}
