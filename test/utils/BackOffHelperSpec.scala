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

package utils

import exceptions.HttpStatusException
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BackOffHelperSpec extends PlaySpec with MockitoSugar with ScalaFutures with OneAppPerSuite {

  override lazy val app = new GuiceApplicationBuilder()
    .bindings(bind[BackOffHelper].to[BackOffHelperInstance])
    .build()
  val backOffHelper = app.injector.instanceOf( classOf[BackOffHelper])
  val TIMEOUT = 5

  "BackOffHelper" must {

    "return a successful Future" in {
      val successfulFunction = () => Future.successful("A successful future")

      whenReady(backOffHelper.expBackOffHelper(successfulFunction)) {
        result => result mustEqual "A successful future"
      }
    }

    "retry on a 503 HTTP exception " in {
      val failedFunction = () => Future.failed(HttpStatusException(SERVICE_UNAVAILABLE, Some("Bad Request")))
      whenReady(backOffHelper.expBackOffHelper(failedFunction).failed, timeout(Span(TIMEOUT, Seconds))) {
        case e: HttpStatusException => e.status mustEqual SERVICE_UNAVAILABLE
      }
    }

    "show that it pass after it fails" in {

      val NUMBER_OF_RETRIES = 5
      var counter = 0

      val failThenSuccessFunc = () => {
        if(counter < NUMBER_OF_RETRIES) {
          counter = counter + 1
          Future.failed(HttpStatusException(SERVICE_UNAVAILABLE, Some("Bad Request")))
        }
        else {
          Future.successful("A successful future")
        }
      }
      whenReady(backOffHelper.expBackOffHelper(failThenSuccessFunc), timeout(Span(TIMEOUT, Seconds))) {
        result =>  {
          result mustEqual "A successful future"
          counter must be >= NUMBER_OF_RETRIES
        }
      }
    }
  }
}

private  class BackOffHelperInstance extends BackOffHelper {
}

