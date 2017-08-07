/*
 * Copyright 2017 HM Revenue & Customs
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

package services

import connectors.PayAPIConnector
import exceptions.{HttpStatusException, PaymentException}
import generators.PaymentGenerator
import models.{PaymentStatuses, RefreshStatusResult}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers._
import repositories.PaymentRepository
import uk.gov.hmrc.play.http.HeaderCarrier
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PaymentServiceSpec extends PlaySpec with MockitoSugar with PaymentGenerator with ScalaFutures with IntegrationPatience {

  implicit val hc: HeaderCarrier = new HeaderCarrier()

  val testPayAPIConnector = mock[PayAPIConnector]
  val testPaymentRepo = mock[PaymentRepository]

  def testPayment = paymentGen.sample.get

  val testPaymentService = new PaymentService(testPayAPIConnector, testPaymentRepo)

  "PaymentService" when {
    "savePayment is called" must {
      "respond with payment if call to connector is successful" in {

        val payment = testPayment.copy(amlsRefNo = Some(amlsRegistrationNumber))

        when {
          testPayAPIConnector.getPayment(payment._id)(hc)
        } thenReturn {
          Future.successful(payment)
        }

        when {
          testPaymentService.paymentsRepository.insert(payment)
        } thenReturn {
          Future.successful(payment)
        }

        whenReady(testPaymentService.savePayment(payment._id, amlsRegistrationNumber)) { res =>
          res mustBe Some(payment)

          verify(
            testPaymentService.paymentsRepository
          ).insert(payment)
        }

      }

      "respond with None if call to connector returns HttpStatusException NotFound" in {

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.failed(new HttpStatusException(NOT_FOUND, None))
        }

        val result = testPaymentService.savePayment(testPayment._id, amlsRegistrationNumber)
        await(result) mustBe None

      }

      "respond with PaymentException if connector returns HttpStatusException with anything else" in {

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, None))
        }

        val result = testPaymentService.savePayment(testPayment._id, amlsRegistrationNumber).failed

        await(result) mustBe PaymentException(Some(INTERNAL_SERVER_ERROR), "Could not retrieve payment")

      }

      "replay exception if anything other than HttpStatusException" in {

        val e = new Exception("")

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.failed(e)
        }

        val result = testPaymentService.savePayment(testPayment._id, amlsRegistrationNumber).failed

        await(result) mustBe e

      }

    }

    "getPaymentByReference is called" must {
      val paymentRef = paymentRefGen.sample.get
      val payment = testPayment.copy(reference = paymentRef)

      when {
        testPaymentRepo.find(any())(any())
      } thenReturn Future.successful(List(payment))

      whenReady(testPaymentService.getPaymentByReference(paymentRef)) {
        case Some(result) =>
          verify(testPaymentRepo).find("reference" -> paymentRef)
          result mustBe payment
        case _ => fail("No payment was returned")
      }
    }

    "refreshStatus is called" must {
      "refresh the status" in {
        val paymentRef = paymentRefGen.sample.get
        val paymentId = paymentIdGen.sample.get
        val amlsPayment = testPayment.copy(reference = paymentRef, _id = paymentId, status = PaymentStatuses.Created)
        val payApiPayment = amlsPayment.copy(status = PaymentStatuses.Successful)
        val updatedPayment = amlsPayment.copy(status = payApiPayment.status)

        when {
          testPaymentRepo.find(any())(any())
        } thenReturn Future.successful(List(amlsPayment))

        when {
          testPaymentRepo.insert(any())
        } thenReturn Future.successful(updatedPayment)

        when {
          testPayAPIConnector.getPayment(eqTo(paymentId))(any())
        } thenReturn Future.successful(payApiPayment)

        testPaymentService.refreshStatus(paymentRef) map { result =>
          result mustBe RefreshStatusResult(paymentRef, paymentId, PaymentStatuses.Successful)
          verify(testPaymentRepo).insert(updatedPayment)
        }
      }

      "return None" when {
        "the payment is not found" in {
          when {
            testPaymentRepo.find(any())(any())
          } thenReturn Future.successful(List())

          testPaymentService.refreshStatus(paymentRefGen.sample.get) map { result =>
            result mustBe None
          }
        }
      }
    }
  }
}
