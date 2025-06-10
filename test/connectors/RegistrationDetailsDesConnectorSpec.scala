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
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfter
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.RequestBuilder
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}
import utils.{AmlsBaseSpec, ApiRetryHelper}

import scala.concurrent.Future

class RegistrationDetailsDesConnectorSpec extends AmlsBaseSpec with BeforeAndAfter {
  when(mockAppConfig.desUrl).thenReturn("http://localhost:1234")
  when(mockAppConfig.desToken).thenReturn("token")
  when(mockAppConfig.desEnv).thenReturn("ist0")

  val connector           = new RegistrationDetailsDesConnector(mockAppConfig, mockHttpClient)
  val mockApiRetryHelper  = mock[ApiRetryHelper]
  val mockRequestBuilder  = mock[RequestBuilder]
  val safeId              = "SAFEID"
  val url                 = s"http://localhost:1234/registration/details?safeid=$safeId"
  val registrationDetails =
    RegistrationDetails(isAnIndividual = false, Organisation("Test organisation", Some(false), Some(Partnership)))

  "RegistrationDetailsDesConnector" must {

    "return registration details successfully" in {
      val response = HttpResponse(OK, json = Json.toJson(registrationDetails), headers = Map.empty)

      when {
        connector.httpClientV2.get(url"$url")
      } thenReturn mockRequestBuilder

      when(
        mockRequestBuilder.setHeader(
          ("Authorization", "Bearer token"),
          ("Environment", "ist0"),
          ("Accept", "application/json"),
          ("Content-Type", "application/json;charset=utf-8")
        )
      ).thenReturn(mockRequestBuilder)

      when(mockRequestBuilder.execute[HttpResponse](any(), any()))
        .thenReturn(Future.successful(response))

      whenReady(connector.getRegistrationDetails(safeId)) { result =>
        result mustEqual registrationDetails
      }
    }

  }
}
