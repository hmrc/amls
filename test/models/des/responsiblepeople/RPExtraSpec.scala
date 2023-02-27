/*
 * Copyright 2022 HM Revenue & Customs
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
import models.fe.responsiblepeople.ResponsiblePeople
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

import utils.StatusConstants

class RPExtraSpec extends PlaySpec with GuiceOneAppPerSuite{

  "RPExtra" should {
    "serialise Json successfully" in {
      val extra = RPExtra(
        Some(StringOrInt(112233)),
        Some("1990-2-2"),
        Some(StatusConstants.Deleted),
        None,
        Some("10"),
        Some("Failed"),
        Some("2001-3-3"),
        Some("Passed"),
        Some("2002-6-6"))

      val json = Json.obj("lineId" ->112233,
        "endDate" ->"1990-2-2",
        "status" ->"Deleted",
        "retest" ->"10",
        "testResultFitAndProper" ->"Failed",
        "testDateFitAndProper" ->"2001-3-3",
        "testResultApprovalCheck" ->"Passed",
        "testDateApprovalCheck" ->"2002-6-6")

      RPExtra.reads.reads(json) must be(JsSuccess(extra))
    }

    "serialise Json successfully1" in {
      val json = Json.obj(
        "endDate" ->"1990-2-2",
        "status" ->"Deleted",
        "retest" ->"10",
        "testResultFitAndProper" ->"Failed",
        "testDateFitAndProper" ->"2001-3-3",
        "testResultApprovalCheck" ->"Passed",
        "testDateApprovalCheck" ->"2002-6-6")

      RPExtra.reads.reads(json) must be(JsSuccess(
        RPExtra(
          None,
          Some("1990-2-2"),
          Some("Deleted"),
          None,
          Some("10"),
          Some("Failed"),
          Some("2001-3-3"),
          Some("Passed"),
          Some("2002-6-6")))
      )
    }

    "serialise Json successfully2" in {
      val json = Json.obj(
        "status" ->"Deleted")

      RPExtra.reads.reads(json) must be(JsSuccess(RPExtra(None,None,Some("Deleted"),None,None,None,None,None)))
    }

    "Deserialise Json successfully1" in {
      val json = Json.obj(
        "status" ->"Deleted")

      RPExtra.jsonWrites.writes(RPExtra(None,None,Some("Deleted"),None,None,None,None,None)) must be(json)
    }

    "Deserialise Json successfully2" in {

      val json = Json.obj()

      RPExtra.jsonWrites.writes(RPExtra(None,None,None,None,None,None,None,None)) must be(json)
    }

    "successfully format" in {
      val extra = RPExtra(
        Some(StringOrInt(112233)),
        Some("1990-2-2"),
        Some(StatusConstants.Deleted),
        None,
        Some("10"),
        Some("Failed"),
        Some("2001-3-3"),
        Some("Passed"),
        Some("2002-6-6"))

      RPExtra.reads.reads(RPExtra.jsonWrites.writes(extra)) must be(JsSuccess(extra))
    }

    "successfully format2" in {
      val extra = RPExtra(None)

      RPExtra.reads.reads(RPExtra.jsonWrites.writes(extra)) must be(JsSuccess(extra))
    }

    "Should create an correct RPExtra object for deleted Responsible People" in {
      val rp = ResponsiblePeople(lineId=Some(1),status=Some(StatusConstants.Deleted), hasChanged = true )
      val rpe = RPExtra.conv(rp)
      rpe.lineId must be(Some(StringOrInt("1")))
      rpe.status must be (Some(StatusConstants.Deleted))
    }

    "Should create an correct RPExtra object for added Responsible People" in {
      val rp = ResponsiblePeople(lineId=None,status=None, hasChanged = true )
      val rpe = RPExtra.conv(rp)
      rpe.status must be (None)
    }

    "Should create an correct RPExtra object for updated Responsible People" in {
      val rp = ResponsiblePeople(lineId=Some(1),status=Some(StatusConstants.Updated), hasChanged = true )
      val rpe = RPExtra.conv(rp)
      rpe.lineId must be(Some(StringOrInt("1")))
      rpe.status must be (Some(StatusConstants.Updated))
    }

    "Should create an correct RPExtra object for unchanged Responsible People" in {
      val rp = ResponsiblePeople(lineId=Some(1),status=None, hasChanged = false )
      val rpe = RPExtra.conv(rp)
      rpe.lineId must be(Some(StringOrInt("1")))
      rpe.status must be (Some(StatusConstants.Unchanged))
    }

  }

}
