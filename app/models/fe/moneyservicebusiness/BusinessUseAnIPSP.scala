/*
 * Copyright 2022 HM Revenue & Customs
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

package models.fe.moneyservicebusiness

import models.des.msb.{IpspDetails, MsbMtDetails}
import play.api.libs.json._

sealed trait BusinessUseAnIPSP

case class BusinessUseAnIPSPYes(name: String , reference: String) extends BusinessUseAnIPSP

case object BusinessUseAnIPSPNo extends BusinessUseAnIPSP

object BusinessUseAnIPSP {

  implicit val jsonReads: Reads[BusinessUseAnIPSP] = {
    import play.api.libs.functional.syntax._
    (__ \ "useAnIPSP").read[Boolean] flatMap {
      case true => ((__ \ "name").read[String] and
        (__ \ "referenceNumber").read[String]) (BusinessUseAnIPSPYes.apply _)
      case false => Reads(_ => JsSuccess(BusinessUseAnIPSPNo))
    }
  }

  implicit val jsonWrites = Writes[BusinessUseAnIPSP] {
    case BusinessUseAnIPSPYes(name ,referenceNumber) => Json.obj(
                                          "useAnIPSP" -> true,
                                           "name" -> name,
                                           "referenceNumber" -> referenceNumber

                                        )
    case BusinessUseAnIPSPNo => Json.obj("useAnIPSP" -> false)
  }

  implicit def convMsbMt(msbMt: Option[MsbMtDetails]): Option[BusinessUseAnIPSP] = {
    msbMt match {
      case Some(msbDtls) => convIdDetails(msbDtls.ipspServicesDetails.ipspDetails.fold[Option[IpspDetails]](None)(x => x.headOption))
      case None => None
    }
  }

  def convIdDetails(ipspDtls: Option[IpspDetails]) : Option[BusinessUseAnIPSP] = {
    ipspDtls match {
      case Some(ipsp) => Some(BusinessUseAnIPSPYes(ipsp.ipspName, ipsp.ipspMlrRegNo))
      case None => Some(BusinessUseAnIPSPNo)
    }
  }

}
