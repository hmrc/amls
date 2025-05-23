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

package models.des.amp

import play.api.libs.json.{Json, OFormat}

case class Amp(
  transactionsAccptOvrThrshld: TransactionsAccptOvrThrshld,
  sysAutoIdOfLinkedTransactions: Boolean,
  ampPercentageTurnover: Int
)

case class TransactionsAccptOvrThrshld(transactionsAccptOvrThrshldAnswer: Boolean, dateOfTheFirst: Option[String])

object Amp {

  implicit val format: OFormat[Amp] = Json.format[Amp]

  def getPercentage(percentage: Option[String]): Int =
    percentage match {
      case Some("zeroToTwenty")          => 20
      case Some("twentyOneToForty")      => 40
      case Some("fortyOneToSixty")       => 60
      case Some("sixtyOneToEighty")      => 80
      case Some("eightyOneToOneHundred") => 100
      case _                             => 0
    }

  implicit def conv(ampOpt: Option[models.fe.amp.Amp]): Option[Amp] =
    ampOpt.map(amp => amp.data).map { amp =>
      Amp(
        transactionsAccptOvrThrshld =
          TransactionsAccptOvrThrshld(amp.soldOverThreshold, amp.dateTransactionOverThreshold),
        sysAutoIdOfLinkedTransactions = amp.identifyLinkedTransactions,
        ampPercentageTurnover = getPercentage(amp.percentageExpectedTurnover)
      )
    }
}

object TransactionsAccptOvrThrshld {
  implicit val format: OFormat[TransactionsAccptOvrThrshld] = Json.format[TransactionsAccptOvrThrshld]
}
