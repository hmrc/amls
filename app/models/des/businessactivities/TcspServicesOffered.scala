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

package models.des.businessactivities

import models.fe.tcsp._
import play.api.libs.json.Json

case class TcspServicesOffered (
                                nomineeShareholders : Boolean,
                                trusteeProvider : Boolean,
                                regOffBusinessAddrVirtualOff : Boolean,
                                compDirSecPartnerProvider : Boolean,
                                trustOrCompFormAgent : Boolean
                              )

object TcspServicesOffered {

  implicit val format = Json.format[TcspServicesOffered]

  implicit def covn(tcsp: Option[Tcsp]): Option[TcspServicesOffered]  = {
    tcsp match {
      case Some(data) => data.tcspTypes.fold[Set[ServiceProvider]](Set.empty)(x=> x.serviceProviders)
      case _ => None
    }
  }

  implicit def conv1(serviceProviders : Set[ServiceProvider]) : Option[TcspServicesOffered] = {

    val tcspServicesOffered = serviceProviders.foldLeft[TcspServicesOffered](TcspServicesOffered(false, false,false, false,false))((result, service) =>
      service match  {
      case NomineeShareholdersProvider => result.copy(nomineeShareholders = true)
      case TrusteeProvider => result.copy(trusteeProvider = true)
      case RegisteredOfficeEtc => result.copy(regOffBusinessAddrVirtualOff = true)
      case CompanyDirectorEtc => result.copy(compDirSecPartnerProvider = true)
      case CompanyFormationAgent(_, _) => result.copy(trustOrCompFormAgent = true)
    })
    Some(tcspServicesOffered)
  }
}
