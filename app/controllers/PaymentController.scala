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

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services.PaymentService
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.ControllerHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PaymentController @Inject()(
                                   private[controllers] val paymentService: PaymentService
                                 ) extends BaseController with ControllerHelper {

  def savePayment(accountType: String, ref: String, amlsRegistrationNumber: String) = Action.async(parse.text) {
    implicit request: Request[String] => {
      amlsRegNoRegex.findFirstMatchIn(amlsRegistrationNumber) match {
        case Some(_) => {
          Logger.debug(s"[PaymentController][savePayment]: Received paymentId ${request.body}")
          paymentService.savePayment(request.body, amlsRegistrationNumber) map {
            case Some(_) => Created
            case _ => InternalServerError
          }
        }
        case None =>
          Future.successful {
            BadRequest(toError("Invalid amlsRegistrationNumber"))
          }
      }
    }
  }

  def getPaymentByRef(accountType: String, ref: String, paymentReference: String) = Action.async {
    implicit request => paymentService.getPaymentByReference(paymentReference) map {
      case Some(payment) => Ok(Json.toJson(payment))
      case _ => NotFound
    }
  }

  def refreshStatus(accountType: String, ref: String, paymentReference: String) = Action.async {
    implicit request => paymentService.refreshStatus(paymentReference) map { r => Ok(Json.toJson(r)) }
  }

}
