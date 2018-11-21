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

import audit.SubscriptionViewEvent
import exceptions.HttpStatusException
import metrics.API5
import models.des.SubscriptionView
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpReads, HttpResponse}
import utils.ApiRetryHelper
import scala.concurrent.{ExecutionContext, Future}

trait ViewDESConnector extends DESConnector {

  private[connectors] def httpGet: HttpGet

    def view(amlsRegistrationNumber: String)(
      implicit ec: ExecutionContext,
      hc: HeaderCarrier,
      apiRetryHelper: ApiRetryHelper
    ): Future[SubscriptionView] = {
      apiRetryHelper.doWithBackoff(() => viewFunction(amlsRegistrationNumber))
    }

  private def viewFunction(amlsRegistrationNumber: String)
          (
            implicit ec: ExecutionContext,
            hc: HeaderCarrier
          ): Future[SubscriptionView] = {

    val bodyParser = JsonParsed[SubscriptionView]
    val timer = metrics.timer(API5)
    val prefix = "[DESConnector][view]"

    val Url = s"$fullUrl/$amlsRegistrationNumber"

    httpGet.GET[HttpResponse](Url)(implicitly[HttpReads[HttpResponse]], desHeaderCarrier,ec) map {
      response =>
        timer.stop()
        Logger.debug(s"$prefix - Base Response: ${response.status}")
        Logger.debug(s"$prefix - Response Body: ${response.body}")
        response
    } flatMap {
      case status(OK) & bodyParser(JsSuccess(body: SubscriptionView, _)) =>
        metrics.success(API5)
        audit.sendDataEvent(SubscriptionViewEvent(amlsRegistrationNumber, body))
        Logger.debug(s"$prefix - Success response")
        Logger.debug(s"$prefix - Response body: $body")
        Future.successful(body)
      case r@status(OK) & bodyParser(JsError(errs)) =>
        metrics.failed(API5)
        Logger.warn(s"$prefix - Deserialisation Errors: $errs")
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some("Failed to parse the json response from DES (API5)")))
      case r@status(s) =>
        metrics.failed(API5)
        Logger.warn(s"$prefix - Failure response: $s")
        Future.failed(HttpStatusException(s, Option(r.body)))
    } recoverWith {
      case e: HttpStatusException =>
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(API5)
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }
  }

}
