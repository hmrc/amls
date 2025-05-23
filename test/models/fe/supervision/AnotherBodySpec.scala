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

package models.fe.supervision

import models.des.supervision.{SupervisionDetails, SupervisorDetails}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

import java.time.LocalDate

class AnotherBodySpec extends PlaySpec {

  "AnotherBody" should {

    "Json read and writes" must {

      "Serialise AnotherBodyNo as expected" in {
        Json.toJson(AnotherBodyNo: AnotherBody) must be(Json.obj("anotherBody" -> false))
      }

      "Serialise AnotherBodyYes service as expected" in {

        val start = LocalDate.of(1990, 2, 24) // scalastyle:off magic.number
        val end   = LocalDate.of(1998, 2, 24) // scalastyle:off magic.number
        val input = AnotherBodyYes("Name", start, end, "Reason")

        val expectedJson = Json.obj(
          "anotherBody"    -> true,
          "supervisorName" -> "Name",
          "startDate"      -> Json.obj("supervisionStartDate" -> "1990-02-24"),
          "endDate"        -> Json.obj("supervisionEndDate" -> "1998-02-24"),
          "endingReason"   -> Json.obj("supervisionEndingReason" -> "Reason")
        )

        Json.toJson(input: AnotherBody) must be(expectedJson)
      }

      "Deserialise AnotherBodyNo as expected" in {
        val json     = Json.obj("anotherBody" -> false)
        val expected = JsSuccess(AnotherBodyNo)
        Json.fromJson[AnotherBody](json) must be(expected)
      }

      "Deserialise AnotherBodyYes as expected" in {

        val input = Json.obj(
          "anotherBody"    -> true,
          "supervisorName" -> "Name",
          "startDate"      -> Json.obj("supervisionStartDate" -> "1990-02-24"),
          "endDate"        -> Json.obj("supervisionEndDate" -> "1998-02-24"),
          "endingReason"   -> Json.obj("supervisionEndingReason" -> "Reason")
        )

        val start    = LocalDate.of(1990, 2, 24) // scalastyle:off magic.number
        val end      = LocalDate.of(1998, 2, 24) // scalastyle:off magic.number
        val expected = AnotherBodyYes("Name", start, end, "Reason")

        Json.fromJson[AnotherBody](input) must be(JsSuccess(expected))
      }

      "fail when on missing all data" in {
        Json.fromJson[AnotherBody](Json.obj()) must
          be(JsError((JsPath \ "anotherBody") -> JsonValidationError("error.path.missing")))
      }
    }

    "convert des SupervisionDetails to frontend AnotherBody" in {

      val desModel = Some(
        SupervisionDetails(
          true,
          Some(
            SupervisorDetails(
              "NameOfLastSupervisor",
              "2001-01-01",
              "2001-01-01",
              Some(false),
              "SupervisionEndingReason"
            )
          )
        )
      )

      val convertedModel = Some(
        AnotherBodyYes(
          "NameOfLastSupervisor",
          LocalDate.of(2001, 1, 1),
          LocalDate.of(2001, 1, 1),
          "SupervisionEndingReason"
        )
      )
      AnotherBody.conv(desModel) must be(convertedModel)
    }

    "convert des SupervisionDetails to frontend AnotherBody when SupervisorDetails is none" in {

      val desModel = Some(SupervisionDetails(false, None))

      val convertedModel = Some(AnotherBodyNo)
      AnotherBody.conv(desModel) must be(convertedModel)
    }

    "convert des SupervisionDetails to frontend AnotherBody when input is none" in {

      AnotherBody.conv(None) must be(None)
    }
  }
}
