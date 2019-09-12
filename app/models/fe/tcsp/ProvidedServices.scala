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

package models.fe.tcsp

import models.des.businessactivities.BusinessActivities
import play.api.data.validation.ValidationError
import play.api.libs.json.Reads.StringReads
import play.api.libs.json._
import utils.CommonMethods

sealed trait TcspService {

  val value: String = this match {
    case PhonecallHandling => "01"
    case EmailHandling => "02"
    case EmailServer => "03"
    case SelfCollectMailboxes => "04"
    case MailForwarding => "05"
    case Receptionist => "06"
    case ConferenceRooms => "07"
    case Other(_) => "08"
  }
}

case object PhonecallHandling extends TcspService
case object EmailHandling extends TcspService
case object EmailServer extends TcspService
case object SelfCollectMailboxes extends TcspService
case object MailForwarding extends TcspService
case object Receptionist extends TcspService
case object ConferenceRooms extends TcspService
case class Other(details: String) extends TcspService

case class ProvidedServices(services: Set[TcspService])

object ProvidedServices {

  def convOther(other: Boolean, details: Option[String]): Option[TcspService] =
    other match {
      case true => Some(Other(details.getOrElse("")))
      case false => None
    }


  implicit val jsonReads: Reads[ProvidedServices] =
    (__ \ "services").read[Set[String]].flatMap { x =>
      x.map {
        case "01" => Reads(_ => JsSuccess(PhonecallHandling)) map identity[TcspService]
        case "02" => Reads(_ => JsSuccess(EmailHandling)) map identity[TcspService]
        case "03" => Reads(_ => JsSuccess(EmailServer)) map identity[TcspService]
        case "04" => Reads(_ => JsSuccess(SelfCollectMailboxes)) map identity[TcspService]
        case "05" => Reads(_ => JsSuccess(MailForwarding)) map identity[TcspService]
        case "06" => Reads(_ => JsSuccess(Receptionist)) map identity[TcspService]
        case "07" => Reads(_ => JsSuccess(ConferenceRooms)) map identity[TcspService]
        case "08" =>
          (JsPath \ "details").read[String].map(Other.apply _) map identity[TcspService]
        case _ =>
          Reads(_ => JsError((JsPath \ "services") -> JsonValidationError("error.invalid")))
      }.foldLeft[Reads[Set[TcspService]]](
        Reads[Set[TcspService]](_ => JsSuccess(Set.empty))
      ) {
        (result, data) =>
          data flatMap { m =>
            result.map { n =>
              n + m
            }
          }
      }
    } map ProvidedServices.apply

  implicit val jsonWrites = Writes[ProvidedServices] { ps =>
    Json.obj(
      "services" -> (ps.services map {
        _.value
      }).toSeq
    ) ++ ps.services.foldLeft[JsObject](Json.obj()) {
      case (m, Other(details)) =>
        m ++ Json.obj("details" -> details)
      case (m, _) =>
        m
    }
  }

  implicit def conv(ba: BusinessActivities): Option[ProvidedServices] = {
    val tcspService: Option[Set[TcspService]] = ba.tcspServicesforRegOffBusinessAddrVirtualOff match {
      case Some(tcspService) => Some(Set(
        CommonMethods.getSpecificType(tcspService.callHandling, PhonecallHandling),
        CommonMethods.getSpecificType(tcspService.emailHandling, EmailHandling),
        CommonMethods.getSpecificType(tcspService.emailServer, EmailServer),
        CommonMethods.getSpecificType(tcspService.selfCollectMailboxes, SelfCollectMailboxes),
        CommonMethods.getSpecificType(tcspService.mailForwarding, MailForwarding),
        CommonMethods.getSpecificType(tcspService.receptionist, Receptionist),
        CommonMethods.getSpecificType(tcspService.conferenceRooms, ConferenceRooms),
        convOther(tcspService.other, tcspService.specifyOther)
      ).flatten)
      case None => None
    }

    Some(ProvidedServices(tcspService.getOrElse(Set())))
  }
}
