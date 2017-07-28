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

import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import play.api.test.Helpers._

class PaymentControllerSpec extends PlaySpec with MockitoSugar {

  trait Fixture {
    val testController = new PaymentController()

    val accountType = "org"
    val accountRef = "TestOrgRef"
    val amlsRegistrationNumber = "XAML00000567890"

    val postRequest = FakeRequest("POST", "/")
      .withHeaders("CONTENT_TYPE" -> "application/json")
      .withBody[String]("")

    private val postRequestWithNoBody = FakeRequest("POST", "/")
      .withHeaders("CONTENT_TYPE" -> "application/json")
  }

  "PaymentController" must {
    "return OK" when {
      "AMLSRefNo is found" in new Fixture {

        val result = testController.savePayment(accountType, accountRef, amlsRegistrationNumber)(postRequest)

        status(result) mustBe OK


      }
    }
    "return BAD_REQUEST" when {
      "AMLSRefNo is not found" in new Fixture {

        val result = testController.savePayment(accountType, accountRef, "123")(postRequest)

        status(result) mustBe BAD_REQUEST

      }
    }
  }

}
