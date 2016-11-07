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

package services

import connectors.{DESConnector, GovernmentGatewayAdminConnector, SubscribeDESConnector}
import models.{KnownFact, KnownFactsForService}
import models.des.{SubscriptionRequest, SubscriptionResponse}
import repositories.FeeResponseRepository
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait SubscriptionService {

  private[services] def desConnector: SubscribeDESConnector

  private[services] def ggConnector: GovernmentGatewayAdminConnector

  private[services] def feeResponseRepository: FeeResponseRepository

  def subscribe
  (safeId: String, request: SubscriptionRequest)
  (implicit
   hc: HeaderCarrier,
   ec: ExecutionContext
  ): Future[SubscriptionResponse] =
    for {
      response <- desConnector.subscribe(safeId, request)
      _ <- ggConnector.addKnownFacts(KnownFactsForService(Seq(
        KnownFact("SafeId", safeId),
        KnownFact("MLRRefNumber", response.amlsRefNo)

      )))
      inserted <- feeResponseRepository.insert(response)
    } yield {
      response
    }
}

object SubscriptionService extends SubscriptionService {
  // $COVERAGE-OFF$
  override private[services] val desConnector = DESConnector
  override private[services] val ggConnector = GovernmentGatewayAdminConnector
  override private[services] val feeResponseRepository = FeeResponseRepository()
}
