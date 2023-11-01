/*
 * Copyright 2023 HM Revenue & Customs
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

import models.des.aboutthebusiness.{Address => DesAddress}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class RegisteredOfficeSpec extends PlaySpec {

  "RegisteredOffice" must {

    "json read the given UK address" in {

      val data = RegisteredOfficeUK("38B", Some("Longbenton"), Some("line 1"), None, "NE7 7DX")
      val jsonObj = Json.obj(
        "addressLine1" -> "38B",
        "addressLine2" -> "Longbenton",
        "addressLine3" -> "line 1",
        "addressLine4" -> JsNull,
        "postCode" -> "NE7 7DX")

      Json.fromJson[RegisteredOffice](jsonObj) must be
      JsSuccess(data, JsPath \ "postCode")
    }

    "json read the given non UK address" in {

      val data = RegisteredOfficeNonUK("38B", Some("some place"), Some("line 1"), None, "AR")
      val jsonObj = Json.obj(
        "addressLineNonUK1" -> "38B",
        "addressLineNonUK2" -> "some place",
        "addressLineNonUK3" -> "line 1",
        "addressLineNonUK4" -> JsNull,
        "country" -> "AR")

      Json.fromJson[RegisteredOffice](jsonObj) must be
      JsSuccess(data, JsPath \ "country")
    }

    "write correct value to json" in {
      val data = RegisteredOfficeUK("38B", Some("Longbenton"), Some("line 1"), None, "NE7 7DX")

      Json.toJson(data: RegisteredOffice) must
        be(Json.obj(
          "addressLine1" -> "38B",
          "addressLine2" -> "Longbenton",
          "addressLine3" -> "line 1",
          "addressLine4" -> JsNull,
          "postCode" -> "NE7 7DX")
        )
    }

    "write correct value to json for NonUk Registered office" in {
      val data = RegisteredOfficeNonUK("38B", Some("some place"), Some("line 1"), None, "AR")

      Json.toJson(data: RegisteredOffice) must
        be(Json.obj(
          "addressLineNonUK1" -> "38B",
          "addressLineNonUK2" -> "some place",
          "addressLineNonUK3" -> "line 1",
          "addressLineNonUK4" -> JsNull,
          "country" -> "AR")
        )
    }

    "convert des model to frontend ATB model" in {
      val desModel = DesAddress("addr1", Some("addr2"), None, None, "UK", None)
      RegisteredOffice.conv(desModel) must be(RegisteredOfficeNonUK("addr1", Some("addr2"), None, None, "UK"))
    }
  }
}
