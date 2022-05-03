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

package models.fe

import models.des.{SubscriptionResponse => DesSubscriptionResponse}
import play.api.libs.json._

case class SubscriptionResponse(
                                 etmpFormBundleNumber: String,
                                 amlsRefNo: String,
                                 addedResponsiblePeople: Int = 0,
                                 addedResponsiblePeopleFitAndProper: Int = 0,
                                 addedResponsiblePeopleApprovalCheck: Int = 0,
                                 premiseFYNumber: Int = 0,
                                 subscriptionFees: Option[SubscriptionFees],
                                 previouslySubmitted: Boolean = false
                               )

object SubscriptionResponse {
  implicit val format = Json.format[SubscriptionResponse]

  def convert(desResponse: DesSubscriptionResponse): SubscriptionResponse = {
      SubscriptionResponse(desResponse.etmpFormBundleNumber,
        desResponse.amlsRefNo,
        desResponse.fpNumbers.getOrElse(0) + desResponse.approvalCheckNumbers.getOrElse(0),
        desResponse.fpNumbers.getOrElse(0),
        desResponse.approvalCheckNumbers.getOrElse(0),
        desResponse.premiseFYNumber.getOrElse(0),
        Some(SubscriptionFees(desResponse.paymentReference,
          desResponse.registrationFee.getOrElse(0),
          desResponse.fpFee,
          desResponse.fpFeeRate,
          desResponse.premiseFee,
          desResponse.premiseFeeRate,
          desResponse.totalFees,
          desResponse.approvalCheckFeeRate,
          desResponse.approvalCheckFee)
        )
      )
  }
}
