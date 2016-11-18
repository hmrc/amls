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

import models.des.msb.{MoneyServiceBusiness => DesMoneyServiceBusiness, CountriesList, MsbMtDetails}
import play.api.data.mapping._
import play.api.libs.json.{Reads, Writes}
import utils.TraversableValidators

case class MostTransactions(countries: Seq[String])

private sealed trait MostTransactions0 {

  private implicit def rule[A]
  (implicit
   a: Path => RuleLike[A, Seq[String]],
   cR: Rule[Seq[String], Seq[String]]
  ): Rule[A, MostTransactions] =
    From[A] { __ =>

      import TraversableValidators._

      implicit val emptyToNone: String => Option[String] = {
        case "" => None
        case s => Some(s)
      }

      val seqR = {
        (seqToOptionSeq[String] compose flattenR[String] compose cR)
          .compose(minLengthR[Seq[String]](1))
          .compose(maxLengthR[Seq[String]](3))
      }

      (__ \ "mostTransactionsCountries").read(seqR) fmap MostTransactions.apply
    }

  private implicit def write[A]
  (implicit
   a: Path => WriteLike[Seq[String], A]
  ): Write[MostTransactions, A] =
    To[A] { __ =>

      import play.api.libs.functional.syntax.unlift
      (__ \ "mostTransactionsCountries").write[Seq[String]] contramap unlift(MostTransactions.unapply)
    }

  val jsonR: Reads[MostTransactions] = {
    import play.api.data.mapping.json.Rules.{JsValue => _, pickInJson => _, _}
    import utils.JsonMapping._
    implicitly
  }

  val jsonW: Writes[MostTransactions] = {
    import play.api.data.mapping.json.Writes._
    import utils.JsonMapping._
    implicitly
  }
}

object MostTransactions {

  private object Cache extends MostTransactions0

  implicit val jsonR: Reads[MostTransactions] = Cache.jsonR
  implicit val jsonW: Writes[MostTransactions] = Cache.jsonW

  implicit def convMsbMt(msbMt: Option[MsbMtDetails]): Option[MostTransactions] = {
    msbMt flatMap {m =>
      m.countriesLrgstTranscsSentTo.fold[Option[MostTransactions]] {None} {
        case CountriesList(Nil) => None
        case CountriesList(countries) => Some(MostTransactions(countries))}
    }
  }
}
