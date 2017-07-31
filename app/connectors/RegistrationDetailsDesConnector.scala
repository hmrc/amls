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

import models.des.registrationdetails.RegistrationDetails
import play.api.Logger
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait RegistrationDetailsDesConnector extends DESConnector  {

  override val requestUrl = "anti-money-laundering/registration"

  private val debug: String => String => Unit = method => msg => Logger.debug(s"[RegistrationDetailsConnector.$method] $msg")

  def getRegistrationDetails(safeId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[RegistrationDetails] = {
    val d = debug("getRegistrationDetails")
    val url = s"$fullUrl/details?safeid=$safeId"

    d(s"Requesting registration details for $safeId")
    httpGet.GET[RegistrationDetails](url) map { result =>
      d(s"Response: ${result.toString}")
      result
    }
  }

}
