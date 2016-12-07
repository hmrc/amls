/*
 * Copyright 2016 HM Revenue & Customs
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

import models.des.SubscriptionView

case class Tcsp (tcspTypes: Option[TcspTypes] = None,
                 providedServices: Option[ProvidedServices] = None,
                 servicesOfAnotherTCSP: Option[ServicesOfAnotherTCSP] = None) {

  def tcspTypes(trust: TcspTypes): Tcsp =
    this.copy(tcspTypes = Some(trust))

  def providedServices(ps: ProvidedServices): Tcsp =
    this.copy(providedServices = Some(ps))

  def servicesOfAnotherTCSP(p: ServicesOfAnotherTCSP): Tcsp =
    this.copy(servicesOfAnotherTCSP = Some(p))
}

object Tcsp {
  import play.api.libs.json._

  val key = "tcsp"

  implicit val formats = Json.format[Tcsp]

  implicit def default(tcsp: Option[Tcsp]): Tcsp =
    tcsp.getOrElse(Tcsp())

  implicit def conv(view: SubscriptionView): Option[Tcsp] = {
      Some(Tcsp(view, view.businessActivities, view))
    }
}
