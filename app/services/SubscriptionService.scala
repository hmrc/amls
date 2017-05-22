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

import com.eclipsesource.schema.{SchemaType, SchemaValidator}
import connectors.{DESConnector, GovernmentGatewayAdminConnector, SubscribeDESConnector}
import exceptions.{HttpExceptionBody, HttpStatusException}
import models.{KnownFact, KnownFactsForService}
import models.des.SubscriptionRequest
import models.fe.SubscriptionResponse
import play.api.Logger
import play.api.libs.json.{JsResult, JsValue, Json}
import repositories.FeeResponseRepository
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait SubscriptionService {

  val BAD_REQUEST: scala.Int = 400

  val amlsRegistrationNumberRegex = "X[A-Z]ML00000[0-9]{6}$".r


  private[services] def desConnector: SubscribeDESConnector

  private[services] def ggConnector: GovernmentGatewayAdminConnector

  private[services] def feeResponseRepository: FeeResponseRepository

  private[services] val validator: SchemaValidator = new SchemaValidator()

  private[services] def validateResult(request: SubscriptionRequest): JsResult[JsValue]

  val stream: InputStream = getClass.getResourceAsStream("/resources/API4_Request.json")
  val lines = scala.io.Source.fromInputStream(stream).getLines
  val linesString = lines.foldLeft[String]("")((x, y) => x.trim ++ y.trim)
  val duplicateSubscriptionMessage = "Business Partner already has an active AMLS Subscription"


  def subscribe
  (safeId: String, request: SubscriptionRequest)
  (implicit
   hc: HeaderCarrier,
   ec: ExecutionContext
  ): Future[SubscriptionResponse] = {
    import com.eclipsesource.schema._
    val result = validateResult(request)
    if (!result.isSuccess) {
      val errors = result.fold(invalid = { errors =>
        errors.foldLeft[String]("") {
          (a, b) => a + "," + b._1.toJsonString
        }
      }, valid = { post => post })
      Logger.warn(s"[SubscriptionService][subscribe] Schema Validation Failed : safeId: $safeId : Error Paths : ${errors}")
    } else {
      Logger.debug(s"[SubscriptionService][subscribe] : safeId: $safeId : Validation passed")
    }

    def failResponse(ex: HttpStatusException, body: HttpExceptionBody) = {
      Logger.warn(s" - Status: ${ex.status}, Message: $body")
      Future.failed(ex)
    }

    for {
      response <- desConnector.subscribe(safeId, request).recoverWith {
        case ex@HttpStatusException(BAD_REQUEST, _) => {
          ex.jsonBody map {
            case body if (body.reason.startsWith(duplicateSubscriptionMessage)) => {

              amlsRegistrationNumberRegex.findFirstIn(body.reason)
                .fold[Future[models.des.SubscriptionResponse]](failResponse(ex, body)) {
                amlsRegNo =>
                  Future.successful(models.des.SubscriptionResponse("", amlsRegNo,0,None,0,0,""))
              }
            }
            case body => {
              failResponse(ex, body)
            }
          }
        }.getOrElse(Future.failed(ex))

        case e@HttpStatusException(status, Some(body)) =>
          Logger.warn(s" - Status: ${status}, Message: $body")
          Future.failed(e)
      }
      _ <- ggConnector.addKnownFacts(KnownFactsForService(Seq(
        KnownFact("SafeId", safeId),
        KnownFact("MLRRefNumber", response.amlsRefNo)

      )))
      inserted <- feeResponseRepository.insert(response)
    } yield {
      SubscriptionResponse.convert(response)
    }
  }
}

object SubscriptionService extends SubscriptionService {
  // $COVERAGE-OFF$
  override private[services] val desConnector = DESConnector
  override private[services] val ggConnector = GovernmentGatewayAdminConnector
  override private[services] val feeResponseRepository = FeeResponseRepository()

  override private[services] def validateResult(request: SubscriptionRequest) = validator.validate(Json.fromJson[SchemaType](Json.parse(linesString.trim.drop(1))).get, Json.toJson(request))
}
