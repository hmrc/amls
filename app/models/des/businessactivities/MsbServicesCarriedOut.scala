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

package models.des.businessactivities

import models.fe.businessmatching._
import play.api.libs.json.Json

case class MsbServicesCarriedOut (mt: Boolean, ce: Boolean, smdcc: Boolean, nonSmdcc: Boolean, fx: Boolean)

object MsbServicesCarriedOut {

  implicit val format =  Json.format[MsbServicesCarriedOut]

  implicit def conv(feModel: BusinessMatching): Option[MsbServicesCarriedOut] = {
    feModel.msbServices.fold[Set[MsbService]](Set.empty)(x => x.services)
  }

  implicit def convert(services: Set[MsbService]): Option[MsbServicesCarriedOut] = {
    val msbServices = services.foldLeft[MsbServicesCarriedOut](MsbServicesCarriedOut(false, false, false, false, false))((result, service) =>
      service match {
        case TransmittingMoney => result.copy(mt = true)
        case CurrencyExchange => result.copy(ce = true)
        case ChequeCashingNotScrapMetal => result.copy(nonSmdcc = true)
        case ChequeCashingScrapMetal => result.copy(smdcc = true)
        case _ => result.copy(fx = false)
      }
    )
    Some(msbServices)
  }
}
