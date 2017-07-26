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

import javax.inject.{Inject, Singleton}

import models.Payment
import play.api.Logger
import play.api.libs.json.Json
import reactivemongo.api.{DB, DefaultDB}
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PaymentsRepository @Inject()(mongo: () => DB) extends ReactiveRepository[Payment, String]("payments", mongo, Payment.format) {

  override def indexes: Seq[Index] = {
    import reactivemongo.bson.DefaultBSONHandlers._

    Seq(Index(Seq("createdAt" -> IndexType.Ascending), name = Some("paymentDetailsExpiry"),
      options = BSONDocument("expireAfterSeconds" -> 2592000)))


  }

  def insert(newPayment: Payment): Future[Payment] = collection.insert(newPayment).flatMap(checkSuccessfulAndReturn(newPayment))

  def findLatestByAmlsReference(amlsReferenceNumber: String) = {
    collection.find(Json.obj("amlsReferenceNumber" -> amlsReferenceNumber)).sort(Json.obj("createdAt" -> -1)).one[Payment]
  }

  private def checkSuccessfulAndReturn(payment: Payment): WriteResult => Future[Payment] = {
    case writeResult: WriteResult if isError(writeResult) => {
      Logger.debug(s"[PaymentsMongoRepository][insert] : { paymentDetails : $payment , result: ${writeResult.ok}, errors: ${writeResult.errmsg} }")
      Future.failed(new Exception(writeResult.errmsg.getOrElse("[PaymentsMongoRepository][insert] Unknown write result error.")))
    }
    case _ => Future.successful(payment)
  }

  private def isError(writeResult: WriteResult) = !writeResult.ok || writeResult.hasErrors
}