/*
 * Copyright 2018 HM Revenue & Customs
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

import models.payapi.CardTypes._
import models.payapi.PaymentStatuses._
import models.payapi.TaxTypes._
import models.payapi.{Card, Payment, Provider}
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

  def cardGen = for {
    cardType <- cardTypesGen
    creditCardCommission <- numGen
  } yield Card(
    cardType.head,
    Some(creditCardCommission)
  )

  def cardTypesGen = Gen.pick(1,
    `visa-debit`,
    `visa-credit`,
    `mastercard-debit`,
    `mastercard-credit`,
    `visa-electron`,
    `maestro`
  )

  def providerGen = alphaNumOfLengthGen(refLength).map(str => Provider(str, str))

  def now = LocalDateTime.now()

  def paymentStatusGen = Gen.pick(1,
    Created,
    Successful,
    Sent,
    Failed,
    Cancelled
  )

  val payApiPaymentGen: Gen[Payment] = for {
    _id <- hashGen
    amlsRefNo <- amlsRefNoGen
    taxType <- taxTypesGen
    ref <- paymentRefGen
    desc <- alphaNumOfLengthGen(refLength)
    amountInPence <- numGen
    commissionInPence <- numGen
    totalInPence <- numGen
    url <- alphaNumOfLengthGen(refLength)
    card <- cardGen
    provider <- providerGen
    paymentStatus <- paymentStatusGen
  } yield Payment(
    _id,
    Some(amlsRefNo),
    taxType.head,
    ref,
    desc,
    amountInPence,
    commissionInPence,
    totalInPence,
    url,
    Some(card),
    Map.empty,
    Some(provider),
    Some(now),
    paymentStatus.head
  )

}
