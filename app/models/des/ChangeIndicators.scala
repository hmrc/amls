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

package models.des

import config.AmlsConfig
import play.api.libs.json.Json

case class ChangeIndicators(businessDetails: Boolean = false,
                            businessAddress: Boolean = false,
                            businessReferences: Boolean = false,
                            tradingPremises: Boolean = false,
                            businessActivities: Boolean = false,
                            bankAccountDetails: Boolean = false,
                            msb: Boolean = false,
                            hvd: Boolean = false,
                            asp: Boolean = false,
                            aspOrTcsp: Boolean = false,
                            tcsp: Boolean = false,
                            eab: Boolean = false,
                            responsiblePersons: Boolean = false,
                            filingIndividual: Boolean = false
                           )

object ChangeIndicators {

  implicit def format = {

    if(!AmlsConfig.release7){
      Json.format[ChangeIndicators]
    }
    else {
      Json.format[ChangeIndicators]
    }
  }
}
