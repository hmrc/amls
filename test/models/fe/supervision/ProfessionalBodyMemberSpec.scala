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

package models.fe.supervision

import models.des.supervision.{MemberOfProfessionalBody, ProfessionalBodyDesMember, ProfessionalBodyDetails}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class ProfessionalBodyMemberSpec extends PlaySpec with MockitoSugar {

  "JSON validation" must {

      "validate given option Yes" in {
        val json =  Json.obj("isAMember" -> true)

        Json.fromJson[ProfessionalBodyMember](json) must
          be(JsSuccess(ProfessionalBodyMemberYes))
      }

      "validate given values with option No" in {
        val json =  Json.obj("isAMember" -> false)

        Json.fromJson[ProfessionalBodyMember](json) must
          be(JsSuccess(ProfessionalBodyMemberNo))
      }

      "fail when on path is missing" in {
        Json.fromJson[ProfessionalBodyMember](Json.obj()) must
          be(JsError((JsPath \ "isAMember") -> ValidationError("error.path.missing")))
      }

      "fail when on invalid data" in {
        Json.fromJson[ProfessionalBodyMember](Json.obj("isAMember" -> "")) must
          be(JsError((JsPath \ "isAMember") -> ValidationError("error.expected.jsboolean")))
      }

      "write valid data in using json write" in {
        Json.toJson[ProfessionalBodyMember](ProfessionalBodyMemberYes) must be (Json.obj("isAMember" -> true))
      }

      "write valid data in using json write with Option No" in {
        Json.toJson[ProfessionalBodyMember](ProfessionalBodyMemberNo) must be (Json.obj("isAMember" -> false))
      }

  }

  "convert des to frontend model successfully when ProfessionalBodyDesMember is none" in {
    val desModel = Some(ProfessionalBodyDetails(
      true,
      Some("DetailsIfFinedWarned"),
      None
    ))
    ProfessionalBodyMember.conv(desModel) must be(None)
  }

}
