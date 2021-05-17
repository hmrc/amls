/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}
import WithdrawalReason._

class WithdrawSubscriptionRequestSpec extends PlaySpec {

  val withdrawalReasons: Map[String, WithdrawalReason] = Map(
    "Out of scope" -> OutOfscope,
    "Not trading in own right" -> NotTradingInOwnRight,
    "Under another supervisor" -> UnderAnotherSupervisor,
    "Joined AWRS Group" -> JoinedAWRSGroup
  )

  "WithdrawSubscriptionRequest" must {

    "successfully read json" when {

      withdrawalReasons foreach {
        case (str,md) => {
          s"withdrawalReason is $str" in {
            val inputRequest = Json.obj(
              "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
              "withdrawalDate" -> "2015-08-23",
              "withdrawalReason" -> str
            )

            val model = WithdrawSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", md, None)

            WithdrawSubscriptionRequest.format.reads(inputRequest) must be(JsSuccess(model))
          }
        }
      }

      "withdrawalReasonOthers has value" in {
        val inputRequest = Json.obj("acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "withdrawalDate" -> "2015-08-23",
          "withdrawalReason" -> "Other, please specify",
          "withdrawalReasonOthers" -> "Other Reason")

        val model = WithdrawSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", WithdrawalReason.Other, Some("Other Reason"))

        WithdrawSubscriptionRequest.format.reads(inputRequest) must be(JsSuccess(model))
      }

      "withdrawalReasonOthers is none" in {

        val inputRequest  = Json.obj(
          "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "withdrawalDate" -> "2015-08-23",
          "withdrawalReason" -> "Out of scope"
        )

        val model = WithdrawSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", WithdrawalReason.OutOfscope, None)

        WithdrawSubscriptionRequest.format.reads(inputRequest) must be(JsSuccess(model))

      }
    }

    "successfully write json" when {

      withdrawalReasons foreach {
        case (str, md) => {
          s"withdrawalReason is $str" in {
            val json = Json.obj(
              "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
              "withdrawalDate" -> "2015-08-23",
              "withdrawalReason" -> str
            )

            val model = WithdrawSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", md, None)

            WithdrawSubscriptionRequest.format.writes(model) must be(json)
          }
        }
      }

      "withdrawalReasonOthers has value" in {
        val json = Json.obj("acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "withdrawalDate" -> "2015-08-23",
          "withdrawalReason" -> "Other, please specify",
          "withdrawalReasonOthers" -> "Other Reason")

        val model = WithdrawSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", WithdrawalReason.Other, Some("Other Reason"))

        WithdrawSubscriptionRequest.format.writes(model) must be(json)
      }

      "withdrawalReasonOthers is none" in {

        val json  = Json.obj(
          "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "withdrawalDate" -> "2015-08-23",
          "withdrawalReason" -> "Out of scope"
        )

        val model = WithdrawSubscriptionRequest("AEF7234BGG12539GH143856HEA123412", "2015-08-23", WithdrawalReason.OutOfscope, None)

        WithdrawSubscriptionRequest.format.writes(model) must be(json)
      }
    }

    "throw error on invalid data" when {
      "withdrawalReason is invalid" in {
        val inputRequest  = Json.obj(
          "acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
          "withdrawalDate" -> "2015-08-23",
          "withdrawalReason" -> "invalid"
        )

        WithdrawSubscriptionRequest.format.reads(inputRequest) must be(JsError(List((JsPath \"withdrawalReason" \"withdrawalReason",
          List(JsonValidationError(List("error.invalid"))))))
        )
      }
    }
  }

}
