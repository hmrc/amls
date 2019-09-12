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

import models.des.businessactivities.EabServices
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json._


class ServicesSpec extends PlaySpec with MockitoSugar {

  "ServicesSpec" must {

    val businessServices: Set[Service] = Set(Residential, Commercial, Auction, Relocation,
      BusinessTransfer, AssetManagement, LandManagement, Development, SocialHousing)

    "JSON validation" must {

      "successfully validate given values" in {
        val json = Json.obj("services" -> Seq("01", "02", "03", "04", "05", "06", "07", "08", "09"), "dateOfChange" -> "2016-02-24")

        Json.fromJson[Services](json) must
          be(JsSuccess(Services(businessServices,  Some("2016-02-24")), JsPath))
      }

      "fail when on path is missing" in {
        Json.fromJson[Services](Json.obj("service" -> Seq("01"))) must
          be(JsError((JsPath \ "services") -> ValidationError("error.path.missing")))
      }

      "fail when on invalid data" in {
        Json.fromJson[Services](Json.obj("services" -> Seq("40"))) must
          be(JsError(((JsPath \ "services") (0) \ "services") -> JsonValidationError("error.invalid")))
      }

      "successfully validate json write" in {
        val json = Json.obj("services" -> Set("01", "02", "03", "04", "05", "06", "07", "08", "09"))
        Json.toJson(Services(businessServices)) must be(json)

      }
    }

    "convert des model to frontend eab service model" in {
      val convertedModel = Services(Set(Residential, Development, AssetManagement, Auction, Relocation))
      val desModel = Some(EabServices(true, false, true, true, false, true, false, true, false))
      Services.conv(desModel) must be(Some(convertedModel))
    }

    "convert des model to frontend eab service model when EabServices has false for all the options" in {
      val desModel = Some(EabServices(false, false, false, false, false, false, false, false, false))
      Services.conv(desModel) must be(None)
    }
  }
}
