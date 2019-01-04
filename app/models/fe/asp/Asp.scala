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

package models.fe.asp

import models.des.SubscriptionView

case class Asp(
              services: Option[ServicesOfBusiness] = None,
              otherBusinessTaxMatters: Option[OtherBusinessTaxMatters] = None
              ) {

  def services(p: ServicesOfBusiness): Asp =
    this.copy(services = Some(p))

  def otherBusinessTaxMatters(p: OtherBusinessTaxMatters): Asp =
    this.copy(otherBusinessTaxMatters = Some(p))
}

object Asp {

  import play.api.libs.json._

  val key = "asp"

  implicit val format = Json.format[Asp]

  implicit def default(details: Option[Asp]): Asp =
    details.getOrElse(Asp())

  implicit def conv(view: SubscriptionView): Option[Asp] = {
    (view.asp,view.businessActivities.aspServicesOffered) match {
      case (Some(asp),_) => Some(Asp(view.businessActivities, view.asp))
      case (None,Some(aspActivities)) => Some(Asp(view.businessActivities, Some(OtherBusinessTaxMattersNo)))
      case (None,None) => None
    }
  }
}
