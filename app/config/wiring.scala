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

import akka.actor.ActorSystem
import com.typesafe.config.Config
import javax.inject.Inject
import play.api.Mode.Mode
import play.api.{Application, Configuration}
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.hooks.HttpHooks
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.auth.controllers.AuthParamsControllerConfig
import uk.gov.hmrc.play.auth.microservice.connectors.AuthConnector
import uk.gov.hmrc.play.auth.microservice.filters.AuthorisationFilter
import uk.gov.hmrc.play.config.{AppName, ControllerConfig, RunMode, ServicesConfig}
import uk.gov.hmrc.play.http.ws._
import uk.gov.hmrc.play.microservice.config.LoadAuditingConfig
import uk.gov.hmrc.play.microservice.filters.{AuditFilter, LoggingFilter, MicroserviceFilterSupport}

trait Hooks extends HttpHooks with HttpAuditing {
  override val hooks = Seq.empty
}

class WSHttp @Inject()(app: Application, auditConn: MicroserviceAuditConnector)
  extends HttpGet with WSGet with HttpPut with WSPut with HttpPost with WSPost with HttpDelete  with WSDelete
      with Hooks with HttpPatch with WSPatch with AppName with RunMode {

  override protected def configuration: Option[Config] = Some(app.configuration.underlying)

  override protected def appNameConfiguration: Configuration = app.configuration

  override protected def mode: Mode = app.mode

  override protected def runModeConfiguration: Configuration = app.configuration

  override protected def actorSystem: ActorSystem = app.actorSystem

  override lazy val auditConnector: AuditConnector = auditConn
}

class MicroserviceAuditConnector @Inject()(app: Application) extends AuditConnector with RunMode {

  override lazy val auditingConfig = LoadAuditingConfig("auditing")

  override protected def mode: Mode = app.mode

  override protected def runModeConfiguration: Configuration = app.configuration
}

class MicroserviceAuthConnector @Inject()(app: Application, ac: MicroserviceAuditConnector)
  extends  WSHttp(app, ac) with AuthConnector with ServicesConfig {

  override protected def configuration: Option[Config] = Some(app.configuration.underlying)

  override protected def appNameConfiguration: Configuration = app.configuration

  override protected def mode: Mode = app.mode

  override protected def runModeConfiguration: Configuration = app.configuration

  override val authBaseUrl = baseUrl("auth")

  override protected def actorSystem: ActorSystem = app.actorSystem
}

class ControllerConfiguration @Inject()(app: Application) extends ControllerConfig {
  override lazy val controllerConfigs: Config = app.configuration.underlying.getConfig("controllers")
}

class AuthParamsControllerConfiguration @Inject()(config: ControllerConfiguration) extends AuthParamsControllerConfig {
  lazy val controllerConfigs = config.controllerConfigs
}

class  MicroserviceAuditFilter @Inject()(
  app: Application,
  ac: MicroserviceAuditConnector,
  cc: ControllerConfiguration
) extends AuditFilter
  with AppName
  with MicroserviceFilterSupport {
  override val auditConnector = ac
  override def controllerNeedsAuditing(controllerName: String) = cc.paramsForController(controllerName).needsAuditing
  override protected def appNameConfiguration: Configuration = app.configuration
}

class MicroserviceLoggingFilter @Inject()(config: ControllerConfiguration)  extends LoggingFilter with MicroserviceFilterSupport{
  override def controllerNeedsLogging(controllerName: String) = config.paramsForController(controllerName).needsLogging
}

class MicroserviceAuthFilter @Inject()(
  apcc: AuthParamsControllerConfiguration,
  mac: MicroserviceAuthConnector,
  cc: ControllerConfiguration
) extends AuthorisationFilter
  with MicroserviceFilterSupport {
  override val authParamsConfig = apcc
  override val authConnector = mac
  override def controllerNeedsAuth(controllerName: String): Boolean = cc.paramsForController(controllerName).needsAuth
}
