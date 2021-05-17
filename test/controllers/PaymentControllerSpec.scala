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

package controllers

import cats.data.OptionT
import cats.implicits._
import generators.PaymentGenerator
import models.payapi.PaymentStatuses
import models.payments._
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.PaymentService
import utils.{AmlsBaseSpec, AuthAction, SuccessfulAuthAction}

import scala.concurrent.Future

class PaymentControllerSpec extends AmlsBaseSpec with PaymentGenerator {

  trait Fixture {

    val testPaymentService = mock[PaymentService]

    def testPayment = paymentGen.sample.get

    val testPaymentId = testPayment._id

    val safeId = amlsRefNoGen.sample.get

    val authAction: AuthAction = SuccessfulAuthAction

    val testController = new PaymentController(
      paymentService = testPaymentService,
      authAction = authAction,
      bodyParsers = mockBodyParsers,
      cc = mockCC)

    val accountType = "org"
    val accountRef = "TestOrgRef"
    val request = FakeRequest("GET", "/")
  }

  trait CreateRequestFixture extends Fixture {
    val postRequest = FakeRequest("POST", "/")
      .withHeaders("CONTENT_TYPE" -> "text/plain")
      .withBody[String]("")
  }

  "PaymentController" when {
    "saving a new payment" must {
      "return CREATED" when {
        "paymentService returns payment details" in new CreateRequestFixture {

          when {
            testPaymentService.createPayment(any(), any(), any())(any(), any())
          } thenReturn {
            Future.successful(Some(testPayment))
          }

          val result = testController.savePayment(accountType, accountRef, amlsRegistrationNumber, safeId)(postRequest)

          status(result) mustBe CREATED

        }
      }
      "return INTERNAL_SERVER_ERROR" when {
        "paymentService does not return payment details" in new CreateRequestFixture {

          when {
            testPaymentService.createPayment(any(), any(), any())(any(), any())
          } thenReturn {
            Future.successful(None)
          }

          val result = testController.savePayment(accountType, accountRef, amlsRegistrationNumber, safeId)(postRequest)

          status(result) mustBe INTERNAL_SERVER_ERROR

        }
      }
      "return BAD_REQUEST" when {
        "amlsRefNo does not meet regex" in new CreateRequestFixture {

          when {
            testPaymentService.createPayment(any(), any(), any())(any(), any())
          } thenReturn {
            Future.successful(None)
          }

          val result = testController.savePayment(accountType, accountRef, "amlsRefNo", safeId)(postRequest)

          status(result) mustBe BAD_REQUEST
        }
      }
    }

    "retrieving a payment by payment reference" must {
      "return the payment" in new Fixture {
        val paymentRef = paymentRefGen.sample.get
        val payment = paymentGen.sample.get

        when {
          testPaymentService.getPaymentByPaymentReference(eqTo(paymentRef))(any(), any())
        } thenReturn Future.successful(Some(payment))

        val result = testController.getPaymentByRef(accountType, accountRef, paymentRef)(request)

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(payment)
      }

      "return a 404 Not Found when the reference doesn't match" in new Fixture {
        when {
          testPaymentService.getPaymentByPaymentReference(any())(any(), any())
        } thenReturn Future.successful(None)

        val result = testController.getPaymentByRef(accountType, accountRef, paymentRefGen.sample.get)(request)

        status(result) mustBe NOT_FOUND
      }

    }

    "retrieving a payment by AMLS reference" must {
      "return the payment" in new Fixture {
        val amlsRef = amlsRefNoGen.sample.get
        val payment = paymentGen.sample.get

        when {
          testPaymentService.getPaymentByAmlsReference(eqTo(amlsRef))(any(), any())
        } thenReturn Future.successful(Some(payment))

        val result = testController.getPaymentByAmlsRef(accountType, accountRef, amlsRef)(request)

        status(result) mustBe OK
        contentAsJson(result) mustBe Json.toJson(payment)
      }

      "return a 404 Not Found when the reference doesn't match" in new Fixture {
        when {
          testPaymentService.getPaymentByAmlsReference(any())(any(), any())
        } thenReturn Future.successful(None)

        val result = testController.getPaymentByAmlsRef(accountType, accountRef, paymentRefGen.sample.get)(request)

        status(result) mustBe NOT_FOUND
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

    "updating payment BACS flag" must {
      "set the BACS flag according to input data" in new Fixture {
        val payment = paymentGen.sample.get.copy(isBacs = None)
        val bacsRequest = SetBacsRequest(isBacs = true)

        when {
          testController.paymentService.getPaymentByPaymentReference(eqTo(payment.reference))(any(), any())
        } thenReturn Future.successful(Some(payment))

        when {
          testController.paymentService.updatePayment(any())(any(), any())
        } thenReturn Future.successful(true)

        val putRequest = FakeRequest("PUT", "/").withBody[JsValue](Json.toJson(bacsRequest))

        val result = testController.updateBacsFlag(accountType, accountRef, payment.reference)(putRequest)

        status(result) mustBe NO_CONTENT
        verify(testController.paymentService).updatePayment(eqTo(payment.copy(isBacs = Some(true))))(any(), any())
      }

      "return 404 Not Found if the payment was not found" in new Fixture {
        val bacsRequest = SetBacsRequest(isBacs = true)

        when {
          testController.paymentService.getPaymentByPaymentReference(any())(any(), any())
        } thenReturn Future.successful(None)

        val putRequest = FakeRequest("PUT", "/").withBody[JsValue](Json.toJson(bacsRequest))
        val result = testController.updateBacsFlag(accountType, accountRef, paymentRefGen.sample.get)(putRequest)

        status(result) mustBe NOT_FOUND
      }
    }
  }

  "PaymentController" must {
    "use the payments service to create a new bacs payment" in new Fixture {
      val createBacsRequest = createBacsPaymentRequestGen.sample.get
      val payment = Payment(createBacsRequest)

      when {
        testController.paymentService.createBacsPayment(eqTo(createBacsRequest))(any(), any())
      } thenReturn Future.successful(payment)

      val postRequest = FakeRequest("POST", "/").withBody[JsValue](Json.toJson(createBacsRequest))
      val result = testController.createBacsPayment(accountType, accountRef)(postRequest)

      status(result) mustBe CREATED
      contentAsJson(result) mustBe Json.toJson(payment)
    }

    "return 400 if the input json can't be parsed" in new Fixture {

      val postRequest = FakeRequest("POST", "/").withBody[JsValue](Json.obj("nonsense" -> "value"))
      val result = testController.createBacsPayment(accountType, accountRef)(postRequest)

      status(result) mustBe BAD_REQUEST
    }
  }
}
