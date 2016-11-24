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

package models.fe.moneyservicebusiness

import models.des.msb.{CountriesList, MsbAllDetails}
import models.fe.moneyservicebusiness.ExpectedThroughput._
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class ExpectedThroughputSpec extends PlaySpec {

  "ExpectedThroughput" should {

    "JSON validation" must {

      "successfully validate given an enum value" in {

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "01")) must
          be(JsSuccess(ExpectedThroughput.First, JsPath \ "throughput"))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "02")) must
          be(JsSuccess(ExpectedThroughput.Second, JsPath \ "throughput"))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "03")) must
          be(JsSuccess(ExpectedThroughput.Third, JsPath \ "throughput"))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "04")) must
          be(JsSuccess(ExpectedThroughput.Fourth, JsPath \ "throughput"))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "05")) must
          be(JsSuccess(ExpectedThroughput.Fifth, JsPath \ "throughput"))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "06")) must
          be(JsSuccess(ExpectedThroughput.Sixth, JsPath \ "throughput"))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "07")) must
          be(JsSuccess(ExpectedThroughput.Seventh, JsPath \ "throughput"))
      }

      "write the correct value" in {

        Json.toJson(ExpectedThroughput.First) must
          be(Json.obj("throughput" -> "01"))

        Json.toJson(ExpectedThroughput.Second) must
          be(Json.obj("throughput" -> "02"))

        Json.toJson(ExpectedThroughput.Third) must
          be(Json.obj("throughput" -> "03"))

        Json.toJson(ExpectedThroughput.Fourth) must
          be(Json.obj("throughput" -> "04"))

        Json.toJson(ExpectedThroughput.Fifth) must
          be(Json.obj("throughput" -> "05"))

        Json.toJson(ExpectedThroughput.Sixth) must
          be(Json.obj("throughput" -> "06"))

        Json.toJson(ExpectedThroughput.Seventh) must
          be(Json.obj("throughput" -> "07"))
      }

      "throw error for invalid data" in {
        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "20")) must
          be(JsError(JsPath \ "throughput", ValidationError("error.invalid")))
      }
    }

    "convert des to frontend model" in {
      val msbAll = Some(MsbAllDetails(
        None,
        true,
        Some(CountriesList(List("AD", "GB"))),
        true)
      )
      ExpectedThroughput.convMsbAll(msbAll) must be(None)

    }

    "convert des to frontend model when input in none" in {

      ExpectedThroughput.convMsbAll(None) must be(None)

    }

    "return correct throughput when supplied with string" in {

      ExpectedThroughput.convThroughput("99999") must be(First)
      ExpectedThroughput.convThroughput("499999") must be(Second)
      ExpectedThroughput.convThroughput("20000000") must be(Fourth)
      ExpectedThroughput.convThroughput("100000000") must be(Fifth)
      ExpectedThroughput.convThroughput("1000000000") must be(Sixth)
      ExpectedThroughput.convThroughput("10000000000") must be(Seventh)

    }

  }
}
