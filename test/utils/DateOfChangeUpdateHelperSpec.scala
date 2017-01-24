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

        val request = DesConstants.AmendVariationRequestModel

        val updatedViewModel = DesConstants.SubscriptionViewModelAPI5.copy(
          hvd = Some(DesConstants.testHvd.copy(
            dateOfTheFirst = Some(new LocalDate(1900,1,1).toString("yyyy-MM-dd"))
          ))
        )
        val updatedRequest = request.copy(
          hvd = Some(DesConstants.testHvd.copy(
            dateChangeFlag = Some(true)
          ))
        )

        val result = testDateOfChangeFlagUpdatedHelper.updateWithHvdDateOfChangeFlag(
          request,
          updatedViewModel
        )

        result.hvd must be(updatedRequest.hvd)
      }

      "supervisor startDate has been changed" in {

        val request = DesConstants.AmendVariationRequestModel

        val updatedViewModel = DesConstants.SubscriptionViewModelAPI5.copy(
          aspOrTcsp = Some(DesConstants.testAmendAspOrTcsp.copy(
            supervisionDetails = Some(DesConstants.testSupervisionDetails.copy(
              supervisorDetails = Some(DesConstants.testSupervisorDetails.copy(
                supervisionStartDate = new LocalDate(1900,1,1).toString("yyyy-MM-dd")
              ))
            ))
          ))
        )
        val updatedRequest = request.copy(
          aspOrTcsp = Some(DesConstants.testAmendAspOrTcsp.copy(
            supervisionDetails = Some(DesConstants.testSupervisionDetails.copy(
              supervisorDetails = Some(DesConstants.testSupervisorDetails.copy(
                dateChangeFlag = Some(true)
              ))
            ))
          ))
        )

        val result = testDateOfChangeFlagUpdatedHelper.updateWithSupervisorDateOfChangeFlag(
          request,
          updatedViewModel
        )

        result.aspOrTcsp must be(updatedRequest.aspOrTcsp)
      }
      "business activities commenceDate has been changed" in {

      }
    }

  }

}
