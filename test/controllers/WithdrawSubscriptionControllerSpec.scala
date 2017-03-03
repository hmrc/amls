package controllers

import connectors.WithdrawSubscriptionConnector
import models.des.WithdrawSubscriptionResponse
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Matchers._
import org.mockito.Mockito._
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest

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

  "WithdrawSubscriptionController" must {
    "successfully return success response on valid request" in new Fixture {
      when(withdrawSubscriptionController.withdrawSubscriptionConnector.withdrawal(any(), any())
      (any(), any(), any())).thenReturn(Future.successful(success))

      val result = withdrawSubscriptionController.withdrawal(amlsRegistrationNumber)(postRequest)

    }
  }
}
