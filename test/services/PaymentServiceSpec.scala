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

package services

import cats.implicits._
import connectors.PayAPIConnector
import exceptions.{HttpStatusException, PaymentException}
import generators.PaymentGenerator
import models.payapi.{PaymentStatuses, Payment => PayApiPayment}
import models.payments.{Payment, PaymentStatusResult}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers._
import reactivemongo.api.commands.{UpdateWriteResult, Upserted, WriteError}
import repositories.PaymentRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

class PaymentServiceSpec extends PlaySpec with MockitoSugar with ScalaFutures with IntegrationPatience with PaymentGenerator with BeforeAndAfter {

  implicit val hc: HeaderCarrier = new HeaderCarrier()

  val testPayAPIConnector = mock[PayAPIConnector]
  val testPaymentRepo = mock[PaymentRepository]
  val testPayApiPayment = payApiPaymentGen.sample.get
  val safeId = amlsRefNoGen.sample.get
  val testPayment = Payment(amlsRefNoGen.sample.get, safeId, testPayApiPayment)
  val testPaymentService = new PaymentService(testPayAPIConnector, testPaymentRepo)

  val successWriteResult = mock[UpdateWriteResult]
  when(successWriteResult.ok) thenReturn true

  def errorWriteResult(error: String): UpdateWriteResult = UpdateWriteResult(
    ok = false,
    0,
    0,
    Seq.empty[Upserted],
    Seq.empty[WriteError],
    None,
    None,
    Some(error)
  )

  before {
    Seq(testPayAPIConnector, testPaymentRepo, testPayAPIConnector).foreach(reset(_))
  }

  "PaymentService" when {
    "createPayment is called" must {
      "respond with payment if call to connector is successful" in {
        when {
          testPayAPIConnector.getPayment(eqTo(testPayApiPayment.id))(any())
        } thenReturn {
          Future.successful(testPayApiPayment)
        }

        when {
          testPaymentService.paymentsRepository.insert(any())
        } thenReturn {
          Future.successful(testPayment)
        }

        whenReady(testPaymentService.createPayment(testPayApiPayment.id, amlsRegistrationNumber, safeId)) { res =>
          res mustBe Some(testPayment)

          verify(
            testPaymentService.paymentsRepository
          ).insert(any[Payment])
        }

      }

      "respond with None if call to connector returns HttpStatusException NotFound" in {

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.failed(HttpStatusException(NOT_FOUND, None))
        }

        val result = testPaymentService.createPayment(testPayApiPayment.id, amlsRegistrationNumber, safeId)
        await(result) mustBe None

      }

      "respond with PaymentException if connector returns HttpStatusException with anything else" in {

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.failed(HttpStatusException(INTERNAL_SERVER_ERROR, None))
        }

        val result = testPaymentService.createPayment(testPayApiPayment.id, amlsRegistrationNumber, safeId).failed

        await(result) mustBe PaymentException(Some(INTERNAL_SERVER_ERROR), "Could not retrieve payment")

      }

      "replay exception if anything other than HttpStatusException" in {

        val e = new Exception("")

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.failed(e)
        }

        val result = testPaymentService.createPayment(testPayApiPayment.id, amlsRegistrationNumber, safeId).failed

