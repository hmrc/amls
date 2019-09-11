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
import models.des.businessactivities.BusinessActivities
import models.des.estateagentbusiness.EabAll
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class ProfessionalBodySpec extends PlaySpec with MockitoSugar {
  "JSON validation" must {

    "successfully validate given an enum value" in {

      Json.fromJson[ProfessionalBody](Json.obj("penalised" -> false)) must
        be(JsSuccess(ProfessionalBodyNo))
    }

    "successfully validate given an `Yes` value" in {

      val json = Json.obj("penalised" -> true, "professionalBody" -> "details")

      Json.fromJson[ProfessionalBody](json) must
        be(JsSuccess(ProfessionalBodyYes("details"), JsPath \ "professionalBody"))
    }

    "fail to validate when given an empty `Yes` value" in {

      val json = Json.obj("penalised" -> true)

      Json.fromJson[ProfessionalBody](json) must
        be(JsError((JsPath \ "professionalBody") -> ValidationError("error.path.missing")))
    }

    "write the correct value" in {

      Json.toJson(ProfessionalBodyNo) must
        be(Json.obj("penalised" -> false))

      Json.toJson(ProfessionalBodyYes("details")) must
        be(Json.obj(
          "penalised" -> true,
          "professionalBody" -> "details"
        ))
    }

    "convert ProfessionalBody des model to frontend model with yes and given string" in {
      val des = EabAll(
        false,
        None,
        prevWarnedWRegToEstateAgencyActivities = true,
        prevWarnWRegProvideDetails = Some("test")
      )
      val view = DesConstants.SubscriptionViewModel.copy(eabAll = Some(des))
      ProfessionalBody.conv(view) must be(Some(ProfessionalBodyYes("test")))
    }
    "convert ProfessionalBody des model to frontend model no selected" in {
      val des = EabAll(
        estateAgencyActProhibition = true,
        estAgncActProhibProvideDetails = Some("test"),
        false,
        None
      )
      val view = DesConstants.SubscriptionViewModel.copy(eabAll = Some(des))
      ProfessionalBody.conv(view) must be(Some(ProfessionalBodyNo))
    }
    "return none given no model and carry out Eab" in {
      val view = DesConstants.SubscriptionViewModel.copy(eabAll = None)
      ProfessionalBody.conv(view) must be(Some(ProfessionalBodyNo))
    }
    "return none given no model" in {
      val view = DesConstants.SubscriptionViewModel.copy(eabAll = None,businessActivities = BusinessActivities())
      ProfessionalBody.conv(view) must be(None)
    }

  }


}
