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

package models.fe.businessactivities

import models.des.businessactivities.{BusinessActivityDetails, OtherBusinessActivities, ExpectedAMLSTurnover => DesExpectedAMLSTurnover}
import models.fe.businessactivities.ExpectedBusinessTurnover.{First, Second, Third, Fourth, Fifth, Sixth, Seventh}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class ExpectedBusinessTurnoverSpec extends PlaySpec with MockitoSugar {

  "ExpectedBusinessTurnover" should {

    "JSON validation" must {

      "successfully validate given an enum value" in {

        Json.fromJson[ExpectedBusinessTurnover](Json.obj("expectedBusinessTurnover" -> "01")) must
          be(JsSuccess(ExpectedBusinessTurnover.First))

        Json.fromJson[ExpectedBusinessTurnover](Json.obj("expectedBusinessTurnover" -> "02")) must
          be(JsSuccess(ExpectedBusinessTurnover.Second))

        Json.fromJson[ExpectedBusinessTurnover](Json.obj("expectedBusinessTurnover" -> "03")) must
          be(JsSuccess(ExpectedBusinessTurnover.Third))

        Json.fromJson[ExpectedBusinessTurnover](Json.obj("expectedBusinessTurnover" -> "04")) must
          be(JsSuccess(ExpectedBusinessTurnover.Fourth))

        Json.fromJson[ExpectedBusinessTurnover](Json.obj("expectedBusinessTurnover" -> "05")) must
          be(JsSuccess(ExpectedBusinessTurnover.Fifth))

        Json.fromJson[ExpectedBusinessTurnover](Json.obj("expectedBusinessTurnover" -> "06")) must
          be(JsSuccess(ExpectedBusinessTurnover.Sixth))

        Json.fromJson[ExpectedBusinessTurnover](Json.obj("expectedBusinessTurnover" -> "07")) must
          be(JsSuccess(ExpectedBusinessTurnover.Seventh))
      }


      "write the correct value" in {
        Json.toJson(ExpectedBusinessTurnover.First) must
          be(Json.obj("expectedBusinessTurnover" -> "01"))

        Json.toJson(ExpectedBusinessTurnover.Second) must
          be(Json.obj("expectedBusinessTurnover" -> "02"))

        Json.toJson(ExpectedBusinessTurnover.Third) must
          be(Json.obj("expectedBusinessTurnover" -> "03"))

        Json.toJson(ExpectedBusinessTurnover.Fourth) must
          be(Json.obj("expectedBusinessTurnover" -> "04"))

        Json.toJson(ExpectedBusinessTurnover.Fifth) must
          be(Json.obj("expectedBusinessTurnover" -> "05"))

        Json.toJson(ExpectedBusinessTurnover.Sixth) must
          be(Json.obj("expectedBusinessTurnover" -> "06"))

        Json.toJson(ExpectedBusinessTurnover.Seventh) must
          be(Json.obj("expectedBusinessTurnover" -> "07"))
      }

      "throw error for invalid data" in {
        Json.fromJson[ExpectedBusinessTurnover](Json.obj("expectedBusinessTurnover" -> "20")) must
          be(JsError(JsPath \ "expectedBusinessTurnover", ValidationError("error.invalid")))
      }
    }

    "convert des to frontend model successfully when involved in other is false with other value" in {
      val desModel = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","14999","99999")))))
      ExpectedBusinessTurnover.conv(desModel) must be(Some(First))

      val desModel1 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","49999","99999")))))
      ExpectedBusinessTurnover.conv(desModel1) must be(Some(Second))

      val desModel2 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","99999","99999")))))
      ExpectedBusinessTurnover.conv(desModel2) must be(Some(Third))

      val desModel3 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","249999","99999")))))
      ExpectedBusinessTurnover.conv(desModel3) must be(Some(Fourth))

      val desModel4 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","999999","99999")))))
      ExpectedBusinessTurnover.conv(desModel4) must be(Some(Fifth))

      val desModel5 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","10000000","99999")))))
      ExpectedBusinessTurnover.conv(desModel5) must be(Some(Sixth))

      val desModel6 = BusinessActivityDetails(false, Some(DesExpectedAMLSTurnover(None, Some(OtherBusinessActivities("","100000000","99999")))))
      ExpectedBusinessTurnover.conv(desModel6) must be(Some(Seventh))

    }

    "convert des to frontend model successfully when Des ExpectedAMLSTurnover is none" in {
      val desModel = BusinessActivityDetails(false, None)
      ExpectedBusinessTurnover.conv(desModel) must be(None)

    }
  }
}
