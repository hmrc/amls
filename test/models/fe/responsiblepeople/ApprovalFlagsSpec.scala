/*
 * Copyright 2026 HM Revenue & Customs
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

package models.fe.responsiblepeople

import org.scalatestplus.play._
import play.api.libs.json._

class ApprovalFlagsSpec extends PlaySpec {

  "ApprovalFlags JSON format" should {

    "deserialize full JSON correctly" in {
      val json = Json.parse(
        """
          |{
          |  "hasAlreadyPassedFitAndProper": true,
          |  "hasAlreadyPaidApprovalCheck": false
          |}
          |""".stripMargin
      )

      val result = json.as[ApprovalFlags](ApprovalFlags.reads)

      result.hasAlreadyPassedFitAndProper mustBe Some(true)
      result.hasAlreadyPaidApprovalCheck mustBe Some(false)
    }

    "deserialize partial JSON correctly" in {
      val json = Json.parse(
        """
          |{
          |  "hasAlreadyPassedFitAndProper": true
          |}
          |""".stripMargin
      )

      val result = json.as[ApprovalFlags](ApprovalFlags.reads)

      result.hasAlreadyPassedFitAndProper mustBe Some(true)
      result.hasAlreadyPaidApprovalCheck mustBe None
    }

    "deserialize empty JSON to None fields" in {
      val json = Json.parse("""{}""")

      val result = json.as[ApprovalFlags]

      result.hasAlreadyPassedFitAndProper mustBe None
      result.hasAlreadyPaidApprovalCheck mustBe None
    }

    "serialize correctly to JSON" in {
      val model = ApprovalFlags(
        hasAlreadyPassedFitAndProper = Some(true),
        hasAlreadyPaidApprovalCheck = Some(false)
      )

      val json = Json.toJson(model)

      (json \ "hasAlreadyPassedFitAndProper").as[Boolean] mustBe true
      (json \ "hasAlreadyPaidApprovalCheck").as[Boolean] mustBe false
    }

    "serialize None values as null or omit depending on format" in {
      val model = ApprovalFlags()

      val json = Json.toJson(model)

      (json \ "hasAlreadyPassedFitAndProper").toOption mustBe empty
      (json \ "hasAlreadyPaidApprovalCheck").toOption mustBe empty
    }

    "round-trip JSON format should preserve values" in {
      val original = ApprovalFlags(
        hasAlreadyPassedFitAndProper = Some(true),
        hasAlreadyPaidApprovalCheck = Some(true)
      )

      val json = Json.toJson(original)
      val back = json.as[ApprovalFlags](ApprovalFlags.reads)

      back mustBe original
    }
  }
}
