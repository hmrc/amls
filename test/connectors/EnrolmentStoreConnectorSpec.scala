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

package connectors

import com.codahale.metrics.Timer
import config.WSHttp
import generators.{AmlsReferenceNumberGenerator, BaseGenerator}
import metrics.{GGAdmin, Metrics}
import models.enrolment.{AmlsEnrolmentKey, KnownFact, KnownFacts}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito.{verify, when}
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.auth.microservice.connectors.AuthConnector

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentStoreConnectorSpec extends PlaySpec
  with MustMatchers
  with ScalaFutures
  with MockitoSugar
  with AmlsReferenceNumberGenerator
  with BaseGenerator
  with OneAppPerSuite {

  trait Fixture {

    implicit val hc = HeaderCarrier()
    implicit val ec = mock[ExecutionContext]

    val metrics = mock[Metrics]
    val http = mock[WSHttp]
    val authConnector = mock[AuthConnector]

    val mockTimer = mock[Timer.Context]

    val connector = new EnrolmentStoreConnector(http, metrics)
    val baseUrl = "http://localhost:7775"
    val enrolKey = AmlsEnrolmentKey(amlsRegistrationNumber)

    when {
      connector.metrics.timer(eqTo(GGAdmin))
    } thenReturn mockTimer

  }

  "enrol" when {
    "called" must {
      "call the ES6 enrolment store endpoint for known facts" in new Fixture {

        val knownFacts = KnownFacts(Set(
          KnownFact("Postcode", postcodeGen.sample.get),
          KnownFact("SafeId", "safeId"),
          KnownFact("MLRRefNumber", amlsRegistrationNumber)
        ))

        val endpointUrl = s"$baseUrl/enrolments/${enrolKey.key}"

        val response = HttpResponse(OK, responseString = Some("message"))

        when {
          http.POST[KnownFacts, HttpResponse](any(), any(), any())(any(), any(), any(), any())
        } thenReturn Future.successful(response)

        whenReady(connector.enrol(enrolKey, knownFacts)) { result =>
          result mustEqual response
          verify(http).POST[KnownFacts, HttpResponse](eqTo(endpointUrl), eqTo(knownFacts), any())(any(), any(), any(), any())
        }
      }
    }
  }

}