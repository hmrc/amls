/*
 * Copyright 2018 HM Revenue & Customs
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

package connectors

import javax.inject.Inject

import config.AmlsConfig
import metrics.{EnrolmentStoreKnownFacts, Metrics}
import models.enrolment.{AmlsEnrolmentKey, KnownFacts}
import play.api.Logger
import uk.gov.hmrc.http.{CorePost, HeaderCarrier, HttpResponse}
import utils.HttpResponseHelper

import scala.concurrent.{ExecutionContext, Future}

class EnrolmentStoreConnector @Inject()(
                                         val http: CorePost,
                                         val metrics: Metrics) extends HttpResponseHelper {

  def enrol(enrolmentKey: AmlsEnrolmentKey, knownFacts: KnownFacts)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {

    val url = s"${AmlsConfig.enrolmentStoreUrl}/enrolment-store/enrolments/${enrolmentKey.key}"

    val prefix = "[EnrolmentStore][Enrolments]"
    val timer = metrics.timer(EnrolmentStoreKnownFacts)

    http.POST(url, knownFacts)

  }


}
