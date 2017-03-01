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

import play.api.libs.json._

case class SubscriptionResponse(
                                 etmpFormBundleNumber: String,
                                 amlsRefNo: String,
                                 registrationFee: Option[BigDecimal],
                                 fpFee: Option[BigDecimal],
                                 premiseFee: BigDecimal,
                                 totalFees: BigDecimal,
                                 paymentReference: String,
                                 premiseFeeRate: Option[BigDecimal] = None,
                                 fpFeeRate: Option[BigDecimal] = None
                               )

object SubscriptionResponse {
  implicit val reads = Json.reads[SubscriptionResponse]

  implicit val writes: Writes[SubscriptionResponse] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    (
      (__ \ "etmpFormBundleNumber").write[String] and
        (__ \ "amlsRefNo").write[String] and
        (__ \ "registrationFee").write[BigDecimal].contramap[Option[BigDecimal]](
          _ match {
            case None => 0
            case x => x.get
          }) and
        (__ \ "fpFee").writeNullable[BigDecimal] and
        (__ \ "premiseFee").write[BigDecimal] and
        (__ \ "totalFees").write[BigDecimal] and
        (__ \ "paymentReference").write[String] and
        (__ \ "premiseFeeRate").writeNullable[BigDecimal] and
        (__ \ "fpFeeRate").writeNullable[BigDecimal]
      ) (unlift(SubscriptionResponse.unapply _))
  }
}
