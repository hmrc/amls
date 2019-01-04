/*
 * Copyright 2019 HM Revenue & Customs
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

import models.des.businessactivities.{BusinessActivitiesAll, BusinessActivityDetails, ExpectedAMLSTurnover => DesExpectedAMLSTurnover}
import play.api.libs.json._

sealed trait InvolvedInOther

case class InvolvedInOtherYes(details: String) extends InvolvedInOther

case object InvolvedInOtherNo extends InvolvedInOther

object InvolvedInOther {

  implicit val jsonReads: Reads[InvolvedInOther] =
    (__ \ "involvedInOther").read[Boolean] flatMap {
      case true => (__ \ "details").read[String] map InvolvedInOtherYes.apply
      case false => Reads(_ => JsSuccess(InvolvedInOtherNo))
    }

  implicit val jsonWrites = Writes[InvolvedInOther] {
    case InvolvedInOtherYes(details) => Json.obj(
                                          "involvedInOther" -> true,
                                          "details" -> details
                                        )
    case involvedInOtherNo => Json.obj("involvedInOther" -> false)
  }

  def conv(activityDtls: BusinessActivityDetails) : Option[InvolvedInOther] = {
    activityDtls.actvtsBusRegForOnlyActvtsCarOut match {
      case true => Some(InvolvedInOtherNo)
      case false => activityDtls.respActvtsBusRegForOnlyActvtsCarOut.fold[Option[InvolvedInOther]](None)(x => x.otherBusActivitiesCarriedOut match {
        case Some(other) => Some(InvolvedInOtherYes(other.otherBusinessActivities))
        case None => None
      })
    }
  }
}
