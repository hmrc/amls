/*
 * Copyright 2021 HM Revenue & Customs
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

package models.fe.moneyservicebusiness

import models.des.msb.{MsbAllDetails, MsbCeDetailsR7, MsbFxDetails, MsbMtDetails, MoneyServiceBusiness => DesMoneyServiceBusiness}
import play.api.libs.json._

case class MoneyServiceBusiness(
                                 throughput : Option[ExpectedThroughput] = None,
                                 businessUseAnIPSP: Option[BusinessUseAnIPSP] = None,
                                 identifyLinkedTransactions: Option[IdentifyLinkedTransactions] = None,
                                 sendMoneyToOtherCountry: Option[SendMoneyToOtherCountry] = None,
                                 fundsTransfer : Option[FundsTransfer] = None,
                                 branchesOrAgents: Option[BranchesOrAgents] = None,
                                 transactionsInNext12Months: Option[TransactionsInNext12Months] = None,
                                 ceTransactionsInNext12Months: Option[CETransactionsInNext12Months] = None,
                                 sendTheLargestAmountsOfMoney: Option[SendTheLargestAmountsOfMoney] = None,
                                 mostTransactions: Option[MostTransactions] = None,
                                 whichCurrencies : Option[WhichCurrencies] = None,
                                 fxTransactionsInNext12Months: Option[FXTransactionsInNext12Months] = None
                               )

object MoneyServiceBusiness {

  val key = "msb"

  implicit val format =  Json.format[MoneyServiceBusiness]

  implicit def default(value : Option[MoneyServiceBusiness]) :  MoneyServiceBusiness = {
    value.getOrElse(MoneyServiceBusiness())
  }

  implicit def conv(desView: models.des.SubscriptionView): Option[MoneyServiceBusiness] = {
    desView.msb match {
      case Some(msb) =>  Some(MoneyServiceBusiness(
        throughput = getMsbAll(desView.msb),
        businessUseAnIPSP = getMsbMtDetails(desView.msb),
        identifyLinkedTransactions = getMsbAll(desView.msb),
        sendMoneyToOtherCountry = getMsbMtDetails(desView.msb),
        fundsTransfer = getMsbMtDetails(desView.msb),
        branchesOrAgents = getMsbAll(desView.msb),
        transactionsInNext12Months = getMsbMtDetails(desView.msb),
        ceTransactionsInNext12Months = getMsbCeDetails(desView.msb),
        sendTheLargestAmountsOfMoney =  getMsbMtDetails(desView.msb),
        mostTransactions = getMsbMtDetails(desView.msb),
        whichCurrencies = getMsbCeDetails(desView.msb),
        fxTransactionsInNext12Months = getMsbFxDetails(desView.msb)
      ))
      case _ => None
    }
  }


  implicit def getMsbAll(msb: Option[DesMoneyServiceBusiness]): Option[MsbAllDetails] = {
    msb flatMap (msbDtls => msbDtls.msbAllDetails)
  }

  implicit def getMsbMtDetails(msb: Option[DesMoneyServiceBusiness]): Option[MsbMtDetails] = {
    msb flatMap (msbDtls => msbDtls.msbMtDetails)
  }

  implicit def getMsbCeDetails(msb: Option[DesMoneyServiceBusiness]): Option[MsbCeDetailsR7] = {
    msb flatMap (msbDtls => msbDtls.msbCeDetails)
  }

  implicit def getMsbFxDetails(msb: Option[DesMoneyServiceBusiness]): Option[MsbFxDetails] = {
    msb flatMap (msbDtls => msbDtls.msbFxDetails)
  }
}