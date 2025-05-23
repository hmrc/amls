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

package audit

import models.enrolment.{KnownFact => EnrolmentKnownFact, KnownFacts}
import models.{KnownFact, KnownFactsForService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions._
import uk.gov.hmrc.play.audit.model.DataEvent
import utils._

object KnownFactsEvent {
  def apply(knownFacts: KnownFactsForService)(implicit hc: HeaderCarrier): DataEvent = {

    val factsMap = knownFacts.facts.map { case KnownFact(key, value) =>
      (key, value)
    }.toMap

    DataEvent(
      auditSource = AuditHelper.appName,
      auditType = "OutboundCall",
      tags = hc.toAuditTags("AddKnownFacts", "N/A"),
      detail = hc.toAuditDetails() ++ factsMap
    )
  }

  def apply(knownFacts: KnownFacts)(implicit hc: HeaderCarrier): DataEvent = {

    val factsMap = knownFacts.verifiers.map { case EnrolmentKnownFact(key, value) =>
      (key, value)
    }.toMap

    DataEvent(
      auditSource = AuditHelper.appName,
      auditType = "OutboundCall",
      tags = hc.toAuditTags("AddKnownFacts", "N/A"),
      detail = hc.toAuditDetails() ++ factsMap
    )
  }
}
