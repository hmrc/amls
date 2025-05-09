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

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneOffset}

case class ReadStatusResponse(
  processingDate: LocalDateTime,
  formBundleStatus: String,
  statusReason: Option[String],
  deRegistrationDate: Option[LocalDate],
  currentRegYearStartDate: Option[LocalDate],
  currentRegYearEndDate: Option[LocalDate],
  renewalConFlag: Boolean,
  renewalSubmissionFlag: Option[Boolean] = None,
  currentAMLSOutstandingBalance: Option[String] = None,
  businessContactNumber: Option[String] = None,
  safeId: Option[String] = None
) {

  def isRenewalPeriod(dateTime: LocalDate = LocalDate.now()) = {

    val renewalWindow = 30

    currentRegYearEndDate match {
      case Some(endDate) => !dateTime.atStartOfDay().isAfter(endDate.atStartOfDay().minusDays(renewalWindow))
      case _             => false
    }

  }

}

object ReadStatusResponse {

  val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC)

  implicit val readsLocalDateTime: Reads[LocalDateTime] = Reads[LocalDateTime](js =>
    js.validate[String].map[LocalDateTime](dtString => LocalDateTime.parse(dtString, dateTimeFormat))
  )

  implicit val localDateTimeWrite: Writes[LocalDateTime] = new Writes[LocalDateTime] {
    def writes(dateTime: LocalDateTime): JsValue = JsString(dateTimeFormat.format(dateTime.atOffset(ZoneOffset.UTC)))
  }

  implicit val format: OFormat[ReadStatusResponse] = Json.format[ReadStatusResponse]

}
