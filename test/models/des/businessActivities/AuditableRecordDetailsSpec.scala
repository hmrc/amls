/*
 * Copyright 2022 HM Revenue & Customs
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

package models.des.businessActivities

import models.des.businessactivities.{AuditableRecordsDetails, TransactionRecordingMethod}
import models.fe.businessactivities.{DigitalSoftware, DigitalSpreadsheet, Paper, TransactionTypes, BusinessActivities => FEBusinessActivities}
import org.scalatestplus.play.PlaySpec

class AuditableRecordDetailsSpec extends PlaySpec {
  "TransactionRecord" must {
    "convertible to DES record" in {
      val FETransactionRecord = Some(FEBusinessActivities(
        transactionRecord = Some(true),
        transactionRecordTypes = Some(TransactionTypes(Set(Paper, DigitalSpreadsheet, DigitalSoftware("Value"))))
      ))

      val auditableRecordsDetails = AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, true, Some("Value"))))

      AuditableRecordsDetails.convert(FETransactionRecord.get) must be(auditableRecordsDetails)
    }

    "convertible to DES record without all records" in {
      val FETransactionRecord = Some(FEBusinessActivities(
        transactionRecord = Some(true),
        transactionRecordTypes = Some(TransactionTypes(Set(Paper, DigitalSpreadsheet)))
      ))

      val auditableRecordsDetails = AuditableRecordsDetails("Yes", Some(TransactionRecordingMethod(true, true, false, None)))

      AuditableRecordsDetails.convert(FETransactionRecord.get) must be(auditableRecordsDetails)
    }
  }
}
