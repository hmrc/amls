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

package models.fe.businessactivities

import models.des.businessactivities.FranchiseDetails
import play.api.libs.json._

sealed trait BusinessFranchise

case class BusinessFranchiseYes(value: String) extends BusinessFranchise

case object BusinessFranchiseNo extends BusinessFranchise

object BusinessFranchise {

  implicit val jsonReads: Reads[BusinessFranchise] =
    (__ \ "businessFranchise").read[Boolean] flatMap {
      case true  => (__ \ "franchiseName").read[String] map BusinessFranchiseYes.apply
      case false => Reads(_ => JsSuccess(BusinessFranchiseNo))
    }

  implicit val jsonWrites: Writes[BusinessFranchise] = Writes[BusinessFranchise] {
    case BusinessFranchiseYes(value) =>
      Json.obj(
        "businessFranchise" -> true,
        "franchiseName"     -> value
      )
    case BusinessFranchiseNo         => Json.obj("businessFranchise" -> false)
  }

  def conv(des: Option[FranchiseDetails]): Option[BusinessFranchise] =
    des match {
      case Some(data) =>
        data.franchiserName.fold[Option[String]](None)(x => x.headOption) match {
          case Some(yes) => Some(BusinessFranchiseYes(yes))
          case None      => Some(BusinessFranchiseNo)
        }
      case None       => Some(BusinessFranchiseNo)
    }
}
