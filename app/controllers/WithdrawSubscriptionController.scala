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

import javax.inject.{Inject, Singleton}
import connectors.WithdrawSubscriptionConnector
import models.des.WithdrawSubscriptionRequest
import play.api.libs.json._
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.{ApiRetryHelper, AuthAction, ControllerHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class WithdrawSubscriptionController @Inject()(
                                                connector: WithdrawSubscriptionConnector,
                                                implicit val apiRetryHelper: ApiRetryHelper,
                                                authAction: AuthAction
                                              ) extends BaseController with ControllerHelper {

  def withdrawal(accountType: String, ref: String, amlsRegistrationNumber: String) = authAction.async(parse.json) {
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
