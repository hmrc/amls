/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import exceptions.HttpStatusException
import org.scalatest.time.{Seconds, Span}
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApiRetryHelperSpec extends AmlsBaseSpec {

  val TIMEOUT = 5

  "ApiRetryHelper" must {

    "return a successful Future" in {
      val successfulFunction = () => Future.successful("A successful future")

      whenReady(apiRetryHelper.doWithBackoff(successfulFunction)) {
        result => result mustEqual "A successful future"
      }
    }

    "retry on a 503 HTTP exception " in {
      val failedFunction = () => Future.failed(HttpStatusException(SERVICE_UNAVAILABLE, Some("Bad Request")))
      whenReady(apiRetryHelper.doWithBackoff(failedFunction).failed, timeout(Span(TIMEOUT, Seconds))) {
        case e: HttpStatusException => e.status mustEqual SERVICE_UNAVAILABLE
      }
    }

    "back off exponentially" in {
      def getMinimumExpectedDuration(iteration: Int, expectedTime: Long, currentWait: Int): Long = {
        if(iteration >= maxRetries) {
          expectedTime + currentWait
        } else {
          val nextWait:Int = Math.ceil(currentWait * waitFactor).toInt
          getMinimumExpectedDuration(iteration + 1, expectedTime + currentWait, nextWait)
        }
      }

      val failedFunction = () => Future.failed(HttpStatusException(SERVICE_UNAVAILABLE, Some("Bad Request")))
      val startTime = LocalDateTime.now

      whenReady(apiRetryHelper.doWithBackoff(failedFunction).failed, timeout(Span(TIMEOUT, Seconds))) {
        case e: HttpStatusException => e.status mustEqual SERVICE_UNAVAILABLE
      }

      val endTime = LocalDateTime.now
      val expectedTime = getMinimumExpectedDuration(1, initialWaitMs, initialWaitMs)
      ChronoUnit.MILLIS.between(startTime, endTime) must be >= expectedTime
    }

    "show that it pass after it fails" in {

      val numberOfRetries = 5
      var counter = 0

      val failThenSuccessFunc = () => {
        if(counter < numberOfRetries) {
          counter = counter + 1
          Future.failed(HttpStatusException(SERVICE_UNAVAILABLE, Some("Bad Request")))
        }
        else {
          Future.successful("A successful future")
        }
      }

      whenReady(apiRetryHelper.doWithBackoff(failThenSuccessFunc), timeout(Span(TIMEOUT, Seconds))) {
        result =>  {
          result mustEqual "A successful future"
          counter must be >= numberOfRetries
        }
      }
    }
  }
}
