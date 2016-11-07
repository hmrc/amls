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

package models.des

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsPath, JsSuccess}

class StringOrIntSpec extends PlaySpec {

  "StringOrInt" should {

    "Compare equality of objects" in {
      StringOrInt("test").equals(StringOrInt("test")) mustBe true
      StringOrInt(1).equals(StringOrInt(1)) mustBe true
      StringOrInt(3).equals(StringOrInt("3")) mustBe true
      StringOrInt(4).equals(StringOrInt("Four")) mustBe false
    }

    "Json Validation" must {

      "Successfully read and write data:option Integer" in {
        StringOrInt.reads.reads(StringOrInt.writes.writes(StringOrInt("111111"))) must
          be(JsSuccess(StringOrInt(Right("111111")), JsPath \ "lineId"))
      }

      "Successfully read and write data:option String" in {
        val lineNumber = 112233
        StringOrInt.reads.reads(StringOrInt.writes.writes(StringOrInt(lineNumber))) must
          be(JsSuccess(StringOrInt(Left(lineNumber)), JsPath \ "lineId"))
      }

      "Successfully read and write data:option String less than 6 digits" in {
        val lineNumber = "1"
        StringOrInt.reads.reads(StringOrInt.writes.writes(StringOrInt(lineNumber))) must
          be(JsSuccess(StringOrInt(Right(lineNumber)), JsPath \ "lineId"))
      }
    }
  }
}
