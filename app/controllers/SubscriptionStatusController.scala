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

import connectors.SubscriptionStatusDESConnector
import exceptions.HttpStatusException
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.controller.{BackendController, BaseController}
import utils.{ApiRetryHelper, AuthAction}

import scala.concurrent.Future

@Singleton
class SubscriptionStatusController  @Inject()(ssConn: SubscriptionStatusDESConnector,
                                              authAction: AuthAction,
                                              val cc: ControllerComponents)(implicit val apiRetryHelper: ApiRetryHelper) extends BackendController(cc) {

  private[controllers] def connector: SubscriptionStatusDESConnector = ssConn

  val amlsRegNoRegex = "^X[A-Z]ML00000[0-9]{6}$".r
  val prefix = "[SubscriptionStatusController][get]"

  private def toError(message: String): JsObject =
    Json.obj(
      "errors" -> Seq(message)
    )

  def get(accountType: String, ref: String, amlsRegistrationNumber: String) =
    authAction.async {
      implicit request =>
        Logger.debug(s"$prefix - amlsRegNo: $amlsRegistrationNumber")
        amlsRegNoRegex.findFirstIn(amlsRegistrationNumber) match {
          case Some(_) =>
            connector.status(amlsRegistrationNumber) map {
              response =>
                Ok(Json.toJson(response))
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
