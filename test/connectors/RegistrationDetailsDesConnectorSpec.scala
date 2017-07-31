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
import metrics.Metrics
import models.des.registrationdetails.{Organisation, Partnership, RegistrationDetails}
import org.mockito.Mockito._
import org.mockito.Matchers.{eq => eqTo, _}
import org.scalatest.{BeforeAndAfter, MustMatchers}
import org.scalatest.concurrent._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class RegistrationDetailsDesConnectorSpec extends PlaySpec with MustMatchers with ScalaFutures with MockitoSugar with BeforeAndAfter {

  val mockHttpGet = mock[HttpGet]

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
      val details = RegistrationDetails(isAnIndividual = false, Organisation("Test organisation", false, Partnership))

      when {
        mockHttpGet.GET[RegistrationDetails](eqTo(s"${connector.fullUrl}/details?safeid=$safeId"))(any(), any())
      } thenReturn Future.successful(details)

      whenReady (connector.getRegistrationDetails(safeId)) { r =>
        r mustBe details
      }

    }
  }

}
