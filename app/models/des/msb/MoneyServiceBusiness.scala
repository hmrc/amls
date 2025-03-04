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

package models.des.msb

import models.fe.businessmatching.{CurrencyExchange, ForeignExchange, MsbService, TransmittingMoney}
import play.api.libs.json._

case class MoneyServiceBusiness(
  msbAllDetails: Option[MsbAllDetails],
  msbMtDetails: Option[MsbMtDetails],
  msbCeDetails: Option[MsbCeDetailsR7],
  msbFxDetails: Option[MsbFxDetails]
)

object MoneyServiceBusiness {

  implicit def format: OFormat[MoneyServiceBusiness] = Json.format[MoneyServiceBusiness]

  implicit def conv(
    msbOpt: Option[models.fe.moneyservicebusiness.MoneyServiceBusiness],
    bm: models.fe.businessmatching.BusinessMatching,
    amendVariation: Boolean
  ): Option[MoneyServiceBusiness] =
    msbOpt match {
      case Some(msb)
          if msb != models.fe.moneyservicebusiness.MoneyServiceBusiness(
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None
          ) =>
        val services                             = bm.msbServices.fold[Set[MsbService]](Set.empty)(x => x.msbServices)
        val msbMtDetails: Option[MsbMtDetails]   = services.contains(TransmittingMoney) match {
          case true  => (msb, bm, amendVariation)
          case false => None
        }
        val msbCeDetails: Option[MsbCeDetailsR7] = services.contains(CurrencyExchange) match {
          case true  => msb
          case false => None
        }
        val msbFxDetails: Option[MsbFxDetails]   = services.contains(ForeignExchange) match {
          case true  => msb
          case false => None
        }
        Some(MoneyServiceBusiness(msb, msbMtDetails, msbCeDetails, msbFxDetails))
      case _ => None
    }
}
