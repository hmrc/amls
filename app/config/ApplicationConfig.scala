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

import com.google.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}


@Singleton
class ApplicationConfig @Inject()(config: Configuration, environment: Environment) {

  private def baseUrl(serviceName: String) = {
    val protocol = config.getOptional[String](s"microservice.services.protocol").getOrElse("https")
    val host = config.get[String](s"microservice.services.$serviceName.host")
    val port = config.get[String](s"microservice.services.$serviceName.port")
    s"$protocol://$host:$port"
  }

  lazy val desUrl = baseUrl("des")
  lazy val desToken = config.get[String]("microservice.services.des.auth-token")
  lazy val desEnv = config.get[String]("microservice.services.des.env")
  lazy val ggUrl = baseUrl("government-gateway-admin")

  // exponential back off configuration
  //def maxAttempts = getConfInt("exponential-backoff.max-attempts", defInt = 10)
  def maxAttempts = config.get[Int]("microservice.services.exponential-backoff.max-attempts")
  //def initialWaitMs = getConfInt("exponential-backoff.initial-wait-ms", defInt = 10)
  def initialWaitMs = config.get[Int]("microservice.services.exponential-backoff.initial-wait-ms")
  //def waitFactor = getConfString("exponential-backoff.wait-factor", defString = "1.5").toFloat
  def waitFactor = config.get[String]("microservice.services.exponential-backoff.wait-factor").toFloat

  lazy val payAPIUrl = baseUrl("pay-api")

  lazy val enrolmentStoreUrl = s"${baseUrl("tax-enrolments")}"
  def enrolmentStoreToggle = config.get[Boolean]("feature-toggle.enrolment-store")
}