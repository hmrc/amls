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

package controllers

import connectors.WithdrawSubscriptionConnector
import javax.inject.{Inject, Singleton}
import models.des.WithdrawSubscriptionRequest
import play.api.libs.json._
import play.api.mvc.{ControllerComponents, PlayBodyParsers}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.{ApiRetryHelper, AuthAction, ControllerHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class WithdrawSubscriptionController @Inject()(connector: WithdrawSubscriptionConnector,
                                               authAction: AuthAction,
                                               bodyParsers: PlayBodyParsers,
                                               val cc: ControllerComponents)
                                              (implicit val apiRetryHelper: ApiRetryHelper) extends BackendController(cc) with ControllerHelper {

  def withdrawal(accountType: String, ref: String, amlsRegistrationNumber: String) = authAction.async(bodyParsers.json) {
    implicit request =>
      amlsRegNoRegex.findFirstMatchIn(amlsRegistrationNumber) match {
        case Some(_) => {
          Json.fromJson[WithdrawSubscriptionRequest](request.body) match {
            case JsSuccess(body, _) =>
              connector.withdrawal(amlsRegistrationNumber, body) map {
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
