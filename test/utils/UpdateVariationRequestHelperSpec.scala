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

import connectors.ViewDESConnector
import models.des.{AmendVariationRequest, DesConstants, SubscriptionView}
import org.mockito.Matchers.{eq => eqTo}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

class UpdateVariationRequestHelperSpec extends PlaySpec with MockitoSugar with ScalaFutures with IntegrationPatience {

  val testAmendVariationUpdateRequestHelper = new UpdateVariationRequestHelper{
    override val viewDesConnector: ViewDESConnector = mock[ViewDESConnector]

    def testIsBusinessReferenceChanged(response: SubscriptionView, desRequest: AmendVariationRequest): Boolean = {
      isBusinessReferenceChanged(response: SubscriptionView, desRequest: AmendVariationRequest)
    }

  }

  "successfully evaluate isBusinessReferenceChanged when api5 data is same as api6 " in {
    testAmendVariationUpdateRequestHelper.testIsBusinessReferenceChanged(DesConstants.SubscriptionViewModelForRp, DesConstants.AmendVariationRequestModel) must be(false)
  }


}
