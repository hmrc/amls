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

package models.fe.estateagentbusiness

import models.des.estateagentbusiness.EabResdEstAgncy
import play.api.data.validation.ValidationError
import play.api.libs.json._
import utils.MappingUtils.Implicits._

sealed trait RedressScheme

case object ThePropertyOmbudsman extends RedressScheme
case object OmbudsmanServices extends RedressScheme
case object PropertyRedressScheme extends RedressScheme
case class Other(v: String) extends RedressScheme

case object RedressSchemedNo extends RedressScheme

object RedressScheme {
  implicit val jsonRedressReads : Reads[RedressScheme] = {
    import play.api.libs.json.Reads.StringReads
    (__ \ "isRedress").read[Boolean] flatMap {
      case true =>
      {
        (__ \ "propertyRedressScheme").read[String].flatMap[RedressScheme] {
          case "01" => ThePropertyOmbudsman
          case "02" => OmbudsmanServices
          case "03" => PropertyRedressScheme
          case "04" =>
            (JsPath \ "propertyRedressSchemeOther").read[String] map {
              Other(_)
            }
          case _ =>
            ValidationError("error.invalid")
        }
      }
      case false => Reads(_ => JsSuccess(RedressSchemedNo))
    }
  }

  implicit val jsonRedressWrites = Writes[RedressScheme] {
      case ThePropertyOmbudsman => Json.obj("isRedress" -> true,"propertyRedressScheme" -> "01")
      case OmbudsmanServices => Json.obj("isRedress" -> true,"propertyRedressScheme" -> "02")
      case PropertyRedressScheme => Json.obj("isRedress" -> true,"propertyRedressScheme" -> "03")
      case Other(value) =>
        Json.obj(
          "isRedress" -> true,
          "propertyRedressScheme" -> "04",
          "propertyRedressSchemeOther" -> value
        )
    case RedressSchemedNo => Json.obj("isRedress" -> false)
  }


  implicit def conv(eab:Option[EabResdEstAgncy]): Option[RedressScheme] = {
    eab match {
      case Some(data) => data
      case None => None
    }
  }

  implicit def convEab(eab:EabResdEstAgncy): Option[RedressScheme] ={
    eab.regWithRedressScheme match {
      case true => {
        val redressOption = eab.whichRedressScheme.getOrElse("")
        redressOption match {
          case "The Property Ombudsman Limited" => Some(ThePropertyOmbudsman)
          case "Ombudsman Services" => Some(OmbudsmanServices)
          case "Property Redress Scheme" => Some(PropertyRedressScheme)
          case "Other" => Some(Other(eab.specifyOther.getOrElse("")))
        }
      }
      case false => Some(RedressSchemedNo)
    }
  }
}
