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

package models.fe.eab

import models.des.SubscriptionView
import models.des.businessactivities.{BusinessActivities, EabServices, MlrActivitiesAppliedFor}
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy, LettingAgents}
import play.api.libs.json._
import utils.CommonMethods

final case class Eab(data: EabData)

object Eab {

  implicit val format = Json.format[Eab]

  implicit def conv(view: SubscriptionView): Option[Eab] = {

    hasEabSector(view) match {
      case true => eabSection(view.businessActivities, view.eabAll, view.eabResdEstAgncy, view.lettingAgents)
      case _ => None
    }

  }

  private implicit def eabSection(ba: BusinessActivities, eabAll: Option[EabAll], eabResdEA: Option[EabResdEstAgncy], la: Option[LettingAgents]) = {

    Some(
      Eab(
        EabData(
          ba.eabServicesCarriedOut,
          None,
          (eabResdEA, redressSchemeApplies(ba)) match {
            case (Some(redress), _) => getRedressScheme(redress)
            case (None, true) => Some("notRegistered")
            case _ => None
          },
          (la, lettingAgentApplies(ba)) match {
            case (Some(lettings), _) => lettings.clientMoneyProtection
            case (None, true) => Some(false)
            case _ => None
          },
          eabAll match {
            case Some(all) => all.estateAgencyActProhibition
            case _ => false
          },
          eabAll match {
            case Some(all) => all.estAgncActProhibProvideDetails
            case _ => None
          },
          eabAll match {
            case Some(all) => all.prevWarnedWRegToEstateAgencyActivities
            case _ => false
          },
          (eabAll) match {
            case Some(all) => all.prevWarnWRegProvideDetails
            case _ => None
          }
        )
      )
    )
  }

  private implicit def getRedressScheme(eab: EabResdEstAgncy): Option[String] = {
    eab.regWithRedressScheme match {
      case true => {
        val redressOption = eab.whichRedressScheme.getOrElse("")
        redressOption match {
          case "The Property Ombudsman Limited" => Some("propertyOmbudsman")
          case "Property Redress Scheme" => Some("propertyRedressScheme")
          case "Ombudsman Services" => Some("ombudsmanServices")
          case "Other" => Some("other")
          case _ => None
        }
      }
      case false => Some("notRegistered")
    }
  }

  private implicit def hasEabSector(response: SubscriptionView) = {
    response.businessActivities.mlrActivitiesAppliedFor match {
      case Some(MlrActivitiesAppliedFor(_, _, _, _, true, _, _, _)) => true
      case _ => false
    }
  }

  private implicit def redressSchemeApplies(businessActivities: BusinessActivities) = {
    businessActivities.eabServicesCarriedOut match {
      case Some(EabServices(true, _, _, _, _, _, _, _, _, _)) => true
      case Some(EabServices(_, _, _, _, _, _, _, _, _, Some(true))) => true
      case _ => false
    }
  }

  private implicit def lettingAgentApplies(businessActivities: BusinessActivities) = {
    businessActivities.eabServicesCarriedOut match {
      case Some(EabServices(_, _, _, _, _, _, _, _, _, Some(true))) => true
      case _ => false
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
      case None => List() // Catch all - should never be no services in API5 where we have EAB section...
    }).flatten
  }
}
