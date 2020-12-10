/*
 * Copyright 2020 HM Revenue & Customs
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

package models.fe.businessactivities

import models.des.businessactivities.{BusinessActivityDetails, OtherBusinessActivities, ExpectedAMLSTurnover => DesExpectedAMLSTurnover}
import models.fe.businessactivities.ExpectedAMLSTurnover.{Fifth, First, Fourth, Second, Seventh, Sixth, Third}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

class ExpectedAMLSTurnoverSpec extends PlaySpec with GuiceOneAppPerSuite {

  "ExpectedAMLSTurnover" should {

    "JSON validation" must {

      "successfully validate given an enum value" in {

        Json.fromJson[ExpectedAMLSTurnover](Json.obj("expectedAMLSTurnover" -> "01")) must
          be(JsSuccess(ExpectedAMLSTurnover.First))

        Json.fromJson[ExpectedAMLSTurnover](Json.obj("expectedAMLSTurnover" -> "02")) must
          be(JsSuccess(ExpectedAMLSTurnover.Second))

        Json.fromJson[ExpectedAMLSTurnover](Json.obj("expectedAMLSTurnover" -> "03")) must
          be(JsSuccess(ExpectedAMLSTurnover.Third))

        Json.fromJson[ExpectedAMLSTurnover](Json.obj("expectedAMLSTurnover" -> "04")) must
          be(JsSuccess(ExpectedAMLSTurnover.Fourth))

        Json.fromJson[ExpectedAMLSTurnover](Json.obj("expectedAMLSTurnover" -> "05")) must
          be(JsSuccess(ExpectedAMLSTurnover.Fifth))

        Json.fromJson[ExpectedAMLSTurnover](Json.obj("expectedAMLSTurnover" -> "06")) must
          be(JsSuccess(ExpectedAMLSTurnover.Sixth))

        Json.fromJson[ExpectedAMLSTurnover](Json.obj("expectedAMLSTurnover" -> "07")) must
          be(JsSuccess(ExpectedAMLSTurnover.Seventh))
      }

      "write the correct value" in {

        Json.toJson(ExpectedAMLSTurnover.First) must
          be(Json.obj("expectedAMLSTurnover" -> "01"))

        Json.toJson(ExpectedAMLSTurnover.Second) must
          be(Json.obj("expectedAMLSTurnover" -> "02"))

        Json.toJson(ExpectedAMLSTurnover.Third) must
          be(Json.obj("expectedAMLSTurnover" -> "03"))

        Json.toJson(ExpectedAMLSTurnover.Fourth) must
          be(Json.obj("expectedAMLSTurnover" -> "04"))

        Json.toJson(ExpectedAMLSTurnover.Fifth) must
          be(Json.obj("expectedAMLSTurnover" -> "05"))

        Json.toJson(ExpectedAMLSTurnover.Sixth) must
          be(Json.obj("expectedAMLSTurnover" -> "06"))

        Json.toJson(ExpectedAMLSTurnover.Seventh) must
          be(Json.obj("expectedAMLSTurnover" -> "07"))
      }

      "throw error for invalid data" in {
        Json.fromJson[ExpectedAMLSTurnover](Json.obj("expectedAMLSTurnover" -> "20")) must
          be(JsError(JsPath \ "expectedAMLSTurnover", JsonValidationError("error.invalid")))
      }
    }

    "convert des to frontend model successfully" in {
      val desModel = BusinessActivityDetails(true, Some(DesExpectedAMLSTurnover(Some("£1m-10m"))))
      ExpectedAMLSTurnover.conv(desModel) must be(Some(Sixth))

    }

    "convert des to frontend model successfully when involved in other is false with other value" in {
      val desModel = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","","£0-£15k")))))
      ExpectedAMLSTurnover.conv(desModel) must be(Some(First))

      val desModel1 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","","£15k-50k")))))
      ExpectedAMLSTurnover.conv(desModel1) must be(Some(Second))

      val desModel2 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","","£50k-£100k")))))
      ExpectedAMLSTurnover.conv(desModel2) must be(Some(Third))

      val desModel3 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","","£100k-£250k")))))
      ExpectedAMLSTurnover.conv(desModel3) must be(Some(Fourth))

      val desModel4 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","","£250k-£1m")))))
      ExpectedAMLSTurnover.conv(desModel4) must be(Some(Fifth))

      val desModel5 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","","£1m-10m")))))
      ExpectedAMLSTurnover.conv(desModel5) must be(Some(Sixth))

      val desModel7 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","","£10m+")))))
      ExpectedAMLSTurnover.conv(desModel7) must be(Some(Seventh))
    }

    "convert des to frontend model successfully when Des ExpectedAMLSTurnover is none" in {
      val desModel = BusinessActivityDetails(false, None)
      ExpectedAMLSTurnover.conv(desModel) must be(None)

    }

    "convert des to frontend model successfully1" in {
      val desModel = BusinessActivityDetails(true, Some(DesExpectedAMLSTurnover(None)))
      ExpectedAMLSTurnover.conv(desModel) must be(None)

    }

    "convert des to frontend model successfully when invalid data is supplied" in {
      val desModel = BusinessActivityDetails(true, Some(DesExpectedAMLSTurnover(Some("011122233344"))))
      ExpectedAMLSTurnover.conv(desModel) must be(None)

    }

  }

}
