/*
 * Copyright 2026 HM Revenue & Customs
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

import ch.qos.logback.classic.spi.ILoggingEvent
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import play.api.Logger
import play.api.mvc.Results
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core.{AuthConnector, MissingBearerToken}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import scala.concurrent.{ExecutionContext, Future}

class DefaultAuthActionSpec extends PlaySpec with ScalaFutures with IntegrationPatience with LogCapturing {

  private class FakeAuthConnector(result: Future[Unit]) extends AuthConnector {
    override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext
    ): Future[A] = result.map(_.asInstanceOf[A])(ec)
  }

  private val authLogger: Logger = Logger(classOf[DefaultAuthAction])

  private def logged(logs: List[ILoggingEvent], message: String): Boolean =
    logs.exists(_.getFormattedMessage.contains(message))

  private def runAction(authResult: Future[Unit]) =
    new DefaultAuthAction(new FakeAuthConnector(authResult), stubControllerComponents())(ExecutionContext.global)(
      Results.Ok
    )(FakeRequest())

  "DefaultAuthAction" must {

    "allow the request through when authorisation succeeds" in {
      status(runAction(Future.successful(()))) mustBe OK
    }

    "return 401 and log the reason when authorisation fails" in {
      withCaptureOfLoggingFrom(authLogger) { logs =>
        status(runAction(Future.failed(MissingBearerToken()))) mustBe UNAUTHORIZED
        logged(logs, "[DefaultAuthAction][filter] - Authorisation failed - reason:") mustBe true
        logged(logs, "type: MissingBearerToken") mustBe true
      }
    }

    "log and rethrow when the auth service call fails unexpectedly" in {
      withCaptureOfLoggingFrom(authLogger) { logs =>
        runAction(Future.failed(new RuntimeException("auth down"))).failed.futureValue mustBe a[RuntimeException]
        logged(logs, "[DefaultAuthAction][filter] - Unexpected exception calling auth service") mustBe true
      }
    }
  }
}
