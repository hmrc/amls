/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.{Inject, Provider, Singleton}
import models.Fees
import play.api.{Logger, Logging}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.DefaultDB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import uk.gov.hmrc.mongo.ReactiveRepository
import utils.MongoUtils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait FeesRepository extends ReactiveRepository[Fees, BSONObjectID] {
  def insert(feeResponse: Fees):Future[Boolean]
  def findLatestByAmlsReference(amlsReferenceNumber: String): Future[Option[Fees]]
}

@Singleton
class FeesRepositoryProvider @Inject() (component: ReactiveMongoComponent) extends Provider[FeesRepository] with Logging {

  override def get(): FeesRepository =
    new FeesMongoRepository()(component.mongoConnector.db)
}

class FeesMongoRepository()(implicit mongo: () => DefaultDB) extends ReactiveRepository[Fees, BSONObjectID]("fees", mongo, Fees.format)
  with FeesRepository{

  override def indexes: Seq[Index] = {
    import reactivemongo.bson.DefaultBSONHandlers._

    Seq(Index(Seq("createdAt" -> IndexType.Ascending), name = Some("feeResponseExpiry"),
      options = BSONDocument("expireAfterSeconds" -> 31536000)))
  }

  override def insert(feeResponse: Fees): Future[Boolean] = {
    collection.insert(ordered = false).one(feeResponse) map { lastError =>
      logger.debug(s"[FeeResponseMongoRepository][insert] feeResponse: $feeResponse, result: ${lastError.ok}, errors: ${lastError.writeErrors.getMessages}")
      lastError.ok
    }
  }

  override def findLatestByAmlsReference(amlsReferenceNumber: String): Future[Option[Fees]] = {
    collection.find(Json.obj("amlsReferenceNumber" -> amlsReferenceNumber), Option.empty).sort(Json.obj("createdAt" -> -1)).one[Fees]
  }
}


