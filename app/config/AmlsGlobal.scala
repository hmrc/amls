/*
 * Copyright 2019 HM Revenue & Customs
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

package config

import exceptions.HttpStatusException
import play.api.Mode.Mode
import play.api.libs.json.Json
import play.api.mvc.{EssentialFilter, RequestHeader, Result, Results}
import play.api.{Application, Configuration, Logger, Play}
import uk.gov.hmrc.play.config.RunMode
import uk.gov.hmrc.play.microservice.bootstrap.{DefaultMicroserviceGlobal, ErrorResponse}

import scala.concurrent.Future

object AmlsGlobal extends DefaultMicroserviceGlobal with RunMode {
  override lazy  val auditConnector = MicroserviceAuditConnector

  override def microserviceMetricsConfig(implicit app: Application): Option[Configuration] = app.configuration.getConfig("microservice.metrics")

  override lazy  val loggingFilter = MicroserviceLoggingFilter

  override lazy  val microserviceAuditFilter = MicroserviceAuditFilter

  override def authFilter: Option[EssentialFilter]  = Some(MicroserviceAuthFilter)

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = {
    ex match {
      case e: HttpStatusException =>
        val message = s"! Internal server error, for (${request.method}) [${request.uri}] -> "
        Logger.error(message, ex)

        val response = ErrorResponse(e.status, e.getMessage)
        Future.successful(new Results.Status(e.status)(Json.toJson(response)))
      case _ =>
        super.onError(request, ex)
    }
  }

  override protected def mode: Mode = Play.current.mode

  override protected def runModeConfiguration: Configuration = Play.current.configuration
}
