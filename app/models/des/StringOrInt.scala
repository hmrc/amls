/*
 * Copyright 2017 HM Revenue & Customs
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

package models.des

import play.api.libs.json.Reads._
import play.api.libs.json._

case class StringOrInt (data: Either[Int, String]) {

  override def canEqual(a: Any) = a.isInstanceOf[StringOrInt]

  override def equals(that:Any):Boolean = {
    that match {
      case that: StringOrInt => that.canEqual(this) && that.hashCode == this.hashCode
      case _ => false
    }
  }

  override def hashCode: Int = {
    41 + (41 + (data match {
      case Left(x) => x
      case Right(x) => x
    }).toString.hashCode)
  }

}

object StringOrInt {

  implicit def apply(intData: Int): StringOrInt = StringOrInt(Left(intData))

  implicit def apply(strData: String): StringOrInt = StringOrInt(Right(strData))

  implicit val reads: Reads[StringOrInt] =
  (__ \ "lineId").read[Int].map(intData => StringOrInt(Left(intData))) orElse
    (__ \ "lineId").read[String].map(strData => StringOrInt(Right(strData.replaceFirst("^0+(?!$)", ""))))

  implicit val writes: Writes[StringOrInt] = new Writes[StringOrInt] {
    override def writes(o: StringOrInt): JsValue = Json.obj(
      o.data.fold(
        intData => "lineId" -> intData,
        strData => "lineId" -> f"${strData.toInt}%06d"
      )
    )
  }

  implicit def convToStringOrInt(feLineId: Option[Int]): Option[StringOrInt] = {
    feLineId match {
      case Some(lineId) => Some(StringOrInt(Right(lineId.toString)))
      case _ => None
    }
  }

  implicit def convToInt(desLineId: Option[StringOrInt]): Option[Int] = {
    desLineId match {
      case Some(lineId) => lineId
      case _ => None
    }
  }

  implicit def convToLineId(desLineId: StringOrInt): Option[Int] = {
    desLineId.data match {
      case Left(intData) => Some(intData)
      case Right(strData) => Some(Integer.parseInt(strData))
    }
  }

}
