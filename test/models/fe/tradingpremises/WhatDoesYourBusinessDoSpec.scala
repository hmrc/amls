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

package models.fe.tradingpremises

import models.des.DesConstants
import org.scalatest.{MustMatchers, WordSpec}
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

class WhatDoesYourBusinessDoSpec extends WordSpec with MustMatchers {
  val model = WhatDoesYourBusinessDo(
    Set(
      BusinessActivity.BillPaymentServices,
      BusinessActivity.EstateAgentBusinessService,
      BusinessActivity.MoneyServiceBusiness
    )
  )

  "WhatDoesYourBusinessDo" when {
    val businessServices: Set[BusinessActivity] = Set(BusinessActivity.AccountancyServices,
      BusinessActivity.HighValueDealing,
      BusinessActivity.TrustAndCompanyServices,
      BusinessActivity.TelephonePaymentService)
    "JSON validation" must {

      "successfully validate given values" in {
        val json = Json.obj("activities" -> Seq("01", "04", "06", "07"), "dateOfChange" -> "2010-03-01")

        Json.fromJson[WhatDoesYourBusinessDo](json) must
          be(JsSuccess(WhatDoesYourBusinessDo(businessServices, Some("2010-03-01"))))
      }

      "fail when on invalid data" in {
        Json.fromJson[WhatDoesYourBusinessDo](Json.obj("activities" -> Seq("40"))) must
          be(JsError(((JsPath \ "activities") (0) \ "activities") -> JsonValidationError("error.invalid")))
      }

      "successfully validate json write" in {
        val json = Json.obj("activities" -> Set("01", "04", "06", "07"))
        Json.toJson(WhatDoesYourBusinessDo(businessServices)) must be(json)

      }
    }

    "convert des model to frontend model" in {
      val model = WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing,
        BusinessActivity.BillPaymentServices,
        BusinessActivity.MoneyServiceBusiness,
        BusinessActivity.TrustAndCompanyServices,
        BusinessActivity.ArtMarketParticipant))

      WhatDoesYourBusinessDo.conv(DesConstants.AgentPremisesModel1) must be(model)
    }

    "convert des model to frontend model when msb is not selected" in {
      val model = WhatDoesYourBusinessDo(Set(BusinessActivity.HighValueDealing, BusinessActivity.BillPaymentServices, BusinessActivity.TrustAndCompanyServices, BusinessActivity.ArtMarketParticipant))

      WhatDoesYourBusinessDo.conv(DesConstants.AgentPremisesModel2) must be(model)
    }

    "convert des model to frontend model when non of the services selected" in {
      val model = WhatDoesYourBusinessDo(Set.empty)

      WhatDoesYourBusinessDo.conv(DesConstants.AgentPremisesModel3) must be(model)
    }
  }
}
