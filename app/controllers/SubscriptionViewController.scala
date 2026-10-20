/*
 * Copyright 2026 HM Revenue & Customs
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

import connectors.ViewDESConnector
import domain.AmlsRegistrationNumber
import exceptions.HttpStatusException
import models.fe.SubscriptionView
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.{ApiRetryHelper, AuthAction, ControllerHelper}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubscriptionViewController @Inject() (
  vdc: ViewDESConnector,
  authAction: AuthAction,
  val cc: ControllerComponents
)(implicit val apiRetryHelper: ApiRetryHelper, executionContext: ExecutionContext)
    extends BackendController(cc)
    with Logging
    with ControllerHelper {

  private[controllers] def connector: ViewDESConnector = vdc

  val prefix = "[SubscriptionViewController][view]"

  def view(accountType: String, ref: String, amlsRegistrationNumber: String): Action[AnyContent] =
    authAction.async { implicit request =>
      logger.debug(s"$prefix - amlsRegNo: $amlsRegistrationNumber")

      AmlsRegistrationNumber.fromString(amlsRegistrationNumber) match {
        case Right(amlsRegistrationNumber) =>
          connector.view(amlsRegistrationNumber.regNum) map { response =>
            val feModel: SubscriptionView = response
            logger.info(s"$prefix - Subscription view returned - amlsRegNo: ${amlsRegistrationNumber.regNum}")
            logger.debug(s"$prefix - Converted model: $feModel")
            val json                      = Json.toJson(feModel)
            logger.debug(s"$prefix - Response JSON: $json")
            Ok(json)
          } recoverWith {
            case e @ HttpStatusException(status, Some(body)) =>
              logger.warn(s"$prefix - HttpStatusException from DES view - status: $status, message: $body")
              Future.failed(e)
            case e                                           =>
              logger.warn(
                s"$prefix - Unexpected exception viewing subscription - amlsRegNo: ${amlsRegistrationNumber.regNum}",
                e
              )
              Future.failed(e)
          }
        case Left(_)                       =>
          logger.warn(s"$prefix - Invalid AMLS registration number format")
          Future.successful {
            BadRequest(toError("Invalid AMLS Registration Number"))
          }
      }
    }
}
