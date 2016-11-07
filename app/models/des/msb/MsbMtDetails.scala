/*
 * Copyright 2016 HM Revenue & Customs
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

package models.des.msb

import models.fe.businessmatching.{BusinessAppliedForPSRNumberYes, BusinessAppliedForPSRNumberNo, BusinessAppliedForPSRNumber}
import play.api.libs.json.Json

case class MsbMtDetails (
                          applyForFcapsrRegNo: Boolean,
                          fcapsrRefNo: Option[String],
                          ipspServicesDetails: IpspServicesDetails,
                          informalFundsTransferSystem: Boolean,
                          noOfMoneyTrnsfrTransNxt12Mnths: Option[String],
                          countriesLrgstMoneyAmtSentTo: Option[CountriesList],
                          countriesLrgstTranscsSentTo: Option[CountriesList]
                        )

object MsbMtDetails {

  implicit val format = Json.format[MsbMtDetails]

  implicit def conv(msbNbmTuple:(models.fe.moneyservicebusiness.MoneyServiceBusiness, models.fe.businessmatching.BusinessMatching)) : Option[MsbMtDetails] = {
    val msb= msbNbmTuple._1
    val bm = msbNbmTuple._2
    val (largetAmount, largestTransaction) = msb.sendMoneyToOtherCountry.foldLeft[(Option[CountriesList], Option[CountriesList])](None, None)(
      (x, y)=> y.money match {
      case true =>  (msb.sendTheLargestAmountsOfMoney.fold[Seq[String]](Seq.empty)(x => Seq(Some(x.country_1), x.country_2, x.country_3).flatten),
        msb.mostTransactions.fold[Seq[String]](Seq.empty)(x => x.countries))
      case false =>(None, None)
    })
    val (applyForPsr, psrNumber) = convPsr(bm.businessAppliedForPSRNumber)

    Some(MsbMtDetails(applyForPsr,
      psrNumber,
      msb.businessUseAnIPSP,
      msb.fundsTransfer.fold(false)(x=>x.transferWithoutFormalSystems),
      msb.transactionsInNext12Months.fold[Option[String]](None)(x => Some(x.txnAmount)),
      largetAmount,
      largestTransaction
    ))
  }

  def convPsr(psr: Option[BusinessAppliedForPSRNumber]) : (Boolean, Option[String]) = {
    psr match {
      case Some(data) => data match {
        case BusinessAppliedForPSRNumberYes(number) => (true, Some(number))
        case BusinessAppliedForPSRNumberNo => (false, None)
      }
      case None  => (false, None)
    }
  }

}
