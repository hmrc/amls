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
import models.payapi.PaymentStatus
import models.payments.Payment
import org.mongodb.scala.model._
import org.mongodb.scala.result.{InsertOneResult, UpdateResult}
import play.api.Logging
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(mongoC: MongoComponent)
                                 (implicit executionContext: ExecutionContext)
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
    ),
    extraCodecs = Codecs.playFormatSumCodecs(PaymentStatus.formats)
  ) with Logging {

  /**
    * Records of successful/failed payments are not something that we want to lose. These payments are not audited
    * by the application either so these are the only records that we have of them.
    */
  override lazy val requiresTtlIndex = false

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
    val updates = Seq(
      Updates.set("amlsRefNo", payment.amlsRefNo),
      Updates.set("safeId", payment.safeId),
      Updates.set("reference", payment.reference),
      Updates.set("amountInPence", payment.amountInPence),
      Updates.set("status", payment.status),
      Updates.set("createdAt", payment.createdAt)
    )

    if (payment.isBacs.isDefined) {
      updates :+ Updates.set("isBacs", payment.isBacs)
    }


    if (payment.description.isDefined) {
      updates :+ Updates.set("description", payment.description)
    }


    if (payment.updatedAt.isDefined) {
      updates :+ Updates.set("updatedAt", payment.updatedAt)
    }

    val futureUpdateResult = collection.updateOne(
      filter = Filters.eq("_id", payment._id),
      update = Updates.combine(updates: _*)
    )
      .toFuture()

    futureUpdateResult
      .filter(_.getModifiedCount == 0)
      .foreach(updateResult => logger.error(
        s"""failed to update payment with reference: ${payment.reference}
           |for amls reference number: ${payment.amlsRefNo}, modified count is: ${updateResult.getModifiedCount}""".stripMargin))

    futureUpdateResult
  }

  def findLatestByAmlsReference(amlsReferenceNumber: String): Future[Option[Payment]] = {
    collection
      .find(Filters.eq("amlsRefNo", amlsReferenceNumber))
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