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
import org.mockito.Matchers.{eq => eqTo}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class ResponsiblePeopleUpdateHelperSpec extends PlaySpec with MockitoSugar with ScalaFutures with IntegrationPatience {

  val responsiblePeopleUpdateHelper = new ResponsiblePeopleUpdateHelper {}

  "ResponsiblePeopleUpdateHelper" must {

    "successfully compare and update api6 request with api5 data" when {

      "responsible people start date changes" in {

        val viewModel = DesConstants.SubscriptionViewStatusRP.copy(
          responsiblePersons = DesConstants.SubscriptionViewStatusRP.responsiblePersons map {
            rps => Seq(rps.head.copy(startDate = Some("1970-01-01")))
          }
        )
        val request = DesConstants.updateAmendVariationRequestRP.copy(
          responsiblePersons = DesConstants.updateAmendVariationRequestRP.responsiblePersons map {
            rps => Seq(rps.tail.head)
          }
        )
        val convertedRequest = DesConstants.amendStatusAmendVariationRP.copy(
          responsiblePersons = DesConstants.amendStatusAmendVariationRP.responsiblePersons map {
            rps => Seq(rps.tail.head.copy(dateChangeFlag = Some(true),
              extra = rps.tail.head.extra.copy(status = Some("Updated"))))
          })

        val result = responsiblePeopleUpdateHelper.updateWithResponsiblePeople(request, viewModel)

        result.responsiblePersons must be(convertedRequest.responsiblePersons)

      }

    }

  }

}
