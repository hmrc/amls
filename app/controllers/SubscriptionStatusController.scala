/*
 * Copyright 2023 HM Revenue & Customs
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

import connectors.SubscriptionStatusDESConnector
import exceptions.HttpStatusException
import play.api.Logging
import play.api.libs.json._
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.{ApiRetryHelper, AuthAction, ControllerHelper}

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubscriptionStatusController @Inject()(ssConn: SubscriptionStatusDESConnector,
                                             authAction: AuthAction,
                                             val cc: ControllerComponents)
                                            (implicit val apiRetryHelper: ApiRetryHelper, executionContext: ExecutionContext)
                                            extends BackendController(cc) with Logging with ControllerHelper {

  private[controllers] def connector: SubscriptionStatusDESConnector = ssConn

  val prefix = "[SubscriptionStatusController][get]"

  def get(accountType: String, ref: String, amlsRegistrationNumber: String) =
    authAction.async {
      implicit request =>
        logger.debug(s"$prefix - amlsRegNo: $amlsRegistrationNumber")
        amlsRegNoRegex.findFirstIn(amlsRegistrationNumber) match {
          case Some(_) =>
            connector.status(amlsRegistrationNumber) map {
              response =>
                Ok(Json.toJson(response))
            } recoverWith {
              case e @ HttpStatusException(status, Some(body)) =>
                logger.warn(s"$prefix - Status: ${status}, Message: $body")
                Future.failed(e)
            }

          case _ =>
            Future.successful {
              BadRequest(toError("Invalid AMLS Registration Number"))
            }
        }
    }
}
