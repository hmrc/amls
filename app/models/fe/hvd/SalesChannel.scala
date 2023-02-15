/*
 * Copyright 2023 HM Revenue & Customs
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

package models.fe.hvd

import play.api.libs.json._

sealed trait SalesChannel

case object Retail extends SalesChannel

case object Wholesale extends SalesChannel

case object Auction extends SalesChannel

object SalesChannel {
  implicit val jsonServiceReads: Reads[SalesChannel] =
    Reads {
      case JsString("Retail") => JsSuccess(Retail)
      case JsString("Wholesale") => JsSuccess(Wholesale)
      case JsString("Auction") => JsSuccess(Auction)
      case _ => JsError((JsPath \ "salesChannels") -> JsonValidationError("error.invalid"))
    }

  implicit val jsonServiceWrites =
    Writes[SalesChannel] {
      case Retail => JsString("Retail")
      case Wholesale => JsString("Wholesale")
      case Auction => JsString("Auction")
    }
}
