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

import play.api.libs.json.{Json, Reads}

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

  implicit val reads: Reads[AmendVariationResponse] = {
    import play.api.libs.functional.syntax._
    import play.api.libs.json._
    (
      (__ \ "processingDate").read[String] and
        (__ \ "etmpFormBundleNumber").read[String] and
        (__ \ "registrationFee").readNullable[BigDecimal] and
        (__ \ "fpFee").read(Reads.optionWithNull[BigDecimal]).orElse((__ \ "fPFee").read(Reads.optionWithNull[BigDecimal])).orElse(Reads.pure(None)) and
        (__ \ "premiseFee").readNullable[BigDecimal] and
        (__ \ "totalFees").readNullable[BigDecimal] and
        (__ \ "paymentReference").readNullable[String] and
        (__ \ "difference").readNullable[BigDecimal] and
        (__ \ "addedResponsiblePeople").readNullable[Int] and
        (__ \ "addedResponsiblePeopleFitAndProper").readNullable[Int] and
        (__ \ "addedFullYearTradingPremises").readNullable[Int] and
        (__ \ "halfYearlyTradingPremises").readNullable[Int] and
        (__ \ "zeroRatedTradingPremises").readNullable[Int]
      ) apply AmendVariationResponse.apply _
  }
}
