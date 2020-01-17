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

package models.fe.asp

import models.des.businessactivities.BusinessActivities
import play.api.data.validation.ValidationError
import play.api.libs.json._
import utils.CommonMethods

case class ServicesOfBusiness(services: Set[Service], dateOfChange: Option[String] = None)

sealed trait Service

case object Accountancy extends Service

case object PayrollServices extends Service

case object BookKeeping extends Service

case object Auditing extends Service

case object FinancialOrTaxAdvice extends Service

object Service {

  implicit val jsonServiceReads: Reads[Service] =
    Reads {
      case JsString("01") => JsSuccess(Accountancy)
      case JsString("02") => JsSuccess(PayrollServices)
      case JsString("03") => JsSuccess(BookKeeping)
      case JsString("04") => JsSuccess(Auditing)
      case JsString("05") => JsSuccess(FinancialOrTaxAdvice)
      case _ => JsError((JsPath \ "services") -> JsonValidationError("error.invalid"))
    }

  implicit val jsonServiceWrites = Writes[Service] {
      case Accountancy => JsString("01")
      case PayrollServices => JsString("02")
      case BookKeeping => JsString("03")
      case Auditing => JsString("04")
      case FinancialOrTaxAdvice => JsString("05")
  }
}

object ServicesOfBusiness {
  implicit val formats = Json.format[ServicesOfBusiness]

  implicit def conv(ba: BusinessActivities): Option[ServicesOfBusiness] = {
    val services: Option[Set[Service]] = ba.aspServicesOffered match {
      case Some(services) => Some(Set(
          CommonMethods.getSpecificType[Service](services.accountant, Accountancy),
          CommonMethods.getSpecificType[Service](services.payrollServiceProvider, PayrollServices),
          CommonMethods.getSpecificType[Service](services.bookKeeper, BookKeeping),
          CommonMethods.getSpecificType[Service](services.auditor, Auditing),
          CommonMethods.getSpecificType[Service](services.financialOrTaxAdvisor, FinancialOrTaxAdvice)
        ).flatten)
      case None => None
    }
    services.map(ServicesOfBusiness(_))
  }

}
