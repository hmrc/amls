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

import models.des.businessactivities.{BusinessActivityDetails, OtherBusinessActivities, ExpectedAMLSTurnover => DesExpectedAMLSTurnover}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

class InvolvedInOtherActivitiesSpec extends PlaySpec with MockitoSugar {

  "JSON validation" must {
    "successfully validate given an enum value" in {

      Json.fromJson[InvolvedInOther](Json.obj("involvedInOther" -> false)) must
        be(JsSuccess(InvolvedInOtherNo))
    }

    "successfully validate given an `Yes` value" in {

      val json = Json.obj("involvedInOther" -> true, "details" ->"test")

      Json.fromJson[InvolvedInOther](json) must
        be(JsSuccess(InvolvedInOtherYes("test"), JsPath \ "details"))
    }

    "fail to validate when given an empty `Yes` value" in {

      val json = Json.obj("involvedInOther" -> true)

      Json.fromJson[InvolvedInOther](json) must
        be(JsError((JsPath \ "details") -> JsonValidationError("error.path.missing")))
    }

    "write the correct value" in {

      Json.toJson(InvolvedInOtherNo: InvolvedInOther) must
        be(Json.obj("involvedInOther" -> false))

      Json.toJson(InvolvedInOtherYes("test"): InvolvedInOther) must
        be(Json.obj(
          "involvedInOther" -> true,
          "details" -> "test"
        ))
    }
  }

  "convert des to frontend model successfully" in {
    val desModel = BusinessActivityDetails(true, None)
    InvolvedInOther.conv(desModel) must be(Some(InvolvedInOtherNo))
  }

  "convert des to frontend model successfully when involved in other is false with other value" in {
    val desModel = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("involve in other text","","99999")))))
    InvolvedInOther.conv(desModel) must be(Some(InvolvedInOtherYes("involve in other text")))
  }

  "convert des to frontend model successfully when OtherBusinessActivities is none" in {
    val desModel = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, None)))
    InvolvedInOther.conv(desModel) must be(None)
  }

  "convert des to frontend model successfully when Des ExpectedAMLSTurnover is none" in {
    val desModel = BusinessActivityDetails(false, None)
    InvolvedInOther.conv(desModel) must be(None)
  }

}
