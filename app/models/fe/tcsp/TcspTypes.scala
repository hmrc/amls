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

package models.fe.tcsp

import models.des.SubscriptionView
import play.api.libs.json._
import utils.CommonMethods

case class TcspTypes(serviceProviders: Set[ServiceProvider])

sealed trait ServiceProvider {
  val value: String =
    this match {
      case NomineeShareholdersProvider => "01"
      case TrusteeProvider => "02"
      case RegisteredOfficeEtc => "03"
      case CompanyDirectorEtc => "04"
      case CompanyFormationAgent => "05"
    }
}

case object NomineeShareholdersProvider extends ServiceProvider

case object TrusteeProvider extends ServiceProvider

case object RegisteredOfficeEtc extends ServiceProvider

case object CompanyDirectorEtc extends ServiceProvider

case object CompanyFormationAgent extends ServiceProvider

object TcspTypes {

  implicit val jsonReads: Reads[TcspTypes] = {
    import play.api.libs.json.Reads._
    import play.api.libs.json._
    (__ \ "serviceProviders").read[Set[String]].flatMap { x: Set[String] =>
      x.map {
        case "01" => Reads(_ => JsSuccess(NomineeShareholdersProvider)) map identity[ServiceProvider]
        case "02" => Reads(_ => JsSuccess(TrusteeProvider)) map identity[ServiceProvider]
        case "03" => Reads(_ => JsSuccess(RegisteredOfficeEtc)) map identity[ServiceProvider]
        case "04" => Reads(_ => JsSuccess(CompanyDirectorEtc)) map identity[ServiceProvider]
        case "05" => Reads(_ => JsSuccess(CompanyFormationAgent)) map identity[ServiceProvider]
        case _ =>
          Reads(_ => JsError((JsPath \ "serviceProviders") -> JsonValidationError("error.invalid")))
      }.foldLeft[Reads[Set[ServiceProvider]]](
        Reads[Set[ServiceProvider]](_ => JsSuccess(Set.empty))
      ) {
        (result, data) =>
          data flatMap { m =>
            result.map { n =>
              n + m
            }
          }
      } map TcspTypes.apply
    }
  }

  implicit val jsonWrite: Writes[TcspTypes] = Writes[TcspTypes] {
    case TcspTypes(services) =>
      Json.obj(
        "serviceProviders" -> (services map {
          _.value
        }).toSeq
      ) ++ services.foldLeft[JsObject](Json.obj()) {
        case (m, _) =>
          m
      }
  }

  implicit def conv(view: SubscriptionView): Option[TcspTypes] = {
    val serviceProviders: Option[Set[ServiceProvider]] = view.businessActivities.tcspServicesOffered match {
      case Some(svcsProviders) => Some(Set(
        CommonMethods.getSpecificType(svcsProviders.nomineeShareholders, NomineeShareholdersProvider),
        CommonMethods.getSpecificType(svcsProviders.trusteeProvider, TrusteeProvider),
        CommonMethods.getSpecificType(svcsProviders.regOffBusinessAddrVirtualOff, RegisteredOfficeEtc),
        CommonMethods.getSpecificType(svcsProviders.compDirSecPartnerProvider, CompanyDirectorEtc),
        CommonMethods.getSpecificType(svcsProviders.trustOrCompFormAgent, CompanyFormationAgent)
      ).flatten)
      case None => None
    }

    Some(TcspTypes(serviceProviders.getOrElse(Set())))
  }
}
