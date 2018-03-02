/*
 * Copyright 2018 HM Revenue & Customs
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

import java.util.concurrent.TimeUnit

import models.Fees
import play.api.Logger
import play.api.libs.json.Json
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.commands.Command
import reactivemongo.api.{BSONSerializationPack, DefaultDB}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONObjectID}
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}
import reactivemongo.bson.DefaultBSONHandlers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, duration}
import scala.util.{Failure, Success}

trait FeesRepository extends Repository[Fees, BSONObjectID] {

  def insert(feeResponse: Fees):Future[Boolean]

  def findLatestByAmlsReference(amlsReferenceNumber: String):Future[Option[Fees]]

}

class FeesMongoRepository()(implicit mongo: () => DefaultDB) extends ReactiveRepository[Fees, BSONObjectID]("fees", mongo, Fees.format)
  with FeesRepository{

  private lazy val feeResponseIndex = Index(Seq("createdAt" -> IndexType.Ascending),
                                                name = Some("feeResponseExpiry"), options = BSONDocument("expireAfterSeconds" -> "31536000"))
  private lazy val amlsRefNumberIndex = Index(Seq("amlsReferenceNumber" -> IndexType.Descending), name = Some("amlsRefNumber"))

  def commandResult()(implicit ec: ExecutionContext): Future[BSONDocument] = {
    //db.runCommand( {"collMod": "fees" , "index": { "keyPattern": {"createdAt" : 1} , "expireAfterSeconds": 31536000 }})
    val commandDoc = BSONDocument(
      "collMod" -> "fees",
      "index" -> BSONDocument("keyPattern" -> BSONDocument("createdAt" -> 1), "expireAfterSeconds" -> "31536000")
    )
    println(s">>>>>>>>>>>>>>>>>>>>>>>>>  About to run index update ${BSONDocument.pretty(commandDoc)}")

    val runner: Command.CommandWithPackRunner[BSONSerializationPack.type] = Command.run(BSONSerializationPack)

    val stuff = runner.apply(collection.db, runner.rawCommand(commandDoc)).one[BSONDocument]

    println(">>>>>>>>>>>>>>>>>>>>>>>>>  index update run")

    stuff
  }

  override def indexes: Seq[Index] = {

    //REMOVE LINES BELOW AFTER 1st DEPLOYMENT to live
    val resultOfCommand: Future[BSONDocument] = commandResult()

    val test = resultOfCommand.map(a => a).onComplete {
      case Success(res) => {
        println(s">>>>>>>>>>>>>>>>>>>>>>>>  ${BSONDocument.pretty(res)}")
      }
      case Failure(t) => {
        println(s">>>>>>>>>>>>>  ERROR  >>>>>>>>>>>  ${t}")
      }
    }
val result: BSONDocument = Await.result(resultOfCommand, Duration(10, TimeUnit.SECONDS))

    println(s">>>>>>>>>>>>>>>old>>>>>>>>>  ${result.get("expireAfterSeconds_old")}")
    println(s"new ${result.get("expireAfterSeconds_new")}")
    println(s">>>>>>>>>>>>>>>ok>>>>>>>>>  ${result.get("ok")}")
    //REMOVE LINES ABOVE AFTER 1st DEPLOYMENT to live

    Seq(feeResponseIndex, amlsRefNumberIndex)
  }

  override def insert(feeResponse: Fees):Future[Boolean] = {
    collection.insert[Fees](feeResponse) map { lastError =>
      Logger.debug(s"[FeeResponseMongoRepository][insert] : { feeResponse : $feeResponse , result: ${lastError.ok}, errors: ${lastError.errmsg} }")
      lastError.ok
    }
  }

  override def findLatestByAmlsReference(amlsReferenceNumber: String) = {
    collection.find(Json.obj("amlsReferenceNumber" -> amlsReferenceNumber)).sort(Json.obj("createdAt" -> -1)).one[Fees]
  }
}

object FeesRepository extends MongoDbConnection {

  private lazy val feesRepository = new FeesMongoRepository

  def apply(): FeesMongoRepository = feesRepository
}
