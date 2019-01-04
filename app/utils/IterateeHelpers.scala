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

import play.api.Logger
import play.api.libs.iteratee.{Input, Iteratee}
import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.Result

import scala.concurrent.{ExecutionContext, Future}

trait IterateeHelpers {

  implicit def toIterateeOps(iteratee: Iteratee[Array[Byte], Result]): IterateeOps =
    new IterateeOps {
      override val target = iteratee
    }

  trait IterateeOps {

    val target: Iteratee[Array[Byte], Result]

    def apply[B]
    (b: B)
    (implicit
     f: Format[B],
     ec: ExecutionContext
    ): Future[Result] = {
      val bytes = Json.toJson(b).toString
      target.feed(Input.El(bytes.getBytes)) flatMap { _.run }
    }

    def json
    (b: JsValue)
    (implicit
     ec: ExecutionContext
    ): Future[Result] = {
      val bytes = b.toString
      target.feed(Input.El(bytes.getBytes)) flatMap { _.run }
    }
  }
}
