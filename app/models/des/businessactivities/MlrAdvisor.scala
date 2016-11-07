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

package models.des.businessactivities

import models.fe.businessactivities.WhoIsYourAccountant
import play.api.libs.json.Json

case class MlrAdvisorDetails(advisorNameAddress: Option[AdvisorNameAddress],
                             agentDealsWithHmrc: Boolean,
                             hmrcAgentRefNo: Option[String])

object MlrAdvisorDetails{
  implicit val format = Json.format[MlrAdvisorDetails]

  implicit def convert(ba: models.fe.businessactivities.BusinessActivities): Option[MlrAdvisorDetails] = {
    ba.whoIsYourAccountant match {
      case None => None
      case _ => Some(MlrAdvisorDetails(ba.whoIsYourAccountant, ba.taxMatters.fold(false)(x => x.manageYourTaxAffairs), None))
    }
  }

  implicit def dealsWithTaxConvert(accountant:Option[WhoIsYourAccountant]): Option[AdvisorNameAddress] ={
    accountant match {
      case Some(data) => data
      case None => None
    }
  }
}

case class MlrAdvisor(doYouHaveMlrAdvisor: Boolean,
                      mlrAdvisorDetails: Option[MlrAdvisorDetails] = None)

object MlrAdvisor{
  implicit val format = Json.format[MlrAdvisor]

  implicit def convert(bact: models.fe.businessactivities.BusinessActivities): MlrAdvisor ={
    bact.accountantForAMLSRegulations match{
      case Some(x) => MlrAdvisor(x.accountantForAMLSRegulations, bact)
      case _ => MlrAdvisor(false)
    }
  }
}
