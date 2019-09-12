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
import javax.inject.{Inject, Singleton}
import play.api.Mode.Mode
import play.api.{Application, Configuration, Environment}
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.hooks.HttpHooks
import uk.gov.hmrc.play.audit.http.HttpAuditing
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.config.{AppName, RunMode}
import uk.gov.hmrc.play.http.ws._

trait Hooks extends HttpHooks with HttpAuditing {
  override val hooks = Seq.empty
}

@Singleton
class WSHttp @Inject()(app: Application, auditConn: AuditConnector, override val runModeConfiguration: Configuration, environment: Environment)
  extends HttpGet with WSGet with HttpPut with WSPut with HttpPost with WSPost with HttpDelete  with WSDelete
      with Hooks with HttpPatch with WSPatch with AppName with RunMode {

  override protected def configuration: Option[Config] = Some(app.configuration.underlying)

  override protected def appNameConfiguration: Configuration = app.configuration

  override protected def mode: Mode = environment.mode

  override protected def actorSystem: ActorSystem = app.actorSystem

  override lazy val auditConnector: AuditConnector = auditConn
}
