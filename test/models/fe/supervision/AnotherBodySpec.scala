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

package models.fe.supervision

import models.des.supervision.{SupervisorDetails, SupervisionDetails}
import org.joda.time.LocalDate
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class AnotherBodySpec extends PlaySpec with MockitoSugar {

  "AnotherBody" should {

    "Json read and writes" must {

      "Serialise AnotherBodyNo as expected" in {
        Json.toJson(AnotherBodyNo) must be(Json.obj("anotherBody" -> false))
      }

      "Serialise AnotherBodyYes service as expected" in {

        val start = new LocalDate(1990, 2, 24) //scalastyle:off magic.number
        val end = new LocalDate(1998, 2, 24) //scalastyle:off magic.number
        val input = AnotherBodyYes("Name", start, end, "Reason")

        val expectedJson = Json.obj(
          "anotherBody" -> true,
          "supervisorName" -> "Name",
          "startDate" -> "1990-02-24",
          "endDate" -> "1998-02-24",
          "endingReason" -> "Reason"
        )

        Json.toJson(input) must be(expectedJson)
      }

      "Deserialise AnotherBodyNo as expected" in {
        val json = Json.obj("anotherBody" -> false)
        val expected = JsSuccess(AnotherBodyNo)
        Json.fromJson[AnotherBody](json) must be(expected)
      }

      "Deserialise AnotherBodyYes as expected" in {

        val input = Json.obj(
          "anotherBody" -> true,
          "supervisorName" -> "Name",
          "startDate" -> "1990-02-24",
          "endDate" -> "1998-02-24",
          "endingReason" -> "Reason"
        )

        val start = new LocalDate(1990, 2, 24) //scalastyle:off magic.number
        val end = new LocalDate(1998, 2, 24) //scalastyle:off magic.number
        val expected = AnotherBodyYes("Name", start, end, "Reason")

        Json.fromJson[AnotherBody](input) must be(JsSuccess(expected))
      }

      "fail when on missing all data" in {
        Json.fromJson[AnotherBody](Json.obj()) must
          be(JsError((JsPath \ "anotherBody") -> ValidationError("error.path.missing")))
      }
    }

    "convert des SupervisionDetails to frontend AnotherBody" in {

      val desModel = Some(SupervisionDetails(
        true,
        Some(SupervisorDetails(
          "NameOfLastSupervisor",
          "2001-01-01",
          "2001-01-01",
          None,
          "SupervisionEndingReason")
        )
      ))

      val convertedModel = Some(AnotherBodyYes("NameOfLastSupervisor", new LocalDate(2001, 1, 1), new LocalDate(2001, 1, 1), "SupervisionEndingReason"))
      AnotherBody.conv(desModel) must be(convertedModel)
    }

    "convert des SupervisionDetails to frontend AnotherBody when SupervisorDetails is none" in {

      val desModel = Some(SupervisionDetails(
        false,
        None
      ))

      val convertedModel = Some(AnotherBodyNo)
      AnotherBody.conv(desModel) must be(convertedModel)
    }

    "convert des SupervisionDetails to frontend AnotherBody when input is none" in {

      AnotherBody.conv(None) must be(None)
    }
  }
}
