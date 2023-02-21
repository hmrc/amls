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

package models.des.supervision

import models.fe.supervision.{AnotherBodyNo, AnotherBodyYes, AnotherBody}
import play.api.libs.json.Json

case class SupervisionDetails(prevSupervisedByMlsRegs: Boolean = false, supervisorDetails: Option[SupervisorDetails])

object SupervisionDetails {

  implicit val format = Json.format[SupervisionDetails]

  implicit def conv(anotherBody: Option[AnotherBody]): Option[SupervisionDetails] = {

    anotherBody match {
      case Some(AnotherBodyYes(supervisorName, startDate, endDate, endingReason)) =>
        Some(SupervisionDetails(prevSupervisedByMlsRegs = true,supervisorDetails = Some(SupervisorDetails(nameOfLastSupervisor = supervisorName, supervisionStartDate = startDate.toString, supervisionEndDate = endDate.toString, dateChangeFlag = Some(false), supervisionEndingReason = endingReason))))
      case Some(AnotherBodyNo) => Some(SupervisionDetails(false, None))
      case _ => None
    }
  }

  implicit def conv1(anotherBody: Option[AnotherBody]): Option[SupervisionDetails] = {
    anotherBody match {
      case Some(AnotherBodyYes(supervisorName, startDate, endDate, endingReason)) =>
        Some(SupervisionDetails(prevSupervisedByMlsRegs = true, supervisorDetails = Some(SupervisorDetails(nameOfLastSupervisor = supervisorName, supervisionStartDate = startDate.toString, supervisionEndDate = endDate.toString, dateChangeFlag = None, supervisionEndingReason = endingReason))))
      case Some(AnotherBodyNo) => Some(SupervisionDetails(false, None))
      case _ => None
    }
  }
}
