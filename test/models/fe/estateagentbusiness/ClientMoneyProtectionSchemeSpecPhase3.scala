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

package models.fe.estateagentbusiness

import models.des.DesConstants
import models.des.businessactivities.BusinessActivities
import models.des.estateagentbusiness.{EabAll, LettingAgents}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._

class ClientMoneyProtectionSchemeSpecPhase3 extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite  {

  override def fakeApplication(): Application = {
    GuiceApplicationBuilder().configure(Map("microservice.services.feature-toggle.phase3-release2-la" -> true)).build()
  }

  "JSON validation" must {

    "phase 3 toggle" must {

      "successfully validate given an enum value" in {

        Json.fromJson[ClientMoneyProtectionScheme](Json.obj("clientMoneyProtection" -> false)) must
          be(JsSuccess(ClientMoneyProtectionSchemeNo))
      }

      "successfully validate given an `Yes` value" in {

        val json = Json.obj("clientMoneyProtection" -> true)

        Json.fromJson[ClientMoneyProtectionScheme](json) must
          be(JsSuccess(ClientMoneyProtectionSchemeYes))
      }

      "write the correct value" in {

        Json.toJson(ClientMoneyProtectionSchemeNo) must
          be(Json.obj("clientMoneyProtection" -> false))

        Json.toJson(ClientMoneyProtectionSchemeYes) must
          be(Json.obj("clientMoneyProtection" -> true))
      }

      "convert ClientMoneyProtectionScheme des model to frontend model with yes and given string" in {

        val des = LettingAgents(Some(true))
        val view = DesConstants.SubscriptionViewModel.copy(lettingAgents = Some(des))
        ClientMoneyProtectionScheme.conv(view) must be(Some(ClientMoneyProtectionSchemeYes))
      }

      "convert ClientMoneyProtectionScheme des model to frontend model no selected" in {

        val des = EabAll(
          estateAgencyActProhibition = true,
          estAgncActProhibProvideDetails = Some("test"),
          false,
          None
        )
        val view = DesConstants.SubscriptionViewModel.copy(eabAll = Some(des))
        ClientMoneyProtectionScheme.conv(view) must be(Some(ClientMoneyProtectionSchemeNo))
      }

      "return no given no model and carry out Eab" in {

        val view = DesConstants.SubscriptionViewModel.copy(eabAll = None)
        ClientMoneyProtectionScheme.conv(view) must be(Some(ClientMoneyProtectionSchemeNo))
      }

      "return none given no model" in {

        val view = DesConstants.SubscriptionViewModel.copy(eabAll = None, businessActivities = BusinessActivities())
        ClientMoneyProtectionScheme.conv(view) must be(None)
      }
    }
  }
}


