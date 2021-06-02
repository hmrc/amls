/*
 * Copyright 2021 HM Revenue & Customs
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

import generators.supervision.BusinessActivityGenerators
import models.des.businessactivities.{BusinessActivitiesAll, MlrAdvisor}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}
import org.mockito.Mockito.when
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class AccountantForAMLSRegulationsSpec extends PlaySpec
  with MockitoSugar
  with BusinessActivityGenerators
  with ScalaCheckPropertyChecks {

  "JSON validation" must {

    "successfully validate given an `true` value" in {
      val json = Json.obj("accountantForAMLSRegulations" -> true)
      Json.fromJson[AccountantForAMLSRegulations](json) must
        be(JsSuccess(AccountantForAMLSRegulations(true)))
    }

    "successfully validate given an `false` value" in {
      val json = Json.obj("accountantForAMLSRegulations" -> false)
      Json.fromJson[AccountantForAMLSRegulations](json) must
        be(JsSuccess(AccountantForAMLSRegulations(false)))
    }

    "write the correct value given an NCARegisteredYes" in {
      Json.toJson(AccountantForAMLSRegulations(true)) must
        be(Json.obj("accountantForAMLSRegulations" -> true))
    }

    "write the correct value given an NCARegisteredNo" in {
      Json.toJson(AccountantForAMLSRegulations(false)) must
        be(Json.obj("accountantForAMLSRegulations" -> false))
    }
  }

  "convertAccountant" must {
    "return the data if it is supplied" in {
      forAll(activityGen) { mlrActivities =>
        val result = AccountantForAMLSRegulations.convertAccountant(Some(MlrAdvisor(doYouHaveMlrAdvisor = true, None)), Some(mlrActivities))

        result must contain(AccountantForAMLSRegulations(true))
      }
    }

    "return None if there is no MLR Advisor data" when {
      "the application is an ASP" in {
        forAll(activityGen) { mlrActivities =>
          val ba = mock[BusinessActivitiesAll]
          when(ba.mlrAdvisor) thenReturn None

          val result = AccountantForAMLSRegulations.convertAccountant(None, Some(mlrActivities.copy(asp = true)))

          result must not be defined
        }
      }
    }

    "return 'No' if there is no MLR Advisor data" when {
      "the application is not an ASP" in {
        forAll(activityGen) { mlrActivities =>
          val ba = mock[BusinessActivitiesAll]
          when(ba.mlrAdvisor) thenReturn None

          val result = AccountantForAMLSRegulations.convertAccountant(None, Some(mlrActivities.copy(asp = false)))

          result must contain(AccountantForAMLSRegulations(false))
        }
      }
    }
  }

}
