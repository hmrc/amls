/*
 * Copyright 2018 HM Revenue & Customs
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

package models.des.businessactivities

import models.fe.asp._
import play.api.libs.json.Json

case class AspServicesOffered (accountant: Boolean,
                               payrollServiceProvider : Boolean,
                               bookKeeper: Boolean,
                               auditor: Boolean,
                               financialOrTaxAdvisor:Boolean
                              )

object AspServicesOffered {

  implicit val format =  Json.format[AspServicesOffered]

  implicit def conv(asp: Option[Asp]) : Option[AspServicesOffered] = {
    asp match {
      case Some(data) => data.services.fold[Set[Service]](Set.empty)(x=> x.services)
      case _ => None
    }
  }

  implicit def conv1(svcs: Set[Service]) : Option[AspServicesOffered] = {
    val (accountant, payrollServiceProvider, bookKeeper,
    auditor, financialOrTaxAdvisor) = svcs.foldLeft[(Boolean, Boolean, Boolean, Boolean, Boolean)](false, false, false, false, false)((x, y) =>
      y match {
        case Accountancy => x.copy(_1 = true)
        case PayrollServices => x.copy(_2 = true)
        case BookKeeping => x.copy(_3 = true)
        case Auditing => x.copy(_4 = true)
        case FinancialOrTaxAdvice => x.copy(_5 = true)
      }
    )
     Some(AspServicesOffered(accountant, payrollServiceProvider, bookKeeper,
       auditor, financialOrTaxAdvisor ))
  }
}
