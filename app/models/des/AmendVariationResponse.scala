/*
 * Copyright 2018 HM Revenue & Customs
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

package models.des

import play.api.libs.json.Json

case class AmendVariationResponse(
                                   processingDate: String,
                                   etmpFormBundleNumber: String,
                                   registrationFee: Option[BigDecimal],
                                   fpNumbers: Option[Int],
                                   fpFeeRate: Option[BigDecimal],
                                   fpFee: Option[BigDecimal],
                                   responsiblePersonNotCharged: Option[Int],
                                   premiseFYNumber: Option[Int],
                                   premiseFYFeeRate: Option[BigDecimal],
                                   premiseFYTotal: Option[BigDecimal],
                                   premiseHYNumber: Option[Int],
                                   premiseHYFeeRate: Option[BigDecimal],
                                   premiseHYTotal: Option[BigDecimal],
                                   premiseFee: Option[BigDecimal],
                                   totalFees: Option[BigDecimal],
                                   paymentReference: Option[String],
                                   difference: Option[BigDecimal],
                                   approvalNumbers: Option[Int] = None,
                                   approvalCheckFeeRate: Option[BigDecimal] = None,
                                   approvalCheckFee: Option[BigDecimal] = None
                                 )

object AmendVariationResponse {

  implicit val format = Json.format[AmendVariationResponse]
}
