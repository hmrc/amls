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

package models.fe.responsiblepeople

import models.des.responsiblepeople.RegDetails
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class SaRegisteredSpec extends PlaySpec with MockitoSugar {

  "SaRegistered" should {

    "JSON validation" must {
      "successfully validate given an enum value" in {

        Json.fromJson[SaRegistered](Json.obj("saRegistered" -> false)) must
          be(JsSuccess(SaRegisteredNo, JsPath \ "saRegistered"))
      }

      "successfully validate given an `Yes` value" in {

        val json = Json.obj("saRegistered" -> true, "utrNumber" -> "0123456789")

        Json.fromJson[SaRegistered](json) must
          be(JsSuccess(SaRegisteredYes("0123456789"), JsPath \ "saRegistered" \ "utrNumber"))
      }

      "fail to validate when given an empty `Yes` value" in {

        val json = Json.obj("saRegistered" -> true)

        Json.fromJson[SaRegistered](json) must
          be(JsError((JsPath \ "saRegistered" \ "utrNumber") -> ValidationError("error.path.missing")))
      }

      "write the correct value" in {

        Json.toJson(SaRegisteredNo) must
          be(Json.obj("saRegistered" -> false))

        Json.toJson(SaRegisteredYes("0123456789")) must
          be(Json.obj(
            "saRegistered" -> true,
            "utrNumber" -> "0123456789"
          ))
      }
    }
  }

  "SaRegisterd conversion" when {
    "Sa registered is false" must {
      "convert model to frontend - SARegisteredNo" in {
        val desModel = Some(RegDetails(
          false,
          None,
          false,
          None
        ))
        SaRegistered.conv(desModel) must be(Some(SaRegisteredNo))
      }
    }

    "SA registered is true" must {
      "convert model to frontend - VATRegisteredYes with Registration Number included" in {
        val desModel = Some(RegDetails(
          false,
          None,
          true,
          Some("SA REG NUMBER")
        ))
        SaRegistered.conv(desModel) must be(Some(SaRegisteredYes("SA REG NUMBER")))
      }
    }

    "No value is supplied " must {
      "convert to SARegisteredNo" in {
        SaRegistered.conv(None) must be(Some(SaRegisteredNo))
      }
    }
  }
}
