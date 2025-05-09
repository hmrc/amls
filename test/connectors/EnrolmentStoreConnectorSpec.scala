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

package connectors

import com.typesafe.config.ConfigFactory
import config.ApplicationConfig
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import models.enrolment.{AmlsEnrolmentKey, KnownFact, KnownFacts}
import play.api.Configuration
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import connectors.EnrolmentStoreConnector
import exceptions.HttpStatusException
import generators.{AmlsReferenceNumberGenerator, BaseGenerator}
import metrics.{EnrolmentStoreKnownFacts, Metrics}
import models.enrolment.{AmlsEnrolmentKey, KnownFacts}
import org.mockito.MockitoSugar.mock
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EnrolmentStoreConnectorSpec
  extends AnyWordSpec
    with AmlsReferenceNumberGenerator with BaseGenerator
    with Matchers
    with MockFactory
    with ScalaFutures {

  private val (protocol, host, port) = ("http", "host", "123")

  private val config = Configuration(
    ConfigFactory.parseString(s"""
                                 | microservice.services.enrolmentStore {
                                 |    protocol = "$protocol"
                                 |    host     = "$host"
                                 |    port     = $port
                                 |  }
                                 |""".stripMargin)
  )

  private val mockHttpClientV2 = mock[HttpClientV2]
  private val mockMetrics = mock[Metrics]
  private val mockAuditConnector = mock[AuditConnector]
  private val mockApplicationConfig = mock[ApplicationConfig]

  private val connector = new EnrolmentStoreConnector(
    mockHttpClientV2,
    mockMetrics,
    mockAuditConnector,
    mockApplicationConfig
  )

  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "EnrolmentStoreConnector" when {
    "addKnownFacts is called successfully" should {
      "return a successful response" in {
        val enrolmentKey = AmlsEnrolmentKey("test-key")
        val knownFacts = KnownFacts(
          Set(
            KnownFact("Postcode", postcodeGen.sample.get),
            KnownFact("SafeId", "safeId"),
            KnownFact("MLRRefNumber", amlsRegistrationNumber)
          )
        )
        val url = s"$protocol://$host:$port/tax-enrolments/enrolments/${enrolmentKey.key}"
        val response = HttpResponse(NO_CONTENT, "")

        (mockHttpClientV2.put(url"$url"))

        (mockMetrics.timer(_: EnrolmentStoreKnownFacts.type))


        connector.addKnownFacts(enrolmentKey, knownFacts).futureValue shouldBe response
      }
    }

    "addKnownFacts fails with an exception" should {
      "return a failed future" in {
        val enrolmentKey = AmlsEnrolmentKey("test-key")
        val knownFacts = KnownFacts(
          Set(
            KnownFact("Postcode", postcodeGen.sample.get),
            KnownFact("SafeId", "safeId"),
            KnownFact("MLRRefNumber", amlsRegistrationNumber)
          )
        )
        val url = s"$protocol://$host:$port/tax-enrolments/enrolments/${enrolmentKey.key}"

        (mockHttpClientV2.put(url"$url"))

        (mockMetrics.failed(_: EnrolmentStoreKnownFacts.type))


        connector.addKnownFacts(enrolmentKey, knownFacts).failed.futureValue shouldBe a[RuntimeException]
      }
    }

    "addKnownFacts returns an error response" should {
      "handle the error and return a failed future" in {
        val enrolmentKey = AmlsEnrolmentKey("test-key")
        val knownFacts = KnownFacts(
          Set(
            KnownFact("Postcode", postcodeGen.sample.get),
            KnownFact("SafeId", "safeId"),
            KnownFact("MLRRefNumber", amlsRegistrationNumber)
          )
        )
        val url = s"$protocol://$host:$port/tax-enrolments/enrolments/${enrolmentKey.key}"
        val response = HttpResponse(INTERNAL_SERVER_ERROR, "Error")

        (mockHttpClientV2.put(url"$url")(_: HeaderCarrier))

        (mockMetrics.failed(_: EnrolmentStoreKnownFacts.type))

        connector.addKnownFacts(enrolmentKey, knownFacts).failed.futureValue shouldBe a[HttpStatusException]
      }
    }
  }
}

