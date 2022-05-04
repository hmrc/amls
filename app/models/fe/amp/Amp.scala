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

package models.fe.amp

import models.des.SubscriptionView
import models.des.businessactivities.AmpServices
import play.api.libs.json._
import utils.CommonMethods

final case class Amp(data: AmpData)

object Amp  {

  implicit val format = Json.format[Amp]

  implicit def conv(view: SubscriptionView): Option[Amp] = {
    view match {
      case SubscriptionView(_,_,_,_,_,_, ba,_,_,_,_,_,_,_,_,_,_,_, Some(amp), _, _) => Some(
        Amp(AmpData(
          view.businessActivities.ampServicesCarriedOut,
          ba.ampServicesCarriedOut.flatMap(s => s.other.specifyOther),
          amp.transactionsAccptOvrThrshld.transactionsAccptOvrThrshldAnswer,
          amp.transactionsAccptOvrThrshld.dateOfTheFirst,
          amp.sysAutoIdOfLinkedTransactions,
          getPercentage(amp.ampPercentageTurnover)))
      )
      case _ => None
    }
  }

  def getPercentage(percentage:Int): Option[String] = {
    percentage match {
      case 20 => Some("zeroToTwenty")
      case 40 => Some("twentyOneToForty")
      case 60 => Some("fortyOneToSixty")
      case 80 => Some("sixtyOneToEighty")
      case 100 => Some("eightyOneToOneHundred")
      case _ => None
    }
  }

  private implicit def conv(services: Option[AmpServices]): List[String] = {
    val optionList = services match {
      case Some(amp) => List(
        CommonMethods.getSpecificType(amp.artGallery, "artGalleryOwner"),
        CommonMethods.getSpecificType(amp.privateDealer, "artDealer"),
        CommonMethods.getSpecificType(amp.intermediary, "artAgent"),
        CommonMethods.getSpecificType(amp.auctionHouse, "artAuctioneer"),
        CommonMethods.getSpecificType(amp.other.otherAnswer, "somethingElse"))
      case None => List()
    }

    optionList.flatten
  }
}
