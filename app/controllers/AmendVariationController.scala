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

import exceptions.HttpStatusException
import javax.inject.{Inject, Singleton}
import models.des.{RequestType, _}
import models.fe
import play.api.Logger
import play.api.data.validation.ValidationError
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc.Request
import services.AmendVariationService
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.{ApiRetryHelper, AuthAction}

import scala.concurrent.Future

@Singleton
class AmendVariationController @Inject()(
  implicit val apiRetryHelper: ApiRetryHelper,
  avs: AmendVariationService,
  authAction: AuthAction
) extends BaseController {

  private[controllers] def service: AmendVariationService = avs

  val amlsRegNoRegex = "^X[A-Z]ML00000[0-9]{6}$".r

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


  def update(amlsRegistrationNumber: String,
             messageType: AmlsMessageType,
             requestType: RequestType)(implicit request: Request[JsValue]) = {
    
    val prefix = "[AmendVariationController][update]"
    amlsRegNoRegex.findFirstIn(amlsRegistrationNumber) match {
      case Some(_) =>
        Json.fromJson[fe.SubscriptionRequest](request.body) match {
          case JsSuccess(body, _) =>
            implicit val mt = messageType
            implicit val requestType = RequestType.Amendment
            service.compareAndUpdate(AmendVariationRequest.convert(body), amlsRegistrationNumber) flatMap {
              updatedAmendRequest =>
                service.update(amlsRegistrationNumber, updatedAmendRequest) map {
                  response =>
                    Ok(Json.toJson(response))
                } recoverWith {
                  case e@HttpStatusException(status, message) =>
                    Logger.warn(s"$prefix - Status: $status, Message: $message")
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

  def amend(accountType: String, ref: String, amlsRegistrationNumber: String) =
    authAction.async(parse.json) {
      implicit request =>
        val prefix = "[AmendVariationController][amend]"
        Logger.debug(s"$prefix - AmlsRegistrationNumber: $amlsRegistrationNumber")
        update(amlsRegistrationNumber, Amendment, RequestType.Amendment)
    }

  def variation(accountType: String, ref: String, amlsRegistrationNumber: String) =
    authAction.async(parse.json) {
      implicit request =>
        val prefix = "[AmendVariationController][variation]"
        Logger.debug(s"$prefix - AmlsRegistrationNumber: $amlsRegistrationNumber")
        update(amlsRegistrationNumber, Variation, RequestType.Variation)
    }

  def renewal(accountType: String, ref: String, amlsRegistrationNumber: String) =
    authAction.async(parse.json) {
      implicit request =>
        val prefix = "[AmendVariationController][renewal]"
        Logger.debug(s"$prefix - AmlsRegistrationNumber: $amlsRegistrationNumber")
        update(amlsRegistrationNumber, Renewal, RequestType.Renewal)
    }

  def renewalAmendment(accountType: String, ref: String, amlsRegistrationNumber: String) =
    authAction.async(parse.json) {
      implicit request =>
        val prefix = "[AmendVariationController][renewalAmendment]"
        Logger.debug(s"$prefix - AmlsRegistrationNumber: $amlsRegistrationNumber")
        update(amlsRegistrationNumber, RenewalAmendment, RequestType.RenewalAmendment)
    }
}

