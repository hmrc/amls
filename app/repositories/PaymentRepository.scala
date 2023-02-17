/*
 * Copyright 2023 HM Revenue & Customs
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
import models.payments.Payment
import org.mongodb.scala.model._
import org.mongodb.scala.result.{InsertOneResult, UpdateResult}
import play.api.Logging
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PaymentRepository @Inject()(mongoC: MongoComponent)
  extends PlayMongoRepository[Payment](
    mongoComponent = mongoC,
    collectionName = "payments",
    domainFormat = Payment.format,
    indexes = Seq(
      IndexModel(
        Indexes.ascending("createdAt"),
        IndexOptions().name("paymentDetailsExpiry")
      ),
      IndexModel(
        Indexes.ascending("amlsRefNo"),
        IndexOptions().name("amlsRefNoIndex")
      ),
      IndexModel(
        Indexes.compoundIndex(Indexes.ascending("reference"), Indexes.descending("createdAt")),
        IndexOptions().name("reference lookup")
      )
    )
  ) with Logging {

  def insert(newPayment: Payment): Future[Payment] = {
    collection
      .insertOne(newPayment)
      .toFuture()
      .flatMap {
        case res: InsertOneResult if !res.wasAcknowledged() => Future.failed(PaymentException(None, res.toString))
        case _ => Future.successful(newPayment)
      }
  }

  def update(payment: Payment): Future[UpdateResult] = {
    collection.updateOne(
      filter = Filters.eq("_id", payment._id),
      update = Updates.combine(
        Updates.set("amlsRefNo", payment.amlsRefNo),
        Updates.set("safeId", payment.safeId),
        Updates.set("reference", payment.reference),
        Updates.set("description", payment.description),
        Updates.set("amountInPence", payment.amountInPence),
        Updates.set("status", payment.status),
        Updates.set("createdAt", payment.createdAt),
        Updates.set("isBacs", payment.isBacs),
        Updates.set("updatedAt", payment.updatedAt),
      )
    )
      .toFuture()
  }

  def findLatestByAmlsReference(amlsReferenceNumber: String): Future[Option[Payment]] = {
    collection
      .find(Filters.eq("amlsReNo", amlsReferenceNumber))
      .sort(Sorts.descending("createdAt"))
      .headOption()
  }

  def findLatestByPaymentReference(paymentReference: String): Future[Option[Payment]] = {
    collection
      .find(Filters.eq("reference", paymentReference))
      .sort(Sorts.descending("createdAt"))
      .headOption()
  }
}