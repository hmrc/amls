/*
 * Copyright 2022 HM Revenue & Customs
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

package models.fe.hvd

import models.des.businessactivities.BusinessActivities
import play.api.libs.json.Reads.StringReads
import play.api.libs.json._
import utils.CommonMethods

case class Products(items: Set[ItemType])

sealed trait ItemType {
  val value: String =
    this match {
      case Alcohol => "01"
      case Tobacco => "02"
      case Antiques => "03"
      case Cars => "04"
      case OtherMotorVehicles => "05"
      case Caravans => "06"
      case Jewellery => "07"
      case Gold => "08"
      case ScrapMetals => "09"
      case MobilePhones => "10"
      case Clothing => "11"
      case Other(_) => "12"
    }
}

case object Alcohol extends ItemType

case object Tobacco extends ItemType

case object Antiques extends ItemType

case object Cars extends ItemType

case object OtherMotorVehicles extends ItemType

case object Caravans extends ItemType

case object Jewellery extends ItemType

case object Gold extends ItemType

case object ScrapMetals extends ItemType

case object MobilePhones extends ItemType

case object Clothing extends ItemType

case class Other(details: String) extends ItemType

object Products {

  def convOther(other: Boolean, specifyOther: String): Option[ItemType] =
    other match{
      case true => Some(Other(specifyOther))
      case false => None
    }

  implicit val jsonReads: Reads[Products] =
    (__ \ "products").read[Set[String]].flatMap { x: Set[String] =>
      x.map {
        case "01" => Reads(_ => JsSuccess(Alcohol)) map identity[ItemType]
        case "02" => Reads(_ => JsSuccess(Tobacco)) map identity[ItemType]
        case "03" => Reads(_ => JsSuccess(Antiques)) map identity[ItemType]
        case "04" => Reads(_ => JsSuccess(Cars)) map identity[ItemType]
        case "05" => Reads(_ => JsSuccess(OtherMotorVehicles)) map identity[ItemType]
        case "06" => Reads(_ => JsSuccess(Caravans)) map identity[ItemType]
        case "07" => Reads(_ => JsSuccess(Jewellery)) map identity[ItemType]
        case "08" => Reads(_ => JsSuccess(Gold)) map identity[ItemType]
        case "09" => Reads(_ => JsSuccess(ScrapMetals)) map identity[ItemType]
        case "10" => Reads(_ => JsSuccess(MobilePhones)) map identity[ItemType]
        case "11" => Reads(_ => JsSuccess(Clothing)) map identity[ItemType]
        case "12" =>
          val test = (JsPath \ "otherDetails").read[String].map(Other.apply _)
          test map identity[ItemType]
        case _ =>
          Reads(_ => JsError((JsPath \ "products") -> JsonValidationError("error.invalid")))
      }.foldLeft[Reads[Set[ItemType]]](
        Reads[Set[ItemType]](_ => JsSuccess(Set.empty))
      ) {
        (result, data) =>
          data flatMap { m =>
            result.map { n =>
              n + m
            }
          }
      }
    } map Products.apply

  implicit val jsonWrite = Writes[Products] {
    case Products(transactions) =>
      Json.obj(
        "products" -> (transactions map {
          _.value
        }).toSeq
      ) ++ transactions.foldLeft[JsObject](Json.obj()) {
        case (m, Other(name)) =>
          m ++ Json.obj("otherDetails" -> name)
        case (m, _) =>
          m
      }
  }

  implicit def conv(ba: BusinessActivities): Option[Products] = {
    val itemTypes: Option[Set[ItemType]] = ba.hvdGoodsSold match {
      case Some(products) => Some(Set(CommonMethods.getSpecificType(products.alcohol, Alcohol),
        CommonMethods.getSpecificType(products.tobacco, Tobacco),
        CommonMethods.getSpecificType(products.antiques, Antiques),
        CommonMethods.getSpecificType(products.caravans, Caravans),
        CommonMethods.getSpecificType(products.cars, Cars),
        CommonMethods.getSpecificType(products.clothing, Clothing),
        CommonMethods.getSpecificType(products.gold, Gold),
        CommonMethods.getSpecificType(products.jewellery, Jewellery),
        CommonMethods.getSpecificType(products.mobilePhones, MobilePhones),
        CommonMethods.getSpecificType(products.otherMotorVehicles, OtherMotorVehicles),
        CommonMethods.getSpecificType(products.scrapMetals, ScrapMetals),
        convOther(products.other, products.specifyOther.getOrElse(""))).flatten)
      case None => None
    }

    itemTypes.map(Products(_))
  }

}
