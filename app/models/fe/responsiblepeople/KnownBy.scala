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

package models.fe.responsiblepeople

import models.des.responsiblepeople.OthrNamesOrAliasesDetails
import play.api.libs.json.{Json, OFormat}

case class KnownBy(hasOtherNames: Boolean, otherNames: Option[String] = None)

object KnownBy {
  implicit val format: OFormat[KnownBy] = Json.format[KnownBy]

  val noOtherNames = KnownBy(hasOtherNames = false)

  implicit def conv(desOtherNames: Option[OthrNamesOrAliasesDetails]): Option[KnownBy] =
    desOtherNames match {
      case Some(pName) =>
        pName.otherNamesOrAliases match {
          case true  =>
            pName.aliases match {
              case Some(name) => Some(KnownBy(hasOtherNames = true, Some(name.mkString(" "))))
              case None       => Some(noOtherNames)
            }
          case false => Some(noOtherNames)
        }
      case None        => Some(noOtherNames)
    }

}
