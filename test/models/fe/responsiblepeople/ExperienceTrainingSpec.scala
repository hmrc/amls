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

package models.fe.responsiblepeople

import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

class ExperienceTrainingSpec extends PlaySpec with MockitoSugar {

  "JSON validation" must {
    "successfully validate given an enum value" in {

      Json.fromJson[ExperienceTraining](Json.obj("experienceTraining" -> false)) must
        be(JsSuccess(ExperienceTrainingNo))
    }

    "successfully validate given an `Yes` value" in {

      val json = Json.obj("experienceTraining" -> true, "experienceInformation" -> "0123456789")

      Json.fromJson[ExperienceTraining](json) must
        be(JsSuccess(ExperienceTrainingYes("0123456789"), JsPath \ "experienceInformation"))
    }

    "fail to validate when given an empty `Yes` value" in {

      val json = Json.obj("experienceTraining" -> true)

      Json.fromJson[ExperienceTraining](json) must
        be(JsError((JsPath \ "experienceInformation") -> JsonValidationError("error.path.missing")))
    }

    "write the correct value for Yes" in {

      Json.toJson(ExperienceTrainingYes("0123456789"): ExperienceTraining) must
        be(Json.obj(
          "experienceTraining" -> true,
          "experienceInformation" -> "0123456789"
        ))
    }

    "write the correct value for No" in {
      Json.toJson(ExperienceTrainingNo: ExperienceTraining) must be(Json.obj("experienceTraining" -> false))

      val json = Json.obj("experienceTraining" -> false)

      Json.fromJson[ExperienceTraining](json) must
        be(JsSuccess(ExperienceTrainingNo))

    }

  }

}
