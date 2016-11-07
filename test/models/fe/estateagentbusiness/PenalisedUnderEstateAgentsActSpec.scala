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

package models.fe.estateagentbusiness

import models.des.estateagentbusiness.EabAll
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}


class PenalisedUnderEstateAgentsActSpec extends PlaySpec with MockitoSugar {
  "JSON validation" must {

    "successfully validate given an enum value" in {
      Json.fromJson[PenalisedUnderEstateAgentsAct](Json.obj("penalisedUnderEstateAgentsAct" -> false)) must
        be(JsSuccess(PenalisedUnderEstateAgentsActNo, JsPath \ "penalisedUnderEstateAgentsAct"))
    }

    "successfully validate given an `Yes` value" in {
      val json = Json.obj("penalisedUnderEstateAgentsAct" -> true, "penalisedUnderEstateAgentsActDetails" -> "Do not remember why penalised before")
      Json.fromJson[PenalisedUnderEstateAgentsAct](json) must
        be(JsSuccess(PenalisedUnderEstateAgentsActYes("Do not remember why penalised before"),
          JsPath \ "penalisedUnderEstateAgentsAct" \ "penalisedUnderEstateAgentsActDetails"))
    }

    "fail to validate when given an empty `Yes` value" in {
      val json = Json.obj("penalisedUnderEstateAgentsAct" -> true)
      Json.fromJson[PenalisedUnderEstateAgentsAct](json) must
        be(JsError((JsPath \ "penalisedUnderEstateAgentsAct" \ "penalisedUnderEstateAgentsActDetails") -> ValidationError("error.path.missing")))
    }

    "write the correct value" in {
      Json.toJson(PenalisedUnderEstateAgentsActNo) must be(Json.obj("penalisedUnderEstateAgentsAct" -> false))
      Json.toJson(PenalisedUnderEstateAgentsActYes("Do not remember why penalised before")) must
        be(Json.obj(
          "penalisedUnderEstateAgentsAct" -> true,
          "penalisedUnderEstateAgentsActDetails" -> "Do not remember why penalised before"
        ))
    }

    "convert PenalisedUnderEstateAgentsAct des model to frontend model with yes and given string" in {
      val des = EabAll(
        estateAgencyActProhibition = true,
        estAgncActProhibProvideDetails = Some("test"),
        false,
        None
      )
      PenalisedUnderEstateAgentsAct.conv(Some(des)) must be(Some(PenalisedUnderEstateAgentsActYes("test")))
    }
    "convert PenalisedUnderEstateAgentsAct des model to frontend model no selected" in {
      val des = EabAll(
        false,
        None,
        prevWarnedWRegToEstateAgencyActivities = true,
        prevWarnWRegProvideDetails = Some("test")
      )
      PenalisedUnderEstateAgentsAct.conv(Some(des)) must be(Some(PenalisedUnderEstateAgentsActNo))
    }
    "return none given no model" in {
      PenalisedUnderEstateAgentsAct.conv(None) must be(None)
    }

  }

}
