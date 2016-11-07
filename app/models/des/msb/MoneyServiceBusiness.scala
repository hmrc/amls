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

package models.des.msb

import models.fe.businessmatching.{CurrencyExchange, MsbService, TransmittingMoney}
import play.api.libs.json.Json

case class MoneyServiceBusiness (msbAllDetails: Option[MsbAllDetails],
                                 msbMtDetails: Option[MsbMtDetails],
                                 msbCeDetails: Option[MsbCeDetails],
                                 msbFxDetails: Option[MsbFxDetails]
                                )

object MoneyServiceBusiness {

  implicit val format = Json.format[MoneyServiceBusiness]

  implicit def conv(msbOpt: Option[models.fe.moneyservicebusiness.MoneyServiceBusiness], bm: models.fe.businessmatching.BusinessMatching)
  : Option[MoneyServiceBusiness] = {

    msbOpt match {
      case Some(msb) => {
        val services = bm.msbServices.fold[Set[MsbService]](Set.empty)(x => x.services)
        val msbMtDetails: Option[MsbMtDetails] = services.contains(TransmittingMoney) match {
          case true =>(msb, bm)
          case false => None
        }
        val msbCeDetails: Option[MsbCeDetails] = services.contains(CurrencyExchange) match {
          case true => msb
          case false => None
        }
        Some(MoneyServiceBusiness(msb, msbMtDetails, msbCeDetails, msb))
      }
      case _ =>None
    }
  }
}
