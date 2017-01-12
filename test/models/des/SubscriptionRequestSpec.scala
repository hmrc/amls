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

package models.des

import models._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{Json, JsUndefined}
import utils.AckRefGenerator

class SubscriptionRequestSpec extends PlaySpec with MockitoSugar {

  implicit val ackref = new AckRefGenerator {
    override def ackRef: String = "1234"
  }

  "SubscriptionRequest serialisation" when {
    "businessReferencesAllButSp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          des.SubscriptionRequest(
            acknowledgementReference = ackref.ackRef,
            businessDetails = DefaultDesValues.BusinessCustomerDetails,
            businessActivities = DefaultDesValues.BusinessActivitiesSection,
            eabAll = DefaultDesValues.EabAllDetails,
            eabResdEstAgncy = DefaultDesValues.EabResd,
            businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
            businessReferencesAll = DefaultDesValues.PrevRegMLR,
            businessReferencesAllButSp = None,
            businessReferencesCbUbLlp = DefaultDesValues.CorpTaxRegime,
            tradingPremises = DefaultDesValues.TradingPremisesSection,
            bankAccountDetails = DefaultDesValues.bankDetailsSection,
            msb = DefaultDesValues.msbSection,
            hvd = DefaultDesValues.hvdSection,
            filingIndividual = DefaultDesValues.filingIndividual,
            tcspAll = DefaultDesValues.tcspAllSection,
            tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
            responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
            asp = DefaultDesValues.AspSection,
            aspOrTcsp = DefaultDesValues.AspOrTcspSection,
            declaration = Declaration(true)
          )
        ) \ "businessReferencesAllButSp") must be (a[JsUndefined])


      }
    }

    "businessReferencesCbUbLlp is None" must {
      "exclude the field from Json" in {
        (Json.toJson(
          des.SubscriptionRequest(
            acknowledgementReference = ackref.ackRef,
            businessDetails = DefaultDesValues.BusinessCustomerDetails,
            businessActivities = DefaultDesValues.BusinessActivitiesSection,
            eabAll = DefaultDesValues.EabAllDetails,
            eabResdEstAgncy = DefaultDesValues.EabResd,
            businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
            businessReferencesAll = DefaultDesValues.PrevRegMLR,
            businessReferencesAllButSp = DefaultDesValues.VatALlBuySp,
            businessReferencesCbUbLlp = None,
            tradingPremises = DefaultDesValues.TradingPremisesSection,
            bankAccountDetails = DefaultDesValues.bankDetailsSection,
            msb = DefaultDesValues.msbSection,
            hvd = DefaultDesValues.hvdSection,
            filingIndividual = DefaultDesValues.filingIndividual,
            tcspAll = DefaultDesValues.tcspAllSection,
            tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
            responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
            asp = DefaultDesValues.AspSection,
            aspOrTcsp = DefaultDesValues.AspOrTcspSection,
            declaration = Declaration(true)
          )
        ) \ "businessReferencesCbUbLlp") must be (a[JsUndefined])
      }
    }
  }

  "SubscriptionRequestSpec" must {
    "convert correctly" in {

      val desSubscriptionReq =
        des.SubscriptionRequest(
          acknowledgementReference = ackref.ackRef,
          businessDetails = DefaultDesValues.BusinessCustomerDetails,
          businessActivities = DefaultDesValues.BusinessActivitiesSection,
          eabAll = DefaultDesValues.EabAllDetails,
          eabResdEstAgncy = DefaultDesValues.EabResd,
          businessContactDetails = DefaultDesValues.AboutTheBusinessSection,
          businessReferencesAll = DefaultDesValues.PrevRegMLR,
          businessReferencesAllButSp = DefaultDesValues.VatALlBuySp,
          businessReferencesCbUbLlp = DefaultDesValues.CorpTaxRegime,
          tradingPremises = DefaultDesValues.TradingPremisesSection,
          bankAccountDetails = DefaultDesValues.bankDetailsSection,
          msb = DefaultDesValues.msbSection,
          hvd = DefaultDesValues.hvdSection,
          filingIndividual = DefaultDesValues.filingIndividual,
          tcspAll = DefaultDesValues.tcspAllSection,
          tcspTrustCompFormationAgt = DefaultDesValues.tcspTrustCompFormationAgtSection,
          responsiblePersons = DefaultDesValues.ResponsiblePersonsSection,
          asp = DefaultDesValues.AspSection,
          aspOrTcsp = DefaultDesValues.AspOrTcspSection,
          declaration = Declaration(true)
        )

      val feSubscriptionReq = {
        import models.fe.SubscriptionRequest
        SubscriptionRequest(
          businessMatchingSection = BusinessMatchingSection.model,
          eabSection = EabSection.model,
          aboutTheBusinessSection = AboutTheBusinessSection.model ,
          tradingPremisesSection = TradingPremisesSection.model ,
          bankDetailsSection = BankDetailsSection.model ,
          aboutYouSection = AboutYouSection.model,
          businessActivitiesSection = BusinessActivitiesSection.model ,
          responsiblePeopleSection = ResponsiblePeopleSection.model,
          tcspSection = ASPTCSPSection.TcspSection,
          aspSection = ASPTCSPSection.AspSection,
          msbSection = MsbSection.completeModel,
          hvdSection = HvdSection.completeModel,
          supervisionSection = SupervisionSection.completeModel
        )
      }

      des.SubscriptionRequest.convert(feSubscriptionReq) must be(desSubscriptionReq)

    }
  }
}
