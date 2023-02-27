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

import connectors.ViewDESConnector
import exceptions.HttpStatusException

import javax.inject.{Inject, Singleton}
import models.fe.SubscriptionView
import play.api.{Logger, Logging}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.{ApiRetryHelper, AuthAction}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SubscriptionViewController @Inject()(vdc: ViewDESConnector,
                                           authAction: AuthAction,
                                           val cc: ControllerComponents)(implicit val apiRetryHelper: ApiRetryHelper) extends BackendController(cc) with Logging {

  private[controllers] def connector: ViewDESConnector = vdc

  val amlsRegNoRegex = "^X[A-Z]ML00000[0-9]{6}$".r
  val prefix = "[SubscriptionViewController][get]"

  private def toError(message: String): JsObject =
    Json.obj(
      "errors" -> Seq(message)
    )

  def view(accountType: String, ref: String, amlsRegistrationNumber: String) =
    authAction.async {
      implicit request =>
        logger.debug(s"$prefix - amlsRegNo: $amlsRegistrationNumber")
        amlsRegNoRegex.findFirstIn(amlsRegistrationNumber) match {
          case Some(_) =>
            connector.view(amlsRegistrationNumber) map {
              response =>
               val feModel:SubscriptionView = response
                val prefix = "[SubscriptionViewController][view]"
                logger.debug(s"$prefix model - $feModel")
                val json = Json.toJson(feModel)
                logger.debug(s"$prefix Json - $json")
                Ok(json)
            } recoverWith {
              case e@HttpStatusException(status, Some(body)) =>
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
