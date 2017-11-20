/*
 * Copyright 2017 HM Revenue & Customs
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

package models.fe.businessmatching

import models.des.businessactivities.MlrActivitiesAppliedFor
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json._


class BusinessActivitiesSpec extends PlaySpec with MockitoSugar {

  "BusinessActivitiesSpec" must {

    "JSON validation" must {

      "successfully validate given an enum value" in {
        val json = Json.obj("businessActivities" -> Seq("05", "06", "07"))

        Json.fromJson[BusinessActivities](json) must be(JsSuccess(BusinessActivities(
          Set(MoneyServiceBusiness, TrustAndCompanyServices, TelephonePaymentService),
          None
        ), JsPath))

        Json.fromJson[BusinessActivities](Json.obj("businessActivities" -> Seq("01", "02", "03"))) must be(JsSuccess(BusinessActivities(
          Set(AccountancyServices, BillPaymentServices, EstateAgentBusinessService),
          None
        ), JsPath))

        Json.fromJson[BusinessActivities](Json.obj("businessActivities" -> Seq("04"))) must be(JsSuccess(BusinessActivities(
          Set(HighValueDealing),
          None
        ), JsPath))

      }

      "fail when on invalid data" in {
        Json.fromJson[BusinessActivities](Json.obj("businessActivity" -> "01")) must
          be(JsError((JsPath \ "businessActivities") -> ValidationError("error.path.missing")))
      }
    }

    "validate json write" in {
      Json.toJson(BusinessActivities(Set(HighValueDealing, EstateAgentBusinessService))) must
        be(Json.obj("businessActivities" -> Seq("04", "03")))
    }

    "successfully validate json write" in {
      val json = Json.obj("businessActivities" -> Seq("02", "07", "01"))
      Json.toJson(BusinessActivities(Set(BillPaymentServices, TelephonePaymentService, AccountancyServices))) must be(json)

    }

    "throw error for invalid data" in {
      Json.fromJson[BusinessActivities](Json.obj("businessActivities" -> Seq(JsString("20")))) must
        be(JsError((JsPath \ "businessActivities") (0) \ "businessActivities", ValidationError("error.invalid")))
    }

    "convert DesMlrActivitiesAppliedFor to frontend BusinessActivities" in {
      BusinessActivities.conv(Some(MlrActivitiesAppliedFor(true, false, false, true, true, true, false))) must be(
        BusinessActivities(Set(MoneyServiceBusiness, TrustAndCompanyServices, EstateAgentBusinessService, BillPaymentServices)))
    }

    "convert DesMlrActivitiesAppliedFor to frontend BusinessActivities when input is false" in {
      BusinessActivities.conv(Some(MlrActivitiesAppliedFor(false, false, false, false, false, false, false))) must be(
        BusinessActivities(Set.empty))
    }
  }
}
