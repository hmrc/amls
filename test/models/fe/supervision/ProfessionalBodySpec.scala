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

package models.fe.supervision

import models.des.supervision.{MemberOfProfessionalBody, ProfessionalBodyDesMember, ProfessionalBodyDetails}
import org.scalatest.mock.MockitoSugar
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

      val json = Json.obj("penalised" -> true, "professionalBody" ->"details")

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

    "convert when input is none" in {
      ProfessionalBody.conv(None) must be(Some(ProfessionalBodyNo))
    }

    "convert des to frontend and return ProfessionalBodyNo" in {
      val desModel = Some(ProfessionalBodyDetails(
        false,
        None,
        None
      ))
      ProfessionalBody.conv(desModel) must be(Some(ProfessionalBodyNo))
    }
  }
}
