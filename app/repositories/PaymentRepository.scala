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

package repositories

import exceptions.PaymentException
import javax.inject.{Inject, Singleton}
import models.payments.Payment
import play.api.Logger
import play.api.libs.json.Json
import reactivemongo.api.DefaultDB
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONObjectID
import reactivemongo.play.json.ImplicitBSONHandlers._
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.MongoUtils._

@Singleton
class PaymentRepository @Inject()(mongo: () => DefaultDB) extends ReactiveRepository[Payment, BSONObjectID]("payments", mongo, Payment.format) {

  override def indexes: Seq[Index] = {
    Seq(
      Index(
        key = Seq("createdAt" -> IndexType.Ascending),
        name = Some("paymentDetailsExpiry")
      ),
      Index(
        key = Seq("amlsRefNo" -> IndexType.Ascending),
        name = Some("amlsRefNoIndex")
      ),
      Index(
        key = Seq("reference" -> IndexType.Ascending, "createdAt" -> IndexType.Descending),
        name = Some("reference lookup")
      )
    )
  }

  def insert(newPayment: Payment): Future[Payment] = collection.insert(newPayment).flatMap(checkSuccessfulAndReturn(newPayment))

  def update(payment: Payment): Future[UpdateWriteResult] = collection.update(
    Json.obj("_id" -> payment._id),
    payment
  )

  def findLatestByAmlsReference(amlsReferenceNumber: String): Future[Option[Payment]] = {
    collection.find(Json.obj("amlsRefNo" -> amlsReferenceNumber)).sort(Json.obj("createdAt" -> -1)).one[Payment]
  }

  def findLatestByPaymentReference(paymentReference: String): Future[Option[Payment]] =
    collection.find(Json.obj("reference" -> paymentReference)).sort(Json.obj("createdAt" -> -1)).one[Payment]

  private def checkSuccessfulAndReturn(payment: Payment): WriteResult => Future[Payment] = {
    case writeResult: WriteResult if isError(writeResult) => {
      Logger.debug(s"[PaymentsMongoRepository][insert] paymentDetails: $payment, result: ${writeResult.ok}, errors: ${writeResult.writeErrors.getMessages}")
      Future.failed(PaymentException(None, writeResult.writeErrors.getMessages))
    }
    case _ => Future.successful(payment)
  }

  private def isError(writeResult: WriteResult) = !writeResult.ok || writeResult.writeErrors.nonEmpty
}
