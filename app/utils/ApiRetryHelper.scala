/*
 * Copyright 2021 HM Revenue & Customs
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

import akka.actor.ActorSystem
import akka.pattern.Patterns.after
import config.ApplicationConfig
import exceptions.HttpStatusException

import javax.inject._
import play.api.{Logger, Logging}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiRetryHelper @Inject()(val as: ActorSystem, appConfig: ApplicationConfig) extends Logging {

  lazy val maxAttempts: Int = appConfig.maxAttempts
  lazy val initialWaitInMS: Int = appConfig.initialWaitMs
  lazy val waitFactor: Float= appConfig.waitFactor

  def doWithBackoff[T](f: () => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    expBackOffHelper(1, initialWaitInMS, f)
  }

  private def expBackOffHelper[T] (currentAttempt: Int,
                                   currentWait: Int,
                                   f: () => Future[T])(implicit ec: ExecutionContext): Future[T] = {

    f.apply().recoverWith {
      case e: HttpStatusException =>
        if ( e.status >= 500 && e.status < 600 && currentAttempt < maxAttempts) {
          val wait = Math.ceil(currentWait * waitFactor).toInt
          logger.warn(s"Failure, retrying after $wait ms")
          after(wait.milliseconds, as.scheduler, ec, Future.successful(1)).flatMap { _ =>
            expBackOffHelper(currentAttempt + 1, wait.toInt, f)
          }
        } else {
          Future.failed(e)
        }
      case e =>
        Future.failed(e)
    }
  }
}
