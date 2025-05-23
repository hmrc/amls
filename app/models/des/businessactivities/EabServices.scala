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

package models.des.businessactivities

import play.api.libs.json.{Json, OFormat}

case class EabServices(
  residentialEstateAgency: Boolean,
  commercialEstateAgency: Boolean,
  auctioneer: Boolean,
  relocationAgent: Boolean,
  businessTransferAgent: Boolean,
  assetManagementCompany: Boolean,
  landManagementAgent: Boolean,
  developmentCompany: Boolean,
  socialHousingProvider: Boolean,
  lettingAgents: Option[Boolean] = None
)

object EabServices {
  implicit val format: OFormat[EabServices] = Json.format[EabServices]

  def default: EabServices =
    EabServices(
      residentialEstateAgency = false,
      commercialEstateAgency = false,
      auctioneer = false,
      relocationAgent = false,
      businessTransferAgent = false,
      assetManagementCompany = false,
      landManagementAgent = false,
      developmentCompany = false,
      socialHousingProvider = false,
      Some(false)
    )

  implicit def convert(eab: Option[models.fe.eab.Eab]): Option[EabServices] = eab match {
    case Some(eab) =>
      convServices(eab.data.eabServicesProvided)
    case _         => None
  }

  implicit def convServices(services: List[String]): Option[EabServices] = {
    val eabServices = services.foldLeft[EabServices](default)((eabServices: EabServices, service) =>
      service match {
        case "residential"            => eabServices.copy(residentialEstateAgency = true)
        case "commercial"             => eabServices.copy(commercialEstateAgency = true)
        case "auctioneering"          => eabServices.copy(auctioneer = true)
        case "relocation"             => eabServices.copy(relocationAgent = true)
        case "businessTransfer"       => eabServices.copy(businessTransferAgent = true)
        case "assetManagement"        => eabServices.copy(assetManagementCompany = true)
        case "landManagement"         => eabServices.copy(landManagementAgent = true)
        case "developmentCompany"     => eabServices.copy(developmentCompany = true)
        case "socialHousingProvision" => eabServices.copy(socialHousingProvider = true)
        case "lettings"               => eabServices.copy(lettingAgents = Some(true))
      }
    )
    Some(eabServices)
  }
}
