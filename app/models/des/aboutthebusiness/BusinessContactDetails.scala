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

package models.des.aboutthebusiness

import models.fe.businessdetails._
import play.api.libs.json.{Json, OFormat}

case class BusinessContactDetails(
  businessAddress: Address,
  altCorrespondenceAddress: Boolean,
  alternativeAddress: Option[AlternativeAddress] = None,
  businessTelNo: String,
  businessEmail: String
)

object BusinessContactDetails {
  implicit val format: OFormat[BusinessContactDetails] = Json.format[BusinessContactDetails]

  implicit def convert(businessDetails: BusinessDetails): BusinessContactDetails =
    BusinessContactDetails(
      businessAddress = businessDetails.registeredOffice,
      altCorrespondenceAddress = businessDetails.altCorrespondenceAddress,
      alternativeAddress = businessDetails.correspondenceAddress,
      businessTelNo = businessDetails.contactingYou.phoneNumber,
      businessEmail = businessDetails.contactingYou.email
    )
}
