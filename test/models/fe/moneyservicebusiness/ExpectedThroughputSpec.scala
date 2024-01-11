/*
 * Copyright 2024 HM Revenue & Customs
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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

class ExpectedThroughputSpec extends PlaySpec with GuiceOneAppPerSuite {

  "ExpectedThroughput" should {

    "JSON validation" must {

      "successfully validate given an enum value" in {

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "01")) must
          be(JsSuccess(ExpectedThroughput.First))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "02")) must
          be(JsSuccess(ExpectedThroughput.Second))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "03")) must
          be(JsSuccess(ExpectedThroughput.Third))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "04")) must
          be(JsSuccess(ExpectedThroughput.Fourth))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "05")) must
          be(JsSuccess(ExpectedThroughput.Fifth))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "06")) must
          be(JsSuccess(ExpectedThroughput.Sixth))

        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "07")) must
          be(JsSuccess(ExpectedThroughput.Seventh))
      }

      "write the correct value" in {

        Json.toJson(ExpectedThroughput.First: ExpectedThroughput) must
          be(Json.obj("throughput" -> "01"))

        Json.toJson(ExpectedThroughput.Second: ExpectedThroughput) must
          be(Json.obj("throughput" -> "02"))

        Json.toJson(ExpectedThroughput.Third: ExpectedThroughput) must
          be(Json.obj("throughput" -> "03"))

        Json.toJson(ExpectedThroughput.Fourth: ExpectedThroughput) must
          be(Json.obj("throughput" -> "04"))

        Json.toJson(ExpectedThroughput.Fifth: ExpectedThroughput) must
          be(Json.obj("throughput" -> "05"))

        Json.toJson(ExpectedThroughput.Sixth: ExpectedThroughput) must
          be(Json.obj("throughput" -> "06"))

        Json.toJson(ExpectedThroughput.Seventh: ExpectedThroughput) must
          be(Json.obj("throughput" -> "07"))
      }

      "throw error for invalid data" in {
        Json.fromJson[ExpectedThroughput](Json.obj("throughput" -> "20")) must
          be(JsError(JsPath \ "throughput", JsonValidationError("error.invalid")))
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

      ExpectedThroughput.convThroughput("£0-£15k") must be(First)
      ExpectedThroughput.convThroughput("£15k-50k") must be(Second)
      ExpectedThroughput.convThroughput("£50k-£100k") must be(Third)
      ExpectedThroughput.convThroughput("£100k-£250k") must be(Fourth)
      ExpectedThroughput.convThroughput("£250k-£1m") must be(Fifth)
      ExpectedThroughput.convThroughput("£1m-10m") must be(Sixth)
      ExpectedThroughput.convThroughput("£10m+") must be(Seventh)

    }

  }
}
