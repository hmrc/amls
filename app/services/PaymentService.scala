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

package services

import javax.inject.{Inject, Singleton}
import cats.data.OptionT
import cats.implicits._
import connectors.PayAPIConnector
import exceptions.{HttpStatusException, PaymentException}
import models.payapi
import models.payments.{CreateBacsPaymentRequest, Payment, PaymentStatusResult}
import org.mongodb.scala.result.UpdateResult
import play.api.Logging
import play.api.http.Status._
import repositories.PaymentRepository

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HeaderCarrier

@Singleton
class PaymentService @Inject() (val paymentConnector: PayAPIConnector, val paymentsRepository: PaymentRepository)(
  implicit ec: ExecutionContext
) extends Logging {

  def createPayment(paymentId: String, amlsRegistrationNumber: String, safeId: String)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[Option[Payment]] =
    (for {
      pm         <- paymentConnector.getPayment(paymentId)
      newPayment <- paymentsRepository.insert(Payment(amlsRegistrationNumber, safeId, pm))
    } yield newPayment.some) recoverWith {
      case e: HttpStatusException if e.status.equals(NOT_FOUND) => Future.successful(None)
      case e: HttpStatusException                               =>
        Future.failed(PaymentException(Some(e.status), e.body.getOrElse("Could not retrieve payment")))
      case e: PaymentException                                  => Future.failed(e)
    }

  def createBacsPayment(request: CreateBacsPaymentRequest)(implicit ec: ExecutionContext): Future[Payment] =
    paymentsRepository.findLatestByPaymentReference(request.paymentReference) flatMap {
      case Some(p) =>
        val copied = p.copy(isBacs = Some(true))
        paymentsRepository.update(copied) map { _ => copied }
      case _       => paymentsRepository.insert(Payment(request))
    }

  def getPaymentByAmlsReference(amlsRefNo: String): Future[Option[Payment]] = {
    logger.debug(s"Searching for payment by amls reference $amlsRefNo ...")
    val futureOptPayment = paymentsRepository.findLatestByAmlsReference(amlsRefNo)

    futureOptPayment.foreach {
      case Some(payment) => Some(payment)
      case None          => logger.error(s"unable to find payment in database for amls ref number $amlsRefNo"); None
    }

    futureOptPayment
  }

  def getPaymentByPaymentReference(paymentReference: String): Future[Option[Payment]] = {
    logger.debug(s"Searching for payment by payment reference $paymentReference ...")
    val futureOptPayment = paymentsRepository.findLatestByPaymentReference(paymentReference)

    futureOptPayment.foreach {
      case Some(payment) => Some(payment)
      case None          => logger.error(s"unable to find payment in database for payment reference $paymentReference"); None
    }

    futureOptPayment
  }

  def updatePayment(payment: Payment)(implicit ec: ExecutionContext): Future[Boolean] =
    paymentsRepository.update(payment) map {
      case r if r.wasAcknowledged() => true
      case _: UpdateResult          =>
        throw new Exception(s"Unknown error when trying to update payment ref ${payment.reference}")
    }

  def refreshStatus(
    paymentReference: String
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): OptionT[Future, PaymentStatusResult] =
    for {
      payment: Payment                 <- OptionT(getPaymentByPaymentReference(paymentReference))
      refreshedPayment: payapi.Payment <- OptionT.liftF(paymentConnector.getPayment(payment._id))
      _                                <- OptionT.liftF(paymentsRepository.update(payment.copy(status = refreshedPayment.status)))
    } yield PaymentStatusResult(paymentReference, refreshedPayment.id, refreshedPayment.status)

}
