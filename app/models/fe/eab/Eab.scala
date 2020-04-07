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

package models.fe.eab

import models.des.SubscriptionView
import models.des.businessactivities.EabServices
import models.des.estateagentbusiness.EabResdEstAgncy
import play.api.libs.json._
import utils.CommonMethods

final case class Eab(data: EabData)

object Eab  {

  implicit val format = Json.format[Eab]

  implicit def conv(view: SubscriptionView): Option[Eab] = {
    view match {
      case SubscriptionView(_, _, _, _, _, _, ba, _, _, _, _, _, _, _, _, Some(eabAll), Some(eabResdEA), _, _, Some(la), _) =>
        Some(Eab(EabData(
          ba.eabServicesCarriedOut,
          None,
          getRedressScheme(eabResdEA),
          la.clientMoneyProtection,
          eabAll.estateAgencyActProhibition,
          eabAll.estAgncActProhibProvideDetails,
          eabAll.prevWarnedWRegToEstateAgencyActivities,
          eabAll.prevWarnWRegProvideDetails)))
      case SubscriptionView(_, _, _, _, _, _, ba, _, _, _, _, _, _, _, _, Some(eabAll), Some(eabResdEA), _, _, _, _) =>
        Some(Eab(EabData(
          ba.eabServicesCarriedOut,
          None,
          getRedressScheme(eabResdEA),
          None,
          eabAll.estateAgencyActProhibition,
          eabAll.estAgncActProhibProvideDetails,
          eabAll.prevWarnedWRegToEstateAgencyActivities,
          eabAll.prevWarnWRegProvideDetails)))
      case SubscriptionView(_, _, _, _, _, _, ba, _, _, _, _, _, _, _, _, Some(eabAll), _, _, _, Some(la), _) =>
        Some(Eab(EabData(
          ba.eabServicesCarriedOut,
          None,
          None,
          la.clientMoneyProtection,
          eabAll.estateAgencyActProhibition,
          eabAll.estAgncActProhibProvideDetails,
          eabAll.prevWarnedWRegToEstateAgencyActivities,
          eabAll.prevWarnWRegProvideDetails
        )))
      case SubscriptionView(_, _, _, _, _, _, ba, _, _, _, _, _, _, _, _, Some(eabAll), _, _, _, _, _) =>
        Some(Eab(EabData(
          ba.eabServicesCarriedOut,
          None,
          None,
          None,
          eabAll.estateAgencyActProhibition,
          eabAll.estAgncActProhibProvideDetails,
          eabAll.prevWarnedWRegToEstateAgencyActivities,
          eabAll.prevWarnWRegProvideDetails
        )))
      case _ => None
    }
  }

  private implicit def getRedressScheme(eab:EabResdEstAgncy): Option[String] = {
    eab.regWithRedressScheme match {
      case true => {
        val redressOption = eab.whichRedressScheme.getOrElse("")
        redressOption match {
          case "The Property Ombudsman Limited" => Some("propertyOmbudsman")
          case "Property Redress Scheme" => Some("propertyRedressScheme")
          case _ => None
        }
      }
      case false => Some("notRegistered")
    }
  }

  private implicit def conv(services: Option[EabServices]): List[String] = {
    (services match {
      case Some(eab) => List(
        CommonMethods.getSpecificType(eab.assetManagementCompany, "assetManagement"),
        CommonMethods.getSpecificType(eab.auctioneer, "auctioneering"),
        CommonMethods.getSpecificType(eab.businessTransferAgent, "businessTransfer"),
        CommonMethods.getSpecificType(eab.commercialEstateAgency, "commercial"),
        CommonMethods.getSpecificType(eab.developmentCompany, "developmentCompany"),
        CommonMethods.getSpecificType(eab.landManagementAgent, "landManagement"),
        CommonMethods.getSpecificType(eab.relocationAgent, "relocation"),
        CommonMethods.getSpecificType(eab.residentialEstateAgency, "residential"),
        CommonMethods.getSpecificType(eab.socialHousingProvider, "socialHousingProvision"),
        CommonMethods.getSpecificTypeWithOption(eab.lettingAgents, "lettings"))
      case None => List()
    }).flatten
  }
}