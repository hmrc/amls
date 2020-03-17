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
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._

class ClientMoneyProtectionSchemeSpec extends PlaySpec with MockitoSugar with OneAppPerSuite {

  "phase 3 toggle off" must {

    "return none given no model and carry out Eab" in {
      val view = DesConstants.SubscriptionViewModel.copy(eabAll = None)
      ClientMoneyProtectionScheme.conv(view) must be(None)
    }
  }
}

