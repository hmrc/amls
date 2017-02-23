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

package models.des

import play.api.libs.json.Json

case class AmendVariationResponse(
                                  processingDate: String,
                                  etmpFormBundleNumber: String,
                                  registrationFee: Option[BigDecimal] = Some(0),
                                  fpFee: Option[BigDecimal] = None,
                                  premiseFee: Option[BigDecimal] = Some(0),
                                  totalFees: Option[BigDecimal] = Some(0),
                                  paymentReference: Option[String] = None,
                                  difference: Option[BigDecimal] = None,
                                  addedResponsiblePeople: Option[Int] = Some(0),
                                  addedResponsiblePeopleFitAndProper: Option[Int] = Some(0),
                                  addedFullYearTradingPremises: Option[Int] = Some(0),
                                  halfYearlyTradingPremises: Option[Int] = Some(0),
                                  zeroRatedTradingPremises: Option[Int] = Some(0)
                                )

object AmendVariationResponse {
  implicit val format = Json.format[AmendVariationResponse]
}
