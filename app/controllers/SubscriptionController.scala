/*
 * Copyright 2019 HM Revenue & Customs
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

 import exceptions.{DuplicateSubscriptionException, HttpStatusException}
 import javax.inject.{Inject, Singleton}
 import models.des.{RequestType, SubscriptionRequest}
 import models.fe
 import models.fe.SubscriptionErrorResponse
 import play.api.Logger
 import play.api.libs.json._
 import play.api.mvc.{Action, ControllerComponents, PlayBodyParsers}
 import services.SubscriptionService
 import uk.gov.hmrc.play.bootstrap.controller.BackendController
 import utils.{ApiRetryHelper, AuthAction}

import scala.concurrent.ExecutionContext.Implicits.global
 import scala.concurrent.Future
 import scala.util.matching.Regex

@Singleton
class SubscriptionController @Inject()(val subscriptionService: SubscriptionService,
                                       authAction: AuthAction,
                                       bodyParsers: PlayBodyParsers,
                                       val cc: ControllerComponents)(implicit val apiRetryHelper: ApiRetryHelper) extends BackendController(cc) {

  val safeIdRegex: Regex = "^X[A-Z]000[0-9]{10}$".r
  val prefix = "[SubscriptionController][subscribe]"

  private def toError(errors: Seq[(JsPath, Seq[JsonValidationError])]): JsObject =
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

  def subscribe(accountType: String, ref: String, safeId: String): Action[JsValue] =
    authAction.async(bodyParsers.json) {
      implicit request =>
        Logger.debug(s"$prefix - SafeId: $safeId")
        safeIdRegex.findFirstIn(safeId) match {
          case Some(_) =>
            implicit val requestType: RequestType.Subscription.type = RequestType.Subscription

            Json.fromJson[fe.SubscriptionRequest](request.body) match {
              case JsSuccess(body, _) =>
                subscriptionService.subscribe(safeId, SubscriptionRequest.convert(body)) map {
                  response =>
                    Ok(Json.toJson(response))
                } recoverWith {
                  case ex: DuplicateSubscriptionException =>
                    Future.successful(
                      UnprocessableEntity(Json.toJson(SubscriptionErrorResponse(ex.amlsRegNumber, ex.message)).toString)
                        .as("application/json"))

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