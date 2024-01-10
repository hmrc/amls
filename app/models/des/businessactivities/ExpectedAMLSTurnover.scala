/*
 * Copyright 2023 HM Revenue & Customs
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

import models.fe.businessactivities.{InvolvedInOtherNo, InvolvedInOtherYes}
import play.api.libs.json.{Json, OFormat}

case class OtherBusinessActivities(otherBusinessActivities: String, anticipatedTotBusinessTurnover: String, mlrActivityTurnover: String)

object OtherBusinessActivities {
  implicit val format: OFormat[OtherBusinessActivities] = Json.format[OtherBusinessActivities]
}

case class ExpectedAMLSTurnover(mlrActivityTurnover: Option[String] = None, otherBusActivitiesCarriedOut: Option[OtherBusinessActivities] = None)

//noinspection ScalaStyle
object ExpectedAMLSTurnover {
  implicit val format: OFormat[ExpectedAMLSTurnover] = Json.format[ExpectedAMLSTurnover]

  implicit def convert(bact: models.fe.businessactivities.BusinessActivities): Option[ExpectedAMLSTurnover] = {
    bact.involvedInOther match {
      case Some(InvolvedInOtherNo) => bact.expectedAMLSTurnover match {
        case Some(aMLSTurnover) => Some(ExpectedAMLSTurnover(convertAMLSTurnover(aMLSTurnover)))
        case None => None
      }
      case Some(InvolvedInOtherYes(x)) => Some(ExpectedAMLSTurnover(otherBusActivitiesCarriedOut = Some(
        OtherBusinessActivities(x, convertTurnover(bact.expectedBusinessTurnover), bact.expectedAMLSTurnover.fold("")(
          x => convertAMLSTurnover(x).getOrElse(""))))
      ))
      case _ => None
    }
  }

  def convertAMLSTurnover(to: models.fe.businessactivities.ExpectedAMLSTurnover): Option[String] = {
    import models.fe.businessactivities.ExpectedAMLSTurnover.{Fifth, First, Fourth, Second, Seventh, Sixth, Third}

    to match {
      case First => Some("£0-£15k")
      case Second => Some("£15k-50k")
      case Third => Some("£50k-£100k")
      case Fourth => Some("£100k-£250k")
      case Fifth => Some("£250k-£1m")
      case Sixth => Some("£1m-10m")
      case Seventh => Some("£10m+")
      case _ => None
    }
  }

  def convertTurnover(to: Option[models.fe.businessactivities.ExpectedBusinessTurnover]): String = {
    import models.fe.businessactivities.ExpectedBusinessTurnover.{Fifth, First, Fourth, Second, Seventh, Sixth, Third}

    to match {
      case Some(First) => "£0-£15k"
      case Some(Second) => "£15k-50k"
      case Some(Third) => "£50k-£100k"
      case Some(Fourth) => "£100k-£250k"
      case Some(Fifth) => "£250k-£1m"
      case Some(Sixth) => "£1m-10m"
      case Some(Seventh) => "£10m+"
      case None => ""
    }
  }

}
