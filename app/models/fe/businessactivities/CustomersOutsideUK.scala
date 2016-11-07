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

package models.fe.businessactivities

import models.des.businessactivities.BusinessActivitiesAll
import play.api.data.mapping.forms._
import play.api.data.mapping._
import play.api.libs.functional.Monoid
import play.api.libs.json.{Json, Reads, Writes}
import utils.{JsonMapping, TraversableValidators}

case class CustomersOutsideUK(countries: Option[Seq[String]])

sealed trait CustomersOutsideUK0 {

  val minLength = 1
  val maxLength = 10

  import JsonMapping._

  private implicit def rule[A]
  (implicit
   bR: Path => Rule[A, Boolean],
   sR: Path => Rule[A, Seq[String]],
   cR: Rule[Seq[String], Seq[String]]
  ): Rule[A, CustomersOutsideUK] =
    From[A] { __ =>

      import utils.MappingUtils.Implicits.RichRule
      import TraversableValidators._

      implicit val emptyToNone: String => Option[String] = {
        case "" => None
        case s => Some(s)
      }

      val boolR =
        bR andThen {
          _ withMessage "error.required.ba.select.country"
        }

      val countrySeqR = {
        (seqToOptionSeq[String] compose flattenR[String] compose cR)
          .compose(minLengthR[Seq[String]](minLength) withMessage "error.invalid.ba.select.country")
          .compose(maxLengthR[Seq[String]](maxLength))
      }

      (__ \ "isOutside").read(boolR).flatMap[Option[Seq[String]]] {
        case true =>
          (__ \ "countries").read(countrySeqR) fmap Some.apply
        case false =>
          Rule(_ => Success(None))
      } fmap CustomersOutsideUK.apply
    }

  private implicit def write[A]
  (implicit
   mon: Monoid[A],
   a: Path => WriteLike[Boolean, A],
   b: Path => WriteLike[Option[Seq[String]], A]
  ): Write[CustomersOutsideUK, A] =
    To[A] { __ =>
      (
        (__ \ "isOutside").write[Boolean].contramap[Option[_]] {
          case Some(_) => true
          case None => false
        } and
          (__ \ "countries").write[Option[Seq[String]]]
        )(a => (a.countries, a.countries))
    }

  val jsonR: Reads[CustomersOutsideUK] = {
    import play.api.data.mapping.json.Rules.{JsValue => _, pickInJson => _, _}
    implicitly
  }

  val jsonW: Writes[CustomersOutsideUK] = {
    import play.api.data.mapping.json.Writes._
    implicitly
  }
}

object CustomersOutsideUK {

  private object Cache extends CustomersOutsideUK0

  val minLength = Cache.minLength
  val maxLength = Cache.maxLength

  implicit val jsonR: Reads[CustomersOutsideUK] = Cache.jsonR
  implicit val jsonW: Writes[CustomersOutsideUK] = Cache.jsonW

  implicit def conv(des: BusinessActivitiesAll) : Option[CustomersOutsideUK] = {
    des.nonUkResidentCustDetails.whichCountries match {
      case Some(countries) => Some(CustomersOutsideUK(Some(countries)))
      case _ => Some(CustomersOutsideUK(None))
    }
  }
}
