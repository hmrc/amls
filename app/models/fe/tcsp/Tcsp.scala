/*
 * Copyright 2023 HM Revenue & Customs
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

case class Tcsp(tcspTypes: Option[TcspTypes] = None,
                onlyOffTheShelfCompsSold: Option[OnlyOffTheShelfCompsSold] = None,
                complexCorpStructureCreation: Option[ComplexCorpStructureCreation] = None,
                providedServices: Option[ProvidedServices] = None,
                doesServicesOfAnotherTCSP: Option[Boolean] = None,
                servicesOfAnotherTCSP: Option[ServicesOfAnotherTCSP] = None) {

  def tcspTypes(trust: TcspTypes): Tcsp =
    this.copy(tcspTypes = Some(trust))

  def onlyOffTheShelfCompsSold(x: OnlyOffTheShelfCompsSold): Tcsp =
    this.copy(onlyOffTheShelfCompsSold = Some(x))

  def complexCorpStructureCreation(x: ComplexCorpStructureCreation): Tcsp =
    this.copy(complexCorpStructureCreation = Some(x))

  def providedServices(ps: ProvidedServices): Tcsp =
    this.copy(providedServices = Some(ps))

  def doesServicesOfAnotherTCSP(x: Boolean): Tcsp =
    this.copy(doesServicesOfAnotherTCSP = Some(x))

  def servicesOfAnotherTCSP(p: ServicesOfAnotherTCSP): Tcsp =
    this.copy(servicesOfAnotherTCSP = Some(p))
}

object Tcsp {

  def convBool(tcsp: models.des.tcsp.TcspAll): Option[Boolean] = {
    Some(tcsp.anotherTcspServiceProvider)
  }

  import play.api.libs.json._

  val key = "tcsp"

  implicit val formats = Json.format[Tcsp]

  implicit def default(tcsp: Option[Tcsp]): Tcsp =
    tcsp.getOrElse(Tcsp())

  implicit def conv(view: SubscriptionView): Option[Tcsp] = {
    (view.tcspAll, view.businessActivities.tcspServicesOffered) match {
      case (Some(tcspAll), _) => Some(Tcsp(view, view, view, view.businessActivities, convBool(tcspAll), view))
      case (None, Some(tcspServicesOffered)) => Some(Tcsp(view, view, view, view.businessActivities, None, view))
      case (None, None) => None
      case _ => None
    }
  }
}
