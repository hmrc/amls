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

import connectors.ViewDESConnector
import exceptions.HttpStatusException
import models.{SubscriptionViewModel, des}
import models.des.DesConstants
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.IterateeHelpers

import scala.concurrent.Future

class SubscriptionViewControllerSpec
  extends PlaySpec
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with IterateeHelpers
    with OneAppPerSuite {

  object SubscriptionViewController extends SubscriptionViewController {
    override val connector = mock[ViewDESConnector]
  }

  val request = FakeRequest()
    .withHeaders(CONTENT_TYPE -> "application/json")

  "SubscriptionViewController" must {

    val amlsRegistrationNumber = "XAML00000567890"


    "return a `BadRequest` response when the amls registration number is invalid" in {

      val result = SubscriptionViewController.view("test", "test", "test")(request)
      val failure = Json.obj("errors" -> Seq("Invalid AMLS Registration Number"))

      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(failure)
    }

    "return a valid response when the amls registration number is valid" in {

      val response = DesConstants.SubscriptionViewModelForRp

      when {
        SubscriptionViewController.connector.view(eqTo(amlsRegistrationNumber))(any())
      } thenReturn Future.successful(response)

      val result = SubscriptionViewController.view("test", "test", amlsRegistrationNumber)(request)

      status(result) must be(OK)
      contentAsJson(result) must be(Json.toJson(SubscriptionViewModel.convertedViewModel))

    }
    "return an invalid response when the service fails" in {

      when {
        SubscriptionViewController.connector.view(eqTo(amlsRegistrationNumber))(any())
      } thenReturn Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

      whenReady (SubscriptionViewController.view("test", "test", amlsRegistrationNumber)(request).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }

  }

  val testViewSuccessModel = des.SubscriptionView(
    etmpFormBundleNumber = "111111",
    DesConstants.testBusinessDetails,
    DesConstants.testViewBusinessContactDetails,
    DesConstants.testBusinessReferencesAll,
    Some(DesConstants.testbusinessReferencesAllButSp),
    Some(DesConstants.testBusinessReferencesCbUbLlp),
    DesConstants.testBusinessActivities,
    DesConstants.testTradingPremisesAPI5,
    DesConstants.testBankDetails,
    Some(DesConstants.testMsb),
    Some(DesConstants.testHvd),
    Some(DesConstants.testAsp),
    Some(DesConstants.testAspOrTcsp),
    Some(DesConstants.testTcspAll),
    Some(DesConstants.testTcspTrustCompFormationAgt),
    Some(DesConstants.testEabAll),
    Some(DesConstants.testEabResdEstAgncy),
    Some(DesConstants.testResponsiblePersons),
    DesConstants.extraFields
  )
}
