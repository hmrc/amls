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

package repositories

import models.Payment
import play.api.Logger
import play.api.libs.json.Json
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DefaultDB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait PaymentsRepository extends Repository[Payment, BSONObjectID] {

  def insert(paymentDetails: Payment):Future[Boolean]

  def findLatestByAmlsReference(amlsReferenceNumber: String):Future[Option[Payment]]

}

class PaymentsMongoRepository()(implicit mongo: () => DefaultDB) extends ReactiveRepository[Payment, BSONObjectID]("payments", mongo, Payment.format)
  with PaymentsRepository{



  override def indexes: Seq[Index] = {
    import reactivemongo.bson.DefaultBSONHandlers._

    Seq(Index(Seq("createdAt" -> IndexType.Ascending), name = Some("paymentDetailsExpiry"),
      options = BSONDocument("expireAfterSeconds" -> 2592000)))


  }

  override def insert(paymentDetails: Payment):Future[Boolean] = {
    collection.insert[Payment](paymentDetails) map { lastError =>
      Logger.debug(s"[PaymentsMongoRepository][insert] : { paymentDetails : $paymentDetails , result: ${lastError.ok}, errors: ${lastError.errmsg} }")
      lastError.ok
    }
  }

  override def findLatestByAmlsReference(amlsReferenceNumber: String) = {
    collection.find(Json.obj("amlsReferenceNumber" -> amlsReferenceNumber)).sort(Json.obj("createdAt" -> -1)).one[Payment]
  }
}

object PaymentsRepository extends MongoDbConnection {

  private lazy val paymentsRepository = new PaymentsMongoRepository

  def apply(): PaymentsMongoRepository = paymentsRepository
}
