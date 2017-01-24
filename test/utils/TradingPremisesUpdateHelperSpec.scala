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

package utils

import models.des
import models.des.tradingpremises._
import models.des.{DesConstants, StringOrInt}
import org.mockito.Matchers.{eq => eqTo}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class TradingPremisesUpdateHelperSpec extends PlaySpec with MockitoSugar with ScalaFutures with IntegrationPatience {

  val testTradingPremisesUpdatedHelper = new TradingPremisesUpdateHelper {}

  "TradingPremisesUpdateHelperSpec" must {

    "successfully update agent premises start date changed flag when start date is amended" in {
      val viewModel = DesConstants.SubscriptionViewModelAPI5

      val agentPremisesData = AgentPremises("string",
        des.tradingpremises.Address("string", "string", Some("string"), Some("string"), "GB", Some("string")), true,
        Msb(false, false, false, false, false),
        models.des.tradingpremises.Hvd(false),
        Asp(false),
        Tcsp(false),
        Eab(true),
        Bpsp(true),
        Tditpsp(false),
        "2008-01-01",
        None,
        None,
        None)

      val agentDetailsData = AgentDetails(
        "Sole Proprietor",
        Some("entity name"),
        agentPremisesData,
        Some(StatusConstants.Unchanged),
        Some(StringOrInt(111111)),
        None)

      val agentPremisesExpectedData = agentPremisesData.copy(dateChangeFlag = Some(true))
      val agentDetailsExpectedData = agentDetailsData.copy(status = Some(StatusConstants.Updated), agentPremises = agentPremisesExpectedData)

      val modelWithChangedStartDate = DesConstants.updateAmendVariationCompleteRequest1.copy(
        tradingPremises = DesConstants.testAmendTradingPremisesAPI6.copy(
          agentBusinessPremises = Some(AgentBusinessPremises(true, Some(Seq(agentDetailsData))))))

      val result = testTradingPremisesUpdatedHelper.updateWithTradingPremises(modelWithChangedStartDate, viewModel)

      result.tradingPremises.agentBusinessPremises must be(Some(AgentBusinessPremises(true, Some(Seq(agentDetailsExpectedData)))))


    }

    "successfully update own business start date changed flag when start date is amended" in {
      val viewModel = DesConstants.SubscriptionViewModelAPI5

      val data = OwnBusinessPremisesDetails(
        "OwnBusinessTradingName",
        Address("OwnBusinessAddressLine1",
          "OwnBusinessAddressLine2",
          Some("OwnBusinessAddressLine3"),
          Some("OwnBusinessAddressLine4"),
          "GB",
          Some("YY1 1YY")),
        false,
        Msb(false, false, false, false, false),
        Hvd(false),
        Asp(false),
        Tcsp(true),
        Eab(true),
        Bpsp(true),
        Tditpsp(false),
        "2001-05-01",
        None,
        Some(StringOrInt(444444)),
        Some(StatusConstants.Unchanged),
        None,
        None,
        None
      )

      val expectedData = data.copy(dateChangeFlag = Some(true), status = Some(StatusConstants.Updated))

      val modelWithchangedStartDate = DesConstants.updateAmendVariationCompleteRequest1.copy(
        tradingPremises = DesConstants.testAmendTradingPremisesAPI6.copy(
          ownBusinessPremises = Some(OwnBusinessPremises(true, Some(Seq(data))))))

      val result = testTradingPremisesUpdatedHelper.updateWithTradingPremises(modelWithchangedStartDate, viewModel)


      result.tradingPremises.ownBusinessPremises must be(Some(OwnBusinessPremises(true, Some(Seq(expectedData)))))

    }

    "user has deleted a record, added a new record, modified one record and not changed one record of trading premises" in {
      val viewModel = DesConstants.SubscriptionViewStatusTP

      val testRequest = DesConstants.amendStatusAmendVariationTP.copy(
        businessActivities = DesConstants.testBusinessActivitiesWithDateChangeFlag.copy(
          all = Some(DesConstants.testBusinessActivitiesAllWithDateChangeFlag.copy(
            DateChangeFlag = Some(false)
          ))
        ),
        aspOrTcsp = Some(DesConstants.testAspOrTcsp.copy(
          supervisionDetails = Some(DesConstants.testSupervisionDetails.copy(
            supervisorDetails = Some(DesConstants.testSupervisorDetails.copy(
              dateChangeFlag = Some(false)
            ))
          ))
        ))
      )

      val result = testTradingPremisesUpdatedHelper.updateWithTradingPremises(DesConstants.amendStatusDesVariationRequestTP, viewModel)

      result.tradingPremises must be(testRequest.tradingPremises)

    }

  }
}
