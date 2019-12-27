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

package models.fe.hvd

import models.des.DesConstants
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}


class ProductsSpec extends PlaySpec with MockitoSugar {

  "Products" must {

    "JSON validation" must {

      "successfully validate given values" in {
        val json =  Json.obj(
          "products" -> Seq("06","07", "08", "02", "01", "11"))

        Json.fromJson[Products](json) must
          be(JsSuccess(Products(Set(Clothing, Jewellery, Alcohol, Caravans, Gold, Tobacco))))
      }

      "successfully validate given values with option other details" in {
        val json =  Json.obj(
          "products" -> Seq("09", "12"),
        "otherDetails" -> "test")

        Json.fromJson[Products](json) must
          be(JsSuccess(Products(Set(Other("test"), ScrapMetals))))
      }

      "fail when on path is missing" in {
        Json.fromJson[Products](Json.obj(
          "product" -> Seq("01"))) must
          be(JsError((JsPath \ "products") -> JsonValidationError("error.path.missing")))
      }

      "fail when on invalid data" in {
        Json.fromJson[Products](Json.obj("products" -> Seq("40"))) must
          be(JsError(JsPath \ "products"  -> JsonValidationError("error.invalid")))
      }

      "write valid data in using json write" in {
        Json.toJson[Products](Products(Set(Tobacco, Other("test657")))) must be (
        Json.obj("products" -> Json.arr("02", "12"),
          "otherDetails" -> "test657"
        ))
      }
    }
    "convert to None given hvdGoodsSold = None" in {
      Products.conv(DesConstants.testBusinessActivities.copy(hvdGoodsSold = None)) must be(None)
    }
  }
}
