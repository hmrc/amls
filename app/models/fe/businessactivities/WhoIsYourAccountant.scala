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

package models.fe.businessactivities

import models.des.businessactivities.{MlrAdvisorDetails, MlrAdvisor}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsSuccess, Json, Reads, Writes, __}

case class WhoIsYourAccountant(accountantsName: String,
                               accountantsTradingName: Option[String],
                               address: AccountantsAddress)

object WhoIsYourAccountant {

  import play.api.libs.json._

  implicit val jsonWrites : Writes[WhoIsYourAccountant] = Writes[WhoIsYourAccountant] { data:WhoIsYourAccountant =>
    Json.obj("accountantsName" -> data.accountantsName,
             "accountantsTradingName" -> data.accountantsTradingName) ++
      Json.toJson(data.address).as[JsObject]
  }

  implicit val jsonReads : Reads[WhoIsYourAccountant] =
    ((__ \ "accountantsName").read[String] and
      (__ \ "accountantsTradingName").readNullable[String] and
      __.read[AccountantsAddress])(WhoIsYourAccountant.apply _)

  implicit def conv(mlrAdvisor: MlrAdvisor): Option[WhoIsYourAccountant] = {
    mlrAdvisor.mlrAdvisorDetails match {
      case Some(mlrDtls) => mlrDtls.advisorNameAddress match {
        case Some(advisorDtls) => Some(WhoIsYourAccountant(advisorDtls.name, advisorDtls.tradingName, advisorDtls.address))
        case None => None
      }
      case None => None
    }
  }
}
