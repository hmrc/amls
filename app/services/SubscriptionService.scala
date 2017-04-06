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

import java.io.InputStream

import com.eclipsesource.schema.SchemaValidator
import connectors.{DESConnector, GovernmentGatewayAdminConnector, SubscribeDESConnector}
import models.{KnownFact, KnownFactsForService}
import models.des.{SubscriptionRequest, SubscriptionResponse}
import play.api.Logger
import play.api.libs.json.Json
import repositories.FeeResponseRepository
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait SubscriptionService {

  private[services] def desConnector: SubscribeDESConnector

  private[services] def ggConnector: GovernmentGatewayAdminConnector

  private[services] def feeResponseRepository: FeeResponseRepository

  private val validator = new SchemaValidator()

  val stream: InputStream = getClass.getResourceAsStream("/resources/API4_Request.json")
  val lines = scala.io.Source.fromInputStream(stream).getLines
  val linesString = lines.foldLeft[String]("")((x, y) => x.trim ++ y.trim)


  def subscribe
  (safeId: String, request: SubscriptionRequest)
  (implicit
   hc: HeaderCarrier,
   ec: ExecutionContext
  ): Future[SubscriptionResponse] = {
    import com.eclipsesource.schema._
    val validateResult = validator.validate(Json.fromJson[SchemaType](Json.parse(linesString.trim.drop(1))).get, Json.toJson(request))
    if(!validateResult.isSuccess){
      val errors = validateResult.fold(invalid = { errors =>
        errors.foldLeft[String]("") {
          (a, b) => a + "," + b._1.toJsonString
        }
      }, valid = { post => post })
      Logger.warn(s"[SubscriptionService][subscribe] Schema Validation Failed : safeId: $safeId : Error Paths : ${errors}")
    }else {
      Logger.debug(s"[SubscriptionService][subscribe] : safeId: $safeId : Validation passed")
    }
    for {
      response <- desConnector.subscribe(safeId, request)
      _ <- ggConnector.addKnownFacts(KnownFactsForService(Seq(
        KnownFact("SafeId", safeId),
        KnownFact("MLRRefNumber", response.amlsRefNo)

      )))
      inserted <- feeResponseRepository.insert(response)
    } yield {
      response
    }
  }
}

object SubscriptionService extends SubscriptionService {
  // $COVERAGE-OFF$
  override private[services] val desConnector = DESConnector
  override private[services] val ggConnector = GovernmentGatewayAdminConnector
  override private[services] val feeResponseRepository = FeeResponseRepository()
}
