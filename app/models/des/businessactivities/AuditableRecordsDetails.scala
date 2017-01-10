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

package models.des.businessactivities

import models.fe.businessactivities._
import play.api.libs.json.Json


case class TransactionRecordingMethod(manual: Boolean = false,
                                      spreadsheet: Boolean = false,
                                      commercialPackage: Boolean = false,
                                      commercialPackageName: Option[String] = None)

object TransactionRecordingMethod {
  implicit val format = Json.format[TransactionRecordingMethod]

  implicit def convert(record: Set[TransactionType]): Option[TransactionRecordingMethod] = {
    val (digiTrType, value) = record.foldLeft[(Boolean, Option[String])](false, None) {
      case (m, DigitalSoftware(str)) =>
        (true, Some(str))
      case (m, _) =>
        m
    }
    Some(TransactionRecordingMethod(record.contains(Paper), record.contains(DigitalSpreadsheet), digiTrType, value))
  }
}

case class AuditableRecordsDetails(detailedRecordsKept: String,
                                   transactionRecordingMethod: Option[TransactionRecordingMethod] = None)

object AuditableRecordsDetails {
  implicit val format = Json.format[AuditableRecordsDetails]

  implicit def convert(record: Option[TransactionRecord]): AuditableRecordsDetails = {
    record match {
      case Some(TransactionRecordYes(x)) => AuditableRecordsDetails("Yes", x)
      case _ => AuditableRecordsDetails("No")
    }
  }
}
