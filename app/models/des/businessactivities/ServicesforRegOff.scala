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

package models.des.businessactivities

import models.fe.tcsp._
import play.api.libs.json.Json

case class ServicesforRegOff (
                               callHandling : Boolean = false,
                               emailHandling : Boolean = false,
                               emailServer : Boolean = false,
                               selfCollectMailboxes : Boolean = false,
                               mailForwarding : Boolean = false,
                               receptionist : Boolean = false,
                               conferenceRooms : Boolean = false,
                               other : Boolean = false,
                               specifyOther : Option[String] = None
                             )

object ServicesforRegOff {

  implicit val format =  Json.format[ServicesforRegOff]

  implicit def conv(tcsp: Option[Tcsp]) : Option[ServicesforRegOff] = {
    tcsp match {
      case Some(data) => data.providedServices.fold[Set[TcspService]](Set.empty) { x => x.services}
      case _ => None
    }
  }

  implicit def conv1(svcs: Set[TcspService]) : Option[ServicesforRegOff] = {
      val servicesforRegOff = svcs.foldLeft[ServicesforRegOff](ServicesforRegOff(false, false, false,false,false,false,false,false, None)) ((x, y) =>
        y match {
          case PhonecallHandling => x.copy(callHandling = true)
          case EmailHandling => x.copy(emailHandling = true)
          case EmailServer => x.copy(emailServer = true)
          case SelfCollectMailboxes => x.copy(selfCollectMailboxes = true)
          case MailForwarding => x.copy(mailForwarding = true)
          case Receptionist => x.copy(receptionist = true)
          case ConferenceRooms => x.copy(conferenceRooms = true)
          case Other(dtls) => x.copy(other = true, specifyOther = Some(dtls))

        }
      )
      Some(servicesforRegOff)
  }
}
