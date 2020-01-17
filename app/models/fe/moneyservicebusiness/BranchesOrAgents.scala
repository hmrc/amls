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

package models.fe.moneyservicebusiness

import models.des.msb.MsbAllDetails
import play.api.libs.json.Json

case class BranchesOrAgents(hasCountries:Boolean, countries: Option[Seq[String]])

object BranchesOrAgents {

  implicit val format = Json.format[BranchesOrAgents]

  implicit def convMsbAll(msbAll: Option[MsbAllDetails]): Option[BranchesOrAgents] = {
    msbAll map { allDtls =>
      BranchesOrAgents(allDtls.countriesList.nonEmpty,
        allDtls.countriesList map { countries => countries.listOfCountries }
      )
    }
  }
}
