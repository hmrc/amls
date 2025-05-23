/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.libs.json._

case class SubscriptionResponse(
  etmpFormBundleNumber: String,
  amlsRefNo: String,
  registrationFee: Option[BigDecimal],
  fpFee: Option[BigDecimal],
  premiseFee: BigDecimal,
  totalFees: BigDecimal,
  paymentReference: String,
  fpNumbers: Option[Int] = None,
  fpFeeRate: Option[BigDecimal] = None,
  responsiblePersonNotCharged: Option[Int] = None,
  premiseFYNumber: Option[Int] = None,
  premiseFeeRate: Option[BigDecimal] = None,
  approvalCheckNumbers: Option[Int] = None,
  approvalCheckFeeRate: Option[BigDecimal] = None,
  approvalCheckFee: Option[BigDecimal] = None
)

object SubscriptionResponse {
  implicit val format: OFormat[SubscriptionResponse] = Json.format[SubscriptionResponse]
}
