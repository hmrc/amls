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

import com.google.inject.Inject
import models.Fees
import org.mongodb.scala.model._
import org.mongodb.scala.result.InsertOneResult
import play.api.Logging
import repositories.FeesRepository.feesResponseExpiryTimeSeconds
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeesRepository @Inject()(mongoC: MongoComponent)
                              (implicit executionContext: ExecutionContext)
                              extends PlayMongoRepository[Fees](
    mongoComponent = mongoC,
    collectionName = "fees",
    domainFormat = Fees.format,
    indexes = Seq(
      IndexModel(
        Indexes.ascending("createdAt"),
        IndexOptions().name("feeResponseExpiry").expireAfter(feesResponseExpiryTimeSeconds, SECONDS)
      ),
      IndexModel(
        Indexes.descending("amlsReferenceNumber"),
        IndexOptions().name("amlsRefNumber")
      )
    )
  ) with Logging {

  def insert(feeResponse: Fees): Future[Boolean] = {
    collection
      .insertOne(feeResponse)
      .toFuture()
      .map { writeRes: InsertOneResult =>
        logger.debug(s"[FeeResponseMongoRepository][insert] feeResponse: $feeResponse, result id: ${writeRes.getInsertedId}," +
          s"acknowledged: ${writeRes.getInsertedId}")
        writeRes.wasAcknowledged()
      }
  }

  def findLatestByAmlsReference(amlsReferenceNumber: String): Future[Option[Fees]] = {
    collection
      .find(Filters.eq("amlsReferenceNumber", amlsReferenceNumber))
      .sort(Sorts.descending("createdAt"))
      .headOption()
  }
}

object FeesRepository {
  val feesResponseExpiryTimeSeconds = 31536000
}
