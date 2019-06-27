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

import javax.inject.{Inject, Singleton}
import models.Fees
import play.api.Logger
import play.api.libs.json.Json
import repositories.FeesRepository
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.AuthAction

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class FeeResponseController @Inject()(
                                       implicit val repository: FeesRepository,
                                       authAction: AuthAction
                                     ) extends BaseController {

  def get(accountType: String, ref: String, amlsRegistrationNumber: String) =
    authAction.async {
      implicit request => {
        repository.findLatestByAmlsReference(amlsRegistrationNumber) map {
          case Some(feeResponse) => {
            Logger.debug(s"[FeeResponseController - get : ${Json.toJson(feeResponse)}]")
            Ok(Json.toJson[Fees](feeResponse))
          }
          case None => NotFound
        }
      }.recoverWith {
        case e:Throwable => {
          Logger.error(s"[FeeResponseController - get] ",e)
          Future.successful(InternalServerError)
        }
      }

    }
}
