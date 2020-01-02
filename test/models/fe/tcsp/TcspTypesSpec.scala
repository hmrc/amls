/*
 * Copyright 2020 HM Revenue & Customs
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

package models.fe.tcsp

import org.scalatestplus.play.PlaySpec
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsSuccess, Json, JsonValidationError}

class TcspTypesSpec extends PlaySpec {

  "TrustOrCompanyServiceProviders" must {

    val Services = TcspTypes(Set(NomineeShareholdersProvider, TrusteeProvider, RegisteredOfficeEtc, CompanyDirectorEtc, CompanyFormationAgent))

    "Json Validation" must {

      "successfully validate given values with option CompanyDirectorEtc" in {
        val json =  Json.obj(
          "serviceProviders" -> Seq("01","02","03","04", "05")
        )

        Json.fromJson[TcspTypes](json) must
          be(JsSuccess(Services))
      }

      "Read and Write Json valid data successfully" in {

        TcspTypes.jsonReads.reads(Json.toJson(Services))
      }

      "throw error message on reading invalid data" in {

        Json.fromJson[TcspTypes](Json.obj("serviceProviders" -> Seq("40"))) must
          be(JsError(JsPath \ "serviceProviders" -> JsonValidationError("error.invalid")))

      }
    }
  }
}
