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

package models.fe.businessmatching

import models.des.msb.{MsbMtDetails}
import play.api.libs.json._

sealed trait BusinessAppliedForPSRNumber
case class BusinessAppliedForPSRNumberYes(regNumber: String) extends BusinessAppliedForPSRNumber
case object BusinessAppliedForPSRNumberNo extends BusinessAppliedForPSRNumber

object BusinessAppliedForPSRNumber {

  implicit val jsonReads: Reads[BusinessAppliedForPSRNumber] =
    (__ \ "appliedFor").read[Boolean] flatMap {
      case true => (__ \ "regNumber").read[String] map BusinessAppliedForPSRNumberYes.apply
      case false => Reads(_ => JsSuccess(BusinessAppliedForPSRNumberNo))
  }

  implicit val jsonWrites = Writes[BusinessAppliedForPSRNumber] {
    case BusinessAppliedForPSRNumberYes(value) => Json.obj(
      "appliedFor" -> true,
      "regNumber" -> value
    )
    case BusinessAppliedForPSRNumberNo => Json.obj("appliedFor" -> false)
  }

  implicit def convMsbMt(msbMt: Option[MsbMtDetails]): Option[BusinessAppliedForPSRNumber] = {
    msbMt match {
      case Some(msbDtls) => msbDtls.applyForFcapsrRegNo match {
        case true =>Some(BusinessAppliedForPSRNumberYes(msbDtls.fcapsrRefNo.getOrElse("")))
        case false =>Some(BusinessAppliedForPSRNumberNo)
      }
      case None => None
    }
  }
}
