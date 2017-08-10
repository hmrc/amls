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

package controllers

import cats.data.OptionT
import generators.PaymentGenerator
import org.mockito.Mockito._
import org.mockito.Matchers.{eq => eqTo, _}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.PaymentService
import cats.implicits._
import models.payapi.PaymentStatuses
import models.payments.{PaymentStatusResult, RefreshPaymentStatusRequest}

import scala.concurrent.Future

class PaymentControllerSpec extends PlaySpec with MockitoSugar with PaymentGenerator {

  trait Fixture {

    val testPaymentService = mock[PaymentService]

    def testPayment = paymentGen.sample.get

    val testPaymentId = testPayment._id

    val testController = new PaymentController(
      paymentService = testPaymentService
    )

    val accountType = "org"
    val accountRef = "TestOrgRef"
    val request = FakeRequest("GET", "/")
  }

  trait CreateRequestFixture extends Fixture {
    val postRequest = FakeRequest("POST", "/")
      .withHeaders("CONTENT_TYPE" -> "text/plain")
      .withBody[String]("")

    private val postRequestWithNoBody = FakeRequest("POST", "/")
      .withHeaders("CONTENT_TYPE" -> "text/plain")
  }

  "PaymentController" when {
    "saving a new payment" must {
      "return CREATED" when {
        "paymentService returns payment details" in new CreateRequestFixture {

          when {
            testPaymentService.savePayment(any(), any())(any(), any())
          } thenReturn {
            Future.successful(Some(testPayment))
          }

          val result = testController.savePayment(accountType, accountRef, amlsRegistrationNumber)(postRequest)

          status(result) mustBe CREATED

        }
      }
      "return INTERNAL_SERVER_ERROR" when {
        "paymentService does not return payment details" in new CreateRequestFixture {

          when {
            testPaymentService.savePayment(any(), any())(any(), any())
          } thenReturn {
            Future.successful(None)
          }

          val result = testController.savePayment(accountType, accountRef, amlsRegistrationNumber)(postRequest)

          status(result) mustBe INTERNAL_SERVER_ERROR

        }
      }
      "return BAD_REQUEST" when {
        "amlsRefNo does not meet regex" in new CreateRequestFixture {

          when {
            testPaymentService.savePayment(any(), any())(any(), any())
          } thenReturn {
            Future.successful(None)
          }

          val result = testController.savePayment(accountType, accountRef, "amlsRefNo")(postRequest)

          status(result) mustBe BAD_REQUEST
        }
      }
    }

    "querying a payment reference" must {
      "find a payment given a payment reference" in new Fixture {
        val paymentRef = paymentRefGen.sample.get
        val payment = paymentGen.sample.get

        when {
          testPaymentService.getPaymentByReference(eqTo(paymentRef))(any(), any())
        } thenReturn Future.successful(Some(payment))

        val result = testController.getPaymentByRef(accountType, accountRef, paymentRef)(request)

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(payment)
      }

      "return a 404 Not Found" when {
        "the reference number does not match a payment" in new Fixture {
          when {
            testPaymentService.getPaymentByReference(any())(any(), any())
          } thenReturn Future.successful(None)

          val result = testController.getPaymentByRef(accountType, accountRef, paymentRefGen.sample.get)(request)

          status(result) mustBe NOT_FOUND
        }
      }
    }

    "refreshing the payment status" must {
      "refresh the status using the payments service" in new Fixture {
        val paymentRef = paymentRefGen.sample.get
        val refreshRequest = RefreshPaymentStatusRequest(paymentRef)
        val statusResult = PaymentStatusResult(paymentRef, paymentIdGen.sample.get, PaymentStatuses.Successful)

        val putRequest = FakeRequest("PUT", "/").withBody[JsValue](Json.toJson(refreshRequest))

        when {
          testPaymentService.refreshStatus(eqTo(paymentRef))(any(), any())
        } thenReturn OptionT[Future, PaymentStatusResult](Future.successful(statusResult.some))

        val result = testController.refreshStatus(accountType, accountRef)(putRequest)

        status(result) mustBe OK
      }

      "return a 404 when there is no RefreshStatusResult" in new Fixture {
        val paymentRef = paymentRefGen.sample.get
        val refreshRequest = RefreshPaymentStatusRequest(paymentRef)

        val putRequest = FakeRequest("PUT", "/").withBody[JsValue](Json.toJson(refreshRequest))

        when {
          testPaymentService.refreshStatus(eqTo(paymentRef))(any(), any())
        } thenReturn OptionT[Future, PaymentStatusResult](Future.successful(None))

        val result = testController.refreshStatus(accountType, accountRef)(putRequest)

        status(result) mustBe NOT_FOUND
      }

      "return Bad Request when the input json cannot be parsed" in new Fixture {
        val putRequest = FakeRequest("PUT", "/").withBody[JsValue](Json.obj("random_property" -> "some value"))
        val result = testController.refreshStatus(accountType, accountRef)(putRequest)

        status(result) mustBe BAD_REQUEST
      }
    }
  }

}
