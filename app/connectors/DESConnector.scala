/*
 * Copyright 2021 HM Revenue & Customs
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
import javax.inject.{Inject, Singleton}
import play.mvc.Http.HeaderNames
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import utils._

@Singleton
class DESConnector @Inject()(applicationConfig: ApplicationConfig,
                             val auditConnector: AuditConnector) extends HttpResponseHelper {

  private[connectors] val baseUrl: String = applicationConfig.desUrl
  private[connectors] val token: String = s"Bearer ${applicationConfig.desToken}"
  private[connectors] val env: String = applicationConfig.desEnv
  private[connectors] val requestUrl = "anti-money-laundering/subscription"
  private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl"
  private[connectors] val audit: Audit = new Audit(AuditHelper.appName, auditConnector)

  protected def desHeaders = Seq(
    "Authorization" -> token,
    "Environment" -> env,
    HeaderNames.ACCEPT -> "application/json",
    HeaderNames.CONTENT_TYPE -> "application/json;charset=utf-8"
  )

}