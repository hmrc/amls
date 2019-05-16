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

import connectors.{DESConnector, ViewDESConnector}
import exceptions.HttpStatusException
import javax.inject.{Inject, Singleton}
import models.fe.SubscriptionView
import play.api.Logger
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Action
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.ApiRetryHelper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SubscriptionViewController @Inject()(
  implicit val apiRetryHelper: ApiRetryHelper
) extends BaseController {

  private[controllers] def connector: ViewDESConnector = DESConnector

  val amlsRegNoRegex = "^X[A-Z]ML00000[0-9]{6}$".r
  val prefix = "[SubscriptionViewController][get]"

  private def toError(message: String): JsObject =
    Json.obj(
      "errors" -> Seq(message)
    )

  def view(accountType: String, ref: String, amlsRegistrationNumber: String) =
    Action.async {
      implicit request =>
        Logger.debug(s"$prefix - amlsRegNo: $amlsRegistrationNumber")
        amlsRegNoRegex.findFirstIn(amlsRegistrationNumber) match {
          case Some(_) =>
            connector.view(amlsRegistrationNumber) map {
              response =>
               val feModel:SubscriptionView = response
                val prefix = "[SubscriptionViewController][view]"
                Logger.debug(s"$prefix model - $feModel")
                val json = Json.toJson(feModel)
                Logger.debug(s"$prefix Json - $json")
                Ok(json)
            } recoverWith {
              case e@HttpStatusException(status, Some(body)) =>
                Logger.warn(s"$prefix - Status: ${status}, Message: $body")
                Future.failed(e)
            }

          case _ =>
            Future.successful {
              BadRequest(toError("Invalid AMLS Registration Number"))
            }
        }
    }
}
