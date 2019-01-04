/*
 * Copyright 2019 HM Revenue & Customs
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

import models.des.DesConstants
import org.joda.time.LocalDate
import org.mockito.Matchers.{eq => eqTo}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class DateOfChangeUpdateHelperSpec extends PlaySpec with MockitoSugar with ScalaFutures with IntegrationPatience {

  val testDateOfChangeFlagUpdatedHelper = new DateOfChangeUpdateHelper {}

  "DateOfChangeUpdateHelper" must {

    "return an updated request model with a flag set to true" when {

      "hvd dateOfTheFirst has been changed" in {

        val viewModel = DesConstants.SubscriptionViewModelAPI5

        val changeToHvd = DesConstants.testHvd.copy(
          dateOfTheFirst = Some(new LocalDate(1900,1,1).toString("yyyy-MM-dd"))
        )

        val request = DesConstants.AmendVariationRequestModel.copy(
          hvd = Some(changeToHvd)
        )

        val expectedRequest = request.copy(
          hvd = Some(changeToHvd.copy(
            dateChangeFlag = Some(true)
          ))
        )

        val result = testDateOfChangeFlagUpdatedHelper.updateWithHvdDateOfChangeFlag(
          request,
          viewModel
        )

        result.hvd must be(expectedRequest.hvd)
      }

      "supervisor startDate has been changed" in {

        val viewModel = DesConstants.SubscriptionViewModelAPI5

        val changeToSupervisor = DesConstants.testSupervisorDetails.copy(
          supervisionStartDate = new LocalDate(1900,1,1).toString("yyyy-MM-dd")
        )

        val request = DesConstants.AmendVariationRequestModel.copy(
          aspOrTcsp = Some(DesConstants.testAmendAspOrTcsp.copy(
            supervisionDetails = Some(DesConstants.testSupervisionDetails.copy(
              supervisorDetails = Some(changeToSupervisor)
            ))
          ))
        )
        val expectedRequest = request.copy(
          aspOrTcsp = Some(DesConstants.testAmendAspOrTcsp.copy(
            supervisionDetails = Some(DesConstants.testSupervisionDetails.copy(
              supervisorDetails = Some(changeToSupervisor.copy(
                dateChangeFlag = Some(true)
              ))
            ))
          ))
        )

        val result = testDateOfChangeFlagUpdatedHelper.updateWithSupervisorDateOfChangeFlag(
          request,
          viewModel
        )

        result.aspOrTcsp must be(expectedRequest.aspOrTcsp)
      }

      "business activities commenceDate has been changed" in {

        val viewModel = DesConstants.SubscriptionViewModelAPI5

        val changeToBusinessActivitiesAll = DesConstants.testBusinessActivitiesAll.copy(
          activitiesCommenceDate = Some(new LocalDate(1900,1,1).toString("yyyy-MM-dd"))
        )

        val request = DesConstants.AmendVariationRequestModel.copy(
          businessActivities = DesConstants.testBusinessActivities.copy(
            all = Some(changeToBusinessActivitiesAll)
          )
        )
        val expectedRequest = request.copy(
          businessActivities = DesConstants.testBusinessActivities.copy(
            all = Some(changeToBusinessActivitiesAll.copy(
              dateChangeFlag = Some(true)
            ))
          )
        )

        val result = testDateOfChangeFlagUpdatedHelper.updateWithBusinessActivitiesDateOfChangeFlag(
          request,
          viewModel
        )

        result.businessActivities.all must be(expectedRequest.businessActivities.all)
      }
    }

    "return an updated request model with a flag set to false" when {

      "hvd dateOfTheFirst has not been changed" in {

        val viewModel = DesConstants.SubscriptionViewModelAPI5

        val request = DesConstants.AmendVariationRequestModel

        val expectedRequest = request.copy(
          hvd = Some(DesConstants.testHvd.copy(
            dateChangeFlag = Some(false)
          ))
        )

        val result = testDateOfChangeFlagUpdatedHelper.updateWithHvdDateOfChangeFlag(
          request,
          viewModel
        )

        result.hvd must be(expectedRequest.hvd)
      }

      "supervisor startDate has not been changed" in {

        val viewModel = DesConstants.SubscriptionViewModelAPI5

        val request = DesConstants.AmendVariationRequestModel

        val expectedRequest = request.copy(
          aspOrTcsp = Some(DesConstants.testAmendAspOrTcsp.copy(
            supervisionDetails = Some(DesConstants.testSupervisionDetails.copy(
              supervisorDetails = Some(DesConstants.testSupervisorDetails.copy(
                dateChangeFlag = Some(false)
              ))
            ))
          ))
        )

        val result = testDateOfChangeFlagUpdatedHelper.updateWithSupervisorDateOfChangeFlag(
          request,
          viewModel
        )

        result.aspOrTcsp must be(expectedRequest.aspOrTcsp)
      }

      "business activities commenceDate has not been changed" in {

        val viewModel = DesConstants.SubscriptionViewModelAPI5

        val request = DesConstants.AmendVariationRequestModel

        val expectedRequest = request.copy(
          businessActivities = DesConstants.testBusinessActivities.copy(
            all = Some(DesConstants.testBusinessActivitiesAll.copy(
              dateChangeFlag = Some(false)
            ))
          )
        )

        val result = testDateOfChangeFlagUpdatedHelper.updateWithBusinessActivitiesDateOfChangeFlag(
          request,
          viewModel
        )

        result.businessActivities.all must be(expectedRequest.businessActivities.all)
      }
    }

  }

}
