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

import audit.SubscriptionStatusEvent
import exceptions.HttpStatusException
import metrics.API9
import models.des
import models.des.ReadStatusResponse
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsSuccess, Json, Writes}
import uk.gov.hmrc.play.http.{HttpGet, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait SubscriptionStatusDESConnector extends DESConnector {

  private[connectors] def httpGet: HttpGet

  def status(amlsRegistrationNumber: String)
            (implicit ec: ExecutionContext, wr: Writes[ReadStatusResponse]): Future[ReadStatusResponse] = {

    val prefix = "[DESConnector][readstatus]"
    val bodyParser = JsonParsed[ReadStatusResponse]
    val timer = metrics.timer(API9)
    Logger.debug(s"$prefix - reg no: $amlsRegistrationNumber")

    val Url = s"$fullUrl/$amlsRegistrationNumber"

    httpGet.GET[HttpResponse](s"$Url/status") map {
      response =>
        timer.stop()
        Logger.debug(s"$prefix - Base Response: ${response.status}")
        Logger.debug(s"$prefix - Response Body: ${response.body}")
        response
    }flatMap {
      case r @ status(OK) & bodyParser(JsSuccess(body: des.ReadStatusResponse, _)) =>
        metrics.success(API9)
        audit.sendDataEvent(SubscriptionStatusEvent(amlsRegistrationNumber, body))
        Logger.debug(s"$prefix - Success response")
        Logger.debug(s"$prefix - Response body: ${Json.toJson(body)}")
        Future.successful(body)
      case r @ status(s) =>
        metrics.failed(API9)
        Logger.warn(s"$prefix - Failure response: $s")
        Future.failed(HttpStatusException(s, Option(r.body)))
    } recoverWith {
      case e: HttpStatusException =>
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(e)
      case e =>
        timer.stop()
        metrics.failed(API9)
        Logger.warn(s"$prefix - Failure: Exception", e)
        Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, Some(e.getMessage)))
    }

  }

}
