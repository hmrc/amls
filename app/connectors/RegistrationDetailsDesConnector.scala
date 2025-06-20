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

import config.ApplicationConfig
import models.des.registrationdetails.RegistrationDetails
import play.api.http.Status.OK
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import utils.ApiRetryHelper

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RegistrationDetailsDesConnector @Inject() (
  private[connectors] val appConfig: ApplicationConfig,
  private[connectors] val httpClientV2: HttpClientV2
) extends DESConnector(appConfig) {

  def getRegistrationDetails(
    safeId: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext, apiRetryHelper: ApiRetryHelper): Future[RegistrationDetails] =
    apiRetryHelper.doWithBackoff(() => getRegistrationDetailsFunction(safeId))

  private def getRegistrationDetailsFunction(
    safeId: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[RegistrationDetails] = {
    val url = s"${appConfig.desUrl}/registration/details?safeid=$safeId"
    httpClientV2.get(url"$url").setHeader(desHeaders: _*).execute[HttpResponse].map {
      case response if response.status == OK => response.json.as[RegistrationDetails]
      case response                          =>
        throw new RuntimeException(s"Call to get registration details failed with status ${response.status}")
    }
  }
}
