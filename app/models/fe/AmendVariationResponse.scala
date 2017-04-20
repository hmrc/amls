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

package models.fe

import play.api.libs.json.Json
import models.des.{AmendVariationResponse => DesAmendVariationResponse}

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
                                   addedFullYearTradingPremises: Int = 0,
                                   halfYearlyTradingPremises: Int = 0,
                                   zeroRatedTradingPremises: Int = 0
                                 )

object AmendVariationResponse {

  implicit val format = Json.format[AmendVariationResponse]

  def convert(desResponse: DesAmendVariationResponse): AmendVariationResponse = {

    AmendVariationResponse(
      desResponse.processingDate,
      desResponse.etmpFormBundleNumber,
      desResponse.registrationFee.getOrElse(0),
      desResponse.fpFee,
      desResponse.fpFeeRate,
      desResponse.premiseFee.getOrElse(0),
      desResponse.premiseFYFeeRate,
      desResponse.totalFees.getOrElse(0),
      desResponse.paymentReference,
      desResponse.difference,
      desResponse.fpNumbers.getOrElse(0),
      desResponse.fpNumbers.getOrElse(0)-desResponse.fpNumbersNotCharged.getOrElse(0),
      desResponse.premiseFYNumber.getOrElse(0),
      desResponse.premiseHYNumber.getOrElse(0)
    )

  }

}
