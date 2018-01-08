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

import models.des.estateagentbusiness.EabAll
import play.api.libs.json._

sealed trait PenalisedUnderEstateAgentsAct

case class PenalisedUnderEstateAgentsActYes(value: String) extends PenalisedUnderEstateAgentsAct

case object PenalisedUnderEstateAgentsActNo extends PenalisedUnderEstateAgentsAct

object PenalisedUnderEstateAgentsAct {


  implicit val jsonReads: Reads[PenalisedUnderEstateAgentsAct] =
    (__ \ "penalisedUnderEstateAgentsAct").read[Boolean] flatMap {
      case true => (__ \ "penalisedUnderEstateAgentsActDetails").read[String] map (PenalisedUnderEstateAgentsActYes.apply _)
      case false => Reads(_ => JsSuccess(PenalisedUnderEstateAgentsActNo))
    }

  implicit val jsonWrites = Writes[PenalisedUnderEstateAgentsAct] {
    case PenalisedUnderEstateAgentsActYes(value) => Json.obj(
      "penalisedUnderEstateAgentsAct" -> true,
      "penalisedUnderEstateAgentsActDetails" -> value
    )
    case PenalisedUnderEstateAgentsActNo => Json.obj("penalisedUnderEstateAgentsAct" -> false)
  }

  implicit def conv(desView: models.des.SubscriptionView): Option[PenalisedUnderEstateAgentsAct] = {
    desView.eabAll match {
      case Some(data) => data.estateAgencyActProhibition match {
        case true => Some(PenalisedUnderEstateAgentsActYes(data.estAgncActProhibProvideDetails.getOrElse("")))
        case false => Some(PenalisedUnderEstateAgentsActNo)
      }
      case None if(desView.businessActivities.eabServicesCarriedOut.isDefined) => Some(PenalisedUnderEstateAgentsActNo)
      case None => None
    }
  }
}
