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
import models.Payment
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.Helpers._
import repositories.PaymentRepository
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class PaymentServiceSpec extends PlaySpec with MockitoSugar with PaymentGenerator with ScalaFutures {

  implicit val hc: HeaderCarrier = new HeaderCarrier()

  val testPayAPIConnector = mock[PayAPIConnector]
  val testPaymentRepo = mock[PaymentRepository]

  val testPayment = paymentGen.sample.get

  val testPaymentService = new PaymentService(testPayAPIConnector, testPaymentRepo)

  "PaymentService" when {
    "getPayment is called" must {
      "respond with payment if call to connector is successful" in {

        def payment = testPayment

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.successful(payment)
        }

        val result = testPaymentService.getPayment(payment._id)
        await(result) mustBe Some(payment)

      }

      "respond with None if call to connector returns HttpStatusException NotFound" in {

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.failed(new HttpStatusException(NOT_FOUND, None))
        }

        val result = testPaymentService.getPayment(testPayment._id)
        await(result) mustBe None

      }

      "respond with PaymentException if connector returns HttpStatusException with anything else" in {

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.failed(new HttpStatusException(INTERNAL_SERVER_ERROR, None))
        }

        val result = testPaymentService.getPayment(testPayment._id).failed

        await(result) mustBe PaymentException(Some(INTERNAL_SERVER_ERROR), "Could not retrieve payment")

      }

      "respond with PaymentException if connector returns anything else" in {

        when {
          testPayAPIConnector.getPayment(any())(any())
        } thenReturn {
          Future.failed(new Exception(""))
        }

        val result = testPaymentService.getPayment(testPayment._id).failed

        await(result) mustBe PaymentException(None, "Could not retrieve payment")

      }

    }

    "savePayment is called" must {

      "send payment to insert" when {
        "call to getPayment is successful" in {

          val payment = testPayment

          val testPaymentService = new PaymentService(testPayAPIConnector, testPaymentRepo) {
            override def getPayment(paymentId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Payment]] =
              Future.successful(Some(payment))
          }

          when {
            testPaymentService.paymentsRepository.insert(payment)
          } thenReturn {
            Future.successful(payment)
          }

          whenReady(testPaymentService.savePayment(payment._id)) { res =>
            verify(
              testPaymentService.paymentsRepository
            ).insert(payment)
          }
        }
      }

    }
  }

}
