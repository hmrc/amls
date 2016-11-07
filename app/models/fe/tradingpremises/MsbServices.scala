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

package models.fe.tradingpremises

import models.des.tradingpremises.Msb
import play.api.data.mapping._
import play.api.data.validation.ValidationError
import play.api.libs.json._
import utils.{CommonMethods, JsonMapping, TraversableValidators}

sealed trait MsbService

case object TransmittingMoney extends MsbService
case object CurrencyExchange extends MsbService
case object ChequeCashingNotScrapMetal extends MsbService
case object ChequeCashingScrapMetal extends MsbService

case class MsbServices(services : Set[MsbService])

object MsbService {

  implicit val serviceR = Rule[String, MsbService] {
    case "01" => Success(TransmittingMoney)
    case "02" => Success(CurrencyExchange)
    case "03" => Success(ChequeCashingNotScrapMetal)
    case "04" => Success(ChequeCashingScrapMetal)
    case _ => Failure(Seq(Path -> Seq(ValidationError("error.invalid"))))
  }

  implicit val serviceW = Write[MsbService, String] {
    case TransmittingMoney => "01"
    case CurrencyExchange => "02"
    case ChequeCashingNotScrapMetal => "03"
    case ChequeCashingScrapMetal => "04"
  }

  // TODO: Create generic rules that will remove the need for this
  implicit val jsonR: Rule[JsValue, MsbService] = {
    import play.api.data.mapping.json.Rules._
    stringR compose serviceR
  }

  // TODO: Create generic writes that will remove the need for this
  implicit val jsonW: Write[MsbService, JsValue] = {
    import play.api.data.mapping.json.Writes._
    serviceW compose string
  }
}

sealed trait MsbServices0 {

  import JsonMapping._

  private implicit def rule[A]
  (implicit
   p: Path => RuleLike[A, Set[MsbService]]
  ): Rule[A, MsbServices] =
    From[A] { __ =>

      val required =
        TraversableValidators.minLengthR[Set[MsbService]](1)

      (__ \ "msbServices").read(required) fmap MsbServices.apply
    }

  private implicit def write[A]
  (implicit
   p: Path => WriteLike[Set[MsbService], A]
  ): Write[MsbServices, A] =
    To[A] { __ =>

      import play.api.libs.functional.syntax.unlift

      (__ \ "msbServices").write[Set[MsbService]] contramap unlift(MsbServices.unapply)
    }

  val jsonR: Reads[MsbServices] = {
    import play.api.data.mapping.json.Rules.{JsValue => _, pickInJson => _, _}
    implicitly[Reads[MsbServices]]
  }

  val jsonW: Writes[MsbServices] = {
    import play.api.data.mapping.json.Writes._
    implicitly[Writes[MsbServices]]
  }
}

object MsbServices {

  private object Cache extends MsbServices0

  implicit val jsonR: Reads[MsbServices] = Cache.jsonR
  implicit val jsonW: Writes[MsbServices] = Cache.jsonW

  implicit def convMsb(msb: Msb): Option[MsbServices]= {
    val `empty` =  Set.empty[MsbService]
    val services = Set(CommonMethods.getSpecificType[MsbService](msb.mt, TransmittingMoney),
      CommonMethods.getSpecificType[MsbService](msb.ce, CurrencyExchange),
      CommonMethods.getSpecificType[MsbService](msb.nonSmdcc, ChequeCashingNotScrapMetal),
      CommonMethods.getSpecificType[MsbService](msb.smdcc, ChequeCashingScrapMetal)).flatten
    services match {
      case `empty` => None
      case _ => Some(MsbServices(services))
    }
  }
}
