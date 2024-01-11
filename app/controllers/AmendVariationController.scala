/*
 * Copyright 2024 HM Revenue & Customs
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

import exceptions.HttpStatusException
import models.des._
import models.fe
import play.api.Logging
import play.api.libs.json._
import play.api.mvc.{Action, ControllerComponents, PlayBodyParsers, Request, Result}
import services.AmendVariationService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.{ApiRetryHelper, AuthAction, ControllerHelper}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class AmendVariationController @Inject()(avs: AmendVariationService, authAction: AuthAction, bodyParsers: PlayBodyParsers,
                                         val cc: ControllerComponents)
                                        (implicit val apiRetryHelper: ApiRetryHelper, executionContext: ExecutionContext)
                                        extends BackendController(cc) with Logging with ControllerHelper {

  private[controllers] def service: AmendVariationService = avs

  def update(amlsRegistrationNumber: String,
             messageType: AmlsMessageType,
             requestType: RequestType)(implicit request: Request[JsValue]): Future[Result] = {

    val prefix = "[AmendVariationController][update]"
    amlsRegNoRegex.findFirstIn(amlsRegistrationNumber) match {
      case Some(_) =>
        Json.fromJson[fe.SubscriptionRequest](request.body) match {
          case JsSuccess(body, _) =>
            implicit val mt: AmlsMessageType = messageType
            implicit val requestType: RequestType = RequestType.Amendment
            service.compareAndUpdate(AmendVariationRequest.convert(body), amlsRegistrationNumber) flatMap {
              updatedAmendRequest =>
                service.update(amlsRegistrationNumber, updatedAmendRequest) map {
                  response =>
                    Ok(Json.toJson(response))
                } recoverWith {
                  case e @ HttpStatusException(status, message) =>
                    logger.warn(s"$prefix - Status: $status, Message: $message")
                    Future.failed(e)
                }
            }
          case JsError(errors) =>
            Future.successful(BadRequest(toError(errors)))
        }
      case _ =>
        Future.successful {
          BadRequest(toError("Invalid AmlsRegistrationNumber"))
        }
    }

  }

  def amend(accountType: String, ref: String, amlsRegistrationNumber: String): Action[JsValue] =
    authAction.async(bodyParsers.json) {
      implicit request =>
        val prefix = "[AmendVariationController][amend]"
        logger.debug(s"$prefix - AmlsRegistrationNumber: $amlsRegistrationNumber")
        update(amlsRegistrationNumber, Amendment, RequestType.Amendment)
    }

  def variation(accountType: String, ref: String, amlsRegistrationNumber: String): Action[JsValue] =
    authAction.async(bodyParsers.json) {
      implicit request =>
        val prefix = "[AmendVariationController][variation]"
        logger.debug(s"$prefix - AmlsRegistrationNumber: $amlsRegistrationNumber")
        update(amlsRegistrationNumber, Variation, RequestType.Variation)
    }

  def renewal(accountType: String, ref: String, amlsRegistrationNumber: String): Action[JsValue] =
    authAction.async(bodyParsers.json) {
      implicit request =>
        val prefix = "[AmendVariationController][renewal]"
        logger.debug(s"$prefix - AmlsRegistrationNumber: $amlsRegistrationNumber")
        update(amlsRegistrationNumber, Renewal, RequestType.Renewal)
    }

  def renewalAmendment(accountType: String, ref: String, amlsRegistrationNumber: String): Action[JsValue] =
    authAction.async(bodyParsers.json) {
      implicit request =>
        val prefix = "[AmendVariationController][renewalAmendment]"
        logger.debug(s"$prefix - AmlsRegistrationNumber: $amlsRegistrationNumber")
        update(amlsRegistrationNumber, RenewalAmendment, RequestType.RenewalAmendment)
    }
}

