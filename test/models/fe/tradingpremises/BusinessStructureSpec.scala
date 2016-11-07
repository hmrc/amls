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

package models.fe.tradingpremises

import org.scalatestplus.play.PlaySpec
import play.api.data.mapping.{Failure, Path, Success}
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class BusinessStructureSpec extends PlaySpec {

  "BusinessStructure" should {

    "Read JSON data successfully" in {
      Json.fromJson[BusinessStructure](Json.obj("agentsBusinessStructure" -> "01")) must be(JsSuccess(BusinessStructure.SoleProprietor,
        JsPath \ "agentsBusinessStructure"))

      Json.fromJson[BusinessStructure](Json.obj("agentsBusinessStructure" -> "02")) must be(JsSuccess(BusinessStructure.LimitedLiabilityPartnership,
        JsPath \ "agentsBusinessStructure"))

      Json.fromJson[BusinessStructure](Json.obj("agentsBusinessStructure" -> "03")) must be(JsSuccess(BusinessStructure.Partnership,
        JsPath \ "agentsBusinessStructure"))

      Json.fromJson[BusinessStructure](Json.obj("agentsBusinessStructure" -> "04")) must be(JsSuccess(BusinessStructure.IncorporatedBody,
        JsPath \ "agentsBusinessStructure"))

      Json.fromJson[BusinessStructure](Json.obj("agentsBusinessStructure" -> "05")) must be(JsSuccess(BusinessStructure.UnincorporatedBody,
        JsPath \ "agentsBusinessStructure"))
    }

    "Write JSON data successfully" in {

      Json.toJson(BusinessStructure.SoleProprietor) must be(Json.obj("agentsBusinessStructure" -> "01"))
      Json.toJson(BusinessStructure.LimitedLiabilityPartnership) must be(Json.obj("agentsBusinessStructure" -> "02"))
      Json.toJson(BusinessStructure.Partnership) must be(Json.obj("agentsBusinessStructure" -> "03"))
      Json.toJson(BusinessStructure.IncorporatedBody) must be(Json.obj("agentsBusinessStructure" -> "04"))
      Json.toJson(BusinessStructure.UnincorporatedBody) must be(Json.obj("agentsBusinessStructure" -> "05"))
    }

    "throw error for invalid data" in {
      Json.fromJson[BusinessStructure](Json.obj("agentsBusinessStructure" -> "20")) must
        be(JsError(JsPath \ "agentsBusinessStructure", ValidationError("error.invalid")))
    }

    "convert input string successfully" in {
      BusinessStructure.conv("Limited Liability Partnership") must be(Some(BusinessStructure.LimitedLiabilityPartnership))
      BusinessStructure.conv("Partnership") must be(Some(BusinessStructure.Partnership))
      BusinessStructure.conv("Corporate Body") must be(Some(BusinessStructure.IncorporatedBody))
      BusinessStructure.conv("Unincorporated Body") must be(Some(BusinessStructure.UnincorporatedBody))
    }
  }

}
