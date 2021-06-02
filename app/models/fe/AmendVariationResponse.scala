/*
 * Copyright 2021 HM Revenue & Customs
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

import models.des.{AmendVariationRequest, StatusProvider, AmendVariationResponse => DesAmendVariationResponse}
import play.api.libs.json.Json

case class AmendVariationResponse(
                                   processingDate: String,
                                   etmpFormBundleNumber: String,
                                   registrationFee: BigDecimal,
                                   fpFee: Option[BigDecimal],
                                   fpFeeRate: Option[BigDecimal],
                                   premiseFee: BigDecimal,
                                   premiseFeeRate: Option[BigDecimal],
                                   totalFees: BigDecimal,
                                   paymentReference: Option[String],
                                   difference: Option[BigDecimal],
                                   addedResponsiblePeople: Int = 0,
                                   addedResponsiblePeopleFitAndProper: Int = 0,
                                   addedResponsiblePeopleApprovalCheck: Int = 0,
                                   addedFullYearTradingPremises: Int = 0,
                                   halfYearlyTradingPremises: Int = 0,
                                   zeroRatedTradingPremises: Int = 0,
                                   approvalCheckNumbers: Option[Int] = None,
                                   approvalCheckFeeRate: Option[BigDecimal] = None,
                                   approvalCheckFee: Option[BigDecimal] = None
                                 )

object AmendVariationResponse {

  implicit val format = Json.format[AmendVariationResponse]

  def convert(request: AmendVariationRequest, isRenewalPeriod: Boolean, des: DesAmendVariationResponse): AmendVariationResponse = {

    def detailsMatch[T](seqOption: Option[Seq[T]])(implicit statusProvider: StatusProvider[T]) = {

      def statusMatch(status: Option[String]) = status match {
        case Some(st) if st equals "Added" => true
        case None => true
        case _ => false
      }

      seqOption match {
        case Some(contained) => contained count {
          detail => statusMatch(statusProvider.getStatus(detail))
        }
        case _ => 0
      }
    }

    val zeroRated = {
      val addedOwnBusinessTradingPremisesCount = request.tradingPremises.ownBusinessPremises match {
        case Some(ownBusinessPremises) => detailsMatch(ownBusinessPremises.ownBusinessPremisesDetails)
        case None => 0
      }
      val addedAgentTradingPremisesCount = request.tradingPremises.agentBusinessPremises match {
        case Some(agentBusinessPremises) => detailsMatch(agentBusinessPremises.agentDetails)
        case None => 0
      }

      des.premiseHYNumber.getOrElse(0) + des.premiseFYNumber.getOrElse(0) - addedOwnBusinessTradingPremisesCount - addedAgentTradingPremisesCount
    }

    AmendVariationResponse(
      processingDate = des.processingDate,
      etmpFormBundleNumber = des.etmpFormBundleNumber,
      registrationFee = des.registrationFee.getOrElse(0),
      fpFee = des.fpFee,
      fpFeeRate = des.fpFeeRate,
      premiseFee = des.premiseFee.getOrElse(0),
      premiseFeeRate = des.premiseFYFeeRate,
      totalFees = des.totalFees.getOrElse(0),
      paymentReference = des.paymentReference,
      difference = des.difference,
      addedResponsiblePeople = des.fpNumbers.getOrElse(0) + des.approvalCheckNumbers.getOrElse(0),
      addedResponsiblePeopleFitAndProper = des.fpNumbers.getOrElse(0),
      addedResponsiblePeopleApprovalCheck = des.approvalCheckNumbers.getOrElse(0),
      addedFullYearTradingPremises = des.premiseFYNumber.getOrElse(0),
      halfYearlyTradingPremises = des.premiseHYNumber.getOrElse(0),
      zeroRatedTradingPremises = if (isRenewalPeriod) 0 else zeroRated,
      approvalCheckNumbers = Some(des.approvalCheckNumbers.getOrElse(0)),
      approvalCheckFeeRate = Some(des.approvalCheckFeeRate.getOrElse(0)),
      approvalCheckFee = Some(des.approvalCheckFee.getOrElse(0))
    )
  }
}
