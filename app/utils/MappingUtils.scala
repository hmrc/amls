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

package utils

import play.api.libs.json.JsonValidationError

import scala.collection.Iterable

trait MappingUtils {

  object Implicits {

    /*
   * Json reads implicits
   */

    import play.api.libs.json.{JsError, JsSuccess, Reads}

    implicit def toReadsSuccess[A, B <: A](b: B): Reads[A] =
      Reads { _ => JsSuccess(b) }

    implicit def toReadsFailure[A](f: JsonValidationError): Reads[A] =
      Reads { _ => JsError(f) }
  }

  object JsConstraints {

    import play.api.libs.json.Reads
    import play.api.libs.json.Reads._

    def nonEmpty[M](implicit reads: Reads[M], p: M => Iterable[M]) =
      filter[M](JsonValidationError("error.required"))(_.isEmpty)
  }

}

object MappingUtils extends MappingUtils

object CommonMethods {
  def getSpecificType[T](bSelected: Boolean, bType: T) = {
    bSelected match {
      case true => Some(bType)
      case false => None
    }
  }

  def getSpecificTypeWithOption[T](bSelected: Option[Boolean], bType: T) = {
    bSelected match {
      case Some(true) => Some(bType)
      case Some(false) => None
      case _ => None
    }
  }
}
