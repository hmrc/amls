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

import javax.inject.{Inject, Singleton}

import cats.data.OptionT
import cats.implicits._
import connectors.PayAPIConnector
import exceptions.{HttpStatusException, PaymentException}
import models.{Payment, RefreshStatusResult}
import uk.gov.hmrc.play.http.HeaderCarrier
import play.api.http.Status._
import repositories.PaymentRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentService @Inject()(
                                val paymentConnector: PayAPIConnector,
                                val paymentsRepository: PaymentRepository
                              ) {
  def savePayment(paymentId: String, amlsRegistrationNumber: String)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Payment]] = {
    (for {
      pm <- paymentConnector.getPayment(paymentId)
      _ <- paymentsRepository.insert(pm.copy(amlsRefNo = amlsRegistrationNumber.some))
    } yield pm.some) recoverWith {
      case e: HttpStatusException if e.status.equals(NOT_FOUND) => Future.successful(None)
      case e: HttpStatusException => Future.failed(PaymentException(Some(e.status), e.body.getOrElse("Could not retrieve payment")))
      case e: PaymentException => Future.failed(e)
    }
  }

  def getPaymentByReference(paymentReference: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[Payment]] =
    paymentsRepository.find("reference" -> paymentReference).map { r => r.headOption }

  def refreshStatus(paymentReference: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): OptionT[Future, RefreshStatusResult] = {
    for {
      payment <- OptionT(getPaymentByReference(paymentReference))
      refreshedPayment <- OptionT.liftF(paymentConnector.getPayment(payment._id))
      _ <- OptionT.liftF(paymentsRepository.insert(payment.copy(status = refreshedPayment.status)))
    } yield {
      RefreshStatusResult(paymentReference, refreshedPayment._id, refreshedPayment.status)
    }
  }

}
