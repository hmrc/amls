/*
 * Copyright 2018 HM Revenue & Customs
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
import config.AmlsConfig
import metrics.Metrics
import models.des.registrationdetails.{Organisation, Partnership, RegistrationDetails}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, MustMatchers}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import utils.BackOffHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationDetailsDesConnectorSpec extends PlaySpec
  with MustMatchers
  with ScalaFutures
  with MockitoSugar
  with BeforeAndAfter
  with OneAppPerSuite {

  val mockHttpGet = mock[HttpGet]

  implicit val backOffHelper: BackOffHelper = new BackOffHelper(as = app.actorSystem)

  val connector = new RegistrationDetailsDesConnector {
    override private[connectors] def baseUrl = "baseUrl"
    override private[connectors] def env = "ist0"
    override private[connectors] def token = "token"
    override private[connectors] def httpPost = mock[HttpPost]
    override private[connectors] def httpGet = mockHttpGet
    override private[connectors] def metrics = mock[Metrics]
    override private[connectors] def audit = MockAudit
    override private[connectors] def auditConnector = mock[AuditConnector]
    override private[connectors] def fullUrl = s"$baseUrl/$requestUrl"
  }

  implicit val headerCarrier = HeaderCarrier()

  before {
    reset(mockHttpGet)
  }

  "The RegistrationDetailsDesConnector" must {
    "get the registration details" in {

      val safeId = "SAFEID"
      val details = RegistrationDetails(isAnIndividual = false, Organisation("Test organisation", Some(false), Some(Partnership)))

      when {
        mockHttpGet.GET[HttpResponse](eqTo(s"${AmlsConfig.desUrl}/registration/details?safeid=$safeId"))(any(), any(), any())
      } thenReturn Future.successful(HttpResponse(OK, Some(Json.toJson(details))))

      whenReady (connector.getRegistrationDetails(safeId)) { _ mustBe details }

    }
  }

}
