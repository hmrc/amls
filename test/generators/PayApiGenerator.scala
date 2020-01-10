/*
 * Copyright 2020 HM Revenue & Customs
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

package generators

import java.time.LocalDateTime

import models.payapi.Payment
import models.payapi.PaymentStatuses._
import models.payapi.TaxTypes._
import org.scalacheck.Gen

trait PayApiGenerator extends BaseGenerator with AmlsReferenceNumberGenerator {
  val paymentRefGen = alphaNumOfLengthGen(refLength - 1) map { ref => s"X$ref" }
  val paymentIdGen = alphaNumOfLengthGen(refLength)

  def taxTypesGen = Gen.pick(1,
    `self-assessment`,
    `vat`,
    `epaye`,
    `p11d`,
    `stamp-duty`,
    `corporation-tax`,
    `other`
  )

  def now = LocalDateTime.now()

  def paymentStatusGen = Gen.pick(1,
    Created,
    Successful,
    Sent,
    Failed,
    Cancelled
  )

  val payApiPaymentGen: Gen[Payment] = for {
    id <- hashGen
    taxType <- taxTypesGen
    ref <- paymentRefGen
    desc <- alphaNumOfLengthGen(refLength)
    amountInPence <- numGen
    url <- alphaNumOfLengthGen(refLength)
    paymentStatus <- paymentStatusGen
  } yield Payment(
    id,
    taxType.head,
    ref,
    None,
    amountInPence,
    paymentStatus.head
  )

}
