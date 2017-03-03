package controllers

import connectors.WithdrawSubscriptionConnector
import models.des.WithdrawSubscriptionResponse
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Matchers._
import org.mockito.Mockito._
import play.api.libs.json.{JsNull, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, _}

import scala.concurrent.Future

class WithdrawSubscriptionControllerSpec extends PlaySpec with MockitoSugar {

  trait Fixture {
    object withdrawSubscriptionController extends WithdrawSubscriptionController {
      private[controllers] override val withdrawSubscriptionConnector = mock[WithdrawSubscriptionConnector]
    }
  }

  val amlsRegistrationNumber = "XAML00000567890"
  val success = WithdrawSubscriptionResponse("2016-09-17T09:30:47Z")
  val inputRequest = Json.obj("acknowledgementReference" -> "AEF7234BGG12539GH143856HEA123412",
    "withdrawalDate" -> "2015-08-23",
    "withdrawalReason" -> "Other, please specify",
    "withdrawalReasonOthers" -> "Other Reason")

  val postRequest = FakeRequest("POST", "/")
    .withHeaders("CONTENT_TYPE" -> "application/json")
    .withBody[JsValue](inputRequest)

  val postRequestWithNoBody = FakeRequest("POST", "/")
    .withHeaders("CONTENT_TYPE" -> "application/json")
    .withBody[JsValue](JsNull)

  "WithdrawSubscriptionController" must {

    "successfully return success response on valid request" in new Fixture {
      when(withdrawSubscriptionController.withdrawSubscriptionConnector.withdrawal(any(), any())
      (any(), any(), any())).thenReturn(Future.successful(success))

      val result = withdrawSubscriptionController.withdrawal(amlsRegistrationNumber)(postRequest)
       status(result) must be(OK)
       contentAsJson(result) must be(Json.toJson(success))
    }


    "successfully return failed response on invalid request" in new Fixture {
      val response = Json.obj("errors" -> Seq (
        Json.obj("path" ->"obj.withdrawalReason",
        "error" ->"error.path.missing"),
        Json.obj("path" ->"obj.acknowledgementReference",
        "error" ->"error.path.missing"),
        Json.obj("path" ->"obj.withdrawalDate",
        "error" ->"error.path.missing"))
      )

      val result = withdrawSubscriptionController.withdrawal(amlsRegistrationNumber)(postRequestWithNoBody)
      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(response)
    }
  }
}
