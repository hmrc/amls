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

package models.des.responsiblepeople

import models.fe.responsiblepeople.ResponsiblePeople
import play.api.libs.json.Json

case class OthrNamesOrAliasesDetails (otherNamesOrAliases: Boolean,
                                      aliases:Option[Seq[String]])

object OthrNamesOrAliasesDetails {
  implicit val format = Json.format[OthrNamesOrAliasesDetails]

  def from(person: ResponsiblePeople): Option[OthrNamesOrAliasesDetails] = {
    person.knownBy match {
      case Some(name) if name.hasOtherNames => Some(OthrNamesOrAliasesDetails(true, Some(Seq(name.otherNames.get))))
      case _ => Some(OthrNamesOrAliasesDetails(false, None))
    }
  }
}
