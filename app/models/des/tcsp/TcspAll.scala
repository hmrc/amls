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

package models.des.tcsp

import models.fe.tcsp.{ServicesOfAnotherTCSP, ServicesOfAnotherTCSPNo, ServicesOfAnotherTCSPYes, Tcsp}
import play.api.libs.json.{Json, OFormat}

case class TcspAll(anotherTcspServiceProvider: Boolean, tcspMlrRef: Option[String])

object TcspAll {

  implicit val format: OFormat[TcspAll] = Json.format[TcspAll]

  implicit def conv(tcsp: Tcsp): TcspAll =
    (tcsp.doesServicesOfAnotherTCSP, tcsp.servicesOfAnotherTCSP) match {
      case (Some(x), Some(data)) => (x, data)
      case _                     => TcspAll(false, None)
    }

  implicit def conv1(x: (Boolean, ServicesOfAnotherTCSP)): TcspAll =
    (x._1, x._2) match {
      case (true, ServicesOfAnotherTCSPYes(dtls)) => TcspAll(true, dtls)
      case (true, ServicesOfAnotherTCSPNo)        => TcspAll(true, None)
      case (false, _)                             => TcspAll(false, None)
    }
}
