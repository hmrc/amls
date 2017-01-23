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

import connectors.{DESConnector, ViewDESConnector}
import models.des.{AmendVariationRequest, ChangeIndicators, SubscriptionView}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AmendVariationRequestUpdateHelper {
  val viewDesConnector: ViewDESConnector
}

object AmendVariationRequestUpdateHelper
  extends AmendVariationRequestUpdateHelper
    with ResponsiblePeopleUpdateHelper
    with TradingPremisesUpdateHelper
    with DateOfChangeUpdateHelper{

  override val viewDesConnector: ViewDESConnector = DESConnector

  private def view(amlsRegNo: String) = viewDesConnector.view(amlsRegNo)

  def updateWithEtmpFields(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {
    val etmpFields = desRequest.extraFields.setEtmpFields(viewResponse.extraFields.etmpFields)
    desRequest.setExtraFields(etmpFields)
  }

  def updateRequest(desRequest: AmendVariationRequest, viewResponse: SubscriptionView): AmendVariationRequest = {

    val updates: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest] = Set(
      updateWithEtmpFields,
      updateWithTradingPremises,
      updateWithResponsiblePeople,
      updateWithHvdDateOfChangeFlag,
      updateWithSupervisorDateOfChangeFlag,
      updateWithBusinessActivitiesDateOfChangeFlag
    )
    def update(request: AmendVariationRequest, updateF: Set[(AmendVariationRequest, SubscriptionView) => AmendVariationRequest]): AmendVariationRequest = {
      if(updateF.size < 1){
        request
      } else {
        if(updateF.size == 1){
          updateF.head(desRequest, viewResponse)
        } else {
          val updated = updateF.head(desRequest, viewResponse)
          update(updated, updateF.tail)
        }
      }
    }
    update(desRequest, updates)
  }

  def apply(desRequest: AmendVariationRequest, amlsRegNo: String): Future[AmendVariationRequest] = {
    view(amlsRegNo).map { viewResponse =>

      val updatedRequest = updateRequest(desRequest, viewResponse)

      updatedRequest.setChangeIndicator(ChangeIndicators(
        !viewResponse.businessDetails.equals(desRequest.businessDetails),
        !viewResponse.businessContactDetails.businessAddress.equals(desRequest.businessContactDetails.businessAddress),
        isBusinessReferenceChanged(viewResponse, desRequest),
        !viewResponse.tradingPremises.equals(desRequest.tradingPremises),
        !viewResponse.businessActivities.equals(desRequest.businessActivities),
        !viewResponse.bankAccountDetails.equals(desRequest.bankAccountDetails),
        !viewResponse.msb.equals(desRequest.msb),
        !viewResponse.hvd.equals(desRequest.hvd),
        !viewResponse.asp.equals(desRequest.asp),
        !viewResponse.aspOrTcsp.equals(desRequest.aspOrTcsp),
        isTcspChanged(viewResponse, desRequest),
        isEABChanged(viewResponse, desRequest),
        !viewResponse.responsiblePersons.equals(updatedDesRequestWithRp.responsiblePersons),
        !viewResponse.extraFields.filingIndividual.equals(desRequest.extraFields.filingIndividual)
      ))
    }
  }

}