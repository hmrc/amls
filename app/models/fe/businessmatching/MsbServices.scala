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

package models.fe.businessmatching

import models.des.businessactivities.MsbServicesCarriedOut
import play.api.data.validation.ValidationError
import play.api.libs.json._
import utils.CommonMethods

sealed trait MsbService

case object TransmittingMoney extends MsbService
case object CurrencyExchange extends MsbService
case object ChequeCashingNotScrapMetal extends MsbService
case object ChequeCashingScrapMetal extends MsbService
case object ForeignExchange extends MsbService

case class MsbServices(msbServices : Set[MsbService])

object MsbService {

  implicit val jsonR: Reads[MsbService] =
    Reads {
      case JsString("01") => JsSuccess(TransmittingMoney)
      case JsString("02") => JsSuccess(CurrencyExchange)
      case JsString("03") => JsSuccess(ChequeCashingNotScrapMetal)
      case JsString("04") => JsSuccess(ChequeCashingScrapMetal)
      case JsString("05") => JsSuccess(ForeignExchange)
      case _ => JsError((JsPath \ "msbServices") -> JsonValidationError("error.invalid"))
    }

  implicit val jsonW = Writes[MsbService] {
    case TransmittingMoney => JsString("01")
    case CurrencyExchange => JsString("02")
    case ChequeCashingNotScrapMetal => JsString("03")
    case ChequeCashingScrapMetal => JsString("04")
    case ForeignExchange => JsString("05")
  }
}

object MsbServices {

  implicit val formats = Json.format[MsbServices]

  implicit def conv(msb: Option[MsbServicesCarriedOut]): Option[MsbServices]= {
    msb match {
      case Some(msbDtls) => msbDtls
      case None => None
    }
  }

  implicit def convMsb(msb: MsbServicesCarriedOut): Option[MsbServices]= {
    val `empty` =  Set.empty[MsbService]
    val services = Set(CommonMethods.getSpecificType[MsbService](msb.mt, TransmittingMoney),
      CommonMethods.getSpecificType[MsbService](msb.ce, CurrencyExchange),
      CommonMethods.getSpecificType[MsbService](msb.nonSmdcc, ChequeCashingNotScrapMetal),
      CommonMethods.getSpecificType[MsbService](msb.smdcc, ChequeCashingScrapMetal),
      CommonMethods.getSpecificType[MsbService](msb.fx, ForeignExchange)).flatten
    services match {
      case `empty` => None
      case _ => Some(MsbServices(services))
    }
  }
}
