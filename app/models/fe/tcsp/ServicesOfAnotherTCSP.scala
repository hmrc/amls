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

package models.fe.tcsp

import play.api.libs.json._

sealed trait ServicesOfAnotherTCSP

case class ServicesOfAnotherTCSPYes(mlrRefNumber: String) extends ServicesOfAnotherTCSP

case object ServicesOfAnotherTCSPNo extends ServicesOfAnotherTCSP

object ServicesOfAnotherTCSP {

  implicit val jsonReads: Reads[ServicesOfAnotherTCSP] =
    (__ \ "servicesOfAnotherTCSP").read[Boolean] flatMap {
      case true => (__ \ "mlrRefNumber").read[String] map ServicesOfAnotherTCSPYes.apply
      case false => Reads(__ => JsSuccess(ServicesOfAnotherTCSPNo))
    }

  implicit val jsonWrites = Writes[ServicesOfAnotherTCSP] {
    case ServicesOfAnotherTCSPYes(value) => Json.obj(
          "servicesOfAnotherTCSP" -> true,
          "mlrRefNumber" -> value
    )
    case ServicesOfAnotherTCSPNo => Json.obj("servicesOfAnotherTCSP" -> false)

  }

  def mlrExists(mlrRefNo: Option[String]) = {
    !mlrRefNo.getOrElse("").isEmpty
  }

  def mlrNo(mlrRefNo: Option[String]) = {
    mlrRefNo.getOrElse("")
  }

  def tcspServicesOffered(desView: models.des.SubscriptionView) = {
    desView.businessActivities.tcspServicesOffered.isDefined
  }

  def tcspServicesforRegOffBusinessAddrVirtualOff(desView: models.des.SubscriptionView) = {
    desView.businessActivities.tcspServicesforRegOffBusinessAddrVirtualOff.isDefined
  }

  def noneImpliesServicesOfAnotherTCSPNo(desView: models.des.SubscriptionView) = {
    (tcspServicesOffered(desView) || tcspServicesforRegOffBusinessAddrVirtualOff(desView))
  }

  implicit def conv(desView: models.des.SubscriptionView): Option[ServicesOfAnotherTCSP] = {
    desView.tcspAll match{
      case Some(tcsp) => tcsp.anotherTcspServiceProvider match {
        case true if mlrExists(tcsp.tcspMlrRef) =>
          Some(ServicesOfAnotherTCSPYes(mlrNo(tcsp.tcspMlrRef)))
        case _ => Some(ServicesOfAnotherTCSPNo)
      }
      case None if noneImpliesServicesOfAnotherTCSPNo(desView) =>
        Some(ServicesOfAnotherTCSPNo)
      case _ => None
    }
  }
}
