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

package metrics

import com.codahale.metrics.Timer.Context
import com.codahale.metrics.{Counter, Timer}
import com.kenshoo.play.metrics.MetricsRegistry

trait Metrics {
  def timer(api: APITypes): Timer.Context
  def success(api: APITypes): Unit
  def failed(api: APITypes): Unit
}

object Metrics extends Metrics {
  // $COVERAGE-OFF$
  val timers = Map[APITypes, Timer](
    API4 -> MetricsRegistry.defaultRegistry.timer(s"${API4.key}-timer"),
    API5 -> MetricsRegistry.defaultRegistry.timer(s"${API5.key}-timer"),
    API6 -> MetricsRegistry.defaultRegistry.timer(s"${API6.key}-timer"),
    API9 -> MetricsRegistry.defaultRegistry.timer(s"${API9.key}-timer"),
    GGAdmin -> MetricsRegistry.defaultRegistry.timer(s"${GGAdmin.key}-timer")
  )

  val successCounters = Map[APITypes, Counter](
    API4 -> MetricsRegistry.defaultRegistry.counter(s"${API4.key}-success"),
    API5 -> MetricsRegistry.defaultRegistry.counter(s"${API5.key}-success"),
    API6 -> MetricsRegistry.defaultRegistry.counter(s"${API6.key}-success"),
    API9 -> MetricsRegistry.defaultRegistry.counter(s"${API9.key}-success"),
    GGAdmin -> MetricsRegistry.defaultRegistry.counter(s"${GGAdmin.key}-success")
  )

  val failedCounters = Map[APITypes, Counter](
    API4 -> MetricsRegistry.defaultRegistry.counter(s"${API4.key}-failure"),
    API5 -> MetricsRegistry.defaultRegistry.counter(s"${API5.key}-failure"),
    API6 -> MetricsRegistry.defaultRegistry.counter(s"${API6.key}-failure"),
    API9 -> MetricsRegistry.defaultRegistry.counter(s"${API9.key}-failure"),
    GGAdmin -> MetricsRegistry.defaultRegistry.counter(s"${GGAdmin.key}-failure")
  )

  override def timer(api: APITypes): Context = timers(api).time()
  override def success(api: APITypes): Unit = successCounters(api).inc()
  override def failed(api: APITypes): Unit = failedCounters(api).inc()
}
