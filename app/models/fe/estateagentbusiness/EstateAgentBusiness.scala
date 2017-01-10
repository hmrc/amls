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

package models.fe.estateagentbusiness

case class EstateAgentBusiness(
                                services: Option[Services] = None,
                                redressScheme: Option[RedressScheme] = None,
                                professionalBody: Option[ProfessionalBody] = None,
                                penalisedUnderEstateAgentsAct: Option[PenalisedUnderEstateAgentsAct] = None
                              )

object EstateAgentBusiness {
  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val reads: Reads[EstateAgentBusiness] = (
    __.read(Reads.optionNoError[Services]) and
    __.read(Reads.optionNoError[RedressScheme]) and
      __.read(Reads.optionNoError[ProfessionalBody]) and
      __.read(Reads.optionNoError[PenalisedUnderEstateAgentsAct])
    ) (EstateAgentBusiness.apply _)

  implicit val writes: Writes[EstateAgentBusiness] =
    Writes[EstateAgentBusiness] {
      model =>
        Seq(
          Json.toJson(model.services).asOpt[JsObject],
          Json.toJson(model.redressScheme).asOpt[JsObject],
          Json.toJson(model.professionalBody).asOpt[JsObject],
          Json.toJson(model.penalisedUnderEstateAgentsAct).asOpt[JsObject]
        ).flatten.fold(Json.obj()) {
          _ ++ _
        }
    }

  implicit def conv(view: models.des.SubscriptionView): Option[EstateAgentBusiness] = {

      Some(EstateAgentBusiness(view.businessActivities.eabServicesCarriedOut,
        view.eabResdEstAgncy, view, view))

  }
}
