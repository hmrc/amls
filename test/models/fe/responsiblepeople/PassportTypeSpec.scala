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

import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json}

class PassportTypeSpec extends PlaySpec {

  "PassportType" must {

    "JSON" must {

      "Read the json and return the PassportType domain object successfully for the NoPassport" in {

        PassportType.jsonReads.reads(PassportType.jsonWrites.writes(NoPassport)) must
          be(JsSuccess(NoPassport, JsPath \ "passportType"))
      }

      "Read the json and return NonUKPassport" in {
        val model = NonUKPassport("22222222222222222222222")
        PassportType.jsonReads.reads(PassportType.jsonWrites.writes(model)) must
          be(JsSuccess(model, JsPath \ "passportType" \ "nonUKPassportNumber"))
      }

      "Read the json and return UKPassport" in {
        val model = UKPassport("AA1111111")
        PassportType.jsonReads.reads(PassportType.jsonWrites.writes(model)) must
          be(JsSuccess(model, JsPath \ "passportType" \ "ukPassportNumber"))
      }

      "Read the json and return error if an invalid value is found" in {
        val json = Json.obj(
          "passportType" -> "09"
        )
        PassportType.jsonReads.reads(json) must be(JsError((JsPath \ "passportType") -> ValidationError("error.invalid")))
      }
    }
  }
}
