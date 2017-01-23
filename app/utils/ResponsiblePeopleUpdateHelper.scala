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

package utils

import models.des.{AmendVariationRequest, SubscriptionView}
import models.des.responsiblepeople.{RPExtra, ResponsiblePersons}

trait ResponsiblePeopleUpdateHelper {

  def updateWithResponsiblePeople(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {
    desRequest.setResponsiblePersons(
      updatedRPExtraFields(viewResponse.responsiblePersons, desRequest.responsiblePersons)
    )
  }

  private def updateRpExtraField(desRp: ResponsiblePersons, viewRp: Seq[ResponsiblePersons]): ResponsiblePersons = {

    val responsiblePersonsFromView: ResponsiblePersons =  viewRp.find(x => x.extra.lineId.equals(desRp.extra.lineId)).getOrElse(None)

    val desRPExtra = desRp.extra.copy(
      retestFlag = responsiblePersonsFromView.extra.retestFlag,
      testResult = responsiblePersonsFromView.extra.testResult,
      testDate = responsiblePersonsFromView.extra.testDate
    )
    val desResponsiblePersons = desRp.copy(extra = desRPExtra)

    val updatedStatus = desResponsiblePersons.extra.status match {
      case Some(StatusConstants.Deleted) => StatusConstants.Deleted
      case _ => desResponsiblePersons.equals(responsiblePersonsFromView) match {
        case true => StatusConstants.Unchanged
        case false => StatusConstants.Updated
      }
    }

    val statusExtraField = desResponsiblePersons.extra.copy(status = Some(updatedStatus))
    desResponsiblePersons.copy(extra = statusExtraField)
  }

  private def updatedRPExtraFields(viewResponsiblePerson: Option[Seq[ResponsiblePersons]],
                           desResponsiblePerson: Option[Seq[ResponsiblePersons]]): Seq[ResponsiblePersons] = {
    (viewResponsiblePerson, desResponsiblePerson) match {
      case (Some(rp), Some(desRp)) => {
        val (withLineIds, withoutLineIds) = desRp.partition(_.extra.lineId.isDefined)
        val rpWithLineIds = withLineIds.map(updateRpExtraField(_, rp))
        val rpWithoutLineId = withoutLineIds.map(rp => rp.copy(extra = RPExtra(status = Some(StatusConstants.Added))))
        rpWithLineIds ++ rpWithoutLineId
      }
      case _ => desResponsiblePerson.fold[Seq[ResponsiblePersons]](Seq.empty)(x => x)
    }
  }

}
