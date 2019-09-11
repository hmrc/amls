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

package models.fe.asp

import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json._

class ServicesOfBusinessSpec extends PlaySpec with MockitoSugar {

    "JSON validation" must {
      val businessServices: Set[Service] = Set(Accountancy, PayrollServices, BookKeeping, Auditing, FinancialOrTaxAdvice)
      "successfully validate and read services and date of change values" in {

        val json =  Json.obj("services" -> Seq("01","02","03","04","05"),
          "dateOfChange" -> "2016-02-24")

        Json.fromJson[ServicesOfBusiness](json) must
          be(JsSuccess(ServicesOfBusiness(businessServices, Some("2016-02-24")), JsPath))
      }

      "successfully validate selected services value" in {

        val json =  Json.obj("services" -> Seq("01","02","03","04","05"))

        Json.fromJson[ServicesOfBusiness](json) must
          be(JsSuccess(ServicesOfBusiness(businessServices, None)))
      }
      "fail when on invalid data" in {

        Json.fromJson[ServicesOfBusiness](Json.obj("services" -> Seq("40"))) must
          be(JsError(((JsPath \ "services")(0) \ "services") -> ValidationError("error.invalid")))
      }

      "successfully validate json write" in {

        val json = Json.obj("services" -> Set("01","02","03","04","05"))
        Json.toJson(ServicesOfBusiness(businessServices)) must be(json)

      }
    }
}
