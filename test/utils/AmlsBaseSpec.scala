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

package utils

import config.ApplicationConfig
import metrics.Metrics
import org.mockito.MockitoSugar
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.mvc.{ControllerComponents, PlayBodyParsers}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.ExecutionContext

trait AmlsBaseSpec
    extends PlaySpec
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneAppPerSuite
    with MockitoSugar {

  val mockRunModeConf: Configuration     = mock[Configuration]
  val mockEnvironment: Environment       = mock[Environment]
  val mockAppConfig: ApplicationConfig   = mock[ApplicationConfig]
  val mockAuditConnector: AuditConnector = mock[AuditConnector]
  val mockHttpClient: HttpClientV2       = mock[HttpClientV2]
  val mockCC: ControllerComponents       = mock[ControllerComponents]
  val mockBodyParsers: PlayBodyParsers   = mock[PlayBodyParsers]
  val mockMetrics: Metrics               = mock[Metrics]

  val maxRetries        = 10
  val initialWaitMs     = 10
  val waitFactor: Float = 1.5f

  implicit val apiRetryHelper: ApiRetryHelper = new ApiRetryHelper(as = app.actorSystem, mockAppConfig)
  implicit val hc: HeaderCarrier              = HeaderCarrier()
  implicit val ec: ExecutionContext           = app.injector.instanceOf[ExecutionContext]

  when {
    mockAppConfig.maxAttempts
  } thenReturn maxRetries

  when {
    mockAppConfig.initialWaitMs
  } thenReturn initialWaitMs

  when {
    mockAppConfig.waitFactor
  } thenReturn waitFactor
}
