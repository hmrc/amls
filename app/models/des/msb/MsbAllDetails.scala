/*
 * Copyright 2020 HM Revenue & Customs
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

import models.fe.moneyservicebusiness.ExpectedThroughput._
import models.fe.moneyservicebusiness._
import play.api.libs.json.Json

case class MsbAllDetails(anticipatedTotThrputNxt12Mths: Option[String],
                         otherCntryBranchesOrAgents: Boolean,
                         countriesList: Option[CountriesList],
                         sysLinkedTransIdentification: Boolean)

object MsbAllDetails {

  implicit val format = Json.format[MsbAllDetails]

  implicit def conv(msb: models.fe.moneyservicebusiness.MoneyServiceBusiness) : Option[MsbAllDetails] = {

    val (otherCntryBranchesOrAgents, countryList) = convBranchesOrAgents(msb.branchesOrAgents)

    Some(MsbAllDetails(msb.throughput, otherCntryBranchesOrAgents, countryList, msb.identifyLinkedTransactions.fold(false)(x =>x.linkedTxn)))
  }

  implicit def convThroughput(throughput: Option[ExpectedThroughput]): Option[String] = {
    throughput match {
      case Some(data) => data
      case None => None
    }
  }

  implicit def convThroughputValues(throughput: ExpectedThroughput): Option[String] = {
    val value = throughput match {
          case First => "£0-£15k"
          case Second => "£15k-50k"
          case Third => "£50k-£100k"
          case Fourth => "£100k-£250k"
          case Fifth => "£250k-£1m"
          case Sixth => "£1m-10m"
          case Seventh => "£10m+"
        }
    Some(value)
  }

  implicit def convBranchesOrAgents(agents: Option[BranchesOrAgents]) : (Boolean, Option[CountriesList]) = {
    agents match {
      case Some(data) => data.countries match {
        case Some(countries) if(countries.nonEmpty) => (true, countries)
        case None => (false, None)
      }
      case None => (false, None)
    }
  }
}
