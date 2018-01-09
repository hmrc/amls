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

package controllers

import exceptions.{HttpExceptionBody, HttpStatusException}
import models.des.{RequestType, SubscriptionRequest}
import models.fe
import play.api.Logger
import play.api.data.validation.ValidationError
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc.Action
import services.SubscriptionService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future

trait SubscriptionController extends BaseController {

  private[controllers] def service: SubscriptionService

  val safeIdRegex = "^X[A-Z]000[0-9]{10}$".r
  val prefix = "[SubscriptionController][subscribe]"
  val duplicateSubscriptionMessage = "Business Partner already has an active AMLS Subscription"

  private def toError(errors: Seq[(JsPath, Seq[ValidationError])]): JsObject =
    Json.obj(
      "errors" -> (errors map {
        case (path, error) =>
          Json.obj(
            "path" -> path.toJsonString,
            "error" -> error.head.message
          )
      })
    )

  private def toError(message: String): JsObject =
    Json.obj(
      "errors" -> Seq(message)
    )

  def subscribe(accountType: String, ref: String, safeId: String) =
    Action.async(parse.json) {
      implicit request =>
        Logger.debug(s"$prefix - SafeId: $safeId")
        safeIdRegex.findFirstIn(safeId) match {
          case Some(_) =>
            implicit val requestType = RequestType.Subscription
            Json.fromJson[fe.SubscriptionRequest](request.body) match {
              case JsSuccess(body, _) =>
                service.subscribe(safeId, SubscriptionRequest.convert(body)) map {
                  response =>
                    Ok(Json.toJson(response))
                } recoverWith {
                  case ex@HttpStatusException(BAD_REQUEST, _)
                    if ex.jsonBody.fold(false)(_.reason.startsWith(duplicateSubscriptionMessage)) =>

                    Future.successful(UnprocessableEntity(ex.jsonBody.fold(duplicateSubscriptionMessage)(_.reason)))

                  case e @ HttpStatusException(status, Some(body)) =>
                    Logger.warn(s"$prefix - Status: $status, Message: $body")
                    Future.failed(e)
                }
              case JsError(errors) => Future.successful(BadRequest(toError(errors)))
            }
          case _ =>
            Future.successful {
              BadRequest(toError("Invalid SafeId"))
            }
        }
    }
}

object SubscriptionController extends SubscriptionController {
  // $COVERAGE-OFF$
  override private[controllers] val service = SubscriptionService
}
