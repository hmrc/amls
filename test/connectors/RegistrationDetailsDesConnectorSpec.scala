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

package connectors

import models.des.registrationdetails.{Organisation, Partnership, RegistrationDetails}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfter
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}
import utils.AmlsBaseSpec

import scala.concurrent.Future

class RegistrationDetailsDesConnectorSpec extends AmlsBaseSpec with BeforeAndAfter {

  trait Fixture {

    val connector = new RegistrationDetailsDesConnector(mockAppConfig, mockHttpClient) {
      override private[connectors] val baseUrl = "baseUrl"
      override private[connectors] val env     = "ist0"
      override private[connectors] val token   = "token"
      override private[connectors] val fullUrl = s"$baseUrl/$requestUrl"
    }
  }

  "The RegistrationDetailsDesConnector" must {
    "get the registration details" in new Fixture {

      val safeId  = "SAFEID"
      val details =
        RegistrationDetails(isAnIndividual = false, Organisation("Test organisation", Some(false), Some(Partnership)))

      when {
        connector.httpClientV2.get(url"${mockAppConfig.desUrl}/registration/details?safeid=$safeId").setHeader(any()).execute[HttpResponse]
      } thenReturn Future.successful(HttpResponse(status = OK, json = Json.toJson(details), headers = Map.empty))

      whenReady(connector.getRegistrationDetails(safeId)) {
        _ mustBe details
      }

    }
  }
}
