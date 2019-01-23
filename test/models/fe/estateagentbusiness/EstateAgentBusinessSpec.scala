/*
 * Copyright 2019 HM Revenue & Customs
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

import models.des.DesConstants
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class EstateAgentBusinessSpec extends PlaySpec with MockitoSugar {

  val services = Services(Set(Residential, Commercial, Auction))
  val professionalBody = ProfessionalBodyYes("details")
  val penalisedUnderEAAct =  PenalisedUnderEstateAgentsActYes("test")
  val redressSchemeOther = Other("test")

  "EstateAgentBusiness" must {

    "validate complete json" must {
      val completeJson = Json.obj(
        "isRedress" -> true,
        "propertyRedressScheme" -> "04",
        "propertyRedressSchemeOther" -> "test",
        "penalised" -> true,
        "professionalBody" -> "details",
        "penalisedUnderEstateAgentsAct" -> true,
        "penalisedUnderEstateAgentsActDetails" -> "test"
      )

      val completeModel = EstateAgentBusiness(None,Some(redressSchemeOther), Some(professionalBody), Some(penalisedUnderEAAct))

      "Serialise as expected" in {
        Json.toJson(completeModel) must
          be(completeJson)
      }

      "Deserialise as expected" in {
        completeJson.as[EstateAgentBusiness] must
          be(completeModel)
      }
    }

    "converting the des subscription model must yield a frontend Estate Agent model" in {
      EstateAgentBusiness.conv(DesConstants.SubscriptionViewModel) must
        be(Some(
          EstateAgentBusiness(
            DesConstants.testBusinessActivities.eabServicesCarriedOut,
            DesConstants.testEabResdEstAgncy,
            Some(ProfessionalBodyYes(DesConstants.testEabAll.prevWarnWRegProvideDetails.get)),
            Some(PenalisedUnderEstateAgentsActYes(DesConstants.testEabAll.estAgncActProhibProvideDetails.get))
        )))
    }

  }

}
