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

package config

import javax.inject.Inject

import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.config.inject.{ServicesConfig => iServicesConfig}

object AmlsConfig extends ServicesConfig {

  private def loadConfig(key: String) =
    getConfString(key, throw new Exception(s"Config missing key: $key"))

  lazy val desUrl = baseUrl("des")
  lazy val desToken = loadConfig("des.auth-token")
  lazy val desEnv = loadConfig("des.env")

  def release7 = getConfBool("feature-toggle.release7", defBool = false)
  def phase2Changes = getConfBool("feature-toggle.phase-2-changes", defBool = false)

  // exponential back off configuration
  def maxAttempts = getConfInt("exponential-backoff.max-attempts", defInt = 10)
  def initialWaitMs = getConfInt("exponential-backoff.initial-wait-ms", defInt = 10)
  def waitFactor = getConfString("exponential-backoff.wait-factor", defString = "1.5").toFloat

  lazy val payAPIUrl = baseUrl("pay-api")

}

class AppConfig @Inject()(servicesConfig: iServicesConfig){

  lazy val enrolmentStoreUrl = s"${servicesConfig.baseUrl("tax-enrolments")}"
  def enrolmentStoreToggle = servicesConfig.getConfBool("feature-toggle.enrolment-store", defBool = false)

}