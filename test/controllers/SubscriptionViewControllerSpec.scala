/*
 * Copyright 2024 HM Revenue & Customs
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
import models.des.DesConstants
import models.des.businessactivities.{BusinessActivityDetails, ExpectedAMLSTurnover}
import models.des.msb.{CountriesList, MsbAllDetails}
import models.des.tradingpremises.{AgentBusinessPremises, AgentDetails}
import models.{SubscriptionViewModel, des}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.scalatest.concurrent.IntegrationPatience
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.{AmlsBaseSpec, AuthAction, SuccessfulAuthAction}

import scala.concurrent.Future

class SubscriptionViewControllerSpec extends AmlsBaseSpec with IntegrationPatience {

  val authAction: AuthAction = SuccessfulAuthAction

  val Controller: SubscriptionViewController = new SubscriptionViewController(mock[ViewDESConnector], authAction, mockCC)

  val agentDetails = DesConstants.testTradingPremisesAPI5.agentBusinessPremises.fold[Option[Seq[AgentDetails]]](None) {
    x =>
      x.agentDetails match {
        case Some(data) => Some(data.map(y => y.copy(agentPremises = y.agentPremises.copy(startDate = None),
          startDate = y.agentPremises.startDate)))
        case _ => None
      }
  }

  val subscriptionViewModelPhase2 = DesConstants.SubscriptionViewModelForRpPhase2.copy(
    businessActivities = DesConstants.testBusinessActivities.copy(
      all = Some(DesConstants.testBusinessActivitiesAll.copy(
        businessActivityDetails = BusinessActivityDetails(true, Some(ExpectedAMLSTurnover(Some("£50k-£100k"))))
      ))
    ),
    tradingPremises = DesConstants.testTradingPremisesAPI5.copy(
      agentBusinessPremises = Some(AgentBusinessPremises(true, agentDetails))
    ),
    msb = Some(DesConstants.testMsb.copy(
      msbAllDetails = Some(MsbAllDetails(
        Some("£50k-£100k"),
        true,
        Some(CountriesList(List("AD", "GB"))),
        true)
      )))
  )

  val request = FakeRequest()
    .withHeaders(CONTENT_TYPE -> "application/json")

  "SubscriptionViewController" must {

    val amlsRegistrationNumber = "XAML00000567890"


    "return a `BadRequest` response when the amls registration number is invalid" in {

      val result = Controller.view("test", "test", "test")(request)
      val failure = Json.obj("errors" -> Seq("Invalid AMLS Registration Number"))

      status(result) must be(BAD_REQUEST)
      contentAsJson(result) must be(failure)
    }

    "return a valid response when the amls registration number is valid" in {

      when {
        Controller.connector.view(ArgumentMatchers.eq(amlsRegistrationNumber))(any(), any(), any())
      } thenReturn Future.successful(subscriptionViewModelPhase2)

      val result = Controller.view("test", "test", amlsRegistrationNumber)(request)

      status(result) must be(OK)
      contentAsJson(result) must be(Json.toJson(SubscriptionViewModel.convertedViewModelPhase2))

    }

    "return an invalid response when the service fails" in {

      when {
        Controller.connector.view(ArgumentMatchers.eq(amlsRegistrationNumber))(any(), any(), any())
      } thenReturn Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, Some("message")))

      whenReady(Controller.view("test", "test", amlsRegistrationNumber)(request).failed) {
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
    Some(DesConstants.testAmp),
    None,
    DesConstants.extraFields
  )
}

