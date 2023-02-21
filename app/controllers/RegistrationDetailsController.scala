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

import connectors.RegistrationDetailsDesConnector
import javax.inject.{Inject, Singleton}
import models.fe.registrationdetails.RegistrationDetails
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.{ApiRetryHelper, AuthAction}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class RegistrationDetailsController @Inject()(connector: RegistrationDetailsDesConnector,
                                              authAction: AuthAction,
                                              val cc: ControllerComponents)(implicit val apiRetryHelper: ApiRetryHelper) extends BackendController(cc) {

  private[controllers] val registrationDetailsConnector: RegistrationDetailsDesConnector = connector

  def get(accountType: String, ref: String, safeId: String) = authAction.async {
    implicit request =>
      registrationDetailsConnector.getRegistrationDetails(safeId) map { details =>
        Ok(Json.toJson(RegistrationDetails.convert(details)))
      }
  }
}
