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

package models.des.msb

import config.AmlsConfig
import models.fe.businessmatching.{CurrencyExchange, ForeignExchange, MsbService, TransmittingMoney}
import play.api.libs.json._

case class MoneyServiceBusiness(msbAllDetails: Option[MsbAllDetails],
                                msbMtDetails: Option[MsbMtDetails],
                                msbCeDetails: Option[MsbCeDetailsR7],
                                msbFxDetails: Option[MsbFxDetails]
                               )

object MoneyServiceBusiness {

  implicit def format = if (AmlsConfig.release7) {
    Json.format[MoneyServiceBusiness]
  } else {
    val reads: Reads[MoneyServiceBusiness] = {
      import play.api.libs.functional.syntax._
      import play.api.libs.json.Reads._
      import play.api.libs.json._

      (
        (__ \ "msbAllDetails").readNullable[MsbAllDetails] and
          (__ \ "msbMtDetails").readNullable[MsbMtDetails] and
          (__ \ "msbCeDetails").readNullable[MsbCeDetails].map { x: Option[MsbCeDetails] => MsbCeDetailsR7.convertFromOldModel(x) } and
          (__ \ "msbFxDetails").readNullable[MsbFxDetails]

        ) (MoneyServiceBusiness.apply _)

    }

    val msbceWrites = new Writes[MsbCeDetailsR7] {
      override def writes(o: MsbCeDetailsR7): JsValue = {
        MsbCeDetails.writes.writes(MsbCeDetails.convertFromNewModel(o))
      }
    }

    val writes: Writes[MoneyServiceBusiness] = {
      import play.api.libs.functional.syntax._
      import play.api.libs.json._

      (
        (__ \ "msbAllDetails").writeNullable[MsbAllDetails] and
          (__ \ "msbMtDetails").writeNullable[MsbMtDetails] and
          (__ \ "msbCeDetails").writeNullable(msbceWrites) and
          (__ \ "msbFxDetails").writeNullable[MsbFxDetails]
        ) (unlift(MoneyServiceBusiness.unapply _))
    }
    Format(reads, writes)


  }


  implicit def conv(msbOpt: Option[models.fe.moneyservicebusiness.MoneyServiceBusiness], bm: models.fe.businessmatching.BusinessMatching, amendVariation: Boolean)
  : Option[MoneyServiceBusiness] = {

    msbOpt match {
      case Some(msb) if msb != models.fe.moneyservicebusiness.MoneyServiceBusiness(None, None, None, None, None, None, None, None, None, None, None, None) => {
        val services = bm.msbServices.fold[Set[MsbService]](Set.empty)(x => x.msbServices)
        val msbMtDetails: Option[MsbMtDetails] = services.contains(TransmittingMoney) match {
          case true => (msb, bm, amendVariation)
          case false => None
        }
        val msbCeDetails: Option[MsbCeDetailsR7] = services.contains(CurrencyExchange) match {
          case true => msb
          case false => None
        }
        val msbFxDetails: Option[MsbFxDetails] = services.contains(ForeignExchange) match {
          case true => msb
          case false => None
        }
        Some(MoneyServiceBusiness(msb, msbMtDetails, msbCeDetails, msbFxDetails))
      }
      case _ => None
    }
  }
}
