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

package models.des.businessactivities

import play.api.libs.json.{Json, OFormat}
import models.fe.businessactivities._

case class FranchiseDetails(isBusinessAFranchise: Boolean, franchiserName: Option[Seq[String]])

object FranchiseDetails {
  implicit val format: OFormat[FranchiseDetails] = Json.format[FranchiseDetails]

  implicit def convert(franchise: Option[BusinessFranchise]): Option[FranchiseDetails] =
    franchise match {
      case Some(BusinessFranchiseNo)     => Some(FranchiseDetails(false, None))
      case Some(BusinessFranchiseYes(x)) => Some(FranchiseDetails(true, Some(Seq(x))))
      case None                          => None
    }
}
