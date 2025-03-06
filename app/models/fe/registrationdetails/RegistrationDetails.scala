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

package models.fe.registrationdetails

import models.des.registrationdetails.{Individual, Organisation, RegistrationDetails => DesRegistrationDetails}
import play.api.libs.json.{Json, OWrites}

case class RegistrationDetails(companyName: String, isIndividual: Boolean)

object RegistrationDetails {
  implicit val writes: OWrites[RegistrationDetails] = Json.writes[RegistrationDetails]

  implicit def convert(details: DesRegistrationDetails): RegistrationDetails = details match {
    case DesRegistrationDetails(x @ true, details: Individual)    =>
      RegistrationDetails(s"${details.firstName} ${details.lastName}", x)
    case DesRegistrationDetails(x @ false, details: Organisation) => RegistrationDetails(details.organisationName, x)
    case _                                                        => throw new RuntimeException(s"Invalid Organisation Body Details.")
  }
}
