package models.des

import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class WithdrawSubscriptionRequestSpec extends PlaySpec {

  "WithdrawSubscriptionRequest" must {

    "successfully read json" when {

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
          List(ValidationError(List("error.invalid"))))))
        )
      }
    }
  }

}