        await(result) mustBe e
      }
    }

    "retrieving a payment" must {
      "support getting a payment by payment reference" in {
        val paymentRef = paymentRefGen.sample.get
        val payment = testPayment.copy(reference = paymentRef)

        when {
          testPaymentRepo.findLatestByPaymentReference(eqTo(paymentRef))
        } thenReturn Future.successful(Some(payment))

        whenReady(testPaymentService.getPaymentByPaymentReference(paymentRef)) {
          case Some(result) =>
            result mustBe payment
          case _ => fail("No payment was returned")
        }
      }

      "support getting a payment by AMLS reference number" in {
        val amlsRef = amlsRefNoGen.sample.get
        val payment = testPayment.copy(amlsRefNo = amlsRef)

        when {
          testPaymentRepo.findLatestByAmlsReference(eqTo(amlsRef))
        } thenReturn Future.successful(Some(payment))

        whenReady(testPaymentService.getPaymentByAmlsReference(amlsRef)) {
          case Some(result) =>
            result mustBe payment
          case _ => fail("No payment was returned")
        }
      }
    }

    "updatePayment is called" must {
      "update the payment in-place" in {
        val updatedPayment = testPayment.copy()

        when {
          testPaymentRepo.findLatestByPaymentReference(any())
        } thenReturn Future.successful(Some(testPayment))

        when {
          testPaymentRepo.update(any())
        } thenReturn Future.successful(successWriteResult)

        whenReady(testPaymentService.updatePayment(updatedPayment)) { result =>
          result mustBe true
          verify(testPaymentRepo).update(eqTo(updatedPayment))
        }
      }

      "throws an exception when the write failed" in {
        val updatedPayment = testPayment.copy()

        when {
          testPaymentRepo.findLatestByPaymentReference(any())
        } thenReturn Future.successful(Some(testPayment))

        when {
          testPaymentRepo.update(any())
        } thenReturn Future.successful(errorWriteResult("Could not write the payment"))

        intercept[Exception] {
          whenReady(testPaymentService.updatePayment(updatedPayment)) { _ =>
            verify(testPaymentRepo).update(eqTo(updatedPayment))
          }
        }
      }
    }

    "refreshStatus is called" must {
      "refresh the status" in {
        val paymentRef = paymentRefGen.sample.get
        val paymentId = paymentIdGen.sample.get
        val amlsPayment = testPayment.copy(reference = paymentRef, _id = paymentId, status = PaymentStatuses.Created)
        val payApiPayment = testPayApiPayment.copy(status = PaymentStatuses.Successful)
        val updatedPayment = amlsPayment.copy(status = payApiPayment.status)

        when {
          testPaymentRepo.findLatestByPaymentReference(paymentRef)
        } thenReturn Future.successful(Some(amlsPayment))

        when {
          testPaymentRepo.insert(any())
        } thenReturn Future.successful(updatedPayment)

        when {
          testPayAPIConnector.getPayment(eqTo(paymentId))(any())
        } thenReturn Future.successful(payApiPayment)

        testPaymentService.refreshStatus(paymentRef) map { result =>
          result mustBe PaymentStatusResult(paymentRef, paymentId, PaymentStatuses.Successful)
          verify(testPaymentRepo).insert(updatedPayment)
        }
      }

      "return None" when {
        "the payment is not found" in {
          when {
            testPaymentRepo.findLatestByPaymentReference(any())
          } thenReturn Future.successful(None)

          testPaymentService.refreshStatus(paymentRefGen.sample.get) map { result =>
            result mustBe None
          }
        }
      }
    }
  }

  "PaymentService" must {
    "create a bacs payment from a bacs payment request" in {
      val bacsPaymentRequest = createBacsPaymentRequestGen.sample.get

      when {
        testPaymentRepo.findLatestByPaymentReference(any())
      } thenReturn Future.successful(None)

      when {
        testPaymentRepo.insert(any())
      } thenReturn Future.successful(mock[Payment])

      whenReady(testPaymentService.createBacsPayment(bacsPaymentRequest)) { _ =>
        verify(testPaymentRepo).insert(any[Payment])
      }
    }

    "return the existing payment when trying to create a duplicate payment" in {
      val bacsPaymentRequest = createBacsPaymentRequestGen.sample.get
      val payment = paymentGen.sample.get.copy(reference = bacsPaymentRequest.paymentReference)

      when {
        testPaymentRepo.findLatestByPaymentReference(eqTo(bacsPaymentRequest.paymentReference))
      } thenReturn Future.successful(Some(payment))

      when {
        testPaymentRepo.update(any())
      } thenReturn Future.successful(successWriteResult)

      whenReady(testPaymentService.createBacsPayment(bacsPaymentRequest)) { result =>
        result mustBe payment.copy(isBacs = Some(true))
        verify(testPaymentRepo, never).insert(any())
      }
    }
  }
}
