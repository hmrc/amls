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

package models.des

import play.api.libs.json.Reads

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
                            amp: Boolean = false,
                            responsiblePersons: Boolean = false,
                            filingIndividual: Boolean = false
                           )

object ChangeIndicators {

  implicit def format = {

      import play.api.libs.functional.syntax._
      import play.api.libs.json.Reads._
      import play.api.libs.json._
      val jsonReads: Reads[ChangeIndicators] =       (
        (__ \ "businessDetails").read[Boolean] and
          (__ \ "businessAddress").read[Boolean] and
          (__ \ "businessReferences").read[Boolean] and
          (__ \ "tradingPremises").read[Boolean] and
          (__ \ "businessActivities").read[Boolean] and
          (__ \ "bankAccountDetails").read[Boolean] and
          (__ \ "msb" \ "msb").read[Boolean] and
          (__ \ "hvd" \ "hvd").read[Boolean] and
          (__ \ "asp" \ "asp").read[Boolean] and
          (__ \ "aspOrTcsp" \ "aspOrTcsp").read[Boolean] and
          (__ \ "tcsp" \ "tcsp").read[Boolean] and
          (__ \ "eab" \ "eab").read[Boolean] and
          (__ \ "amp" \ "amp").read[Boolean] and
          (__ \ "responsiblePersons").read[Boolean] and
          (__ \ "filingIndividual").read[Boolean]
        ) (ChangeIndicators.apply _)
      val jsonWrites: Writes[ChangeIndicators] = (
        (__ \ "businessDetails").write[Boolean] and
          (__ \ "businessAddress").write[Boolean] and
          (__ \ "businessReferences").write[Boolean] and
          (__ \ "tradingPremises").write[Boolean] and
          (__ \ "businessActivities").write[Boolean] and
          (__ \ "bankAccountDetails").write[Boolean] and
          (__ \ "msb" \ "msb").write[Boolean] and
          (__ \ "hvd" \ "hvd").write[Boolean] and
          (__ \ "asp" \ "asp").write[Boolean] and
          (__ \ "aspOrTcsp" \ "aspOrTcsp").write[Boolean] and
          (__ \ "tcsp" \ "tcsp").write[Boolean] and
          (__ \ "eab" \ "eab").write[Boolean] and
          (__ \ "amp" \ "amp").write[Boolean] and
          (__ \ "responsiblePersons").write[Boolean] and
          (__ \ "filingIndividual").write[Boolean])(unlift(ChangeIndicators.unapply _))

      Format(jsonReads,jsonWrites)
    }
}
