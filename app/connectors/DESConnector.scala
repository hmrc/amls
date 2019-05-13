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

package connectors

import config.{AmlsConfig, MicroserviceAuditConnector, WSHttp}
import javax.inject.Inject
import metrics.Metrics
import play.api.Application
import play.mvc.Http.HeaderNames
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet, HttpPost, HttpPut}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.Audit
import utils._

class DESConnector @Inject()(app: Application)
  extends HttpResponseHelper {

  private[connectors] val baseUrl: String = AmlsConfig.desUrl
  private[connectors] val token: String = s"Bearer ${AmlsConfig.desToken}"
  private[connectors] val env: String = AmlsConfig.desEnv
  private[connectors] val wsHttp:WSHttp = app.injector.instanceOf(classOf[WSHttp])
  private[connectors] val httpPost: HttpPost = wsHttp
  private[connectors] val httpPut: HttpPut = wsHttp
  private[connectors] val httpGet: HttpGet = wsHttp
  private[connectors] val metrics: Metrics = app.injector.instanceOf[Metrics]
  private[connectors] val requestUrl = "anti-money-laundering/subscription"
  private[connectors] val fullUrl: String = s"$baseUrl/$requestUrl"
  private[connectors] val auditConnector: AuditConnector = new MicroserviceAuditConnector(app)
  private[connectors] val audit: Audit = new Audit(AuditHelper.appName, auditConnector)


  protected def desHeaderCarrier(implicit hc: HeaderCarrier) = {

    hc.copy(authorization = Some(Authorization(token))).withExtraHeaders(
      "Environment" -> env,
      HeaderNames.ACCEPT -> "application/json",
      HeaderNames.CONTENT_TYPE -> "application/json;charset=utf-8"
    )
  }
}