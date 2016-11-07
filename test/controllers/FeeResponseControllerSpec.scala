/*
 * Copyright 2016 HM Revenue & Customs
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

import java.lang.RuntimeException

import exceptions.HttpStatusException
import models.des.{FeeResponse, SubscriptionResponseType}
import models.fe.businessmatching.{BusinessActivities => BMBusinessActivities, BusinessType => BT}
import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.FeeResponseRepository
import utils.IterateeHelpers

import scala.concurrent.Future

class FeeResponseControllerSpec extends PlaySpec
  with MockitoSugar
  with ScalaFutures
  with IntegrationPatience
  with IterateeHelpers{


  object TestFeeResponseController extends FeeResponseController {
    override private[controllers] val repository: FeeResponseRepository = mock[FeeResponseRepository]
  }

  "Fee Response Controller" when {

    val amlsRegistrationNumber = "ABCDabcd0000011"

    val request = FakeRequest()
      .withHeaders(CONTENT_TYPE -> "application/json")

    val validFeeResponse = FeeResponse(SubscriptionResponseType, amlsRegistrationNumber, 150.00, Some(100.0), 300.0, 550.0, Some("XA353523452345"), None,
      new DateTime(2017,12,1,1,3,DateTimeZone.UTC))

    "GET" must {
      "return valid fee response" in {
        when(TestFeeResponseController.repository.findLatestByAmlsReference(any())).thenReturn(Future.successful(Some(validFeeResponse)))
        val response = TestFeeResponseController.get("accountType", "id", amlsRegistrationNumber)(request)

        status(response) must be(OK)
        contentAsJson(response) must be(Json.toJson(validFeeResponse))

      }

      "return not found when there is no record" in {

        when(TestFeeResponseController.repository.findLatestByAmlsReference(any())).thenReturn(Future.successful(None))
        val response = TestFeeResponseController.get("accountType", "id", amlsRegistrationNumber)(request)

        status(response) must be(NOT_FOUND)

      }

      "return a 500 error when mongo returns exception" in {

        when(TestFeeResponseController.repository.findLatestByAmlsReference(any())).thenReturn(Future.failed(new RuntimeException))
        val response = TestFeeResponseController.get("accountType", "id", amlsRegistrationNumber)(request)

        status(response) must be(INTERNAL_SERVER_ERROR)

      }
    }

  }

}
