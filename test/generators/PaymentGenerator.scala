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

package generators

import java.time.LocalDateTime

import models.CardTypes._
import models.PaymentStatuses._
import models.TaxTypes._
import models.{Card, Payment, Provider}
import org.scalacheck.Gen

trait PaymentGenerator extends AmlsReferenceNumberGenerator{

  val strLength = 10
  val numLength = 4

  val paymentRefGen = alphaNumOfLengthGen(strLength - 1) map { ref => s"X$ref" }

  val paymentIdGen = alphaNumOfLengthGen(strLength)

  def alphaNumOfLengthGen(maxLength: Int) = {
    Gen.listOfN(maxLength, Gen.alphaNumChar).map(x => x.mkString)
  }

  def numGen = Gen.chooseNum(0,1000)

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

  def providerGen = alphaNumOfLengthGen(strLength).map(str => Provider(str, str))

  def now = LocalDateTime.now()

  def paymentStatusGen = Gen.pick(1,
    Created,
    Successful,
    Sent,
    Failed,
    Cancelled
  )

  val paymentGen: Gen[Payment] = for {
    _id <- paymentIdGen
    amlsRefNo <- amlsRefNoGen
    taxType <- taxTypesGen
    ref <- paymentRefGen
    desc <- alphaNumOfLengthGen(strLength)
    amountInPence <- numGen
    commissionInPence <- numGen
    totalInPence <- numGen
    url <- alphaNumOfLengthGen(strLength)
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
    paymentStatus.head,
    Some(now)
  )

}
