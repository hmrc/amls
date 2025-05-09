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

package models.des.aboutthebusiness

import models.fe.businessdetails.{BusinessDetails, PreviouslyRegisteredNo, PreviouslyRegisteredYes}
import play.api.libs.json.{Json, OFormat}

case class PreviouslyRegisteredMLR(
  amlsRegistered: Boolean,
  mlrRegNumber8Long: Option[String],
  prevRegForMlr: Boolean,
  prevMlrRegNumber: Option[String]
)

object PreviouslyRegisteredMLR {
  implicit val format: OFormat[PreviouslyRegisteredMLR] = Json.format[PreviouslyRegisteredMLR]

  implicit def convert(businessDetails: BusinessDetails): Option[PreviouslyRegisteredMLR] =
    businessDetails.previouslyRegistered match {
      case x: PreviouslyRegisteredYes if x.value.getOrElse("").length == 15 =>
        Some(PreviouslyRegisteredMLR(false, None, true, x.value))
      case x: PreviouslyRegisteredYes if x.value.getOrElse("").length == 8  =>
        Some(PreviouslyRegisteredMLR(true, x.value, false, None))

      case PreviouslyRegisteredYes(None) => Some(PreviouslyRegisteredMLR(true, None, false, None))
      case PreviouslyRegisteredNo        => Some(PreviouslyRegisteredMLR(false, None, false, None))
      case _                             => None
    }
}
