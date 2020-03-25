/*
 * Copyright 2020 HM Revenue & Customs
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

import config.ApplicationConfig
import models.fe.estateagentbusiness._
import play.api.Play
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
                        socialHousingProvider : Boolean,
                        lettingAgents : Option[Boolean] = None
                      )

object EabServices {
  implicit val format = Json.format[EabServices]

  def appConfig = Play.current.injector.instanceOf[ApplicationConfig]

  def default = if(appConfig.phase3Release2La) {
    EabServices(false, false, false, false, false, false, false, false, false, Some(false))
  } else {
    EabServices(false, false, false, false, false, false, false, false, false, None)
  }

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
    val eabServices = services.services.foldLeft[EabServices](default)((eabServices: EabServices, service) => service match {
      case Residential => eabServices.copy(residentialEstateAgency = true)
      case Commercial => eabServices.copy(commercialEstateAgency = true)
      case Auction => eabServices.copy(auctioneer = true)
      case Relocation => eabServices.copy(relocationAgent = true)
      case BusinessTransfer => eabServices.copy(businessTransferAgent = true)
      case AssetManagement => eabServices.copy(assetManagementCompany = true)
      case LandManagement => eabServices.copy(landManagementAgent = true)
      case Development => eabServices.copy(developmentCompany = true)
      case SocialHousing => eabServices.copy(socialHousingProvider = true)
      case Lettings => eabServices.copy(lettingAgents = Some(true))
    })
    Some(eabServices)

  }
}
