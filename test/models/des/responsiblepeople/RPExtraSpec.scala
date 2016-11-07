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

package models.des.responsiblepeople

import models.des.StringOrInt
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}
import utils.StatusConstants

class RPExtraSpec extends PlaySpec {

  "RPExtra" should {
    "serialise Json successfully" in {
      val extra = RPExtra(Some(StringOrInt(112233)),Some("1990-2-2"),
        Some(StatusConstants.Deleted),
        Some(true),
        Some("Passed"),
        Some("2001-3-3"))

      val json = Json.obj("lineId" ->112233,
        "endDate" ->"1990-2-2",
        "status" ->"Deleted",
        "retestFlag" ->true,
        "testResult" ->"Passed",
        "testDate" ->"2001-3-3")

      RPExtra.reads.reads(json) must be(JsSuccess(extra))
    }

    "serialise Json successfully1" in {
      val json = Json.obj(
        "endDate" ->"1990-2-2",
        "status" ->"Deleted",
        "retestFlag" ->true,
        "testResult" ->"Passed",
        "testDate" ->"2001-3-3")

      RPExtra.reads.reads(json) must be(JsSuccess(RPExtra(None,Some("1990-2-2"),Some("Deleted"),Some(true),Some("Passed"),Some("2001-3-3"))))
    }

    "serialise Json successfully2" in {
      val json = Json.obj(
        "status" ->"Deleted")

      RPExtra.reads.reads(json) must be(JsSuccess(RPExtra(None,None,Some("Deleted"),None,None,None)))
    }

    "Deserialise Json successfully1" in {
      val json = Json.obj(
        "status" ->"Deleted")

      RPExtra.jsonWrites.writes(RPExtra(None,None,Some("Deleted"),None,None,None)) must be(json)
    }

    "Deserialise Json successfully2" in {

      val json = Json.obj()

      RPExtra.jsonWrites.writes(RPExtra(None,None,None,None,None,None)) must be(json)
    }

    "successfully format" in {
      val extra = RPExtra(Some(StringOrInt(112233)),Some("1990-2-2"),
        Some(StatusConstants.Deleted),
        Some(true),
        Some("Passed"),
        Some("2001-3-3"))

      RPExtra.reads.reads(RPExtra.jsonWrites.writes(extra)) must be(JsSuccess(extra))
    }

    "successfully format2" in {
      val extra = RPExtra(None)

      RPExtra.reads.reads(RPExtra.jsonWrites.writes(extra)) must be(JsSuccess(extra))
    }
  }

}
