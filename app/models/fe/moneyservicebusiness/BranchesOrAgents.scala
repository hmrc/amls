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

package models.fe.moneyservicebusiness

import models.des.msb.{MoneyServiceBusiness => DesMoneyServiceBusiness, MsbAllDetails}
import play.api.data.mapping._
import play.api.libs.functional.Monoid
import play.api.libs.json.{Reads, Writes}
import utils.{JsonMapping, TraversableValidators}

case class BranchesOrAgents(branches: Option[Seq[String]])

sealed trait BranchesOrAgents0 {

  val minLength = 1
  val maxLength = 10

  import JsonMapping._

  private implicit def rule[A]
  (implicit
   b: Path => Rule[A, Boolean],
   s: Path => Rule[A, Seq[String]],
   cR: Rule[Seq[String], Seq[String]]
  ): Rule[A, BranchesOrAgents] =
    From[A] { __ =>

      import TraversableValidators._

      implicit val emptyToNone: String => Option[String] = {
        case "" => None
        case s => Some(s)
      }

      val boolR =
        b
      val countrySeqR = {
        (seqToOptionSeq[String] compose flattenR[String] compose cR)
          .compose(minLengthR[Seq[String]](minLength))
          .compose(maxLengthR[Seq[String]](maxLength))
      }

      (__ \ "hasCountries").read(boolR).flatMap[Option[Seq[String]]] {
        case true =>
          (__ \ "countries").read(countrySeqR) fmap Some.apply
        case false =>
          Rule(_ => Success(None))
      } fmap BranchesOrAgents.apply
    }

  private implicit def write[A]
  (implicit
   mon: Monoid[A],
   a: Path => WriteLike[Boolean, A],
   b: Path => WriteLike[Option[Seq[String]], A]
  ): Write[BranchesOrAgents, A] =
    To[A] { __ =>
      (
        (__ \ "hasCountries").write[Boolean].contramap[Option[_]] {
          case Some(_) => true
          case None => false
        } and
          (__ \ "countries").write[Option[Seq[String]]]
        )(a => (a.branches, a.branches))
    }

  val jsonR: Reads[BranchesOrAgents] = {
    import play.api.data.mapping.json.Rules.{JsValue => _, pickInJson => _, _}
    implicitly
  }

  val jsonW: Writes[BranchesOrAgents] = {
    import play.api.data.mapping.json.Writes._
    implicitly
  }
}

object BranchesOrAgents {

  private object Cache extends BranchesOrAgents0

  val minLength = Cache.minLength
  val maxLength = Cache.maxLength

  implicit val jsonR: Reads[BranchesOrAgents] = Cache.jsonR
  implicit val jsonW: Writes[BranchesOrAgents] = Cache.jsonW

  implicit def convMsbAll(msbAll: Option[MsbAllDetails]): Option[BranchesOrAgents] = {
    msbAll match {
      case Some(msbDtls) => msbDtls.countriesList match {
        case Some(countriesList) => Some(BranchesOrAgents(Some(countriesList.listOfCountries)))
        case None => None
      }
      case None => None
    }
  }
}
