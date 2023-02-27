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

package utils

import models.des.{AmendVariationRequest, SubscriptionView}
import models.des.supervision.{AspOrTcsp, SupervisionDetails, SupervisorDetails}

trait DateOfChangeUpdateHelper {

  def updateWithHvdDateOfChangeFlag(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {
    val hvdWithDateOfChangeFlag = desRequest.hvd.fold(false)(!_.dateOfTheFirst.equals(viewResponse.hvd.fold[Option[String]](None)(_.dateOfTheFirst)))
    val hvdWithDateOfChange = desRequest.hvd match {
      case Some(hvd) => Some(hvd.copy(dateChangeFlag = Some(hvdWithDateOfChangeFlag)))
      case _ => None
    }
    desRequest.copy(
      hvd = hvdWithDateOfChange
    )
  }

  def updateWithSupervisorDateOfChangeFlag(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {

    def getSupervisorDetails(aspOrTcspOpt: Option[AspOrTcsp]) = for {
      aspOrTcsp <- aspOrTcspOpt
      supervisionDetails <- aspOrTcsp.supervisionDetails
      supervisorDetails <- supervisionDetails.supervisorDetails
    } yield supervisorDetails

    def getSupervisorStartDate(supervisor: Option[SupervisorDetails]) = supervisor.map(_.supervisionStartDate)

    val supervisorFromDesRequest = getSupervisorStartDate(getSupervisorDetails(desRequest.aspOrTcsp))
    val supervisorFromViewResponse = getSupervisorStartDate(getSupervisorDetails(viewResponse.aspOrTcsp))

    val supervisorDateChangeFlag = (supervisorFromDesRequest, supervisorFromViewResponse) match{
      case (Some(cached), Some(request)) => !cached.equals(request)
      case _ => false
    }
    val supervisorWithDateChangeFlag = getSupervisorDetails(desRequest.aspOrTcsp) match {
      case Some(supervision) => Some(supervision.copy(dateChangeFlag = Some(supervisorDateChangeFlag)))
      case _ => None
    }
    desRequest.copy(
      aspOrTcsp = desRequest.aspOrTcsp.fold[Option[AspOrTcsp]](None) { at =>
        Some(at.copy(supervisionDetails = at.supervisionDetails.fold[Option[SupervisionDetails]](None){ sd =>
          Some(sd.copy(supervisorDetails = supervisorWithDateChangeFlag))
        }))
      }
    )
  }

  def updateWithBusinessActivitiesDateOfChangeFlag(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {
    val businessActivitiesCommenceDateChangeFlag = desRequest.businessActivities.all.fold(false){
      !_.activitiesCommenceDate.equals(viewResponse.businessActivities.all.fold[Option[String]](None)(_.activitiesCommenceDate))
    }
    val businessActivitiesWithFlag = desRequest.businessActivities.all match {
      case Some(all) => Some(all.copy(dateChangeFlag = Some(businessActivitiesCommenceDateChangeFlag)))
      case _ => None
    }
    desRequest.copy(
      businessActivities = desRequest.businessActivities.copy(all = businessActivitiesWithFlag)
    )
  }

}
