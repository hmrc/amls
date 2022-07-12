/*
 * Copyright 2022 HM Revenue & Customs
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

package models.des.supervision

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class SupervisorDetails (nameOfLastSupervisor: String,
                              supervisionStartDate: String,
                              supervisionEndDate: String,
                              dateChangeFlag: Boolean = false,
                              supervisionEndingReason: String
                             )

object SupervisorDetails {

  implicit val jsonReads = {
    (
      (__ \ "nameOfLastSupervisor").read[String] and
        (__ \ "supervisionStartDate").read[String] and
        (__ \ "supervisionEndDate").read[String] and
        ((__ \ "dateChangeFlag").read[Boolean] or Reads.pure(false)) and
        (__ \ "supervisionEndingReason").read[String]
      ) (SupervisorDetails.apply _)
  }

  implicit def jsonWrites = Json.writes[SupervisorDetails]


}
