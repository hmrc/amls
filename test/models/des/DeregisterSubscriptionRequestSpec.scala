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

package models.des

import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class DeregisterSubscriptionRequestSpec extends PlaySpec {

  "DeregisterSubscriptionRequest" must {

    "successfully read json" when {

      "deregReasonOther has value" in {
        val inputRequest = Json.obj("acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "deregistrationDate" -> "2015-08-23",
          "deregistrationReason" -> "Other, please specify",
          "deregReasonOther" -> "Other Reason")

        val model = DeregisterSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", DeregistrationReason.Other, Some("Other Reason"))

        DeregisterSubscriptionRequest.format.reads(inputRequest) must be(JsSuccess(model))
      }

      "deregReasonOther is none" in {

        val inputRequest  = Json.obj(
          "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "deregistrationDate" -> "2015-08-23",
          "deregistrationReason" -> "Out of scope"
        )

        val model = DeregisterSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", DeregistrationReason.OutOfscope, None)

        DeregisterSubscriptionRequest.format.reads(inputRequest) must be(JsSuccess(model))

      }
    }

    "successfully write json" when {
      "deregReasonOther has value" in {
        val json = Json.obj("acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "deregistrationDate" -> "2015-08-23",
          "deregistrationReason" -> "Other, please specify",
          "deregReasonOther" -> "Other Reason")

        val model = DeregisterSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", DeregistrationReason.Other, Some("Other Reason"))

        DeregisterSubscriptionRequest.format.writes(model) must be(json)
      }

      "deregReasonOther is none" in {

        val json  = Json.obj(
          "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "deregistrationDate" -> "2015-08-23",
          "deregistrationReason" -> "Out of scope"
        )

        val model = DeregisterSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", DeregistrationReason.OutOfscope, None)

        DeregisterSubscriptionRequest.format.writes(model) must be(json)
      }
    }

    "throw error on invalid data" when {

      "deregistrationReason is invalid" in {
        val inputRequest  = Json.obj(
          "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "deregistrationDate" -> "2015-08-23",
          "deregistrationReason" -> "invalid"
        )

        DeregisterSubscriptionRequest.format.reads(inputRequest) must be(JsError(List((JsPath \"deregistrationReason" \"deregistrationReason",
          List(ValidationError(List("error.invalid"))))))
        )
      }
    }
  }

}
