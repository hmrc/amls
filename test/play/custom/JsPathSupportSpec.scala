/*
 * Copyright 2024 HM Revenue & Customs
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

package play.custom

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsString, JsSuccess, Json, __}
import play.custom.JsPathSupport.readLocalDateTime

import java.time.LocalDateTime

class JsPathSupportSpec extends AnyWordSpec with Matchers {

  "readLocalDateTime" should {

    "read a date String at the top level" in {
      readLocalDateTime.reads(JsString("1970-01-23T18:27:17.851")) shouldBe
        JsSuccess(LocalDateTime.parse("1970-01-23T18:27:17.851"), __)
    }

    "read a Date with ISO offset in the nested $date object" in {
      readLocalDateTime.reads(Json.obj("$date" -> "1970-01-23T18:27:17.851Z")) shouldBe
        JsSuccess(LocalDateTime.parse("1970-01-23T18:27:17.851"), __ \ "$date")
    }

    "read a Long in the nested $date -> $numberLong object" in {
      readLocalDateTime.reads(Json.obj("$date" -> Json.obj("$numberLong" -> "1967237851"))) shouldBe
        JsSuccess(LocalDateTime.parse("1970-01-23T18:27:17.851"), __ \ "$date" \ "$numberLong")
    }
  }
}
