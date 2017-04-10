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

package services

import connectors.{AmendVariationDESConnector, SubscriptionStatusDESConnector, ViewDESConnector}
import models.des.AmendVariationRequest
import org.mockito.Mockito.when
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsResult, JsValue}
import play.api.test.FakeApplication
import repositories.FeeResponseRepository

class AmendVariationServicePreRelease7Spec extends PlaySpec with OneAppPerSuite with MockitoSugar with ScalaFutures with IntegrationPatience {

  override lazy val app = FakeApplication(additionalConfiguration = Map("microservice.services.feature-toggle.release7" -> false))
  val successValidate:JsResult[JsValue] = mock[JsResult[JsValue]]

  object TestAmendVariationService extends AmendVariationService {
    override private[services] val amendVariationDesConnector = mock[AmendVariationDESConnector]
    override private[services] val viewStatusDesConnector: SubscriptionStatusDESConnector = mock[SubscriptionStatusDESConnector]
    override private[services] val feeResponseRepository: FeeResponseRepository = mock[FeeResponseRepository]
    override private[services] val viewDesConnector: ViewDESConnector = mock[ViewDESConnector]
    override private[services] def validateResult(request: AmendVariationRequest) = successValidate
  }

  "AmendVariationService" must {

    when{successValidate.isSuccess} thenReturn true
    "not be applying release 7 transforms when toggled off" in {
      TestAmendVariationService.updates.size must be(3)
    }
  }

}
