/*
 * Copyright 2020 HM Revenue & Customs
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

import cats.data.OptionT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import models.payments.{CreateBacsPaymentRequest, RefreshPaymentStatusRequest, SetBacsRequest}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import services.PaymentService
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import utils.{AuthAction, ControllerHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PaymentController @Inject()(private[controllers] val paymentService: PaymentService,
                                  authAction: AuthAction,
                                  bodyParsers: PlayBodyParsers,
                                  val cc: ControllerComponents) extends BackendController(cc) with ControllerHelper {

  def createBacsPayment(accountType: String, accountRef: String) = authAction.async(bodyParsers.json) {
    implicit request =>
      request.body.asOpt[CreateBacsPaymentRequest] match {
        case Some(r) => paymentService.createBacsPayment(r) map { p => Created(Json.toJson(p)) }
        case _ => Future.successful(BadRequest)
      }
  }

  def savePayment(accountType: String, ref: String, amlsRegistrationNumber: String, safeId: String) =
    authAction.async(bodyParsers.text) {
    implicit request: Request[String] => {
      amlsRegNoRegex.findFirstMatchIn(amlsRegistrationNumber) match {
        case Some(_) => {
          Logger.debug(s"[PaymentController][savePayment]: Received paymentId ${request.body}")
          paymentService.createPayment(request.body, amlsRegistrationNumber, safeId) map {
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

  def getPaymentByRef(accountType: String, ref: String, paymentReference: String) = authAction.async {
    implicit request =>
      paymentService.getPaymentByPaymentReference(paymentReference) map {
        case Some(payment) => Ok(Json.toJson(payment))
        case _ => NotFound
      }
  }

  def getPaymentByAmlsRef(accountType: String, ref: String, amlsReference: String) = authAction.async {
    implicit request =>
      paymentService.getPaymentByAmlsReference(amlsReference) map {
        case Some(payment) => Ok(Json.toJson(payment))
        case _ => NotFound
      }
  }

  def updateBacsFlag(accountType: String, ref: String, paymentReference: String) = authAction.async(bodyParsers.json) {
    implicit request =>
      val processBody = for {
        bacsRequest <- OptionT.fromOption[Future](request.body.asOpt[SetBacsRequest])
        payment <- OptionT(paymentService.getPaymentByPaymentReference(paymentReference))
        _ <- OptionT.liftF(paymentService.updatePayment(payment.copy(isBacs = Some(bacsRequest.isBacs))))
      } yield NoContent

      processBody getOrElse NotFound
  }

  def refreshStatus(accountType: String, ref: String) = authAction.async(bodyParsers.json) {
    implicit request =>
      request.body.asOpt[RefreshPaymentStatusRequest] map { r =>
        paymentService.refreshStatus(r.paymentReference).value map {
          case Some(result) => Ok(Json.toJson(result))
          case _ => NotFound
        }
      } getOrElse Future.successful(BadRequest)
  }
}
