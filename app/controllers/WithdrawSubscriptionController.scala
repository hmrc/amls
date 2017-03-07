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

package controllers

import javax.inject.Inject

import connectors.WithdrawSubscriptionConnector
import models.des.WithdrawSubscriptionRequest
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.mvc.Action
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WithdrawSubscriptionController @Inject()(withdrawSubscriptionConnector: WithdrawSubscriptionConnector) extends BaseController {

  val amlsRegNoRegex = "^X[A-Z]ML00000[0-9]{6}$".r

  private def toError(errors: Seq[(JsPath, Seq[ValidationError])]) = Json.obj(
    "errors" -> (errors map {
      case (path, error) =>
        Json.obj(
          "path" -> path.toJsonString,
          "error" -> error.head.message
        )
    })
  )

  private def toError(message: String) = Json.obj(
    "errors" -> Seq(message)
  )


  def withdrawal(amlsRegistrationNumber: String) = Action.async(parse.json) {
    implicit request =>
      amlsRegNoRegex.findFirstMatchIn(amlsRegistrationNumber) match {
        case Some(_) => {
          Json.fromJson[WithdrawSubscriptionRequest](request.body) match {
            case JsSuccess(body, _) =>
              withdrawSubscriptionConnector.withdrawal(amlsRegistrationNumber, body) map {
                response =>
                  Ok(Json.toJson(response))
              }
            case JsError(errors) =>
              Future.successful(BadRequest(toError(errors)))
          }
        }
        case None =>
          Future.successful {
            BadRequest(toError("Invalid amlsRegistrationNumber"))
          }
      }
  }
}