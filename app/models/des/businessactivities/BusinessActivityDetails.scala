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

package models.des.businessactivities

import models.fe.businessactivities.{InvolvedInOtherYes, InvolvedInOtherNo}
import play.api.libs.json.Json

case class BusinessActivityDetails(actvtsBusRegForOnlyActvtsCarOut: Boolean,
                                   respActvtsBusRegForOnlyActvtsCarOut: Option[ExpectedAMLSTurnover])

object BusinessActivityDetails{
  implicit val format = Json.format[BusinessActivityDetails]

  implicit def convert(bact : models.fe.businessactivities.BusinessActivities) : BusinessActivityDetails = {
    bact.involvedInOther match {
      case Some(InvolvedInOtherNo) => BusinessActivityDetails(true, ExpectedAMLSTurnover.convert(bact))
      case Some(InvolvedInOtherYes(x)) => BusinessActivityDetails(false, bact)
      case _ => BusinessActivityDetails(false, None)
    }
  }
}
