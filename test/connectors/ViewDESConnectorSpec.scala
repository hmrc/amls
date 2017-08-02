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

package connectors

import audit.MockAudit
import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import generators.AmlsReferenceNumberGenerator
import metrics.{API4, API5, Metrics}
import models.des
import models.des._
import models.des.aboutthebusiness.{Address => AboutTheBusinessAddress}
import models.des.aboutthebusiness._
import models.des.aboutyou.{Aboutyou, IndividualDetails}
import models.des.asp.{Asp => AspModel}
import models.des.bankdetails._
import models.des.businessactivities._
import models.des.businessdetails.{BusinessDetails, BusinessType, CorpAndBodyLlps, UnincorpBody}
import models.des.estateagentbusiness.{EabAll, EabResdEstAgncy}
import models.des.hvd.{Hvd => HvdModel}
import models.des.hvd.{HvdFromUnseenCustDetails, ReceiptMethods}
import models.des.msb._
import models.des.payment.Payment
import models.des.responsiblepeople.{Address => ResponsiblePeopleAddress}
import models.des.responsiblepeople._
import models.des.supervision._
import models.des.tcsp.{TcspAll, TcspTrustCompFormationAgt}
import models.des.tradingpremises.{Address => TradingPremisesAddress}
import models.des.tradingpremises._
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ViewDESConnectorSpec
  extends PlaySpec
    with MockitoSugar
    with ScalaFutures
    with IntegrationPatience
    with OneServerPerSuite
    with AmlsReferenceNumberGenerator {

  trait Fixture {

    object testDESConnector extends ViewDESConnector {
      override private[connectors] val baseUrl: String = "baseUrl"
      override private[connectors] val token: String = "token"
      override private[connectors] val env: String = "ist0"
      override private[connectors] val httpGet: HttpGet = mock[HttpGet]
      override private[connectors] val httpPost: HttpPost = mock[HttpPost]
      override private[connectors] val metrics: Metrics = mock[Metrics]
      override private[connectors] val audit = MockAudit
      override private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl/"
      override private[connectors] def auditConnector = mock[AuditConnector]

    }

    implicit val hc = HeaderCarrier()

    val mockTimer = mock[Timer.Context]

    val url = s"${testDESConnector.fullUrl}/$amlsRegistrationNumber"

    when {
      testDESConnector.metrics.timer(eqTo(API5))
    } thenReturn mockTimer
  }


  "DESConnector" must {

    "return a succesful future" in new Fixture {

      val response = HttpResponse(
        responseStatus = OK,
        responseHeaders = Map.empty,
        responseJson = Some(Json.toJson(ViewSuccessModel))
      )

      when(testDESConnector.httpGet.GET[HttpResponse](eqTo(url))(any(), any()))
        .thenReturn(Future.successful(response))

      whenReady(testDESConnector.view(amlsRegistrationNumber)) {
        _ mustEqual ViewSuccessModel
      }
    }

    "return a failed future" in new Fixture {

      val response = HttpResponse(
        responseStatus = BAD_REQUEST,
        responseHeaders = Map.empty
      )
      when {
        testDESConnector.httpGet.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.view(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body mustEqual None
      }
    }

    "return a failed future (json validation)" in new Fixture {

      val response = HttpResponse(
        responseStatus = OK,
        responseHeaders = Map.empty,
        responseString = Some("message")
      )

      when {
        testDESConnector.httpGet.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.successful(response)

      whenReady(testDESConnector.view(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "return a failed future (exception)" in new Fixture {

      when {
        testDESConnector.httpGet.GET[HttpResponse](eqTo(url))(any(), any())
      } thenReturn Future.failed(new Exception("message"))

      whenReady(testDESConnector.view(amlsRegistrationNumber).failed) {
        case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustEqual Some("message")
      }
    }
  }

  val ViewSuccessModel = SubscriptionView(
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
