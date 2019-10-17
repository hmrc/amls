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

package models.des.businessactivities

import models.fe.amp.Amp
import play.api.libs.json.{Json, OFormat}

case class AmpServices( artGallery: Boolean,
                        auctionHouse: Boolean,
                        privateDealer: Boolean,
                        intermediary: Boolean,
                        other: AmpServicesOther )

object AmpServices {
  implicit val format: OFormat[AmpServices] = Json.format[AmpServices]

  val none = AmpServices(artGallery = false, auctionHouse = false, privateDealer = false, intermediary = false, AmpServicesOther(otherAnswer = false, None))

  implicit def conv(services: Option[Amp]) : Option[AmpServices] = {

    services.map(amp => amp.data.typeOfParticipant.foldLeft[AmpServices](none)((ampServices: AmpServices, service) => service match {
      case "artGalleryOwner" => ampServices.copy(artGallery = true)
      case "artDealer" => ampServices.copy(privateDealer = true)
      case "artAgent" => ampServices.copy(intermediary = true)
      case "artAuctioneer" => ampServices.copy(auctionHouse = true)
      case "somethingElse" => ampServices.copy(other = AmpServicesOther( otherAnswer = true,
                                                                         specifyOther = amp.data.typeOfParticipantDetail))
    }))
  }
}

case class AmpServicesOther( otherAnswer: Boolean,
                             specifyOther: Option[String] )

object AmpServicesOther {
  implicit val format: OFormat[AmpServicesOther] = Json.format[AmpServicesOther]
}