/*import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import generators.{AmlsReferenceNumberGenerator, BaseGenerator}
import metrics.EnrolmentStoreKnownFacts
import models.enrolment.{AmlsEnrolmentKey, KnownFact, KnownFacts}
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito._
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import utils.AmlsBaseSpec

import java.net.URL
import scala.concurrent.Future

class EnrolmentStoreConnectorSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator with BaseGenerator {

  trait Fixture {

    val mockTimer = mock[Timer.Context]
    val mockHttpClientV2: HttpClientV2 = mock[HttpClientV2]
    val connector = new EnrolmentStoreConnector(mockHttpClientV2, mockMetrics, mockAuditConnector, mockAppConfig)

    val baseUrl  = "http://localhost:7775"
    val enrolKey = AmlsEnrolmentKey(amlsRegistrationNumber)
    val url      = new URL(s"$baseUrl/tax-enrolments/enrolments/${enrolKey.key}")

    val knownFacts = KnownFacts(
      Set(
        KnownFact("Postcode", postcodeGen.sample.get),
        KnownFact("SafeId", "safeId"),
        KnownFact("MLRRefNumber", amlsRegistrationNumber)
      )
    )

    when {
      connector.metrics.timer(meq(EnrolmentStoreKnownFacts))
    } thenReturn mockTimer

    when {
      connector.config.enrolmentStoreUrl
    } thenReturn baseUrl

  }

  "enrol" when {
    "called" must {
      "call the ES6 enrolment store endpoint for known facts" in new Fixture {

        val enrolmentKey1 = AmlsEnrolmentKey("testKey")
        val knownFacts1 = KnownFacts(Set.empty)
        val url1 = new URL(s"http://localhost:7775/tax-enrolments/enrolments/${enrolmentKey1.key}")
        val response = HttpResponse(NO_CONTENT, "message")

        when(mockAppConfig.enrolmentStoreUrl).thenReturn("http://localhost:7775")
        when(mockHttpClientV2.put(meq(url1)).withBody(any()).execute[HttpResponse])
          .thenReturn(Future.successful(response))

        whenReady(connector.addKnownFacts(enrolmentKey1, knownFacts1)) { result =>
          result mustEqual response
        }
      }

      "return an unsuccessful response when a non-200 response is returned" in new Fixture {

        val enrolmentKey2 = AmlsEnrolmentKey("testKey")
        val knownFacts2 = KnownFacts(Set.empty)
        val url2 = new URL(s"http://localhost:7775/tax-enrolments/enrolments/${enrolmentKey2.key}")
        val response = HttpResponse(INTERNAL_SERVER_ERROR, "error")

        when(mockAppConfig.enrolmentStoreUrl).thenReturn("http://localhost:7775")
        when(mockHttpClientV2.put(meq(url2)).withBody(any()).execute[HttpResponse])
          .thenReturn(Future.successful(response))

        whenReady(connector.addKnownFacts(enrolmentKey2, knownFacts2).failed) { exception =>
          exception mustBe a[HttpStatusException]
          exception.asInstanceOf[HttpStatusException].status mustEqual INTERNAL_SERVER_ERROR
        }
      }

      "return an unsuccessful response when an exception is thrown" in new Fixture {

        whenReady(connector.addKnownFacts(enrolKey, knownFacts).failed) { case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustBe Some("message")
        }
      }
    }
  }
}

package connectors

import com.codahale.metrics.Timer
import exceptions.HttpStatusException
import generators.{AmlsReferenceNumberGenerator, BaseGenerator}
import metrics.EnrolmentStoreKnownFacts
import models.enrolment.{AmlsEnrolmentKey, KnownFact, KnownFacts}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, eq => meq}
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HttpResponse, StringContextOps}
import play.api.libs.json.Json
import uk.gov.hmrc.http.client.HttpClientV2
import utils.AmlsBaseSpec

import java.net.URL
import scala.concurrent.Future

class EnrolmentStoreConnectorSpec extends AmlsBaseSpec with AmlsReferenceNumberGenerator with BaseGenerator {

  trait Fixture {

    val mockTimer = mock[Timer.Context]
    val mockHttpClientV2: HttpClientV2 = mock[HttpClientV2]
    val connector = new EnrolmentStoreConnector(mockHttpClient, mockMetrics, mockAuditConnector, mockAppConfig)

    val baseUrl  = "http://localhost:7775"
    val enrolKey = AmlsEnrolmentKey(amlsRegistrationNumber)
    val url      = s"$baseUrl/tax-enrolments/enrolments/${enrolKey.key}"

    val knownFacts = KnownFacts(
      Set(
        KnownFact("Postcode", postcodeGen.sample.get),
        KnownFact("SafeId", "safeId"),
        KnownFact("MLRRefNumber", amlsRegistrationNumber)
      )
    )

    when {
      connector.metrics.timer(ArgumentMatchers.eq(EnrolmentStoreKnownFacts))
    } thenReturn mockTimer

    when {
      connector.config.enrolmentStoreUrl
    } thenReturn baseUrl

  }

  "enrol" when {
    "called" must {
      "call the ES6 enrolment store endpoint for known facts" in new Fixture {
        val enrolmentKey1 = AmlsEnrolmentKey("testKey")
        val knownFacts1 = KnownFacts(Set.empty)
        val url1 = new URL(s"http://localhost:7775/tax-enrolments/enrolments/${enrolmentKey1.key}")
        val response = HttpResponse(NO_CONTENT, "message")

        when(mockAppConfig.enrolmentStoreUrl).thenReturn("http://localhost:7775")
        when(mockHttpClientV2.put(meq(url1)).withBody(any()).execute[HttpResponse])
          .thenReturn(Future.successful(response))

        whenReady(connector.addKnownFacts(enrolmentKey1, knownFacts1)) { result =>
          result mustEqual response
        }
      }

      "return an unsuccessful response when a non-200 response is returned" in new Fixture {

        val enrolmentKey2 = AmlsEnrolmentKey("testKey")
        val knownFacts2 = KnownFacts(Set.empty)
        val urlstring = s"http://localhost:7775/tax-enrolments/enrolments/${enrolmentKey2.key}"
        val url2 = new URL(urlstring)
        val response = HttpResponse(INTERNAL_SERVER_ERROR, "error")

        when(mockAppConfig.enrolmentStoreUrl).thenReturn("http://localhost:7775")
        when(mockHttpClientV2.put(meq(url2)).withBody(any()).execute[HttpResponse])
          .thenReturn(Future.successful(response))

        whenReady(connector.addKnownFacts(enrolmentKey2, knownFacts2).failed) { exception =>
          exception mustBe a[HttpStatusException]
          exception.asInstanceOf[HttpStatusException].status mustEqual INTERNAL_SERVER_ERROR
        }

        //mockResponse(Future.successful(HttpResponse(status = BAD_REQUEST, body = "")))

        /*whenReady(connector.addKnownFacts(enrolKey, knownFacts).failed) { case HttpStatusException(status, body) =>
          status mustEqual BAD_REQUEST
          body.getOrElse("").isEmpty mustEqual true
        }*/
      }

      "return an unsuccessful response when an exception is thrown" in new Fixture {

        //mockResponse(Future.failed(new Exception("message")))

        whenReady(connector.addKnownFacts(enrolKey, knownFacts).failed) { case HttpStatusException(status, body) =>
          status mustEqual INTERNAL_SERVER_ERROR
          body mustBe Some("message")
        }
      }
    }
  }

}*/