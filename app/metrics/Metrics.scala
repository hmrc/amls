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

package metrics

import javax.inject.{Inject, Singleton}
import com.codahale.metrics.Timer.Context
import com.codahale.metrics.{Counter, MetricRegistry, Timer}
import play.api.Application

@Singleton
class Metrics @Inject()(
                       app: Application,
                       metrics: com.kenshoo.play.metrics.Metrics
                       ) {
  // $COVERAGE-OFF$
  private val registry: Option[MetricRegistry] =
    if(app.configuration.getString("metrics.enabled").map(s => s.toBoolean).getOrElse(false)) {
      Some( metrics.defaultRegistry)
    } else {
      None
    }

  private val timers = registry.map(r => Map[APITypes, Timer](
      API4 -> r.timer(s"${API4.key}-timer"),
      API5 -> r.timer(s"${API5.key}-timer"),
      API6 -> r.timer(s"${API6.key}-timer"),
      API8 -> r.timer(s"${API8.key}-timer"),
      API9 -> r.timer(s"${API9.key}-timer"),
      API10 -> r.timer(s"${API10.key}-timer"),
      GGAdmin -> r.timer(s"${GGAdmin.key}-timer"),
      PayAPI -> r.timer(s"${PayAPI.key}-timer"),
      EnrolmentStoreKnownFacts -> r.timer(s"${EnrolmentStoreKnownFacts.key}-timer")
    )).getOrElse(Map[APITypes, Timer]())

  private val successCounters = registry.map(r => Map[APITypes, Counter](
    API4 -> r.counter(s"${API4.key}-success"),
    API5 -> r.counter(s"${API5.key}-success"),
    API6 -> r.counter(s"${API6.key}-success"),
    API8 -> r.counter(s"${API8.key}-success"),
    API9 -> r.counter(s"${API9.key}-success"),
    API10 -> r.counter(s"${API10.key}-success"),
    GGAdmin -> r.counter(s"${GGAdmin.key}-success"),
    PayAPI -> r.counter(s"${PayAPI.key}-success"),
    EnrolmentStoreKnownFacts -> r.counter(s"${EnrolmentStoreKnownFacts.key}-success")
  )).getOrElse(Map[APITypes, Counter]())

  val failedCounters = registry.map(r => Map[APITypes, Counter](
    API4 -> r.counter(s"${API4.key}-failure"),
    API5 -> r.counter(s"${API5.key}-failure"),
    API6 -> r.counter(s"${API6.key}-failure"),
    API8 -> r.counter(s"${API8.key}-failure"),
    API9 -> r.counter(s"${API9.key}-failure"),
    API10 -> r.counter(s"${API10.key}-failure"),
    GGAdmin -> r.counter(s"${GGAdmin.key}-failure"),
    PayAPI -> r.counter(s"${PayAPI.key}-failure"),
    EnrolmentStoreKnownFacts -> r.counter(s"${EnrolmentStoreKnownFacts.key}-failure")
  )).getOrElse(Map[APITypes, Counter]())

  def timer(api: APITypes): Context = timers(api).time()
  def success(api: APITypes): Unit = successCounters(api).inc()
  def failed(api: APITypes): Unit = failedCounters(api).inc()
}
