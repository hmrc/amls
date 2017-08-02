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

import generators.PaymentGenerator
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.PaymentService

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
    val amlsRegistrationNumber = "XAML00000567890"

    val postRequest = FakeRequest("POST", "/")
      .withHeaders("CONTENT_TYPE" -> "text/plain")
      .withBody[String]("")

    private val postRequestWithNoBody = FakeRequest("POST", "/")
      .withHeaders("CONTENT_TYPE" -> "text/plain")
  }

  "PaymentController" must {
    "return CREATED" when {
      "paymentService returns payment details" in new Fixture {

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
      "paymentService does not return payment details" in new Fixture {

        when {
          testPaymentService.savePayment(any(), any())(any(),any())
        } thenReturn {
          Future.successful(None)
        }

        val result = testController.savePayment(accountType, accountRef, amlsRegistrationNumber)(postRequest)

        status(result) mustBe INTERNAL_SERVER_ERROR

      }
    }
  }

}
