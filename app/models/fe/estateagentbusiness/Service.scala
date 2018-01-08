/*
 * Copyright 2018 HM Revenue & Customs
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

package models.fe.estateagentbusiness

import models.des.businessactivities.EabServices
import play.api.data.validation.ValidationError
import play.api.libs.json._
import utils.CommonMethods

case class Services(services: Set[Service], dateOfChange: Option[String] = None)

sealed trait Service

case object Commercial extends Service

case object Auction extends Service

case object Relocation extends Service

case object BusinessTransfer extends Service

case object AssetManagement extends Service

case object LandManagement extends Service

case object Development extends Service

case object SocialHousing extends Service

case object Residential extends Service

object Service {

  implicit val jsonServiceReads: Reads[Service] =
    Reads {
      case JsString("01") => JsSuccess(Residential)
      case JsString("02") => JsSuccess(Commercial)
      case JsString("03") => JsSuccess(Auction)
      case JsString("04") => JsSuccess(Relocation)
      case JsString("05") => JsSuccess(BusinessTransfer)
      case JsString("06") => JsSuccess(AssetManagement)
      case JsString("07") => JsSuccess(LandManagement)
      case JsString("08") => JsSuccess(Development)
      case JsString("09") => JsSuccess(SocialHousing)
      case _ => JsError((JsPath \ "services") -> ValidationError("error.invalid"))
    }

  implicit val jsonServiceWrites =
    Writes[Service] {
      case Residential => JsString("01")
      case Commercial => JsString("02")
      case Auction => JsString("03")
      case Relocation => JsString("04")
      case BusinessTransfer => JsString("05")
      case AssetManagement => JsString("06")
      case LandManagement => JsString("07")
      case Development => JsString("08")
      case SocialHousing => JsString("09")
    }
}

object Services {
  implicit val formats = Json.format[Services]

  implicit def conv(des: Option[EabServices]): Option[Services] = {

    des.flatMap {
      eabServices => {
        val `empty` = Set.empty[Service]

        val services = Set(
          CommonMethods.getSpecificType[Service](eabServices.residentialEstateAgency, Residential),
          CommonMethods.getSpecificType[Service](eabServices.commercialEstateAgency, Commercial),
          CommonMethods.getSpecificType[Service](eabServices.auctioneer, Auction),
          CommonMethods.getSpecificType[Service](eabServices.relocationAgent, Relocation),
          CommonMethods.getSpecificType[Service](eabServices.businessTransferAgent, BusinessTransfer),
          CommonMethods.getSpecificType[Service](eabServices.assetManagementCompany, AssetManagement),
          CommonMethods.getSpecificType[Service](eabServices.landManagementAgent, LandManagement),
          CommonMethods.getSpecificType[Service](eabServices.developmentCompany, Development),
          CommonMethods.getSpecificType[Service](eabServices.socialHousingProvider, SocialHousing)
        ).flatten

        services match {
          case `empty` => None
          case _ => Some(Services(services))
        }

      }
    }

  }
}
