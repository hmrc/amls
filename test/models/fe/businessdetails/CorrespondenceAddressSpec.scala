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

package models.fe.businessdetails

import models.des.aboutthebusiness.{AlternativeAddress, Address => DesAddress}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class CorrespondenceAddressSpec extends PlaySpec {

  "CorrespondenceAddress" must {

    "json read the given UKCorrespondenceAddress address" in {

      val data = UKCorrespondenceAddress("Name", "Business", "38B", "Longbenton", Some("line 1"), None, "NE7 7DX")
      val jsonObj = Json.obj(
        "yourName" -> "Name",
        "businessName" -> "Business",
        "correspondenceAddressLine1" -> "38B",
        "correspondenceAddressLine2" -> "Longbenton",
        "correspondenceAddressLine3" -> "line 1",
        "correspondencePostCode" -> "NE7 7DX")

      Json.fromJson[CorrespondenceAddress](jsonObj) must be
      JsSuccess(data)
    }

    "json read the given NonUKCorrespondenceAddress address" in {

      val data = NonUKCorrespondenceAddress("Name", "Business", "38B", "some place", Some("line 1"), None, "AR")
      val jsonObj = Json.obj(
        "yourName" -> "Name",
        "businessName" -> "Business",
        "correspondenceAddressLine1" -> "38B",
        "correspondenceAddressLine2" -> "some place",
        "correspondenceAddressLine3" -> "line 1",
        "correspondenceCountry" -> "AR")

      Json.fromJson[CorrespondenceAddress](jsonObj) must be
      JsSuccess(data)
    }

    "write correct value to json" in {
      val data = UKCorrespondenceAddress("Name", "Business", "38B", "Longbenton", Some("line 1"), None, "NE7 7DX")

      Json.toJson(data) must
        be(Json.obj(
          "yourName" -> "Name",
          "businessName" -> "Business",
          "correspondenceAddressLine1" -> "38B",
          "correspondenceAddressLine2" -> "Longbenton",
          "correspondenceAddressLine3" -> "line 1",
          "correspondencePostCode" -> "NE7 7DX")
        )
    }

    "write correct value to json for NonUk Registered office" in {
      val data = NonUKCorrespondenceAddress("Name", "Business", "38B", "some place", Some("line 1"), None, "AR")

      Json.toJson(data) must
        be(Json.obj(
          "yourName" -> "Name",
          "businessName" -> "Business",
          "correspondenceAddressLine1" -> "38B",
          "correspondenceAddressLine2" -> "some place",
          "correspondenceAddressLine3" -> "line 1",
          "correspondenceCountry" -> "AR")
        )
    }

    "convert des model to frontend ATB model" in {

      val desAddress = DesAddress("addr1", "addr2", None, None, "UK", None)
      val desModel = Some(AlternativeAddress("name","trade name",desAddress))

      CorrespondenceAddress.conv(desModel) must be(Some(NonUKCorrespondenceAddress("name", "trade name",
        "addr1","addr2",None,None,"UK")))
    }

    "convert des model to frontend ATB model when input is none" in {
      CorrespondenceAddress.conv(None) must be(None)
    }
  }
}
