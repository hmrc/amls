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
import models.des.responsiblepeople.{RPExtra, ResponsiblePersons}

trait ResponsiblePeopleUpdateHelper {

  def updateWithResponsiblePeople(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {
    desRequest.setResponsiblePersons(
      compareAndUpdateRps(viewResponse.responsiblePersons, desRequest.responsiblePersons)
    )
  }

  private def updateExistingRp(desRp: ResponsiblePersons, viewResponsiblePersons: Seq[ResponsiblePersons]): ResponsiblePersons = {

    val rpOption = viewResponsiblePersons.find(x => x.extra.lineId.equals(desRp.extra.lineId))
    val viewRp: ResponsiblePersons = rpOption.getOrElse(None)

    val desRPExtra = desRp.extra.copy(
      retest = viewRp.extra.retest,
      testResultFitAndProper = viewRp.extra.testResultFitAndProper,
      testDateFitAndProper = viewRp.extra.testDateFitAndProper,
      testResultApprovalCheck = viewRp.extra.testResultApprovalCheck,
      testDateApprovalCheck = viewRp.extra.testDateApprovalCheck)

    val desResponsiblePeople = desRp.copy(extra = desRPExtra)

    val updatedStatus = desResponsiblePeople.extra.status.getOrElse(
      if (desResponsiblePeople.equals(viewRp)) {
      StatusConstants.Unchanged
    } else {
      StatusConstants.Updated
    })

    val statusExtraField = desResponsiblePeople.extra.copy(status = Some(updatedStatus))
    val updatedStatusRp = desResponsiblePeople.copy(extra = statusExtraField)
    updatedStatusRp.copy(nameDetails = updatedStatusRp.nameDetails map {
      nd =>
        nd.copy(previousNameDetails = nd.previousNameDetails map {
          pnd =>
            pnd.copy(dateChangeFlag = Some(pnd.dateOfChange != {
              for {
                nameDetails <- viewRp.nameDetails
                previousNameDetails <- nameDetails.previousNameDetails
                prevDateOfChange <- previousNameDetails.dateOfChange
              } yield prevDateOfChange
            }))
        })
    },
      dateChangeFlag = Some(updatedStatusRp.startDate !=
        viewRp.startDate
      ))

  }

  private def compareAndUpdateRps(viewResponsiblePerson: Option[Seq[ResponsiblePersons]],
                                  desResponsiblePerson: Option[Seq[ResponsiblePersons]]
                                 ): Seq[ResponsiblePersons] = {

    (viewResponsiblePerson, desResponsiblePerson) match {

      case (Some(rp), Some(desRp)) => {

        val (withLineIds, withoutLineIds) = desRp.partition(_.extra.lineId.isDefined)
        val rpWithLineIds = withLineIds.map(updateExistingRp(_, rp))
        val rpWithAddedStatus = withoutLineIds.map(rp => rp.copy(extra = RPExtra(status = Some(StatusConstants.Added))))
        val rpWithDateChangeFlags = rpWithAddedStatus.map(rp => rp.copy(nameDetails = rp.nameDetails map {
          nds =>
            nds.copy(previousNameDetails = nds.previousNameDetails map {
              pnd => pnd.copy(dateChangeFlag = Some(false))
            })
        }, dateChangeFlag = Some(false)))
        rpWithLineIds ++ rpWithDateChangeFlags
      }
      case _ => desResponsiblePerson.fold[Seq[ResponsiblePersons]](Seq.empty)(x => x)
    }
  }
}
