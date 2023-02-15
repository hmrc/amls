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

package models.des.businessdetails

import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class BusinessTypeSpec extends PlaySpec {
  "BusinessType" should {
    "JSON validation" must {

      "successfully validate given an enum value" in {

        Json.fromJson[BusinessType](JsString("Sole Proprietor")) must
          be(JsSuccess(BusinessType.SoleProprietor))

        Json.fromJson[BusinessType](JsString("Corporate Body")) must
          be(JsSuccess(BusinessType.LimitedCompany))

        Json.fromJson[BusinessType](JsString("Limited Liability Partnership")) must
          be(JsSuccess(BusinessType.LPrLLP))

        Json.fromJson[BusinessType](JsString("Partnership")) must
          be(JsSuccess(BusinessType.Partnership))

        Json.fromJson[BusinessType](JsString("Unincorporated Body")) must
          be(JsSuccess(BusinessType.UnincorporatedBody))
      }

      "throw error for invalid data" in {
        Json.fromJson[BusinessType](JsString("")) must
          be(JsError(JsPath, JsonValidationError("error.invalid")))
      }

      "write the correct value" in {

        Json.toJson(BusinessType.SoleProprietor: BusinessType) must
          be(JsString("Sole Proprietor"))

        Json.toJson(BusinessType.LimitedCompany: BusinessType) must
          be(JsString("Corporate Body"))

        Json.toJson(BusinessType.LPrLLP: BusinessType) must
          be(JsString("Limited Liability Partnership"))

        Json.toJson(BusinessType.Partnership: BusinessType) must
          be(JsString("Partnership"))

        Json.toJson(BusinessType.UnincorporatedBody: BusinessType) must
          be(JsString("Unincorporated Body"))


      }
    }
  }

}
