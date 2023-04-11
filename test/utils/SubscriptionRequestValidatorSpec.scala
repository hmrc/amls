/*
 * Copyright 2023 HM Revenue & Customs
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

package utils

import models.des.{Declaration, DesConstants, SubscriptionRequest}
import models.des.businessdetails.{BusinessDetails, BusinessType, CorpAndBodyLlps}
import models.des.tradingpremises.{OwnBusinessPremises, TradingPremises}
import models.{DefaultDesValues, des}
import org.scalatest.EitherValues
import org.scalatestplus.play.PlaySpec

class SubscriptionRequestValidatorSpec extends PlaySpec with EitherValues {

  val validator = new SubscriptionRequestValidator

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  "Subscription request validator" must {
    "validate request" in {
      val jsResult = validator.validateRequest(givenSubscriptionRequest())
      jsResult.isLeft mustBe true
    }
  }

  private def givenSubscriptionRequest(): SubscriptionRequest = {
    SubscriptionRequest(
      acknowledgementReference = ackref.ackRef,
      businessDetails = BusinessDetails(BusinessType.LimitedCompany, Some(CorpAndBodyLlps("ABCDEFGHIJK ABCDE & LETTINGS LTD", "12345678")), None),
      businessActivities = DefaultDesValues.BusinessActivitiesSection,
      eabAll = DefaultDesValues.EabAllDetails,
      eabResdEstAgncy = DefaultDesValues.EabResd,
      businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
      businessReferencesAll = DefaultDesValues.PrevRegMLR,
      businessReferencesAllButSp = None,
      businessReferencesCbUbLlp = DefaultDesValues.CorpTaxRegime,
      tradingPremises = TradingPremises(Some(OwnBusinessPremises(true, Some(Seq(DesConstants.subscriptionRequestOwnBusinessPremisesDetails)))), None),
      bankAccountDetails = DefaultDesValues.bankDetailsSection,
      msb = DefaultDesValues.msbSection,
      hvd = DefaultDesValues.hvdSection,
      filingIndividual = DefaultDesValues.filingIndividual,
      tcspAll = DefaultDesValues.tcspAllSection,
      tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
      responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
      asp = DefaultDesValues.AspSection,
      amp = DefaultDesValues.AmpSection,
      aspOrTcsp = DefaultDesValues.AspOrTcspSection,
      declaration = Declaration(true),
      lettingAgents = None)
  }
}
