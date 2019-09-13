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

package models.fe.businessdetails

import models.des.aboutthebusiness.PreviouslyRegisteredMLRView
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

class PreviouslyRegisteredSpec extends PlaySpec with MockitoSugar {

  "JSON validation" must {

    "successfully validate given an enum value" in {

      Json.fromJson[PreviouslyRegistered](Json.obj("previouslyRegistered" -> false)) must
        be(JsSuccess(PreviouslyRegisteredNo))
    }

    "successfully validate given an `Yes` value" in {

      val json = Json.obj("previouslyRegistered" -> true, "prevMLRRegNo" ->"12345678")

      Json.fromJson[PreviouslyRegistered](json) must
        be(JsSuccess(PreviouslyRegisteredYes("12345678"), JsPath \ "prevMLRRegNo"))
    }

    "fail to validate when given an empty `Yes` value" in {

      val json = Json.obj("previouslyRegistered" -> true)

      Json.fromJson[PreviouslyRegistered](json) must
        be(JsError((JsPath \ "prevMLRRegNo") -> JsonValidationError("error.path.missing")))
    }

    "write the correct value" in {

      Json.toJson(PreviouslyRegisteredNo) must
        be(Json.obj("previouslyRegistered" -> false))

      Json.toJson(PreviouslyRegisteredYes("12345678")) must
        be(Json.obj(
          "previouslyRegistered" -> true,
          "prevMLRRegNo" -> "12345678"
        ))
    }

    "convert des to frontend model when mlrRegNumber is returned from des" in {
      val desModel = Some(PreviouslyRegisteredMLRView(false,
        None,
        true,
        Some("555553333322222")))
      PreviouslyRegistered.convert(desModel) must be(PreviouslyRegisteredYes("555553333322222"))
    }

    "convert des to frontend model when prevmlrRegNumber is returned from des" in {
      val desModel = Some(PreviouslyRegisteredMLRView(true,
        Some("555553333322222"),
        false,
        None))
      PreviouslyRegistered.convert(desModel) must be(PreviouslyRegisteredYes("555553333322222"))
    }

    "convert des to frontend model when prevmlrRegNumber and  mlrRegNumberis os none" in {
      val desModel = Some(PreviouslyRegisteredMLRView(false,
        None,
        false,
        None))
      PreviouslyRegistered.convert(desModel) must be(PreviouslyRegisteredNo)
    }
  }
}
