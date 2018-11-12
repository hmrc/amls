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

import akka.actor.ActorSystem
import exceptions.HttpStatusException
import models.des.DesConstants
import org.scalatest.concurrent.{PatienceConfiguration, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatest.time.{Minute, Second, Seconds, Span}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.FakeApplication
import play.api.test.Helpers._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class BackOffHelperSpec extends PlaySpec with MockitoSugar with ScalaFutures with OneAppPerSuite{

  override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> true))


  val backOffHelper= new BackOffHelper {

  }

  "BackOffHelper" must {

    "return a successful Future" in {
      val successfulFunction = () => Future.successful("A successful future")

      whenReady(backOffHelper.expBackOffHelper(successfulFunction)) {
        result => result mustEqual("A successful future")
      }
    }

    "retry on a 503 HTTP exception " in {
      val failedFunction = () => Future.failed(HttpStatusException(SERVICE_UNAVAILABLE, Some("Bad Request")))
      whenReady(backOffHelper.expBackOffHelper(failedFunction).failed, timeout(Span(5, Seconds))) {
        case e: HttpStatusException => e.status mustEqual SERVICE_UNAVAILABLE
      }
    }
  }
}

